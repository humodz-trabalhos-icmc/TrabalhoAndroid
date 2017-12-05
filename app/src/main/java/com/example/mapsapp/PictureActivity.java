package com.example.mapsapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;

public class PictureActivity extends AppCompatActivity
        implements View.OnClickListener {
    private static final String TAG = "PictureActivity";

    private static final int REQUEST_IMAGE_CAPTURE = 1001;

    GoogleSignInAccount mAccount;
    double mLatitude;
    double mLongitude;

    boolean mPictureTaken = false;
    String mPictureBase64;

    ImageView mIvPicture;

    Button mBtnPictureRetake;
    Button mBtnPictureOk;
    Button mBtnPictureCancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        mIvPicture = findViewById(R.id.iv_picture);

        Bundle extras = getIntent().getExtras();

        mAccount = extras.getParcelable("ACCOUNT");
        mLatitude = extras.getDouble("LATITUDE");
        mLongitude = extras.getDouble("LONGITUDE");

        mBtnPictureRetake = findViewById(R.id.btn_picture_retake);
        mBtnPictureOk = findViewById(R.id.btn_picture_ok);
        mBtnPictureCancel = findViewById(R.id.btn_picture_cancel);

        mBtnPictureRetake.setOnClickListener(this);
        mBtnPictureOk.setOnClickListener(this);
        mBtnPictureCancel.setOnClickListener(this);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            mIvPicture.setImageBitmap(imageBitmap);
            mPictureTaken = true;
            mPictureBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
    }

    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View view) {
        if(view == mBtnPictureRetake) {
            dispatchTakePictureIntent();
        } else if(view == mBtnPictureOk) {
            if(mPictureTaken) {
                uploadPicture();
            } else {
                Toast.makeText(
                        this, "Tire uma foto antes!", Toast.LENGTH_SHORT)
                        .show();
            }
        } else if(view == mBtnPictureCancel) {
            finish();
        }
    }

    private void uploadPicture() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("geo_pictures");

        GeoPicture obj = new GeoPicture(mAccount, mPictureBase64, mLatitude, mLongitude);
        ref.push().setValue(obj);

        Toast.makeText(this, "Upload concluido!", Toast.LENGTH_SHORT).show();

        finish();
    }
}



