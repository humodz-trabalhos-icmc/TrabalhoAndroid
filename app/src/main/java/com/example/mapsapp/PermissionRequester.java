package com.example.mapsapp;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;


public class PermissionRequester {
    private static final String TAG = "PermissionRequester";

    public interface ResultListener {
        void onPermissionGranted();
        void onPermissionDenied();
    }

    private Activity mActivity;
    private ResultListener mResultListener;

    private String mPermissionName;
    private String[] mPermissionList;
    private int mRequestCode;

    public PermissionRequester(
            Activity activity,
            String permissionName, int requestCode) {
        mActivity = activity;
        mResultListener = null;

        mPermissionName = permissionName;
        mPermissionList = new String[]{permissionName};

        mRequestCode = requestCode;
    }

    public void setResultListener(ResultListener callback) {
        mResultListener = callback;
    }

    private void onPermissionGranted() {
        Log.v(TAG, "onPermissionGranted");
        mResultListener.onPermissionGranted();
    }

    public void askUserForPermission() {
        Log.v(TAG, "askUserForPermission");

        int permission_status = ContextCompat.checkSelfPermission(mActivity, mPermissionName);

        if(permission_status == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        Log.v(TAG, "requestPermission");
        ActivityCompat.requestPermissions(mActivity, mPermissionList, mRequestCode);
    }


    // Should be called by Activity.onRequestPermissionsResult
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.v(TAG, "onRequestPermissionsResult");

        boolean correctRequest = requestCode == mRequestCode;
        boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        if(correctRequest) {
            if(granted) {
                onPermissionGranted();
            } else {
                Log.i(TAG, "FAIL: granted");
                mResultListener.onPermissionDenied();
            }
        }
    }
}