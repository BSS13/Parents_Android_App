package com.example.hp.parents;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

public class student_info extends Fragment {

    TextView tv2, tv4, tv6, tv8, tv10, tv12, tv14,tv16;
    ImageView imv1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_info, container, false);
    }

    public void onStart() {
        super.onStart();


        tv2 = (TextView) (getActivity().findViewById(R.id.tv2));
        tv4 = (TextView) (getActivity().findViewById(R.id.tv4));
        tv6 = (TextView) (getActivity().findViewById(R.id.tv6));
        tv8 = (TextView) (getActivity().findViewById(R.id.tv8));
        tv10 = (TextView) (getActivity().findViewById(R.id.tv10));
        tv12 = (TextView) (getActivity().findViewById(R.id.tv12));
        tv14 = (TextView) (getActivity().findViewById(R.id.tv14));
        tv16 = (TextView) (getActivity().findViewById(R.id.tv16));

        imv1 = (ImageView) (getActivity().findViewById(R.id.imv1));

        Thread t=new Thread(new myjob());
        t.start();
    }

    class myjob implements Runnable {
        @Override
        public void run() {
            try {
                // Code to Send Request to Website and Receive JSON Data


                //Send Request to Website
                URL url = new URL(GlobalData.host + "/Student_info_from_app?rollnumber="+ GlobalData.rollnumber);
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

                    JSONObject jsonmain = new JSONObject(datareceived);
                    JSONArray jsonArray;
                    jsonArray = jsonmain.getJSONArray("ans");



                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject singleobject = (JSONObject) (jsonArray.get(i));

                         final String fathername = singleobject.getString("father_name");
                         final String mothername = singleobject.getString("mother_name");
                         final String fatherphone = singleobject.getString("father_phone");
                         final String motherphone = singleobject.getString("mother_phone");
                         final String photo = singleobject.getString("student_photo");
                         final String studentname = singleobject.getString("student_name");
                         final String class1 = singleobject.getString("class");
                         final String section = singleobject.getString("section");
                         final String address = singleobject.getString("address");


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // / Toast.makeText(Connect_Mobile_With_Web3.this, datareceived, Toast.LENGTH_SHORT).show();
                                tv2.setText(studentname);
                                tv4.setText(fathername);
                                tv6.setText(mothername);
                                tv8.setText(fatherphone);
                                tv10.setText(motherphone);
                                tv12.setText(class1);
                                tv14.setText(section);
                                tv16.setText(address);

                                Picasso.with(getActivity().getApplicationContext()).load(GlobalData.host+"/"+photo).into(imv1);

                            }
                        });




                    }



                } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    // IF 404 NOT FOUND , Show Error
                    Toast.makeText(getActivity().getApplicationContext(), "404 NOT Found", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Some ERROR Occoured", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}



