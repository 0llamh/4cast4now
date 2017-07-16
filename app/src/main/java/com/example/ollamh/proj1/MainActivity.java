package com.example.ollamh.proj1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.icu.util.GregorianCalendar;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.JsonReader;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Date;

import org.json.*;

import android.app.NotificationManager;
import android.widget.Toast;

import static android.app.TaskStackBuilder.create;


public class MainActivity extends AppCompatActivity implements LocationListener{
    GifView gifView;
    TextView locationText, dateText, temperatureView, realFeelView, dewPointView;
    String state, city;

    final private String API_KEY = "3b7b12e3bec57d6c";
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 30; // 30 minute
    private String API_URL;
    private static String JSON_STRING;
    private Location location;
    private double latitude, longitude;

    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        JsonFromURLTask task = new JsonFromURLTask();

        gifView = (GifView) findViewById(R.id.gef_view);
        temperatureView = (TextView) findViewById(R.id.temperature);
        locationText = (TextView) findViewById(R.id.location);
        dateText = (TextView) findViewById(R.id.date);
        realFeelView = (TextView) findViewById(R.id.realfeelNumber);
        dewPointView = (TextView) findViewById(R.id.dewpointNumber);

        boolean gps = false, network = false, loc = false;

        // TODO: GET STATE & CITY STRING VALUES

//        try {
//            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            gps = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
//            network = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//            if (!gps && !network){
//                // no provider enabled
//            } else {
//                loc = true;
//                if (network){
//                    location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return location;

        GPSTracker gpsTracker = new GPSTracker(this);



        state="sc";
        city="charleston";
        locationText.setText(city + ", " + state.toUpperCase());
        API_URL = "http://api.wunderground.com/api/" + API_KEY + "/conditions/q/" + state + "/" + city + ".json";
        task.execute(new String[] {API_URL});

        // Get Date & Set TextView accordingly
        dateText.setText(DateFormat.getDateInstance().format(new Date()));

        // Pass Location into getTemperature(state, city) function
//        try {
//            temperatureView.setText(getTemperature(API_URL));
//        } catch (JSONException | IOException e) {
//            Toast.makeText(this, "JSON Error", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }

        sendIntent();
    }

    protected String getTemperature(String jsonstring) throws JSONException, IOException {
        // Given State & City, we can hit our API
        JSONObject json = new JSONObject(jsonstring);
        return json.getJSONObject("current_observation").getString("temp_f") + "°";
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
    protected String getWeatherCondition(String jsonstring) throws JSONException, IOException {
        // Given State & City, we can hit our API
        JSONObject json = new JSONObject(jsonstring);
        return json.getJSONObject("current_observation").getString("weather") + "°";
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
                gifView.setGifMovie(getWeatherCondition(MainActivity.JSON_STRING));
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}
