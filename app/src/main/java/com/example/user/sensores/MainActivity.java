package com.example.user.sensores;


import android.content.Intent;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ArrayAdapter;

import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;


import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public  class MainActivity extends ActionBarActivity implements LocationListener{


    TextView tv,tv2,tv3,tv4,tv5,tv6;


    int precision;

    List<ParseObject> ob;
    private ArrayList values;


    private LocationManager locationManager;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "emNrgdU8T7yMTdRk5EcoRVVx71UW4RsD3Lz7xJJL", "Wj45iPUXi4bFQDjJNff9YxnN01iubq2h1W3lQeqO");

        new GetData().execute();




        precision=3;


        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
        boolean enabled= locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(!enabled)
        {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria,false);
        Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null)
        {
            onLocationChanged(location);
        }



        tv4 =  (TextView)findViewById(R.id.textView4);
        tv5 =  (TextView)findViewById(R.id.textView5);


        tv4.setText("Obteniendo latitud...");
        tv5.setText("Obteniendo longitud...");





    }


    private class SendData extends AsyncTask<Void, Void, Void> {

        Location location;

         public SendData (Location location)
        {
            super();
            this.location = location;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance


                        ParseObject testObject = new ParseObject("DataEntry");

                        //JSONObject name = c.getJSONObject("name");

                        //JSONObject imageObject = c.getJSONObject("picture");

                        testObject.put("latitude",location.getLatitude());
                        testObject.put("longitude",location.getLatitude());


                        testObject.saveInBackground();



            return null;
        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog


            new GetData().execute();
        }

    }


    private class GetData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            // Create the array
            values = new ArrayList<String>();
            try {

                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                        "DataEntry");

                ob = query.find();
                for (ParseObject dato : ob) {

                    values.add(dato.get("first")+ " " + dato.get("last"));

                }
            } catch (com.parse.ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Locate the listview in listview_main.xml
            // Pass the results into ListViewAdapter.java

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);

            //listView.setAdapter(adapter);


        }
    }




        @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider,400,1, (LocationListener) this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates((LocationListener) this);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    public void onLocationChanged(Location location) {


        float lat = (float) location.getLatitude();
        float lng =(float)(location.getLongitude());
        tv4 =  (TextView)findViewById(R.id.textView4);
        tv5 =  (TextView)findViewById(R.id.textView5);
        tv4.setText("Latitud : "+String.valueOf(lat));
        tv4.setTextSize(15);

        tv5.setText("Longitud : "+String.valueOf(lng));
        tv5.setTextSize(15);

        String cityName = "";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> direccion;
        try {
            direccion=gcd.getFromLocation(lat,lng,1);
            if(direccion.size()>0)
            {
                cityName ="Usted está en la ciudad de "+direccion.get(0).getAddressLine(1);
                tv4.setText(tv4.getText()+"\n"+cityName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        new SendData(location).execute();


    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }


    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }


    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }




}
