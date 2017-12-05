package com.example.mapsapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.TextView;

public class SensorActivity extends AppCompatActivity
        implements SensorEventListener {
    private static  final String TAG = "SensorActivity";


    private SensorHelper mSensorHelper;

    private TextView mTvAccelerometer;
    private TextView mTvGyroscope;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        mSensorHelper = new SensorHelper(this, this);
        mTvAccelerometer = findViewById(R.id.tv_accelerometer);
        mTvGyroscope = findViewById(R.id.tv_gyroscope);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int type = sensorEvent.sensor.getType();
        float[] values = sensorEvent.values;

        TextView target = null;

        if(type == Sensor.TYPE_ACCELEROMETER) {
            target = mTvAccelerometer;
            checkAndVibrate(values);
        } else if(type == Sensor.TYPE_GYROSCOPE) {
            target = mTvGyroscope;
        }

        if(target == null) {
            return;
        }

        String[] strings = new String[values.length];

        for(int i = 0; i < values.length; i++) {
            strings[i] = String.format("%+f", values[i]);
        }

        if(strings.length != 0) {
            String text = TextUtils.join("    ", strings);
            target.setText(text);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void checkAndVibrate(float[] v) {
        float magnitude = v[0]*v[0] + v[1]*v[1] + v[2]*v[2];

        if(magnitude > 15*15) {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(250);
        }
    }
}
