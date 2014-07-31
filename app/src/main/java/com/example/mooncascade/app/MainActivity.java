package com.example.mooncascade.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity{
    public static final String myPrefs = "MyPrefs" ;
    //public static final String REQUEST_URL= "http://www.ilmateenistus.ee/ilma_andmed/xml/forecast.php";
    public static SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

        Intent setPrefs = new Intent(this, UserLocationActivity.class);
        String userLocation;

        prefs = getSharedPreferences(myPrefs, Context.MODE_PRIVATE);
        userLocation = prefs.getString("userLocation",null);

        if(!isNetworkAvailable()){
            Intent noCon = new Intent(this, NoConnectionActivity.class);
            startActivity(noCon);
        }
        else if(userLocation == null){
            startActivity(setPrefs);
        }else{
            final Intent weatherDetail = new Intent(this, WeatherDetailActivity.class);
            final Intent weatherList = new Intent(this, ListAllDatesActivity.class);

            Button detailButton, listButton;
            detailButton = (Button) findViewById(R.id.detail);
            listButton = (Button) findViewById(R.id.forecast);

            detailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(weatherDetail);
                }
            });

            listButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(weatherList);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent setPrefs = new Intent(this, UserLocationActivity.class);
        Intent testIntent = new Intent(this, WeatherDetailActivity.class);

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(setPrefs);
            return true;
        } else if(id == R.id.testItem) {
            startActivity(testIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
