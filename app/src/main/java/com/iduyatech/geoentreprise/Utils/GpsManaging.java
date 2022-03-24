package com.iduyatech.geoentreprise.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;


public class GpsManaging {

    private static final int MY_PERMISSIONS_REQUEST_READ_FINE_LOCATION = 100;

    GoogleApiClient yGoogleApiClient;// client qui te permet � se connecter au service Google Play( pour notre cas c'est maps)

    LocationRequest yLocationRequest;  // cet objet te permet de specifi� les qualit�s des services
    Activity activity;

    public GpsManaging(Activity act) {
        activity = act;
    }


// je créer mon client et je le connecte au play services

    public void create_and_connect_GoogleApiClient() {
        if (yGoogleApiClient == null) {
            yGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(@Nullable Bundle bundle) {

                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        }
                    }).build();

            yGoogleApiClient.connect();
            int x = 0;

        } else {
            yGoogleApiClient.connect();
            int x = 0;
        }
    }

    public void getLocationUpdated(final LocationListener listener) {
        yLocationRequest = LocationRequest.create();
        yLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //l
        yLocationRequest.setInterval(1000);




        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                else
                {
                    /*ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_FINE_LOCATION);*/
                }
                LocationServices.FusedLocationApi.requestLocationUpdates(yGoogleApiClient, yLocationRequest, listener);

            }
        },1000);



    }



    public void connectClient() {
        yGoogleApiClient.connect();
    }

    public void deconnectClient() {
        yGoogleApiClient.disconnect();
    }

    public  boolean isGoogleApiClientConnected()
    {
        return yGoogleApiClient!=null && yGoogleApiClient.isConnected();
    }

}
