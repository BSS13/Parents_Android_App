package com.example.hp.parents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

public class ChangePasswordParents extends AppCompatActivity {

    EditText et1, et2, et3, et4;
    TextView tvtop;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_parents);

        et1 = (EditText) (findViewById(R.id.et1));
        et2 = (EditText) (findViewById(R.id.et2));
        et3 = (EditText) (findViewById(R.id.et3));
        et4 = (EditText) (findViewById(R.id.et4));
        tvtop = (TextView) (findViewById(R.id.tvtop));


        et1.setText(GlobalData.rollnumber+"");



        toolbar=(Toolbar)(findViewById(R.id.toolbar));

        toolbar.setTitle("");
        //This will replace toolbar with actionbar
        setSupportActionBar(toolbar);



    }

    public void changepassword(View v) {
        if (et1.getText().toString().equals("") || et2.getText().toString().equals("") || et3.getText().toString().equals("") || et4.getText().toString().equals("")) {
            Toast.makeText(this, "Enter Complete Details", Toast.LENGTH_LONG).show();
        }

        if (!(et3.getText().toString().equals(et4.getText().toString()))) {
            Toast.makeText(this, "New and Confirm do not Match", Toast.LENGTH_LONG).show();
        }


        if (et3.getText().toString().equals(et4.getText().toString())) {
            Thread t=new Thread(new myjob());
            t.start();
        }
    }


    class myjob implements Runnable {
        @Override
        public void run() {
            try {
                // Code to Send Request to Website and Receive JSON Data

                String p,op,np;
                p = et1.getText().toString().trim();
                op = et2.getText().toString().trim();
                np=et3.getText().toString().trim();

                //Send Request to Website
                URL url = new URL(GlobalData.host + "/change_parents_password_from_app?rollnumber=" + p + "&oldpassword=" + op + "&newpassword="+np+"");
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

                            if (datareceived.trim().equals("SUCCESS")) {

                                Toast.makeText(ChangePasswordParents.this, "Password Changed Successfull", Toast.LENGTH_LONG).show();

                            }

                            if (datareceived.trim().equals("FAILURE")){
                                Toast.makeText(ChangePasswordParents.this, "Wrong Password Entered", Toast.LENGTH_LONG).show();

                            }
                        }
                    });

                } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    // IF 404 NOT FOUND , Show Error
                    Toast.makeText(ChangePasswordParents.this, "404 NOT Found", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ChangePasswordParents.this, "Some ERROR Occoured", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }
}

