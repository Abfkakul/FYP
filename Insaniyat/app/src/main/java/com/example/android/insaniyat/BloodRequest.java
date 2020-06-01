package com.example.android.insaniyat;

import com.google.firebase.firestore.GeoPoint;

public class BloodRequest
{
    String name,bloodGroup,phonenumber,type;
    GeoPoint pickupLocation;
    boolean isComplete;
    BloodRequest()
    {

    }

    BloodRequest(String n, String bloodgrp, String phn, String t, GeoPoint loc)
    {
        name = n;
        bloodGroup = bloodgrp;
        phonenumber = phn;
        pickupLocation = loc;
        type = t;
        isComplete =false;
    }

    public String getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public String getBloodGroup()
    {
        return bloodGroup;
    }

    public String getPhonenumber()
    {
        return phonenumber;
    }

    public GeoPoint getPickupLocation() {
        return pickupLocation;
    }
}
