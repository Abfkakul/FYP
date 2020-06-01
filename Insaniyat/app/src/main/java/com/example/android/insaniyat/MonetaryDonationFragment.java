package com.example.android.insaniyat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonetaryDonationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonetaryDonationFragment extends Fragment 
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private double latitude,longitude;
    private Button home,logout,submit;
    private EditText amount;
    private ProgressBar progressbar;
    private FusedLocationProviderClient client;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MonetaryDonationFragment() 
    {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MonetaryDonationFragment newInstance(String param1, String param2) 
    {
        MonetaryDonationFragment fragment = new MonetaryDonationFragment();
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
        
        
        if (getArguments() != null) 
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view =inflater.inflate(R.layout.fragment_monetary_donation, container, false);
        client = LocationServices.getFusedLocationProviderClient(getActivity());

        submit = view.findViewById(R.id.submitWelfare);
        amount = view.findViewById(R.id.amount);
        progressbar = view.findViewById(R.id.progressbar);
        progressbar.setVisibility(View.GONE);
        // Inflate the layout for this fragment

        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                progressbar.setVisibility(View.VISIBLE);
                sendRequest("MoneyDonations");
            }
        });
        return view;
    }
    private void sendRequest(final String path)
    {
        final String money = amount.getText().toString();

        progressbar.setVisibility(View.VISIBLE);
        if (ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }

        client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>()
        {
            @Override
            public void onSuccess(Location location)
            {
                if (location != null)
                {
                    progressbar.setVisibility(View.VISIBLE);
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    final GeoPoint pickupLocation  = new GeoPoint(latitude,longitude);
                    final String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();
                    final DocumentReference userRef = db.collection("Users").document(""+currentUserEmail);

                    userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                    {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot)
                        {
                            if(documentSnapshot.exists())
                            {
                                final DocumentReference pendingRequestCRef = db.collection(""+path).document(""+documentSnapshot.getString("phoneNumber"));
                                pendingRequestCRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                                {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot)
                                    {
                                        if(documentSnapshot.exists())
                                        {
                                            Toast.makeText(getActivity(),"Wait for Previous request's completion!", Toast.LENGTH_SHORT).show();
                                            amount.setText("");
                                            progressbar.setVisibility(View.GONE);
                                        }
                                        else
                                        {
                                            if(amount.getText().toString().isEmpty())
                                            {
                                                Toast.makeText(getActivity(),"Enter amount", Toast.LENGTH_SHORT).show();
                                                amount.requestFocus();
                                                progressbar.setVisibility(View.GONE);
                                            }
                                            else if(Integer.parseInt(money)<1000)
                                            {
                                                Toast.makeText(getActivity(),"Amount must be more than 1000pkr.", Toast.LENGTH_SHORT).show();
                                                amount.requestFocus();
                                                progressbar.setVisibility(View.GONE);
                                            }
                                            else
                                            {
                                                userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                                                {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot)
                                                    {
                                                        if(documentSnapshot.exists())
                                                        {
                                                            String name = documentSnapshot.getString("name");
                                                            String phn  = documentSnapshot.getString("phoneNumber");
                                                            MonetaryDonation pendingRequest = new MonetaryDonation(name,phn,money,pickupLocation);
                                                            pendingRequestCRef.set(pendingRequest);
                                                            Toast.makeText(getActivity(),"Wait for approval", Toast.LENGTH_SHORT).show();
                                                            amount.setText("");
                                                            startActivity(new Intent(getActivity(), HomeActivity.class));
                                                            getActivity().finish();
                                                        }
                                                    }
                                                }).addOnFailureListener(new OnFailureListener()
                                                {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e)
                                                    {
                                                        progressbar.setVisibility(View.GONE);
                                                    }
                                                });
                                            }

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener()
                                {
                                    @Override
                                    public void onFailure(@NonNull Exception e)
                                    {
                                        progressbar.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            progressbar.setVisibility(View.GONE);
                        }
                    });
                }
                else
                {
                    progressbar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Turn on GPS Or some other issue" + location, Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                progressbar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Location not retrieved, ERROR!", Toast.LENGTH_SHORT).show();
            }
        });

        progressbar.setVisibility(View.GONE);

    }

}
