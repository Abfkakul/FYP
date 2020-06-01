package com.example.android.insaniyat;

public class CustomSpinnerItems
{
    private String spinnerText;
    private int spinnerImage;

    public CustomSpinnerItems(String spinnerText, int spinnerImage)
    {
        this.spinnerText = spinnerText;
        this.spinnerImage = spinnerImage;
    }

    public String getSpinnerText()
    {
        return spinnerText;
    }

    public int getSpinnerImage()
    {
        return spinnerImage;
    }
}