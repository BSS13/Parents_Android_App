package com.example.hp.parents;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

public class driver_info extends Fragment {

    TextView tv22, tv44, tv66, tv88, tv1010, tv1212, tv1414,tv1818;
    ImageView imv11;
    Button bt1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_driver_info, container, false);
    }

    public void onStart() {
        super.onStart();


        tv22 = (TextView) (getActivity().findViewById(R.id.tv22));
        tv44 = (TextView) (getActivity().findViewById(R.id.tv44));
        tv66 = (TextView) (getActivity().findViewById(R.id.tv66));
        tv88 = (TextView) (getActivity().findViewById(R.id.tv88));
        tv1010 = (TextView) (getActivity().findViewById(R.id.tv1010));
        tv1212 = (TextView) (getActivity().findViewById(R.id.tv1212));
        tv1414 = (TextView) (getActivity().findViewById(R.id.tv1414));

        tv1818 = (TextView) (getActivity().findViewById(R.id.tv1818));

        imv11 = (ImageView) (getActivity().findViewById(R.id.imv11));

        bt1=(Button)(getActivity().findViewById(R.id.bt1));

        Thread t=new Thread(new myjob());
        t.start();

        tv44.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(Intent.ACTION_DIAL);
                Uri uri=Uri.parse("tel: "+tv44.getText());
                in.setData(uri);
                startActivity(in);
            }
        });

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in=new Intent(getActivity().getApplicationContext(),LiveTrackingDriver.class);
                startActivity(in);
            }
        });


    }

    class myjob implements Runnable {
        @Override
        public void run() {
            try {
                // Code to Send Request to Website and Receive JSON Data


                //Send Request to Website
                URL url = new URL(GlobalData.host + "/Driver_info_from_app?rollnumber="+ GlobalData.rollnumber);
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

                        final String busNo = singleobject.getString("bus_no");
                        final String busType = singleobject.getString("bus_type");
                        final String capacity = singleobject.getString("capacity");
                        final String driverName = singleobject.getString("driver_name");
                        final String driverPhoto= singleobject.getString("driver_photo");
                        final String driverPhone = singleobject.getString("driver_phone");
                        final String schoolInTime = singleobject.getString("school_in_time");
                        final String schoolOutTime = singleobject.getString("school_out_time");

                        final String ownerPhone = singleobject.getString("owner_phone");


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // / Toast.makeText(Connect_Mobile_With_Web3.this, datareceived, Toast.LENGTH_SHORT).show();
                                tv22.setText(driverName);
                                tv44.setText(driverPhone);
                                tv66.setText(busNo);
                                tv88.setText(busType);
                                tv1010.setText(capacity);
                                tv1212.setText(schoolInTime);
                                tv1414.setText(schoolOutTime);

                                tv1818.setText(ownerPhone);

                                Picasso.with(getActivity().getApplicationContext()).load(GlobalData.host+"/"+driverPhoto).into(imv11);

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



