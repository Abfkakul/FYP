package com.example.android.insaniyatvolunteer;

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

public class BloodRequestAdapter extends ArrayAdapter<BloodRequest>
{
    int Color;
    public BloodRequestAdapter(Activity context, ArrayList<BloodRequest> BloodRequests, int color)
    {
        super(context, 0, BloodRequests);
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
                    R.layout.blood_list_item, parent, false);
            Log.d("yahan","hoon yahan");
        }

        // Get the object located at this position in the list
        BloodRequest currentRequest= getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView userTextView = listItemView.findViewById(R.id.userName);
        userTextView.setText("Name : "+currentRequest.getName());

        TextView servingsTextView = listItemView.findViewById(R.id.bloodGroup);
        servingsTextView.setText("Blood Group : "+currentRequest.getBloodGroup());

        TextView numberTextView = listItemView.findViewById(R.id.phoneNumber);
        numberTextView.setText("Phone Number : " + currentRequest.getPhonenumber());

        LinearLayout linearLayout = listItemView.findViewById(R.id.bloodListItem);
        int bgColor = ContextCompat.getColor(getContext(),Color);
        linearLayout.setBackgroundColor(bgColor);

        return listItemView;
    }
}