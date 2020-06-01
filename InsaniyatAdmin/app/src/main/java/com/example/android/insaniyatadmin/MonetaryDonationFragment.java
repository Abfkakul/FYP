package com.example.android.insaniyatadmin;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MonetaryDonationFragment extends Fragment
{
    private String mParam1;
    private String mParam2;

    public MonetaryDonationFragment()
    {
        // Required empty public constructor
    }

    public static MonetaryDonationFragment newInstance(String param1, String param2)
    {
        MonetaryDonationFragment fragment = new MonetaryDonationFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view =inflater.inflate(R.layout.fragment_monetary_donation, container, false);
//        ImageView refresh = view.findViewById(R.id.refresh);

        final ArrayList<MonetaryDonation> list = new ArrayList<MonetaryDonation>();
        final MonetaryDonationAdapter listAdapter = new MonetaryDonationAdapter(getActivity(),list,R.color.darkskyblue);
        final ListView listView = view.findViewById(R.id.moneyList);
        listView.setAdapter(listAdapter);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference pendingRequestsRef=db.collection("MoneyDonations");
        final String[] currentUserName = new String[1];
        final MonetaryDonation[] newMonetaryRequest = {new MonetaryDonation()};

        pendingRequestsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots)
                {
                    String username = documentSnapshot.getString("name");
                    String phn = documentSnapshot.getString("phoneNumber");
                    String amount = documentSnapshot.getString("amount");
                    GeoPoint loc = documentSnapshot.getGeoPoint("location");
                    MonetaryDonation newApproval = new MonetaryDonation(username,phn,amount,loc);
                    currentUserName[0]=username;
                    list.add(newApproval);
                    listView.setAdapter(listAdapter);
                    newMonetaryRequest[0] = newApproval;
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                final MonetaryDonation newApproval = list.get(position);
                btn_showDialog(newApproval);
            }
        });

//        refresh.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                refresh();
//            }
//        });

        return view;
    }

    public void btn_showDialog(final MonetaryDonation newApproval)
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.action_custom_dialog, null);

        Button callButton, completedButton, cancelButton,rejectButton,locationButton;
        callButton = mView.findViewById(R.id.call);
        completedButton = mView.findViewById(R.id.accepted);
        cancelButton = mView.findViewById(R.id.cancel);
        locationButton = mView.findViewById(R.id.location);

        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        callButton.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + newApproval.getPhoneNumber()));
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},1);
                }
                else
                {
                    startActivity(intent);
                }
            }
        });

        locationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                GeoPoint pickupLocation = newApproval.getLocation();
                double latitude = pickupLocation.getLatitude();
                double longitude = pickupLocation.getLongitude();
                Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                alertDialog.dismiss();
            }
        });

        completedButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Money Request Completion")
                        .setMessage("Have you completed this request?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                final CollectionReference pendingMorgueRequestsRef = FirebaseFirestore.getInstance().collection("MoneyDonations");
                                pendingMorgueRequestsRef.document(""+newApproval.getPhoneNumber()).delete();
                                refresh();
                            }
                        })

                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                alertDialog.dismiss();
                                refresh();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }


    private void refresh()
    {
        getActivity().finish();
        startActivity(getActivity().getIntent());
    }

}
