package com.example.android.insaniyat;

import com.google.firebase.firestore.GeoPoint;

public class DastarkhwanRequest
{
    String name,servings, phoneNumber,dastarkhwan,type;
    GeoPoint location;
    boolean approved;

    DastarkhwanRequest()
    {

    }

    DastarkhwanRequest(String n, String s,String d, String phn,String t,boolean b,GeoPoint g)
    {
        name = n;
        servings = s;
        phoneNumber = phn;
        location = g;
        dastarkhwan=d;
        approved = b;
        type = t;
    }

    public String getDastarkhwan()
    {
        return dastarkhwan;
    }

    public String getType()
    {
        return type;
    }

    public void setDastarkhwan(String dastarkhwan) {
        this.dastarkhwan = dastarkhwan;
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
