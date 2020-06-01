package com.example.android.insaniyatvolunteer;

import com.google.firebase.firestore.GeoPoint;

public class PendingRequest
{
    String name,quantity,phonenumber,type;
    GeoPoint pickupLocation;

    PendingRequest()
    {

    }

    PendingRequest(String n, String s, String phn, String t, GeoPoint loc)
    {
        name = n;
        quantity = s;
        phonenumber = phn;
        pickupLocation = loc;
        type = t;
    }

    public String getType()
    {
        return type;
    }

    public String getName()
    {
        return name;
    }

    public String getQuantity()
    {
        return quantity;
    }

    public String getPhonenumber()
    {
        return phonenumber;
    }

    public GeoPoint getPickupLocation() {
        return pickupLocation;
    }
}
