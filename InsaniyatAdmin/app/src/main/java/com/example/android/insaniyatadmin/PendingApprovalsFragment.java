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

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PendingApprovalsFragment extends Fragment
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public PendingApprovalsFragment()
    {
        // Required empty public constructor
    }

    public static PendingApprovalsFragment newInstance(String param1, String param2)
    {
        PendingApprovalsFragment fragment = new PendingApprovalsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_pending_approvals, container, false);
        checkForSmsPermission();
        ImageView refresh;
        
//        refresh = view.findViewById(R.id.refresh);

        final ArrayList<PendingRequest> list = new ArrayList<PendingRequest>();
        final PendingRequestAdapter listAdapter = new PendingRequestAdapter(getActivity(),list,R.color.darkred);
        final ListView listView = view.findViewById(R.id.approvalsList);
        listView.setAdapter(listAdapter);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference pendingRequestsRef=db.collection("PendingWelfareRequests");
        final CollectionReference donationRef = db.collection("WelfareDonations");
        final String[] currentUserName = new String[1];
        final PendingRequest[] newMeal = {new PendingRequest()};

        pendingRequestsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots)
                {
                    String username = documentSnapshot.getString("name");
                    String phn = documentSnapshot.getString("phoneNumber");
                    String type = documentSnapshot.getString("type");
                    GeoPoint loc = documentSnapshot.getGeoPoint("location");
                    PendingRequest newApproval = new PendingRequest();

                    if(type.equals("meal"))
                    {
                        String quantity = documentSnapshot.getString("servings");
                        String dastarkhwanLocation = documentSnapshot.getString("dastarkhwan");
                        newApproval = new PendingRequest(username,quantity,dastarkhwanLocation,phn,type,false,loc);
                    }
                    else
                    {
                        String quantity = documentSnapshot.getString("quantity");
                        newApproval = new PendingRequest(username,quantity,"Welfare",phn,type,false,loc);
                    }
                    currentUserName[0]=username;
                    list.add(newApproval);
                    listView.setAdapter(listAdapter);
                    newMeal[0] = newApproval;
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
                final PendingRequest newApproval = list.get(position);

                btn_showDialog(newApproval,newApproval.getType(),currentUserName[0]);
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

    public void btn_showDialog(final PendingRequest newApproval, final String type, final String currentUserName)
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.approval_custom_dialog, null);

        Button callButton,locationButton,cancelButton,acceptButton;
        callButton = mView.findViewById(R.id.call);
        acceptButton = mView.findViewById(R.id.accepted);
        locationButton = mView.findViewById(R.id.location);
        cancelButton = mView.findViewById(R.id.cancel);

        alert.setView(mView);

        final AlertDialog alertDialog = alert.create();
        alertDialog.setCanceledOnTouchOutside(true);

        callButton.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
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

        acceptButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Request")
                        .setMessage("Do you want to accept getActivity() request?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                refresh();

                                try
                                {
                                    SmsManager smgr = SmsManager.getDefault();
                                    smgr.sendTextMessage(newApproval.getPhoneNumber(),null,"Your "+type +" donation request has been accepted.\nContact on getActivity() number for further information.",null,null);
                                    Toast.makeText(getActivity(), "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(getActivity(), "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                                }
                                if(newApproval.getType().equals("meal"))
                                {
                                    DastarkhwanRequest newRequest = new DastarkhwanRequest(newApproval.getName(),newApproval.getServings(),newApproval.phoneNumber,newApproval.getItemName(),newApproval.getType(),newApproval.getLocation(),true);
                                    CollectionReference DastarkhwanRef = FirebaseFirestore.getInstance().collection("DastarkhwanDonation");
                                    DastarkhwanRef.document(""+newApproval.getPhoneNumber()).set(newRequest);
                                }
                                else
                                {
                                    DonationRequest newRequest = new DonationRequest(newApproval.getName(),newApproval.getServings(),newApproval.phoneNumber,newApproval.getType(),true,newApproval.getLocation());
                                    CollectionReference WelfareRef = FirebaseFirestore.getInstance().collection("WelfareDonations");
                                    WelfareRef.document(""+newApproval.getPhoneNumber()).set(newRequest);
                                }
                                final CollectionReference pendingRequestsRef = FirebaseFirestore.getInstance().collection("PendingWelfareRequests");
                                pendingRequestsRef.document(""+newApproval.getPhoneNumber()).delete();
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

    private void checkForSmsPermission()
    {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.SEND_SMS},
                    1);
        }
    }

}
