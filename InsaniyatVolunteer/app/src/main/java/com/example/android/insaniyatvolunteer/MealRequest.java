package com.example.android.insaniyatvolunteer;

import com.google.firebase.firestore.GeoPoint;

public class MealRequest
{
    private String username, quantity, phonenumber,vEmail,type;
    private GeoPoint pickupLocation;
    private boolean approved,collected;

    MealRequest()
    {
        approved = false;
    }

    MealRequest(String name,String s, String phoneNumber,String t,GeoPoint location,boolean b)
    {
        username = name;
        quantity = s;
        this.phonenumber = phoneNumber;
        approved = b;
        pickupLocation = location;
        collected = false;
        type = t;
    }

    public String getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public GeoPoint getPickupLocation() {
        return pickupLocation;
    }

    public boolean isCollected() {
        return collected;
    }

    public String getvEmail() {
        return vEmail;
    }

    public void setvEmail(String vEmail) {
        this.vEmail = vEmail;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public boolean isApproved() {
        return approved;
    }
}
