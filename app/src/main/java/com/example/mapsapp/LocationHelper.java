package com.example.mapsapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


class LocationHelper
        implements PermissionRequester.ResultListener,
        OnSuccessListener<LocationSettingsResponse>,
        OnFailureListener {

    public interface UpdateListener {
        void onLocationResult(LocationResult locationResult);
    }

    private static final String TAG = "LocationHelper";

    private int mRequestLocationCode;
    private int mRequestSettingsCode;

    private AppCompatActivity mActivity;
    private UpdateListener mUpdateListener;

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private PermissionRequester mPermissionRequester;

    public LocationHelper(
            AppCompatActivity activity,
            int requestLocationCode, int requestSettingsCode) {
        mRequestLocationCode = requestLocationCode;
        mRequestSettingsCode = requestSettingsCode;

        mActivity = activity;
        mUpdateListener = null;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.v(TAG, "onLocationResult");
                if(mUpdateListener != null) {
                    mUpdateListener.onLocationResult(locationResult);
                } else {
                    Log.i(TAG, "FAIL: mUpdateListener != null");
                }
            }
        };

        mPermissionRequester = new PermissionRequester(
                mActivity, Manifest.permission.ACCESS_FINE_LOCATION, mRequestLocationCode);

        mPermissionRequester.setResultListener(this);
    }

    public void setLocationUpdateListener(UpdateListener listener) {
        mUpdateListener = listener;
    }

    public void startUpdates() {
        Log.v(TAG, "enableUpdates");
        mPermissionRequester.askUserForPermission();
    }

    public void stopUpdates() {
        Log.v(TAG, "stopUpdates");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }




    // This section is responsible for asking location permission
    public void onPermissionGranted() {
        Log.v(TAG, "onPermissionGranted");
        askUserToEnableGps();
    }

    public void onPermissionDenied() {
        Log.v(TAG, "onPermissionDenied");
    }

    // Should be called by Activity.onRequestPermissionsResult
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v(TAG, "onRequestPermissionsResult");
        mPermissionRequester.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    // This section is responsible for asking the user to enable GPS
    @SuppressWarnings({"MissingPermission"})
    private void onGpsEnable() {
        Log.v(TAG, "onGpsEnable");

        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,null );
    }

    private void askUserToEnableGps() {
        Log.v(TAG, "askUserToEnableGps");
        mLocationRequest = new LocationRequest();

        mLocationRequest
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(mActivity);

        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(mActivity, this);
        task.addOnFailureListener(mActivity, this);
    }

    @Override
    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
        Log.v(TAG, "onSuccess");
        onGpsEnable();
    }

    @Override
    public void onFailure(@NonNull Exception e) {
        Log.v(TAG, "onFailure");
        int statusCode = ((ApiException) e).getStatusCode();
        switch (statusCode) {
            case CommonStatusCodes.RESOLUTION_REQUIRED:
                try {
                    Log.i(TAG, "RESOLUTION_REQUIRED");
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(mActivity,
                            mRequestSettingsCode);
                } catch (IntentSender.SendIntentException sendEx) {
                    Log.i(TAG, "SendIntentException");
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                Log.i(TAG, "SETTINGS_CHANGE_UNAVAILABLE");
                break;
        }
    }

    // Should be called by mActivity.onActivityResult
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult");
        if(requestCode != mRequestSettingsCode) {
            return;
        }

        if(resultCode == Activity.RESULT_OK) {
            onGpsEnable();
        } else {
            Log.i(TAG, "FAIL: resultCode == Activity.RESULT_OK");
        }
    }
}