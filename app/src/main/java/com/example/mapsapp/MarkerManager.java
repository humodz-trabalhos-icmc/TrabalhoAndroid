package com.example.mapsapp;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class MarkerManager
        implements ChildEventListener,
        GoogleMap.InfoWindowAdapter {
    private static final String TAG = "MarkerManager";

    private Activity mActivity;
    private GoogleMap mMap;
    private DatabaseReference mDatabaseRef;
    private GoogleSignInAccount mAccount;
    private HashMap<String, Marker> mMarkers;


    public MarkerManager(
            Activity activity, GoogleMap map,
            DatabaseReference databaseRef, GoogleSignInAccount account) {
        mActivity = activity;
        mMap = map;
        mDatabaseRef = databaseRef;
        mAccount = account;
        mMarkers = new HashMap<String, Marker>();

        mDatabaseRef.orderByKey().addChildEventListener(this);
    }



    private void addMarker(String key, DataSnapshot snapshot) {
        GeoPicture data = snapshot.getValue(GeoPicture.class);
        LatLng latLng = new LatLng(data.latitude, data.longitude);

        MarkerOptions markerOptions =  new MarkerOptions().position(latLng);

        markerOptions.icon(getMarkerIcon(data));

        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(snapshot);
        mMarkers.put(key, marker);
    }

    private void updateMarker(String key, DataSnapshot snapshot) {
        GeoPicture data = snapshot.getValue(GeoPicture.class);
        Marker marker = mMarkers.get(key);
        LatLng latLng = new LatLng(data.latitude, data.longitude);

        marker.setIcon(getMarkerIcon(data));
        marker.setPosition(latLng);
        marker.setTag(snapshot);
    }

    private void removeMarker(String key) {
        Marker marker = mMarkers.get(key);

        marker.remove();
        mMarkers.remove(key);
    }

    private BitmapDescriptor getMarkerIcon(GeoPicture data) {
        if(data.email.equals(mAccount.getEmail())) {
            return BitmapDescriptorFactory.defaultMarker();
        } else {
            return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
        }
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) mActivity.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(250);
    }



    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.w(TAG, "onCancelled");
    }


    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.w(TAG, "onChildAdded");
        String key = dataSnapshot.getKey();
        addMarker(key, dataSnapshot);
        vibrate();
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Log.w(TAG, "onChildChanged");
        String key = dataSnapshot.getKey();
        updateMarker(key, dataSnapshot);
        vibrate();
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        removeMarker(key);
        vibrate();
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        Log.w(TAG, "onChildMoved");
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        // Getting view from the layout file info_window_layout
        View v = mActivity.getLayoutInflater().inflate(R.layout.info_window, null);

        TextView userTv = v.findViewById(R.id.info_user);
        ImageView pictureIv =  v.findViewById(R.id.info_picture);

        DataSnapshot snapshot = (DataSnapshot) marker.getTag();
        GeoPicture data = snapshot.getValue(GeoPicture.class);

        userTv.setText(data.user);

        byte[] decodedString = Base64.decode(data.picture, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        pictureIv.setImageBitmap(bitmap);

        return v;
    }
}
