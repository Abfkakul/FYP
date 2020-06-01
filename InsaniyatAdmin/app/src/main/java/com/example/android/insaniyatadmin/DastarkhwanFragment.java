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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DastarkhwanFragment#newInstance} factory method to
 * create an instance of getActivity() fragment.
 */
public class DastarkhwanFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DastarkhwanFragment() {
        // Required empty public constructor
    }

    /**
     * Use getActivity() factory method to create a new instance of
     * getActivity() fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DastarkhwanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DastarkhwanFragment newInstance(String param1, String param2) {
        DastarkhwanFragment fragment = new DastarkhwanFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        View view =inflater.inflate(R.layout.fragment_dastarkhwan, container, false);
        ImageView refresh;
//        refresh = view.findViewById(R.id.refresh);

        final ArrayList<DastarkhwanRequest> list = new ArrayList<DastarkhwanRequest>();
        final DastarkhwanRequestAdapter listAdapter = new DastarkhwanRequestAdapter(getActivity(), list, R.color.darkskyblue);
        final ListView listView = view.findViewById(R.id.mealsList);
        listView.setAdapter(listAdapter);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference DastarkhwanDonationsRef = db.collection("DastarkhwanDonation");

        final String[] currentUserName = new String[1];
        final DastarkhwanRequest[] newMeal = {new DastarkhwanRequest()};

        DastarkhwanDonationsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    String username = documentSnapshot.getString("username");
                    String servings = documentSnapshot.getString("servings");
                    String phn = documentSnapshot.getString("phoneNumber");
                    String type = documentSnapshot.getString("type");
                    GeoPoint loc = documentSnapshot.getGeoPoint("pickupLocation");
                    String dastarkhwanLoc = documentSnapshot.getString("dastarkhwanLocation");
                    DastarkhwanRequest newApproval = new DastarkhwanRequest(username, servings, phn,dastarkhwanLoc,type, loc, true);
                    currentUserName[0] = username;
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final DastarkhwanRequest newApproval = list.get(position);
                btn_showDialog(newApproval);
            }
        });

//        refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                refresh();
//            }
//        });
        // Inflate the layout for getActivity() fragment
        return view;
    }

    public void btn_showDialog(final DastarkhwanRequest newApproval) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.action_custom_dialog, null);

        Button callButton, locationButton, completedButton, cancelButton;
        callButton = mView.findViewById(R.id.call);
        locationButton = mView.findViewById(R.id.location);
        completedButton = mView.findViewById(R.id.accepted);
        cancelButton = mView.findViewById(R.id.cancel);

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

                GeoPoint pickupLocation = newApproval.getPickupLocation();
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
                        .setTitle("Meal Request Completion")
                        .setMessage("Have you completed getActivity() request?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                final CollectionReference DastarkhwanDonationsRef=FirebaseFirestore.getInstance().collection("DastarkhwanDonation");
                                DastarkhwanDonationsRef.document(newApproval.getPhoneNumber()).delete();
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
