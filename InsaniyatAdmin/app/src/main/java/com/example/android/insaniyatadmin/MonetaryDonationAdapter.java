package com.example.android.insaniyatadmin;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MonetaryDonationAdapter extends ArrayAdapter<MonetaryDonation>
{
    int Color;
    public MonetaryDonationAdapter(Activity context, ArrayList<MonetaryDonation> approvals, int color)
    {
        super(context, 0, approvals);
        Color=color;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null)
        {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.money_list_item, parent, false);
            Log.d("yahan","hoon yahan");
        }

        // Get the object located at this position in the list
        MonetaryDonation currentRequest= getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView userTextView = listItemView.findViewById(R.id.username);
        userTextView.setText("Name : "+currentRequest.getName());

        TextView bodyNameTextView = listItemView.findViewById(R.id.amount);
        bodyNameTextView.setText("Amount : " + currentRequest.getAmount());

        TextView numberTextView = listItemView.findViewById(R.id.phoneNumber);
        numberTextView.setText("Contact Number : "+currentRequest.getPhoneNumber());

        LinearLayout linearLayout = listItemView.findViewById(R.id.moneyListItem);
        int bgColor = ContextCompat.getColor(getContext(),Color);
        linearLayout.setBackgroundColor(bgColor);

        return listItemView;
    }
}