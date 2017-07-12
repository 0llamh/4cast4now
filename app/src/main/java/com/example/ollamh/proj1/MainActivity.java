package com.example.ollamh.proj1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.*;


public class MainActivity extends AppCompatActivity {
    GifView gifView;
    TextView temperatureView;

    final private String API_KEY = "3b7b12e3bec57d6c";
    private String API_URL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gifView = (GifView) findViewById(R.id.gef_view);
        temperatureView = (TextView) findViewById(R.id.temperature);


        // TODO: GET STATE & CITY STRING VALUES
        //      then, pass into getTemperature(state, city) function
        //
        // temperatureView.setText(getTemperature(state, city));

    }

    protected String getTemperature(String state, String city) throws JSONException {
        // Given State & City, we can hit our API
        API_URL = "http://api.wunderground.com/api/" + API_KEY + "/conditions/q/" + state + "/" + city + ".json"; // + STATE_NAME/CITY_NAME.json"
        JSONObject api = new JSONObject(API_URL);
        String temp = api.getJSONObject("current_observations").getString("temp_f");
        return temp;
    }

}
