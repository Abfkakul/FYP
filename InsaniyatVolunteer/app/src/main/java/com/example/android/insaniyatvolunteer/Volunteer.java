package com.example.android.insaniyatvolunteer;

public class Volunteer
{
    public String email,password,name,phoneNumber,bloodGroup,role;

    public Volunteer()
    {

    }

    public Volunteer(String name, String phone, String email, String password,String bloodGrp)
    {
        this.name=name;
        this.phoneNumber=phone;
        this.email = email;
        this.password=password;
        this.bloodGroup = bloodGrp;
        this.role="volunteer";
    }

    public String getEmail()
    {
        return email;
    }

    public String getPassword()
    {
        return password;
    }

    public String getName()
    {
        return name;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public String getRole()
    {
        return role;
    }
}