package com.example.android.insaniyat;
public class User
{
    public String email,password,name,phoneNumber,role;

    public User()
    {

    }

    public User(String name,String phone,String email,String password)
    {
        this.name=name;
        this.phoneNumber=phone;
        this.email = email;
        this.password=password;
        this.role="user";
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