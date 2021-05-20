package com.example.hp.parents;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class Forgot_Password_PARENTS extends AppCompatActivity {

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    EditText et1, et2;
    private FirebaseAuth mAuth;
    String VerificationId;
    TextView tv2;
    Button bt2,bt6;
    TextView tv111;
    Toolbar toolbar;
    RadioGroup rg;
    TextView tvselect;
    String phoneNumber;

    String fatherphone;
    String motherphone;
    String password;
    String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot__password__parents);

        et1 = (EditText) (findViewById(R.id.et1));
        et2 = (EditText) (findViewById(R.id.code));

        tv2 = (TextView) (findViewById(R.id.tv2));
        bt2 = (Button) (findViewById(R.id.bt2));
        bt6 = (Button) (findViewById(R.id.bt6));

        tv111 = (TextView) (findViewById(R.id.tv111));
        tvselect = (TextView) (findViewById(R.id.tvselect));

        rg=(RadioGroup)(findViewById(R.id.rg));

        bt6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedId = rg.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton rb = (RadioButton) findViewById(selectedId);



                if(selectedId==R.id.fatherphone)
                {
                    phone=fatherphone;
                }

                if(selectedId==R.id.motherphone)
                {
                    phone=motherphone;
                }


                AlertDialog.Builder adb=new AlertDialog.Builder(Forgot_Password_PARENTS.this);
                adb.setTitle("Forgot Password OTP Alert Dialog");
                adb.setIcon(R.drawable.logo2);
                adb.setMessage("Are You Sure You Want To Send OTP to "+phone+" ?");
                adb.setCancelable(false);

                //Buttons
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        go();
                    }
                });

                adb.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


                AlertDialog ad=adb.create();
                ad.show();
            }
        });

        toolbar=(Toolbar)(findViewById(R.id.toolbar));

        toolbar.setTitle("");

        //This will replace toolbar with actionbar
        setSupportActionBar(toolbar);

        et2.setVisibility(View.INVISIBLE);
        tv2.setVisibility(View.INVISIBLE);
        bt2.setVisibility(View.INVISIBLE);

        tvselect.setVisibility(View.INVISIBLE);
        rg.setVisibility(View.INVISIBLE);
        bt6.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // onVerificationCompleted is Auto Called if Auto Detection of SMS is done

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without neebbgxding to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.

                et2.setText(credential.getSmsCode());
                //et2.setVisibility(View.INVISIBLE);
                tv2.setVisibility(View.INVISIBLE);
                bt2.setVisibility(View.INVISIBLE);
                Log.d("MYMSG", "verification completed");
                tv111.setText("Auto Detection Complete");
                tv111.setTextColor(Color.GREEN);

                new Thread(new sendsms(phone,"Your Password for login is :"+password)).start();


                //Toast.makeText(MainActivity.this, "Code verified", Toast.LENGTH_SHORT).show();
//                GotoUserHomeActivity();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.d("MYMSG", "onVerificationFailed");

            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                Log.d("MYMSG", "code sent " + verificationId);
                VerificationId = verificationId;
                et2.setVisibility(View.VISIBLE);
                tv2.setVisibility(View.VISIBLE);
                bt2.setVisibility(View.VISIBLE);
            }
        };
    }

    public void fetchphonenos(View v)
    {

            new Thread(new myjob()).start();
    }

        class myjob implements Runnable {
            @Override
            public void run() {
                try {
                    // Code to Send Request to Website and Receive JSON Data

                    String roll;
                    roll = et1.getText().toString().trim();

                    //Send Request to Website
                    URL url = new URL(GlobalData.host + "/PARENTS_phone_fetch_from_app?rollnumber="+roll);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    //Receive Response from Website
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        // IF 200 OK, Read Incoming Data from Web

                        int size = connection.getContentLength();
                        byte b[] = new byte[size];

                        // Read all data and fill in byte array
                        connection.getInputStream().read(b, 0, size);

                        // Convert to String
                        final String datareceived = new String(b);


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // / Toast.makeText(Connect_Mobile_With_Web3.this, datareceived, Toast.LENGTH_SHORT).show();

                                if (datareceived.trim().equals("FAILURE")) {
                                    Toast.makeText(Forgot_Password_PARENTS.this, "Wrong Credentials", Toast.LENGTH_LONG).show();

                                } else {

                                    try {


                                        JSONObject jsonmain = new JSONObject(datareceived);
                                        JSONArray jsonArray = jsonmain.getJSONArray("ans");


                                        JSONObject singleobject = (JSONObject) (jsonArray.get(0));

                                        fatherphone = singleobject.getString("father_phone");
                                        motherphone = singleobject.getString("mother_phone");
                                        password=singleobject.getString("password");

                                        tvselect.setVisibility(View.VISIBLE);
                                        rg.setVisibility(View.VISIBLE);
                                        bt6.setVisibility(View.VISIBLE);


                                    }
                                    catch (Exception ex)
                                    {
                                        ex.printStackTrace();
                                    }

                                }


                            }
                        });

                    } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                        // IF 404 NOT FOUND , Show Error
                        Toast.makeText(Forgot_Password_PARENTS.this, "404 NOT Found", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(Forgot_Password_PARENTS.this, "Some ERROR Occoured", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    public void go() {
        et2.setText("");
        tv111.setText("");

        phoneNumber = et1.getText().toString();

        // We Register mCallbacks which are attached to verification process
        // Which will try to Authenticate Automatically
        // Otherwise we might need to fill code manually and Then Click Verify

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                120,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks


        Log.d("MYMSG", "Phone No Verification Started");


    }

    public void verify(View view) {
        String code = et2.getText().toString();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(VerificationId, code);
        signInWithPhoneAuthCredential(credential);

    }


    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(MainActivity.this, "Phone Verified", Toast.LENGTH_SHORT).show();

                            et2.setText(credential.getSmsCode());
                            //et2.setVisibility(View.INVISIBLE);
                            tv2.setVisibility(View.INVISIBLE);
                            bt2.setVisibility(View.INVISIBLE);

                            tv111.setText("Manual Verification Complete");
                            tv111.setTextColor(Color.BLUE);

                            new Thread(new sendsms(phone,"Your Password for login is :"+password)).start();


//                            GotoUserHomeActivity();

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(Forgot_Password_PARENTS.this, "Invalid code", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });


    }


//    public void GotoUserHomeActivity() {
//        //Save Phone No in Local Storage for Future use and Change Activity
//        SharedPreferences sharedPreferences = getSharedPreferences("mypref1", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        editor.putString("mobileno", phoneNumber);
//
//        editor.commit();
//
//        Intent in = new Intent(this, UserHomeActivity.class);
//        startActivity(in);
//    }
}



