package com.example.android.insaniyatadmin;

public class Admin
{
    String name,welfare,email,password;

    Admin()
    {

    }

    public Admin(String name, String welfare, String email, String password)
    {
        this.name=name;
        this.welfare=welfare;
        this.email=email;
        this.password=password;
    }

    public String getName()
    {
        return name;
    }

    public String getWelfare()
    {
        return welfare;
    }

    public String getEmail()
    {
        return email;
    }

    public String getPassword()
    {
        return password;
    }
}
