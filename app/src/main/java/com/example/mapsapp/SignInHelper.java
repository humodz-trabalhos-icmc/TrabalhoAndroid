package com.example.mapsapp;


import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SignInHelper implements View.OnClickListener {
    private static final String TAG = "SignInHelper";

    public interface ResultListener {
        void onSignIn(GoogleSignInAccount account);
    }

    private Activity mActivity;
    private int mRequestCode;
    private ResultListener mResultListener;
    private GoogleSignInClient mGoogleSignInClient;


    public SignInHelper(Activity activity, int requestCode) {
        mActivity = activity;
        mRequestCode = requestCode;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(mActivity, gso);
    }


    public void setResultListener(ResultListener listener) {
        mResultListener = listener;
    }


    public void onClick(View v) {
        Log.v(TAG, "onClick");
        signIn();
    }


    public void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mActivity.startActivityForResult(signInIntent, mRequestCode);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult");
        if (requestCode == mRequestCode) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        GoogleSignInAccount account = null;
        try {
            account = completedTask.getResult(ApiException.class);
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }

        if(mResultListener != null) {
            mResultListener.onSignIn(account);
        }
    }
}
