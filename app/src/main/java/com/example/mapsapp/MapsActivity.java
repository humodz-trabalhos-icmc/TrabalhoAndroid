package com.example.mapsapp;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        LocationHelper.UpdateListener,
        GoogleMap.OnInfoWindowClickListener,
        View.OnClickListener {
    private static final String TAG = "MapsActivity";

    private static final int REQUEST_FINE_LOCATION = 1001;
    private static final int REQUEST_CHANGE_SETTINGS = 1002;
    private static final int REQUEST_SEND_PICTURE = 2001;

    boolean mMapLocationNotInitialized = true;
    LocationHelper mLocationHelper;
    MapFragment mMapFragment;
    GoogleMap mMap;
    DatabaseReference mDatabaseRef;

    GoogleSignInAccount mAccount;
    double mLatitude = 0;
    double mLongitude = 0;

    FloatingActionButton mFab;

    MarkerManager mMarkerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(this);

        mAccount = getIntent().getParcelableExtra("ACCOUNT");

        mLocationHelper = new LocationHelper(
                this, REQUEST_FINE_LOCATION, REQUEST_CHANGE_SETTINGS);

        mLocationHelper.setLocationUpdateListener(this);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.main_map);
        mMapFragment.getMapAsync(this);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("geo_pictures");
        Log.v(TAG, mAccount.getDisplayName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationHelper.startUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationHelper.stopUpdates();
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onMapReady(GoogleMap map) {
        Log.v(TAG, "onMapReady");
        mMap = map;

        mMarkerManager = new MarkerManager(this, mMap, mDatabaseRef, mAccount);

        mMap.setOnInfoWindowClickListener(this);
        mMap.setInfoWindowAdapter(mMarkerManager);
    }


    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onLocationResult(LocationResult locationResult) {
        if(mMap == null) {
            return;
        }

        Location lastLocation = locationResult.getLastLocation();

        double lat = lastLocation.getLatitude();
        double lng = lastLocation.getLongitude();

        mLatitude = lat;
        mLongitude = lng;

        LatLng latlng = new LatLng(lat, lng);

        if(mMapLocationNotInitialized) {
            mMapLocationNotInitialized = false;
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
        }
    }

    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLocationHelper.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_SEND_PICTURE && resultCode == RESULT_OK) {
            Log.v(TAG, "REQUEST_SEND_PICTURE");
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(this, CommentsActivity.class);

        DataSnapshot snapshot = (DataSnapshot) marker.getTag();
        String key = snapshot.getKey();
        GeoPicture data = snapshot.getValue(GeoPicture.class);

        intent.putExtra("KEY", key);
        intent.putExtra("DATA", data);
        intent.putExtra("USER", mAccount.getDisplayName());
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if(viewId == R.id.fab) {
            Intent intent = new Intent(this, PictureActivity.class);
            intent.putExtra("ACCOUNT", mAccount);
            intent.putExtra("LATITUDE", mLatitude);
            intent.putExtra("LONGITUDE", mLongitude);
            startActivityForResult(intent, REQUEST_SEND_PICTURE);
        }
    }
}
