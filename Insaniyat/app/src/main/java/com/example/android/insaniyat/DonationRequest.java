package com.example.android.insaniyat;

import com.google.firebase.firestore.GeoPoint;

public class DonationRequest
{
    String name,quantity, phoneNumber,type;
    GeoPoint location;
    boolean approved;

    DonationRequest()
    {

    }

    DonationRequest(String n, String s, String phn,String t,boolean b,GeoPoint g)
    {
        name = n;
        quantity = s;
        phoneNumber = phn;
        location = g;
        approved = b;
        type = t;
    }

    public String getName()
    {
        return name;
    }

    public String getQuantity()
    {
        return quantity;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getType() {
        return type;
    }
}
