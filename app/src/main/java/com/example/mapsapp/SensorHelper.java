package com.example.mapsapp;


import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;

public class SensorHelper {
    private static  final String TAG = "SensorHelper";

    private Activity mActivity;
    private SensorEventListener mSensorEventListener;
    private SensorManager mSensorManager;

    private int[] mSensorTypes = {
            Sensor.TYPE_ACCELEROMETER,
            Sensor.TYPE_GYROSCOPE,
    };

    private ArrayList<Sensor> mSensors = new ArrayList<Sensor>();

    public SensorHelper(Activity activity, SensorEventListener eventListener) {
        mActivity = activity;
        mSensorEventListener = eventListener;

        mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);

        for(int type : mSensorTypes) {
            Sensor sensor = mSensorManager.getDefaultSensor(type);
            if(sensor != null) {
                mSensors.add(sensor);
                mSensorManager.registerListener(
                        mSensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.v(TAG, "null sensor: " + type);
            }
        }
    }
}
