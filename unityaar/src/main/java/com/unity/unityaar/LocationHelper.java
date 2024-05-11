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
import android.os.Build;
import android.util.Log;

import java.util.List;

public class LocationHelper {
    public static final String TAG = "LocationHelper";
    public static boolean hasPermission = false;
    public static LocationData curLocationData;

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

    static Sensor sensor;
    static float presure;
    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.N)
    public static void requestLocationData(Activity context) {
        if (context != null) {
            SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_PRESSURE);
            if(sensors.size() > 0) {
                sensor = sensors.get(0);
                sensorManager.registerListener(new SensorEventCallback() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        super.onSensorChanged(event);
                        Log.d(TAG,"SensorManager presure = " + presure);
                        presure = event.values[0];
                    }
                }, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            }

            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setAltitudeRequired(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
            String provider = locationManager.getBestProvider(criteria, true);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "requestLocationData onLocationChanged Location = " + location.toString());
                    updateLocationData(location);
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
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            criteria.setPowerRequirement(Criteria.POWER_LOW);//低功耗
            criteria.setAltitudeRequired(true);
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
        float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, presure);
        Log.d(TAG,"updateLocationData altitude = " + altitude + " presure = " + presure);
        location.setAltitude(altitude);
        if (curLocationData == null) {
            curLocationData = new LocationData(location.getTime(), location.getLongitude(), location.getLatitude(), location.getAltitude());
        } else {
            curLocationData.updateData(location.getTime(), location.getLongitude(), location.getLatitude(), location.getAltitude());
        }
    }

//    private double getAltitude(Double longitude, Double latitude) {
//        double result = Double.NaN;
//        HttpClient httpClient = new DefaultHttpClient();
//        HttpContext localContext = new BasicHttpContext();
//        String url = "http://gisdata.usgs.gov/"
//                + "xmlwebservices2/elevation_service.asmx/"
//                + "getElevation?X_Value=" + String.valueOf(longitude)
//                + "&Y_Value=" + String.valueOf(latitude)
//                + "&Elevation_Units=METERS&Source_Layer=-1&Elevation_Only=true";
//        HttpGet httpGet = new HttpGet(url);
//        try {
//            HttpResponse response = httpClient.execute(httpGet, localContext);
//            HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                InputStream instream = entity.getContent();
//                int r = -1;
//                StringBuffer respStr = new StringBuffer();
//                while ((r = instream.read()) != -1)
//                    respStr.append((char) r);
//                String tagOpen = "<double>";
//                String tagClose = "</double>";
//                if (respStr.indexOf(tagOpen) != -1) {
//                    int start = respStr.indexOf(tagOpen) + tagOpen.length();
//                    int end = respStr.indexOf(tagClose);
//                    String value = respStr.substring(start, end);
//                    result = Double.parseDouble(value);
//                }
//                instream.close();
//            }
//        } catch (ClientProtocolException e) {}
//        catch (IOException e) {}
//        return result;
//    }
}
