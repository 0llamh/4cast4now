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
import java.util.Date;

import org.json.*;
import android.app.NotificationManager;

import static android.app.TaskStackBuilder.create;


public class MainActivity extends AppCompatActivity {
    GifView gifView;
    TextView temperatureView;
    String state="Fairfax";
    String city="Virginia";

    final private String API_KEY = "3b7b12e3bec57d6c";
    private String API_URL;

    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gifView = (GifView) findViewById(R.id.gef_view);
        temperatureView = (TextView) findViewById(R.id.temperature);

        try {
            temperatureView.setText(getTemperature(state,city));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // TODO: GET STATE & CITY STRING VALUES
        //      then, pass into getTemperature(state, city) function
        //
        // temperatureView.setText(getTemperature(state, city));

        sendIntent();
    }

    protected String getTemperature(String state, String city) throws JSONException {
        // Given State & City, we can hit our API
        API_URL = "http://api.wunderground.com/api/" + API_KEY + "/conditions/q/" + state + "/" + city + ".json"; // + STATE_NAME/CITY_NAME.json"
        JSONObject api = new JSONObject(API_URL);
        return api.getJSONObject("current_observations").getString("temp_f");
    }




    public void sendIntent(){



        Intent intent = new Intent();
        intent.setAction("com.example.ollamh.proj1");
        intent.putExtra("state",state);
        intent.putExtra("city",city);



        sendBroadcast(intent);

    }
}
