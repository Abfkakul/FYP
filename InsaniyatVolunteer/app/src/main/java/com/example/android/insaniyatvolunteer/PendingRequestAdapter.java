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

public class PendingRequestAdapter extends ArrayAdapter<PendingRequest>
{
    int Color;
    public PendingRequestAdapter(Activity context, ArrayList<PendingRequest> approvals, int color)
    {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
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
                    R.layout.approval_list_item, parent, false);
            Log.d("yahan","hoon yahan");
        }

        // Get the object located at this position in the list
        PendingRequest currentRequest= getItem(position);

        // Find the TextView in the list_item.xml layout with the ID version_name
        TextView userTextView = listItemView.findViewById(R.id.userName);
        userTextView.setText("Name : "+currentRequest.getName());

        TextView servingsTextView = listItemView.findViewById(R.id.quantity);

        TextView itemTypeTextView = listItemView.findViewById(R.id.itemType);
        if(currentRequest.getType().equals("meal"))
        {
            servingsTextView.setText("Servings : "+currentRequest.getQuantity());
        }
        else
        {
            servingsTextView.setText("Quantity : "+currentRequest.getQuantity());
        }
        itemTypeTextView.setText("Item : " + currentRequest.getType());

        TextView numberTextView = listItemView.findViewById(R.id.phoneNumber);
        numberTextView.setText("Phone Number : "+currentRequest.getPhonenumber());

        LinearLayout linearLayout = listItemView.findViewById(R.id.listItem);
        int bgColor = ContextCompat.getColor(getContext(),Color);
        linearLayout.setBackgroundColor(bgColor);

        return listItemView;
    }
}