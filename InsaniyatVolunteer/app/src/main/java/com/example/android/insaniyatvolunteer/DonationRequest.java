package com.example.android.insaniyatvolunteer;

import com.google.firebase.firestore.GeoPoint;

public class DonationRequest
{
    private String name,quantity, phoneNumber,type,vEmail;
    private GeoPoint pickupLocation;
    private boolean approved,collected;

    DonationRequest()
    {

    }

    DonationRequest(String n, String s, String phn,String t,boolean b,GeoPoint g)
    {
        name = n;
        quantity = s;
        phoneNumber = phn;
        pickupLocation = g;
        approved = b;
        type = t;
        collected = false;
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

    public GeoPoint getPickupLocation()
    {
        return pickupLocation;
    }

    public String getType()
    {
        return type;
    }

    public String getvEmail() {
        return vEmail;
    }

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public void setvEmail(String vEmail) {
        this.vEmail = vEmail;
    }

}
