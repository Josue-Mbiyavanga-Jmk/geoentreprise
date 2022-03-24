package com.iduyatech.geoentreprise.Utils;


import android.app.Activity;
import android.location.Location;

public class GPS implements  com.google.android.gms.location.LocationListener {

    private double latitude;
    private double longitude;

    GpsManaging gpsManaging;


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }


    public GPS(Activity act) {

        gpsManaging = new GpsManaging(act);

        gpsManaging.create_and_connect_GoogleApiClient();

        gpsManaging.getLocationUpdated(GPS.this);



    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }
}
