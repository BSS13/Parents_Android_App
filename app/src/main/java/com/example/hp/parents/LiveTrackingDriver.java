package com.example.hp.parents;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.Calendar;

public  class LiveTrackingDriver extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnPolylineClickListener,GoogleMap.OnPolygonClickListener
{
    private GoogleMap mMap;
    Toolbar toolbar;
    Double lat,lng,lat2,lng2,distancerounded;
    TextView tv1,tv2;
    double distance;
    int est;
    Polyline line;
    Marker m,m1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_tracking_driver);

        toolbar=(Toolbar)(findViewById(R.id.toolbar));
        tv1=(TextView) (findViewById(R.id.tv1));
        tv2=(TextView)(findViewById(R.id.tv2));

        toolbar.setTitle("");

        //This will replace toolbar with actionbar
        setSupportActionBar(toolbar);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);


        ////////   Logic to get CURRENT LOCATIONS /////////////////



        //---check if GPS_PROVIDER is enabled---

        boolean gpsStatus = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);


        //---check if NETWORK_PROVIDER is enabled---

        boolean networkStatus = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


        mylocationlistener mylocationlistenerobj=new mylocationlistener();

        // check which provider is enabled

        if (gpsStatus==false && networkStatus==false)
        {

            Toast.makeText(this , "Both GPS and Newtork are disabled", Toast.LENGTH_LONG).show();

            //---display the "Location services" settings page---

            Intent in = new  Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

            startActivity(in);
        }


        if(gpsStatus==true)
        {

            Toast.makeText(this, "GPS is Enabled, using it", Toast.LENGTH_LONG).show();

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,mylocationlistenerobj);

        }


        if(networkStatus==true)
        {

            Toast.makeText(this, "Network Location is Enabled, using it", Toast.LENGTH_LONG).show();

            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0,0, mylocationlistenerobj);

        }

    }



    public Double distanceTo(Double lat_a,Double lng_a,Double lat_b, Double lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Double(distance * meterConversion).doubleValue();
    }



    class mylocationlistener implements LocationListener {

        public void onLocationChanged(Location location) {



            lat = location.getLatitude();
            lng = location.getLongitude();

            LatLng mylocation = new LatLng(lat, lng);

if(m1!=null)
{
    m1.remove();
}
            MarkerOptions markerOptions = new MarkerOptions().position(mylocation).title("Your's Location");
           m1 =  mMap.addMarker(markerOptions);


        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }



        // More code goes here, including the onCreate() method described above.

        @Override
        public void onMapReady(final GoogleMap googleMap) {

        mMap = googleMap;
            new Thread(new Runnable()
            {
                  @Override
                  public void run() {


                      try {
                          // Code to Send Request to Website and Receive JSON Data
                          while (true) {


                              //Send Request to Website
                              URL url = new URL(GlobalData.host + "/fetch_live_driver_location_from_app?rollnumber=" + GlobalData.rollnumber);
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

                                  final JSONArray jsonArray = jsonmain.getJSONArray("ans");


                                  final PolylineOptions polylineOptions = new PolylineOptions();
                                  for (int i = 0; i < jsonArray.length(); i++) {
                                      JSONObject singleobject = (JSONObject) (jsonArray.get(i));

                                      Double lat3 = Double.parseDouble(singleobject.getString("lat"));
                                      Double lng3 = Double.parseDouble(singleobject.getString("lng"));

                                      polylineOptions.add(new LatLng(lat3, lng3));

                                      if(i==(jsonArray.length()-1))
                                      {

                                           lat2 = lat3;
                                           lng2 =lng3;


                                      }

                                  }

                                  runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          if(line!=null)
                                          {
                                              line.remove();
                                          }
                                          if(m!=null)
                                          {
                                              m.remove();
                                          }
                                          double lat2 = 0,lng2=0;
                                          try {
                                              lat2 = Double.parseDouble(((JSONObject)jsonArray.get(jsonArray.length()-1)).getString("lat"));
                                              lng2 = Double.parseDouble(((JSONObject)jsonArray.get(jsonArray.length()-1)).getString("lng"));
                                          } catch (JSONException e) {
                                              e.printStackTrace();
                                          }


                                          LatLng mylocation2 = new LatLng(lat2, lng2);


                                          MarkerOptions markerOptions1 = new MarkerOptions().position(mylocation2).title("Bus's  Current Location");
                                          m =  mMap.addMarker(markerOptions1);

                                      }
                                  });
                                if(lat!=null)
                                {
                                    distance = distanceTo(lat,lng,lat2,lng2);

                                    DecimalFormat df2 = new DecimalFormat(".##");

                                    distancerounded = Double.parseDouble(df2.format(distance));

                                    distancerounded=distancerounded/1000.0d;

                                    Double speedIs1KmMinute = 10.00;
                                    Double estimatedDriveTimeInMinutes = distance / speedIs1KmMinute;
                                    est=(int)Math.round(estimatedDriveTimeInMinutes);
                                    est=est/60;

                                }
                                  Log.d("MYMSG",lat+" "+lng+" "+lat2+" "+lng2);


                                  runOnUiThread(new Runnable() {
                                      @Override
                                      public void run() {
                                          line = mMap.addPolyline(polylineOptions);



                                          tv1.setText(distancerounded+" KMs");
                                          tv2.setText(est+" minute(s)");

                                      }
                                  });


                              } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                                  // IF 404 NOT FOUND , Show Error
                                  Toast.makeText(getApplicationContext(), "404 NOT Found", Toast.LENGTH_SHORT).show();

                              } else {
                                  Toast.makeText(getApplicationContext(), "Some ERROR Occoured", Toast.LENGTH_SHORT).show();
                              }


                              try{
                                  Thread.sleep(10000);
                              }
                              catch(Exception ex)
                              {
                                  ex.printStackTrace();
                              }

                          }
                      }catch (Exception ex) {
                          ex.printStackTrace();
                      }
                  }

              }).start();
            // Add polylines and polygons to the map. This section shows just
            // a single polyline. Read the rest of the tutorial to learn more.

            // Position the map's camera near Alice Springs in the center of Australia,
            // and set the zoom factor so most of Australia shows on the screen.

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.6340, 74.8723), 16));

            // Set listeners for click events.
//            googleMap.setOnPolylineClickListener(this);
//            googleMap.setOnPolygonClickListener(this);
           }
            @Override
            public void onPolygonClick(Polygon polygon) {

            }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }

}



