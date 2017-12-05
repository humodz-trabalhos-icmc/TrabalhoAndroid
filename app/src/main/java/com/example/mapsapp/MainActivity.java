package com.example.mapsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;

public class MainActivity extends AppCompatActivity
        implements SignInHelper.ResultListener,
        View.OnClickListener {
    private static final String TAG = "MainActivity";

    private static final int REQUEST_SIGN_IN = 1001;

    private SignInHelper mSignInHelper;
    private SignInButton mBtnSignIn;
    private Button mBtnSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSignInHelper = new SignInHelper(this, REQUEST_SIGN_IN);
        mSignInHelper.setResultListener(this);

        mBtnSignIn = findViewById(R.id.btn_sign_in);
        mBtnSignIn.setOnClickListener(mSignInHelper);

        mBtnSensors = findViewById(R.id.btn_sensors);
        mBtnSensors.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mBtnSensors) {
            Intent intent = new Intent(this, SensorActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSignInHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSignIn(GoogleSignInAccount account) {
        Log.v(TAG, "onSignIn");

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("ACCOUNT", account);
        startActivity(intent);
    }
}
