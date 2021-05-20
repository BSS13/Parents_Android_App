package com.example.hp.parents;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CheckLocation extends Service
{
    double lat, lng;
    Time withinrangetime ;
    public CheckLocation() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }




    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


            LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);

            /////////////////   GET LAST GPS AND NW LOCTIONS if Available  ///////////


            //---check if GPS_PROVIDER is enabled---

            boolean gpsStatus = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);


            //---check if NETWORK_PROVIDER is enabled---

            boolean networkStatus = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            mylocationlistener mylocationlistenerobj = new mylocationlistener();


            // check which provider is enabled

            if (gpsStatus == false && networkStatus == false)

            {

                Toast.makeText(getApplicationContext(), "Both GPS and Newtork are disabled", Toast.LENGTH_LONG).show();


                //---display the "Location services" settings page---

                Intent in = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                startActivity(in);

            }
            if (gpsStatus == true) {

                Toast.makeText(getApplicationContext(), "GPS is Enabled, using it", Toast.LENGTH_LONG).show();

                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 120000, 0, mylocationlistenerobj);

            }


            if (networkStatus == true)

            {

                Toast.makeText(getApplicationContext(), "Network Location is Enabled, using it", Toast.LENGTH_LONG).show();

                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 120000, 0, mylocationlistenerobj);

            }



        new Thread(new Runnable() {
            @Override
            public void run() {

                while(true) {
                    try {
                        // Code to Send Request to Website and Receive JSON Data

                        java.sql.Date date = new java.sql.Date(new Date().getTime());

                        //Send Request to Website
                        URL url = new URL("http://192.168.43.227:8080/School_Bus_Tracking/get_driver_location_for_notification_from_app?rollnumber=" + GlobalData.rollnumber + "&date=" + date);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                        Log.d("ALERT", url.toString());
                        Log.d("ALERT", connection.getResponseCode() + "respone");
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


                            JSONObject singleobject = (JSONObject) (jsonArray.get(0));

                            Double dlat = Double.parseDouble(singleobject.getString("lat"));
                            Double dlng = Double.parseDouble(singleobject.getString("lng"));

                            double earthRadius = 3958.75;
                            double latDiff = Math.toRadians(dlat - lat);
                            double lngDiff = Math.toRadians(dlng - lng);
                            double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                                    Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(dlat)) *
                                            Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
                            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                            double distance = earthRadius * c;

                            int meterConversion = 1609;

                            Double effectivedistance = (distance * meterConversion);
                            Log.d("ALERT", distance + " distance between driver and parent");

                            Time currtime = new Time(System.currentTimeMillis());

                            if (distance < 500.0) {



                                if (withinrangetime==null) {


Log.d("MYMSG","first time");
//                                    Log.d("ALERT", actualdiff + " actualdiff time");
                                    try {
                                        // Code to Send Request to Website and Receive JSON Data


                                        SharedPreferences pref = getSharedPreferences("Parentsphone.txt", MODE_PRIVATE);

                                        String phonenumber = pref.getString("Parentsphone", null);


                                        //Send Request to Website
                                        URL url1 = new URL("http://192.168.43.227:8080/School_Bus_Tracking/Send_School_Bus_Neararound_Notification_from_app?phonenumber=" + phonenumber);
                                        HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();

                                        //Receive Response from Website
                                        if (connection1.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                            // IF 200 OK, Read Incoming Data from Web

                                            int size1 = connection1.getContentLength();
                                            byte b1[] = new byte[size1];

                                            // Read all data and fill in byte array
                                            connection1.getInputStream().read(b1, 0, size1);

                                            // Convert to String
                                            final String datareceived1 = new String(b);

                                            Log.d("MYMESSAGE", datareceived1);
                                            withinrangetime = new Time(System.currentTimeMillis());

                                        } else if (connection1.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                                            // IF 404 NOT FOUND , Show Error
                                            Toast.makeText(CheckLocation.this, "404 NOT Found", Toast.LENGTH_SHORT).show();

                                        } else {
                                            Toast.makeText(CheckLocation.this, "Some ERROR Occoured", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                }
                                else
                                {
                                    Log.d("ALERT", "in if");
                                    String t1 = currtime.toString();

                                    String t2 = withinrangetime.toString();
                                    Log.d("ALERT", t1 + " " + t2);

                                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                                    Date date1 = format.parse(t1);
                                    Date date2 = format.parse(t2);
                                    long difference = date1.getTime() - date2.getTime();
                                    long actualdiff = difference / 1000;

                                    Log.d("ALERT", actualdiff + " actualdiff time");
                                    if(actualdiff>3600)
                                    {

                                    Log.d("ALERT", actualdiff + " actualdiff time");
                                        try {
                                            // Code to Send Request to Website and Receive JSON Data


                                            SharedPreferences pref = getSharedPreferences("Parentsphone.txt", MODE_PRIVATE);

                                            String phonenumber = pref.getString("Parentsphone", null);


                                            //Send Request to Website
                                            URL url1 = new URL("http://192.168.43.227:8080/School_Bus_Tracking/Send_School_Bus_Neararound_Notification_from_app?phonenumber=" + phonenumber);
                                            HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();

                                            //Receive Response from Website
                                            if (connection1.getResponseCode() == HttpURLConnection.HTTP_OK) {
                                                // IF 200 OK, Read Incoming Data from Web

                                                int size1 = connection1.getContentLength();
                                                byte b1[] = new byte[size1];

                                                // Read all data and fill in byte array
                                                connection1.getInputStream().read(b1, 0, size1);

                                                // Convert to String
                                                final String datareceived1 = new String(b);

                                                Log.d("MYMESSAGE", datareceived1);
                                                withinrangetime = new Time(System.currentTimeMillis());

                                            } else if (connection1.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                                                // IF 404 NOT FOUND , Show Error
                                                Toast.makeText(CheckLocation.this, "404 NOT Found", Toast.LENGTH_SHORT).show();

                                            } else {
                                                Toast.makeText(CheckLocation.this, "Some ERROR Occoured", Toast.LENGTH_SHORT).show();
                                            }

                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }

                                    }

                                }


                            }
                        } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                            // IF 404 NOT FOUND , Show Error
                            Toast.makeText(CheckLocation.this, "404 NOT Found", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(CheckLocation.this, "Some ERROR Occoured", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();




        return START_STICKY;



        }



    ///// Inner Class  //////////////////

    class mylocationlistener implements LocationListener {

        public void onLocationChanged(Location location) {

            lat = location.getLatitude();

            lng = location.getLongitude();


            Log.d("ALERT",lat+" "+lng);


        }


        public void onProviderDisabled(String provider)

        {


        }


        public void onProviderEnabled(String provider)

        {


        }


        public void onStatusChanged(String provider, int status, Bundle extras)

        {


        }

    }

}
