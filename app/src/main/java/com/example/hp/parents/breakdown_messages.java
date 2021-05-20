package com.example.hp.parents;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class breakdown_messages extends Fragment {

    ListView lv1;
    ArrayList<Alert> al = new ArrayList<>();
    myadapter ad;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_breakdown_messages, container, false);
    }

    public void onStart() {
        super.onStart();


       lv1=(ListView)(getActivity().findViewById(R.id.lv1));

        ad = new myadapter();

        lv1.setAdapter(ad);


    }

    @Override
    public void onResume() {
        super.onResume();
        Thread t=new Thread(new myjob());
        t.start();
    }

    class myjob implements Runnable {
        @Override
        public void run() {
            try {
                // Code to Send Request to Website and Receive JSON Data


                //Send Request to Website
                URL url = new URL(GlobalData.host + "/get_alert_messages_from_app?rollnumber="+ GlobalData.rollnumber);
                Log.d("MYMSG",url.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                //Receive Response from Website
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // IF 200 OK, Read Incoming Data from Web

                    int size = connection.getContentLength();
                    Log.d("MYMSG",size+" size of data");
                    String datareceived = new String();
                    byte[] buffer = new byte[size];
                    int count=0;
                    while ((count = connection.getInputStream().read(buffer, 0, size)) > 0)
                    { datareceived += new String(buffer, 0, count); }


                    JSONObject jsonmain = new JSONObject(datareceived);
                    JSONArray jsonArray;
                    jsonArray = jsonmain.getJSONArray("ans");
                    Log.d("BREAKDOWN",datareceived);


                    al.clear();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ad.notifyDataSetChanged();
                            // / Toast.makeText(Connect_Mobile_With_Web3.this, datareceived, Toast.LENGTH_SHORT).show();
                        }
                    });

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject singleobject = (JSONObject) (jsonArray.get(i));

                        String msg=singleobject.getString("message");
                        msg = msg.replace("%20"," ");
//                        Double lat=Double.parseDouble(singleobject.getString("lat"));
//                        Double lng =Double.parseDouble(singleobject.getString("lng"));
                        String date = singleobject.getString("date");
                        String time = singleobject.getString("time");

                        al.add(new Alert(msg,0.0,0.0,date,time));




                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ad.notifyDataSetChanged();
                            // / Toast.makeText(Connect_Mobile_With_Web3.this, datareceived, Toast.LENGTH_SHORT).show();
                        }
                    });


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


    class myadapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return al.size();
        }

        @Override
        public Object getItem(int i) {
            return al.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i*10;
        }

        @Override
        public View getView(final int i, View customView, ViewGroup parent) {



            if(customView==null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity().getApplicationContext());
                customView = inflater.inflate(R.layout.single_row_for_alert_message, parent, false);
            }

            final Alert st = al.get(i);

            TextView etmsg,etdatetime,etlocation;

            etmsg = (TextView) (customView.findViewById(R.id.etmsg));
//            etlocation = (TextView) (customView.findViewById(R.id.etlocation));
            etdatetime = (TextView) (customView.findViewById(R.id.etdatetime));


            etmsg.setText(st.message);
            etdatetime.setText(st.date+" "+st.time);
//            etlocation.setText("Lat:"+st.lat+"Lng:"+st.lng);


            return customView;
        }
    }

}



