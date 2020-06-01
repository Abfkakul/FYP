package com.example.android.insaniyat;

public class MorgueRequest
{
    String name,bodyName,phoneNumber,date,type;

    MorgueRequest()
    {

    }

    MorgueRequest(String n,String bName, String phn, String d)
    {
        name = n;
        bodyName = bName;
        phoneNumber = phn;
        date = d;
        type = "morgue";
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }

    public String getBodyName()
    {
        return bodyName;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public String getDate()
    {
        return date;
    }
}
