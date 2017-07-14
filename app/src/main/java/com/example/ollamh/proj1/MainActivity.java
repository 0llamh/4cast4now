package com.example.ollamh.proj1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.icu.util.GregorianCalendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;

import org.json.*;
import com.google.code.gson;
import org.w3c.dom.Text;

import android.app.NotificationManager;

import static android.app.TaskStackBuilder.create;


public class MainActivity extends AppCompatActivity {
    GifView gifView;
    TextView temperatureView, locationText, dateText;
    String state, city;

    final private String API_KEY = "3b7b12e3bec57d6c";
    private String API_URL;

    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gifView = (GifView) findViewById(R.id.gef_view);
        temperatureView = (TextView) findViewById(R.id.temperature);
        locationText = (TextView) findViewById(R.id.location);
        dateText = (TextView) findViewById(R.id.date);


        // TODO: GET STATE & CITY STRING VALUES
        state="Virginia";
        city="Fairfax";
        locationText.setText(city + ", " + state);

        // TODO: Get Date & Set TextView accordingly

        // Pass Location into getTemperature(state, city) function
        try {
            temperatureView.setText(getTemperature(state,city));
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        sendIntent();
    }

    protected String getTemperature(String state, String city) throws JSONException, IOException {
        // Given State & City, we can hit our API
        API_URL = "http://api.wunderground.com/api/" + API_KEY + "/conditions/q/" + state + "/" + city + ".json"; // + STATE_NAME/CITY_NAME.json"
//        JSONObject api = urlToJSON(API_URL);

        // Connect to the URL using java's native library
        URL url = new URL(API_URL);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        // Convert to a JSON object to print data
        JSONParser jp = new JsonParser(); //from gson
        JSONElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
        JSONObject api = root.getAsJsonObject(); //May be an array, may be an object.
        zipcode = rootobj.get("zip_code").getAsString(); //just grab the zipcode

        String temp = api.getJSONObject("current_observation").getString("temp_f") + "°";
        return temp;
    }

//    public static JSONObject getJSONfromURL(String url){
//        initializeInputStream is = null;
//         String result = "";
//         JSONObject jArray = null;
//         http posttry {
//             HttpClient httpclient = new DefaultHttpClient();
//             HttpPost httppost = new HttpPost(url);
//             HttpResponse response = httpclient.execute(httppost);
//             HttpEntity entity = response.getEntity();is = entity.getContent();
//         }
//         catch(Exception e){
//         Log.e("log_tag", "Error in http connection "+e.toString());
//         } //convert response to stringtry{BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
//         StringBuilder sb = new StringBuilder();
//         String line = null;
//         while ((line = reader.readLine()) != null) {
//             sb.append(line + "\n");}is.close();
//             result=sb.toString();
//         }catch(Exception e){Log.e("log_tag", "Error converting result "+e.toString());
//         }
//         try parse the string to a JSON objecttry{
//         jArray = new JSONObject(result);}catch(JSONException e){
//         Log.e("log_tag", "Error parsing data "+e.toString());
//         }
//         return jArray;}
//    }

    private static String readBuffer(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject urlToJSON (String url) throws IOException, JSONException{
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            return new JSONObject(readBuffer(rd));
        } finally {
            is.close();
        }
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
}
