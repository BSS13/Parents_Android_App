package com.example.hp.parents;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;


public class bus_entry_exit_record extends Fragment {


    ListView lv1111;
    Button bt1111;
    TextView tv2222, tv4444;
    EditText et1;
    ArrayList<History> al = new ArrayList<>();
    myadapter ad;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bus_entry_exit_record, container, false);
    }

    public void onStart() {
        super.onStart();

        lv1111 = (ListView) (getActivity().findViewById(R.id.lv1111));
        bt1111 = (Button) (getActivity().findViewById(R.id.bt1111));
        et1=(EditText)(getActivity().findViewById(R.id.et1));

        ad = new myadapter();

        lv1111.setAdapter(ad);



        bt1111.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread t = new Thread(new myjob());
                t.start();
            }
        });

    }





class myjob implements Runnable {
        @Override
        public void run() {
            try {
                // Code to Send Request to Website and Receive JSON Data


                //Send Request to Website
                URL url = new URL(GlobalData.host + "/fetch_bus_entry_exit_history_from_app?rollnumber="+GlobalData.rollnumber +"&date="+et1.getText().toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                //Receive Response from Website
                Log.d("MYMSG",connection.getResponseCode()+" ");
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // IF 200 OK, Read Incoming Data from Web

                    int size = connection.getContentLength();
                    byte b[] = new byte[size];

                    // Read all data and fill in byte array
                    connection.getInputStream().read(b, 0, size);

                    // Convert to String
                    final String datareceived = new String(b);

                    Log.d("MYMSG",datareceived+" ------");
                    JSONObject jsonmain = new JSONObject(datareceived);
                    JSONArray jsonArray;
                    jsonArray = jsonmain.getJSONArray("ans");

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

                        String idate=singleobject.getString("date");
                        String school_entry_time = singleobject.getString("school_entry_time");
                        String school_exit_time = singleobject.getString("school_exit_time");

                        al.add(new History(idate,school_entry_time,school_exit_time));

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
                customView = inflater.inflate(R.layout.single_row_bus_entry_exit_record, parent, false);
            }

            final History st = al.get(i);

            TextView tv111,tv222,tv333;

            tv111 = (TextView) (customView.findViewById(R.id.tv111));
            tv222 = (TextView) (customView.findViewById(R.id.tv222));
            tv333 = (TextView) (customView.findViewById(R.id.tv333));

            tv111.setText(st.date);
            tv222.setText(st.intime);
            tv333.setText(st.outtime);


            return customView;
        }
    }


}



