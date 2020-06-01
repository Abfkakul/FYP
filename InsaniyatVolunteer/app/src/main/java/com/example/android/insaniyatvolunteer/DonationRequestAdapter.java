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

public class DonationRequestAdapter extends ArrayAdapter<DonationRequest>
{
    int Color;
    public DonationRequestAdapter(Activity context, ArrayList<DonationRequest> DonationRequests, int color)
    {
        super(context, 0, DonationRequests);
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
                    R.layout.donation_list_item, parent, false);
            Log.d("yahan","hoon yahan");
        }

        // Get the object located at this position in the list
        DonationRequest currentRequest= getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView userTextView = listItemView.findViewById(R.id.userName);
        userTextView.setText("Name : "+currentRequest.getName());

        TextView itemTypeTextView = listItemView.findViewById(R.id.itemType);
        itemTypeTextView.setText("Item Type : "+currentRequest.getType());

        TextView servingsTextView = listItemView.findViewById(R.id.quantity);
        servingsTextView.setText("Quantity : "+currentRequest.getQuantity());

        TextView numberTextView = listItemView.findViewById(R.id.phoneNumber);
        numberTextView.setText("Phone Number : " + currentRequest.getPhoneNumber());

        LinearLayout linearLayout = listItemView.findViewById(R.id.donationListItem);
        int bgColor = ContextCompat.getColor(getContext(),Color);
        linearLayout.setBackgroundColor(bgColor);

        return listItemView;
    }
}