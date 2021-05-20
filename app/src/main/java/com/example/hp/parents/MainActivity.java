package com.example.hp.parents;

import android.*;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText et1,et2;
    Button bt1;
    Toolbar toolbar;
    TextView pass;
    String fatherphone;
    String motherphone;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        bt1=(Button)(findViewById(R.id.bt1));
        et1=(EditText) (findViewById(R.id.et1));
        et2=(EditText) (findViewById(R.id.et2));

        pass=(TextView)(findViewById(R.id.pass));

        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent();
                in.setClass(MainActivity.this,Forgot_Password_PARENTS.class);
                startActivity(in);
            }
        });

        toolbar=(Toolbar)(findViewById(R.id.toolbar));

        toolbar.setTitle("");

        //This will replace toolbar with actionbar
        setSupportActionBar(toolbar);


        SharedPreferences pref=getSharedPreferences("Parentslogin.txt",MODE_PRIVATE);

        String rollnumber=pref.getString("student_roll",null);

        if(rollnumber==null || rollnumber.equals("") ){

        }

        else{



            Intent in=new Intent(this,ParentsHomeActivity.class);
            Intent in1 = getIntent();
            String id="";
            if(in1!=null)
            {
                id = in1.getStringExtra("id");
                Log.d("MYMSG",id+" in main");

            }
            in.putExtra("id",id);
            startActivity(in);
            finish();
        }

        go6();

    }

    public void go6()

    {

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M)

        {

            //Check If Permissions are already granted, otherwise show Ask Permission Dialog

            if(checkPermission())

            {

                Toast.makeText(this, "All Permissions Already Granted", Toast.LENGTH_SHORT).show();

            }

            else

            {

                Toast.makeText(this, "Permission Not Granted", Toast.LENGTH_SHORT).show();

                requestPermission();

            }

        }

    }



    public boolean checkPermission()

    {

        boolean result1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)== PackageManager.PERMISSION_GRANTED;

        boolean result2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED;

        boolean result3 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED;

        boolean result4 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED;

        boolean result5 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS)== PackageManager.PERMISSION_GRANTED;


        return result1 && result2 && result3 && result4 && result5;

    }



    public void requestPermission()

    {

        //Show ASK FOR PERSMISSION DIALOG (passing array of permissions that u want to ask)

        ActivityCompat.requestPermissions(this,

                new String[]{android.Manifest.permission.CALL_PHONE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.RECEIVE_SMS}, 1);

    }



    // After User Selects Desired Permissions, thid method is automatically called

    // It has request code, permissions array and corresponding grantresults array

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)

    {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);



        if(requestCode==1)

        {

            if(grantResults.length>0)

            {



                if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED && grantResults[2]==PackageManager.PERMISSION_GRANTED && grantResults[3]==PackageManager.PERMISSION_GRANTED && grantResults[4]==PackageManager.PERMISSION_GRANTED)

                {

                    Toast.makeText(this, "All PERMISSON GRANTED", Toast.LENGTH_SHORT).show();

                }

                if(grantResults[0]==PackageManager.PERMISSION_DENIED && grantResults[1]==PackageManager.PERMISSION_DENIED)

                {

                    Toast.makeText(this, "All Permission Denied", Toast.LENGTH_SHORT).show();

                }

            }

        }



    }


    public void go(View v) {

        if(et1.getText().toString().equals("") ||  et1.getText().toString().equals(""))
        {
            Toast.makeText(this,"Please Enter Complete Details",Toast.LENGTH_LONG).show();
        }
        else {
            new Thread(new myjob()).start();
        }
    }
    /////  Inner Class //////
    class myjob implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                // Code to Send Request to Website and Receive JSON Data

                String m,p;
                m=et1.getText().toString().trim();
                p=et2.getText().toString().trim();

                //Send Request to Website
                URL url = new URL(GlobalData.host+"/Parents_login_from_app?rollnumber="+m+"&password="+p+"");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                //Receive Response from Website
                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK)
                {
                    // IF 200 OK, Read Incoming Data from Web

                    int size =  connection.getContentLength() ;
                    byte b[]=new byte[size];

                    // Read all data and fill in byte array
                    connection.getInputStream().read(b,0,size);

                    // Convert to String
                    final String datareceived = new String(b);



                        runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // / Toast.makeText(Connect_Mobile_With_Web3.this, datareceived, Toast.LENGTH_SHORT).show();

                            if(datareceived.trim().equals("FAILURE"))
                            {
                                Toast.makeText(MainActivity.this, "Wrong Credentials", Toast.LENGTH_LONG).show();

                            }
                            else
                            {
                               try {
                                   SharedPreferences pref = getSharedPreferences("Parentslogin.txt", MODE_PRIVATE);
                                   SharedPreferences.Editor editor = pref.edit();

                                   editor.putString("student_roll", et1.getText().toString());

                                   editor.commit();

                                   JSONObject jsonmain = new JSONObject(datareceived);
                                   JSONArray jsonArray;
                                   jsonArray = jsonmain.getJSONArray("ans");


                                   JSONObject singleobject = (JSONObject) (jsonArray.get(0));

                                   fatherphone = singleobject.getString("father_phone");
                                   motherphone = singleobject.getString("mother_phone");


                                   Dialog dialog = new Dialog(MainActivity.this);

                                   dialog.setContentView(R.layout.dialog_select);

                                   RadioButton rbfather,rbmother;
                                   final RadioGroup rg;
                                   Button bt;


                                   bt=(Button)(dialog.findViewById(R.id.bt));
                                   rg=(RadioGroup)(dialog.findViewById(R.id.rg));
                                   rbfather=(RadioButton)(dialog.findViewById(R.id.rbfather));
                                   rbmother=(RadioButton)(dialog.findViewById(R.id.rbmother));

                                   bt.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           int selectedId = rg.getCheckedRadioButtonId();

                                           // find the radiobutton by returned id
                                           RadioButton rb = (RadioButton) findViewById(selectedId);



                                           if(selectedId==R.id.rbfather)
                                           {
                                               phone=fatherphone;

                                               try
                                               {
                                                   new Thread(new Runnable() {
                                                       @Override
                                                       public void run() {
                                                   try

                                                   {

                                                       SharedPreferences pref=getSharedPreferences("Parentsphone.txt",MODE_PRIVATE);
                                                       SharedPreferences.Editor editor=pref.edit();


                                                       editor.putString("Parentsphone",phone);

                                                       editor.commit();

                                                       String packagenameofapp = getPackageName();

                                                       SharedPreferences sharedPreferences=getSharedPreferences("mypref1",MODE_PRIVATE);
                                                       String refreshedToken=sharedPreferences.getString("devicetoken",null);



                                                       String cloudserverip = "server1.vmm.education";

                                                       URL url = new URL("http://"+ cloudserverip +"/VMMCloudMessaging/RecordDeviceInfo?devicetoken="+refreshedToken+"&packagenameofapp="+packagenameofapp+"&mobileno="+phone);
                                                       HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                                                       int status = urlConnection.getResponseCode();
                                                       Log.d("MYMESSAGE","Response status "+ status);

                                                       if(status==200)
                                                       {
                                                           InputStream inputStream = urlConnection.getInputStream();

                                                           int conlength = urlConnection.getContentLength();

                                                           byte b[]=new byte[conlength];

                                                           inputStream.read(b,0,conlength);

                                                           String ans=new String(b);
                                                           Log.d("MYMESSAGE","ans from server "+ans);
                                                           runOnUiThread(new Runnable() {
                                                               @Override
                                                               public void run() {
                                                                   Intent in1=new Intent(MainActivity.this,CheckLocation.class);
                                                                   startService(in1);

                                                                   Intent in = new Intent(MainActivity.this, ParentsHomeActivity.class);
                                                                   startActivity(in);

                                                               }
                                                           });

                                                       }
                                                       else
                                                       {
                                                           Log.d("MYMESSAGE","ERROR -> "+status+" "+urlConnection.getResponseMessage());
                                                       }
                                                   }
                                                   catch(Exception e)
                                                   {
                                                       e.printStackTrace();
                                                   }



                                                       }
                                                   }).start();

                                               }
                                               catch(Exception ex)
                                               {
                                                   ex.printStackTrace();
                                               }

                                           }

                                          else if(selectedId==R.id.rbmother)
                                           {
                                               phone=motherphone;

                                               try
                                               {
                                                   new Thread(new Runnable() {
                                                       @Override
                                                       public void run() {

                                                           try
                                                           {

                                                               SharedPreferences pref=getSharedPreferences("Parentsphone.txt",MODE_PRIVATE);
                                                               SharedPreferences.Editor editor=pref.edit();


                                                               editor.putString("Parentsphone",phone);

                                                               editor.commit();

                                                               String packagenameofapp = getPackageName();

                                                               SharedPreferences sharedPreferences=getSharedPreferences("mypref1",MODE_PRIVATE);
                                                               String refreshedToken=sharedPreferences.getString("devicetoken",null);



                                                               String cloudserverip = "server1.vmm.education";

                                                               URL url = new URL("http://"+ cloudserverip +"/VMMCloudMessaging/RecordDeviceInfo?devicetoken="+refreshedToken+"&packagenameofapp="+packagenameofapp+"&mobileno="+phone);
                                                               HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                                                               int status = urlConnection.getResponseCode();
                                                               Log.d("MYMESSAGE","Response status "+ status);

                                                               if(status==200)
                                                               {
                                                                   InputStream inputStream = urlConnection.getInputStream();

                                                                   int conlength = urlConnection.getContentLength();

                                                                   byte b[]=new byte[conlength];

                                                                   inputStream.read(b,0,conlength);

                                                                   String ans=new String(b);
                                                                   Log.d("MYMESSAGE","ans from server "+ans);
                                                                   runOnUiThread(new Runnable() {
                                                                       @Override
                                                                       public void run() {
                                                                           Intent in1=new Intent(MainActivity.this,CheckLocation.class);
                                                                           startService(in1);

                                                                           Intent in = new Intent(MainActivity.this, ParentsHomeActivity.class);
                                                                           startActivity(in);

                                                                       }
                                                                   });

                                                               }
                                                               else
                                                               {
                                                                   Log.d("MYMESSAGE","ERROR -> "+status+" "+urlConnection.getResponseMessage());
                                                               }
                                                           }
                                                           catch(Exception e)
                                                           {
                                                               e.printStackTrace();
                                                           }
                                                       }
                                                   }).start();


                                               }
                                               catch(Exception ex)
                                               {
                                                   ex.printStackTrace();
                                               }

                                           }

                                           else{
                                               runOnUiThread(new Runnable() {
                                                   @Override
                                                   public void run() {
                                                       Toast.makeText(MainActivity.this,"Select atleast One Number",Toast.LENGTH_LONG).show();
                                                   }
                                               });
                                           }
                                       }
                                   });


                                   dialog.show();

                               }
                               catch(Exception ex)
                               {
                                   ex.printStackTrace();
                               }

                            }


                        }
                    });

                }
                else  if(connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
                {
                    // IF 404 NOT FOUND , Show Error
                    Toast.makeText(MainActivity.this, "404 NOT Found", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(MainActivity.this, "Some ERROR Occoured", Toast.LENGTH_SHORT).show();
                }

            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    ////////////////////////
}

