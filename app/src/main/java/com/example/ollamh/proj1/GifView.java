package com.example.ollamh.proj1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import java.io.InputStream;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.SystemClock;
/**
 * Created by Woodham-PC on 7/11/2017.
 */




public class GifView extends View{

    private InputStream gifInputStream;
    private Movie gifMovie;
    private int movieWidth, movieHeight;
    private long movieDuration;
    private long movieStart;

    public GifView(Context context) {
        super(context);
        init(context);
    }


    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GifView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);
        movieWidth = gifMovie.width();
        movieHeight = gifMovie.height();
        movieDuration = gifMovie.duration();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(movieWidth, movieHeight);
    }

    public void setGifMovie(String condition){
        switch (condition) {
            case "Light Drizzle": case "Heavy Drizzle": case "Drizzle":
            case "Light Rain": case "Heavy Rain":case "Rain":
            case "Light Rain Mist":case "Heavy Rain Mist":case "Rain Mist":
            case "Light Rain Showers":case "Heavy Rain Showers":case "Rain Showers":
                gifInputStream = getContext().getResources().openRawResource(+R.drawable.rainy);
                break;
            case "Light Fog":case "Heavy Fog":case "Fog":case "Light Fog Patches":case "Heavy Fog Patches":
            case "Fog Patches":case "Patches of Fog":case "Shallow Fog":case "Partial Fog":
                gifInputStream = getContext().getResources().openRawResource(+R.drawable.foggy);
                break;
            case "Overcast":case "Mostly Cloudy":
                gifInputStream = getContext().getResources().openRawResource(+R.drawable.overcasty);
                break;
            case "Partly Cloudy":case "Scattered Clouds":
                gifInputStream = getContext().getResources().openRawResource(+R.drawable.partly_cloudy);
                break;
            case "Light Snow":case "Heavy Snow":case "Snow":case "Light Snow Grains":case "Heavy Snow Grains":
            case "Snow Grains":case "Light Low Drifting Snow":case "Heavy Low Drifting Snow":case "Low Drifting Snow":
            case "Light Blowing Snow":case "Heavy Blowing Snow":case "Blowing Snow":case "Light Snow Showers":
            case "Heavy Snow Showers":case "Snow Showers":case "Light Snow Blowing Snow Mist":
            case "Heavy Snow Blowing Snow Mist":case "Snow Blowing Snow Mist":
                gifInputStream = getContext().getResources().openRawResource(+R.drawable.snowy);
                break;
            case "Light Thunderstorm":case "Heavy Thunderstorm":case "Thunderstorm":case "Light Thunderstorms and Rain":
            case "Heavy Thunderstorms and Rain":case "Thunderstorms and Rain":case "Light Thunderstorms and Snow":
            case "Thunderstorms and Snow":case "Light Thunderstorms and Ice Pellets":
            case "Heavy Thunderstorms and Ice Pellets":case "Thunderstorms and Ice Pellets":
            case "Light Thunderstorms with Hail":case "Heavy Thunderstorms with Hail":case "Thunderstorms with Hail":
            case "Light Thunderstorms with Small Hail":case "Heavy Thunderstorms with Small Hail":
            case "Thunderstorms with Small Hail":
                gifInputStream = getContext().getResources().openRawResource(+R.drawable.stormy);
                break;
            default:
                gifInputStream = getContext().getResources().openRawResource(+R.drawable.sunny);
        }
        gifMovie = Movie.decodeStream(gifInputStream);
    }

    public int getMovieWidth() {
        return movieWidth;
    }

    public int getMovieHeight() {
        return movieHeight;
    }

    public long getMovieDuration() {
        return movieDuration;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        long now = SystemClock.uptimeMillis();

        if(movieStart == 0) {
            movieStart = now;
        }

        if(gifMovie != null) {

            int dur = gifMovie.duration();
            if(dur == 0) {
                dur = 1000;
            }

            int relTime = (int)((now - movieStart) % dur);

            gifMovie.setTime(relTime);

            gifMovie.draw(canvas, 0, 0);
            invalidate();
        }
    }
}