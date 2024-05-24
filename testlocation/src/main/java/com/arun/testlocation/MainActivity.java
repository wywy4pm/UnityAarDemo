package com.arun.testlocation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.unity.unityaar.LocationData;
import com.unity.unityaar.LocationHelper;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private Handler handler;
    private LocationData locationData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        handler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean per = LocationHelper.hasLocationPermission(this);
        if (!per) {
            LocationHelper.requestLocationPermission(this);
        } else {
            LocationHelper.requestLocationData(this);
            if (handler != null) {
                handler.postDelayed(runnable, 1000);
            }
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            locationData = LocationHelper.curLocationData;
            if (locationData != null && textView != null) {
                //Log.d("LocationHelper","locationData = " + locationData.toString());

                textView.setText(locationData.toString());
            }
            if (handler != null) {
                handler.postDelayed(this, 1000);
            }
        }
    };
}