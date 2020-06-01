package com.example.android.insaniyat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomBloodGroupSpinnerAdapter extends ArrayAdapter<String>
{

    public CustomBloodGroupSpinnerAdapter(@NonNull Context context, ArrayList<String> customList)
    {
        super(context, 0, customList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        return customView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        return customView(position, convertView, parent);
    }

    public View customView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_layout, parent, false);
        }
        String items = getItem(position);

        TextView spinnerName = convertView.findViewById(R.id.tvCustomSpinner);
        if (items != null)
        {
            spinnerName.setText(items);
        }
        return convertView;
    }
}