package com.example.mapsapp;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.Serializable;

class GeoPicture implements Serializable {
    public String email = "";
    public String user = "";
    public String picture = "";
    public double latitude = 0;
    public double longitude = 0;

    public GeoPicture() {

    }

    public GeoPicture(GoogleSignInAccount account, String p, double lat, double lng) {
        email = account.getEmail();
        user = account.getDisplayName();
        picture = p;
        latitude = lat;
        longitude = lng;
    }
}