package com.example.android.insaniyatadmin;

import com.google.firebase.firestore.GeoPoint;

public class DastarkhwanRequest
{
    private String username, servings, phoneNumber,dastarkhwanLocation,type;
    private GeoPoint pickupLocation;
    private boolean approved,collected;

    DastarkhwanRequest()
    {
        approved = false;
    }

    DastarkhwanRequest(String name,String s, String phoneNumber,String d,String t,GeoPoint location,boolean b)
    {
        username = name;
        servings = s;
        this.phoneNumber = phoneNumber;
        approved = b;
        pickupLocation = location;
        collected = false;
        dastarkhwanLocation=d;
        type = t;
    }

    public String getType()
    {
        return type;
    }

    public String getDastarkhwanLocation() {
        return dastarkhwanLocation;
    }

    public void setDastarkhwanLocation(String dastarkhwanLocation) {
        this.dastarkhwanLocation = dastarkhwanLocation;
    }

    public String getUsername() {
        return username;
    }

    public String getServings() {
        return servings;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public GeoPoint getPickupLocation() {
        return pickupLocation;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public boolean isApproved() {
        return approved;
    }
}
