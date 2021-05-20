package com.example.hp.parents;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public  class TrackingDriverHistory extends AppCompatActivity implements OnMapReadyCallback
{
     GoogleMap mMap;
    Toolbar toolbar;
    Double lat,lng,lat2,lng2,distancerounded;
    EditText edate;
    TextView tv1,tv2;
    double distance;
    Calendar myCalendar;
    int est;
    Marker m;

    Button bt1;

    private void updateLabel()
    {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edate.setText(sdf.format(myCalendar.getTime()));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_driver_history);

        toolbar=(Toolbar)(findViewById(R.id.toolbar));
        tv1=(TextView) (findViewById(R.id.tv1));

       edate=(EditText)(findViewById(R.id.edate));
       bt1=(Button)(findViewById(R.id.bt1));


        toolbar.setTitle("");



        //This will replace toolbar with actionbar
        setSupportActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        myCalendar = Calendar.getInstance();



        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
           @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };



        edate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(TrackingDriverHistory.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable()
                {
                    @Override
                    public void run() {


                        try {
                            // Code to Send Request to Website and Receive JSON Data


                            //Send Request to Website
                            URL url = new URL(GlobalData.host + "/fetch_driver_location_history_from_app?rollnumber=" + GlobalData.rollnumber+"&date="+edate.getText().toString());
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
                                final JSONArray jsonArray;
                                jsonArray = jsonmain.getJSONArray("ans");


                                final PolylineOptions polylineOptions = new PolylineOptions();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject singleobject = (JSONObject) (jsonArray.get(i));

                                    Double lat3 = Double.parseDouble(singleobject.getString("lat"));
                                    Double lng3 = Double.parseDouble(singleobject.getString("lng"));

                                    polylineOptions.add(new LatLng(lat3, lng3));


                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mMap.clear();
                                        mMap.addPolyline(polylineOptions);
                                        double lat1 = 0,lng1=0;
                                        try {
                                            lat1 = Double.parseDouble(((JSONObject)jsonArray.get(0)).getString("lat"));
                                            lng1 = Double.parseDouble(((JSONObject)jsonArray.get(0)).getString("lng"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        LatLng mylocation = new LatLng(lat1, lng1);

                                        if(m!=null)
                                        {
                                            m.remove();
                                        }
                                        MarkerOptions markerOptions = new MarkerOptions().position(mylocation).title("Started From Here");
                                        m =  mMap.addMarker(markerOptions);

                                        double lat2 = 0,lng2=0;
                                        try {
                                            lat2 = Double.parseDouble(((JSONObject)jsonArray.get(jsonArray.length()-1)).getString("lat"));
                                            lng2 = Double.parseDouble(((JSONObject)jsonArray.get(jsonArray.length()-1)).getString("lng"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }


                                        LatLng mylocation2 = new LatLng(lat2, lng2);


                                        MarkerOptions markerOptions1 = new MarkerOptions().position(mylocation2).title("Ended Here");
                                        m =  mMap.addMarker(markerOptions1);
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mylocation2,14.0f));

                                    }
                                });


                            } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                                // IF 404 NOT FOUND , Show Error
                                Toast.makeText(getApplicationContext(), "404 NOT Found", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "Some ERROR Occoured", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                }).start();

            }

        });




        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);


    }


        // More code goes here, including the onCreate() method described above.

        @Override
        public void onMapReady(final GoogleMap googleMap) {

            mMap = googleMap;


            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.6340, 74.8723), 16));


        }

    }




