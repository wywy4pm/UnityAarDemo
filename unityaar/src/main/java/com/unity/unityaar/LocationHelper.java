package com.unity.unityaar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventCallback;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.util.Log;

import java.util.List;

public class LocationHelper {
    public static final String TAG = "LocationHelper";
    public static boolean hasPermission = false;
    public static LocationData curLocationData;
    private static double altitude;

    public static boolean hasLocationPermission(Context context) {
        String[] noPerArray = PermissionUtils.hasPermission(context);
        if (noPerArray == null) {
            return true;
        }
        return false;
    }

    public static void requestLocationPermission(Context context) {
        PermissionActivity.jumpPermission(context);
    }

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.N)
    public static void requestLocationData(Activity context) {
        if (context != null) {

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//            Criteria criteria = new Criteria();
////            criteria.setAccuracy(Criteria.ACCURACY_FINE);
//            criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
//            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
//            criteria.setAltitudeRequired(true);
//            criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
            String provider = LocationManager.GPS_PROVIDER;
//            Log.d(TAG,"provider = " + provider);
            locationManager.requestLocationUpdates(provider,1000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "requestLocationData onLocationChanged Location = " + location.toString());
                    updateLocationData(location);
                }
            });
            locationManager.addNmeaListener(new OnNmeaMessageListener() {
                @Override
                public void onNmeaMessage(String message, long timestamp) {
                    //获取成功，停止监听，发布结果
                    Log.d(TAG, "requestLocationData onNmeaMessage message = " + message);
                    if (message.startsWith("$GPGGA") || message.startsWith("$GNGGA")) {
                        String[] tokens = message.split(",");
                        if (tokens.length < 10) return;
                        if (tokens[9].isEmpty()) return;
                        altitude = Double.parseDouble(tokens[9]);
                        if (curLocationData != null) {
                            curLocationData.setAltitude(altitude);
                        }
                    }
                }
            });
            locationManager.registerGnssStatusCallback(new GnssStatus.Callback() {
                @Override
                public void onStarted() {
                    super.onStarted();
                    Log.d(TAG, "GnssStatus onStarted");
                }

                @Override
                public void onStopped() {
                    super.onStopped();
                    Log.d(TAG, "GnssStatus onStopped");
                }

                @Override
                public void onFirstFix(int ttffMillis) {
                    super.onFirstFix(ttffMillis);
                    Log.d(TAG, "GnssStatus onFirstFix ttffMillis = " + ttffMillis);
                }

                @Override
                public void onSatelliteStatusChanged(GnssStatus status) {
                    super.onSatelliteStatusChanged(status);
                    int satelliteCount = status.getSatelliteCount();
                    int bdSatelliteCount = 0;
                    if (satelliteCount > 0) {
                        for (int i = 0; i < satelliteCount; i++) {
                            // get satellite type
                            int type = status.getConstellationType(i);
                            if (GnssStatus.CONSTELLATION_BEIDOU == type) {
                                // increase if type == BEIDOU
                                bdSatelliteCount++;
                            }
                        }
                        Log.d(TAG, "GnssStatus onSatelliteStatusChanged BDS count：" + bdSatelliteCount);
                    }
                }
            });
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                Log.d(TAG, "getLocationData Location = " + location.toString() + " time = " + location.getTime());
                updateLocationData(location);
            }
        }
    }

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.M)
    public static LocationData getLocationData(Activity context) {
        if (context != null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//            Criteria criteria = new Criteria();
////            criteria.setAccuracy(Criteria.ACCURACY_FINE);
//            criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
//            criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
//            criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
//            criteria.setAltitudeRequired(true);
//            String provider = locationManager.getBestProvider(criteria, true);
//            Log.d(TAG, "getLocationData provider = " + provider);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                Log.d(TAG, "getLocationData Location = " + location.toString() + " time = " + location.getTime());
                updateLocationData(location);
                return curLocationData;
            }
        }
        return null;
    }

    private static void updateLocationData(Location location) {
//        float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, presure);
//        Log.d(TAG,"updateLocationData altitude = " + altitude + " presure = " + presure);
//        location.setAltitude(altitude);
        double alt = altitude != 0 ? altitude : location.getAltitude();
        if (curLocationData == null) {
            curLocationData = new LocationData(location.getTime(), location.getLatitude(), location.getLongitude(), alt);
        } else {
            curLocationData.updateData(location.getTime(), location.getLatitude(), location.getLongitude(), alt);
        }
    }
}
