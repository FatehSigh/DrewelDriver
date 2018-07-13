package com.octalsoftware.drewel.utils;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.octalsoftware.drewel.AppDelegate;
import com.octalsoftware.drewel.constant.Tags;
import com.octalsoftware.drewel.retrofitService.ApiConstant;
import com.octalsoftware.drewel.retrofitService.ExecuteService;
import com.octalsoftware.drewel.retrofitService.RequestModel;
import com.octalsoftware.drewel.retrofitService.ResponseInterface;
import com.octalsoftware.drewel.retrofitService.RestError;

import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ResponseInterface {
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude = 0, currentLongitude = 0;
    public Prefs prefs;

    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppDelegate.Companion.LogT("onCreate called");
        prefs = new Prefs(this);
        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            AppDelegate.Companion.LogE(e);
        }
        if (new Prefs(this).getUserdata() != null)
            callDriverAvailability(1);
        onLocationUpdate();
    }

    private void onLocationUpdate() {
        initGPS();
        showGPSalert();
    }

    private void showGPSalert() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(LocationService.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(LocationService.this)
                    .addOnConnectionFailedListener(LocationService.this).build();
            mGoogleApiClient.connect();
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        locationRequest.setInterval(15 * 1000);
        locationRequest.setFastestInterval(15 * 1000);
        locationRequest.setSmallestDisplacement(2.0f);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        //**************************
        builder.setAlwaysShow(true); //LocationService.this is the key ingredient
        //**************************
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    private void initGPS() {
        mGoogleApiClient = new GoogleApiClient.Builder(LocationService.this)
                .addConnectionCallbacks(LocationService.this)
                .addOnConnectionFailedListener(LocationService.this)
                .addApi(LocationServices.API)
                .build();
        try {
            mGoogleApiClient.connect();
        } catch (Exception e) {
            AppDelegate.Companion.LogE(e);
        }
        AppDelegate.Companion.LogT("mGoogleApiClient Initialited");
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000)
                .setMaxWaitTime(10000)
                .setFastestInterval(10000); // 10 second, in milliseconds
        AppDelegate.Companion.LogT("mLocationRequest Initialized");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
//        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (new Prefs(this).getUserdata() != null)
            callDriverAvailability(2);
        AppDelegate.Companion.LogT("onTaskRemoved called");
        try {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                        this);
                mGoogleApiClient.disconnect();
            }
        } catch (Exception e) {
            AppDelegate.Companion.LogE(e);
        }
        Intent restartService = new Intent(getApplicationContext(),
                LocationService.this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        //Restart the service once it has been killed android
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, restartServicePI);
    }


    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        AppDelegate.Companion.LogT("onConnected Initialited");
        if (location == null) {
            try {
                AppDelegate.Companion.LogT("onConnected Initialited== null");
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, LocationService.this);
            } catch (Exception e) {
                AppDelegate.Companion.LogE(e);
            }
        } else {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            new Prefs(LocationService.this).putStringValue(Tags.LAT, String.valueOf(currentLatitude));
            new Prefs(LocationService.this).putStringValue(Tags.LNG, String.valueOf(currentLongitude));
            try {
                AppDelegate.Companion.LogT("onConnected Initialited== null");
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, LocationService.this);
                try {
                    executeSendLoactionAsync(currentLatitude + "", currentLongitude + "");
                } catch (Exception e) {
                    AppDelegate.Companion.LogE(e);
                }
            } catch (Exception e) {
                AppDelegate.Companion.LogE(e);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        AppDelegate.Companion.LogT("onConnectionFailed Initialited" + connectionResult);
        if (connectionResult.hasResolution()) {
        } else {
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        AppDelegate.Companion.LogT("onLocationChanged from service==>" + location.getLatitude() + "," + location.getLongitude());
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        new Prefs(LocationService.this).putStringValue(Tags.LAT, String.valueOf(currentLatitude));
        new Prefs(LocationService.this).putStringValue(Tags.LNG, String.valueOf(currentLongitude));
        try {
            executeSendLoactionAsync(currentLatitude + "", currentLongitude + "");
        } catch (Exception e) {
//            AppDelegate.Companion.LogE(e);
        }
    }

    private void executeSendLoactionAsync(String lat, String lng) {
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.latitude, lat);
        paramsHashMap.put(Tags.longitude, lng);
        paramsHashMap.put(Tags.language, new Prefs(this).getDefaultLanguage());
        paramsHashMap.put(Tags.user_id, Objects.requireNonNull(new Prefs(this).getUserdata()).user_id);
//        paramsHashMap.put(Tags.device_id, new Prefs(this).getFcMtokeninTemp());
//        paramsHashMap.put(Tags.device_type, "android");

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.updateLocation);
        requestModel.setWebServiceTag(ApiConstant.updateLocation);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(this, true, this, requestModel);

    }

    private void callDriverAvailability(int is_available) {
        HashMap<String, String> paramsHashMap = new HashMap<String, String>();
        paramsHashMap.put(Tags.status, is_available + "");
        paramsHashMap.put(Tags.user_id, Objects.requireNonNull(new Prefs(this).getUserdata()).user_id);
        paramsHashMap.put(Tags.language, new Prefs(this).getDefaultLanguage());
//        paramsHashMap.put(Tags.device_id, new Prefs(this).getFcMtokeninTemp());
//        paramsHashMap.put(Tags.device_type, "android");

        RequestModel requestModel = new RequestModel();
        requestModel.setWebServiceName(ApiConstant.driverStatus);
        requestModel.setWebServiceTag(ApiConstant.driverStatus);
        requestModel.setWebServiceType(ApiConstant.POST);
        requestModel.setParamsHashmap(paramsHashMap);
        new ExecuteService().execute(this, true, this, requestModel);
    }

    @Override
    public void onNoNetwork(String message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(this);
    }

    @Override
    public void onSuccess(String message, String webServiceTag, String successMsg) {
        switch (webServiceTag) {
            case ApiConstant.updateLocation:
                break;
            case ApiConstant.driverStatus:
                break;
        }
    }

    @Override
    public void onFailure(String message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(this);
    }

    @Override
    public void onFailureRetro(RestError message, String webServiceTag) {
        AppDelegate.Companion.hideProgressDialog(this);
    }

}
