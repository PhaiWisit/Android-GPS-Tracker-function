package com.example.gpstracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import static android.content.ContentValues.TAG;

public class LocationService extends Service {
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    Location currentLocation;
    double currentLat;
    double currentLng;
    int check = 0;
    int check2 = 0;
    double sum_distanceMeters ;

    public static final String BROADCAST_ACTION = "com.example.tracking.updateprogress";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);


                if (check == 0) {
                    currentLocation = locationResult.getLastLocation();
                    currentLat = currentLocation.getLatitude();
                    currentLng = currentLocation.getLongitude();
                    check = 1;
                }else{
                    Location location2 = locationResult.getLastLocation();
                    double distanceMeters = currentLocation.distanceTo(location2);


                    if (check2 == 0 && distanceMeters > 2){
                        Intent intent = new Intent(BROADCAST_ACTION);
                        intent.putExtra("change",String.valueOf(distanceMeters));
                        intent.putExtra("distance",String.valueOf(distanceMeters));
                        sendBroadcast(intent);
                        check = 0;
                    }else if(distanceMeters > 0.5 ) {
                        sum_distanceMeters = sum_distanceMeters + distanceMeters;
                        Intent intent = new Intent(BROADCAST_ACTION);
                        intent.putExtra("change",String.valueOf(distanceMeters));
                        intent.putExtra("distance",String.valueOf(sum_distanceMeters));
                        sendBroadcast(intent);
                        check2 = 1;
                        check = 0;
                    }
                }




//                double change = getDistance(currentLat,currentLng,lat,lng);


//                Toast.makeText(getApplicationContext(),"Latitude = " + currentLat + " \n Longitude = " + currentLng,Toast.LENGTH_SHORT).show();
//                Log.e("mylog", " Lat is " + locationResult.getLastLocation().getLatitude() + " Lng is " + locationResult.getLastLocation().getLongitude());
            }
        };
    }

    public double getSum_distanceMeters() {
        return sum_distanceMeters;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requestLocation();
        return super.onStartCommand(intent, flags, startId);
    }

    private void requestLocation() {

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());


    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");

        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }

    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double latA = Math.toRadians(lat1);
        double lonA = Math.toRadians(lon1);
        double latB = Math.toRadians(lat2);
        double lonB = Math.toRadians(lon2);
        double cosAng = (Math.cos(latA) * Math.cos(latB) * Math.cos(lonB - lonA)) +
                (Math.sin(latA) * Math.sin(latB));
        double ang = Math.acos(cosAng);
        double dist = ang * 6371;
        return dist;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }




}
