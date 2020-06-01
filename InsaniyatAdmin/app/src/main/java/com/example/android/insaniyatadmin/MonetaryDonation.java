package com.example.android.insaniyatadmin;

import com.google.firebase.firestore.GeoPoint;

public class MonetaryDonation
{
    String name,phoneNumber,amount;
    GeoPoint location;

    MonetaryDonation()
    {


    }
    MonetaryDonation(String n, String phn,String amount,GeoPoint g)
    {
        name = n;
        phoneNumber = phn;
        this.amount = amount;
        location = g;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAmount() {
        return amount;
    }

    public GeoPoint getLocation() {
        return location;
    }
}