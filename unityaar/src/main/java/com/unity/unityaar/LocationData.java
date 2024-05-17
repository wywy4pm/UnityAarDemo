package com.unity.unityaar;

public class LocationData {
    private long mTime = 0;
    private double mLatitude = 0.0;
    private double mLongitude = 0.0;
    private double mAltitude = 0.0f;

    public LocationData(long mTime, double mLatitude, double mLongitude, double mAltitude) {
        this.mTime = mTime;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mAltitude = mAltitude;
    }

    public void updateData(long mTime, double mLatitude, double mLongitude, double mAltitude) {
        this.mTime = mTime;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mAltitude = mAltitude;
    }

    public long getTime() {
        return mTime;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public double getAltitude() {
        return mAltitude;
    }

    public void setAltitude(double mAltitude) {
        this.mAltitude = mAltitude;
    }

    @Override
    public String toString() {
        return "LocationData{" +
                "mLatitude=" + mLatitude +
                ", mLongitude=" + mLongitude +
                ", mAltitude=" + mAltitude +
                ", mTime=" + mTime +
                '}';
    }
}
