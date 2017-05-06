package com.example.cristiana.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    TextView location;
    public static final String LOC_SAVED = "loc_saved";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* get the location insert by user */
        location = (TextView) findViewById(R.id.city);

        /* skip this page */
        skipThisPage();

        /* add losteners for buttons */
        Button locationButton = (Button) findViewById(R.id.locationButton);
        Button cityButton = (Button) findViewById(R.id.cityButton);

        locationButton.setOnClickListener(this);
        cityButton.setOnClickListener(this);
    }

    private void skipThisPage() {
       /* TODO */
    }

    private void saveLocation() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        preferences.edit().putString(LOC_SAVED, location.getText().toString()).apply();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cityButton) {
            /* check if the city is introduced */
            System.out.println(location.getText());
            if (location.getText().toString().equals("")) {
                String message = "Please insert a city";
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            } else {
                saveLocation();

                Intent intent = new Intent(this, OneDayActivity.class);
                intent.putExtra(LOC_SAVED, location.getText().toString());
                startActivity(intent);
                finish();
                return;
            }
        }

        if (v.getId() == R.id.locationButton) {
            String message = "location button pressed";
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
