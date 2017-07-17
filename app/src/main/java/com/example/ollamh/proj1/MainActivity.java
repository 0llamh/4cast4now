package com.example.ollamh.proj1;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.*;

import android.app.NotificationManager;
import android.widget.Toast;

import static android.app.TaskStackBuilder.create;


public class MainActivity extends AppCompatActivity implements LocationListener{
    GifView gifView;
    TextView locationText, dateText, temperatureView, realFeelView, dewPointView, humidity;
    String state, city;
    double latitude, longitude;

    final private String API_KEY = "3b7b12e3bec57d6c";
    private String API_URL;
    private static String JSON_STRING;

    LocationManager locationManager;
    public boolean locationFlag=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationFlag=true;
        if (!locationFlag)
            finish();
        getLocation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JsonFromURLTask task = new JsonFromURLTask();
        gifView = (GifView) findViewById(R.id.gef_view);
        temperatureView = (TextView) findViewById(R.id.temperature);
        locationText = (TextView) findViewById(R.id.location);
        dateText = (TextView) findViewById(R.id.date);
        realFeelView = (TextView) findViewById(R.id.realfeelNumber);
        humidity = (TextView) findViewById(R.id.humidityNumber);
        dewPointView = (TextView) findViewById(R.id.dewpointNumber);

        // TODO: GET STATE & CITY STRING VALUES


        if(city!=null && state!=null)
            locationText.setText(city + ", " + state);
        API_URL = "http://api.wunderground.com/api/" + API_KEY + "/conditions/q/" + state + "/" + city + ".json";
        task.execute(new String[] {API_URL});

        // Get Date & Set TextView accordingly
        dateText.setText(DateFormat.getDateInstance().format(new Date()));

        sendIntent();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();

        getLocation();
        if(city!=null && state!=null)
            locationText.setText(city + ", " + state);
        API_URL = "http://api.wunderground.com/api/" + API_KEY + "/conditions/q/" + state + "/" + city + ".json";
        JsonFromURLTask task = new JsonFromURLTask();
        task.execute(new String[] {API_URL});

        dateText.setText(DateFormat.getDateInstance().format(new Date()));

    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            //return;
        } else {
            try {

                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider = locationManager.getBestProvider(criteria, true);
                Location location = locationManager.getLastKnownLocation(provider);

                locationManager.requestLocationUpdates(provider, 1000, 0, this);
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Log.d("latitude", latitude + "");
                Log.d("longitude", longitude + "");

                Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
                List<Address> addresses;
                try {
                    addresses = gcd.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0)
                        Log.d("address[0].getLocality:", addresses.get(0).getLocality());
                    city = addresses.get(0).getLocality();
                    state = addresses.get(0).getAdminArea();
                    addresses.clear();
                    Log.d("City", city);
                    Log.d("State", state);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception ex) {
                Toast.makeText(this, "Please Turn on Location Services and Restart App", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        latitude = (double) (location.getLatitude());
        longitude = (double) (location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    protected String getTemperature(String jsonstring) throws JSONException, IOException {
        // Given State & City, we can hit our API
        JSONObject json = new JSONObject(jsonstring);
        String temp = json.getJSONObject("current_observation").getString("temp_f");
        String intTemp="";
        for (int i =0; i<temp.length(); i++)
            if (temp.charAt(i)!='.')

                intTemp+=temp.charAt(i);
            else
                break;
        return intTemp+"°";

        //int i = Integer.parseInt(temp);
        //return i + "°";
    }

    protected String getRealFeel(String jsonstring) throws JSONException, IOException {
        // Given State & City, we can hit our API
        JSONObject json = new JSONObject(jsonstring);
        return json.getJSONObject("current_observation").getString("feelslike_f") + "°";
    }
    protected String getDewPoint(String jsonstring) throws JSONException, IOException {
        // Given State & City, we can hit our API
        JSONObject json = new JSONObject(jsonstring);
        return json.getJSONObject("current_observation").getString("dewpoint_f") + "°";
    }
    protected String getHumidity(String jsonstring) throws JSONException, IOException {
        // Given State & City, we can hit our API
        JSONObject json = new JSONObject(jsonstring);
        return json.getJSONObject("current_observation").getString("relative_humidity");
    }
    protected String getConditions(String jsonstring) throws JSONException, IOException {
        // Given State & City, we can hit our API
        JSONObject json = new JSONObject(jsonstring);
        return json.getJSONObject("current_observation").getString("weather");
    }

    public void sendIntent(){

        Intent intent = new Intent();
        intent.setAction("com.example.ollamh.proj1");
        intent.putExtra("state",state);
        intent.putExtra("city",city);

        Date time = new Date(System.currentTimeMillis());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        AlarmManager interval = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        interval.setRepeating(AlarmManager.RTC_WAKEUP, time.getTime(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);


    }

    protected class JsonFromURLTask extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... url) {
            try {
                InputStream is = new URL(url[0]).openConnection().getInputStream();
                try {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                    StringBuilder sb = new StringBuilder();
                    int cp;
                    while ((cp = rd.read()) != -1) {
                        if ((char) cp != '\n' || (char) cp != '\t')
                            sb.append((char) cp);
                        else
                            sb.append(' ');
                    }
                    MainActivity.JSON_STRING = sb.toString();
                    is.close();
                    Toast.makeText(MainActivity.this, "Downloading Weather Information...", Toast.LENGTH_LONG).show();
                    return MainActivity.JSON_STRING;
                } finally {
                    is.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "Download Failed";
            }

        }

        protected void onPostExecute(String result) {
            try {
                temperatureView.setText(getTemperature(MainActivity.JSON_STRING));
                realFeelView.setText(getRealFeel(MainActivity.JSON_STRING));
                dewPointView.setText(getDewPoint(MainActivity.JSON_STRING));
                humidity.setText(getHumidity(MainActivity.JSON_STRING));
                gifView.setGifMovie(getConditions(MainActivity.JSON_STRING));
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }
    }

}
