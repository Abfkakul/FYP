package com.example.android.insaniyatadmin;

import com.google.firebase.firestore.GeoPoint;

public class PendingRequest
{
    String name,servings, phoneNumber,itemName,type;
    GeoPoint location;
    boolean approved;

    PendingRequest()
    {

    }

    PendingRequest(String n, String s, String d, String phn, String t, boolean b, GeoPoint g)
    {
        name = n;
        servings = s;
        phoneNumber = phn;
        location = g;
        itemName=d;
        approved = b;
        type = t;
    }

    public String getType() {
        return type;
    }

    public String getItemName()
    {
        return itemName;
    }

    public String getName()
    {
        return name;
    }

    public String getServings()
    {
        return servings;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public GeoPoint getLocation() {
        return location;
    }
}
