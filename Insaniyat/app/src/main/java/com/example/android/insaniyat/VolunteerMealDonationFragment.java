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


public class VolunteerMealDonationFragment extends Fragment 
{
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    
    private double latitude,longitude;
    private FusedLocationProviderClient client;
    private EditText quantity;
    private ProgressBar progressbar;
    private Button submit;
    
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VolunteerMealDonationFragment() 
    {
        
    }

    public static VolunteerMealDonationFragment newInstance(String param1, String param2)
    {
        VolunteerMealDonationFragment fragment = new VolunteerMealDonationFragment();
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
    {   View view =inflater.inflate(R.layout.fragment_volunteer_meal_donation, container, false);
        quantity = view.findViewById(R.id.quantity);
        submit = view.findViewById(R.id.submitWelfare);
        progressbar = view.findViewById(R.id.progressbar);
        progressbar.setVisibility(View.GONE);
        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(getActivity());


        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                progressbar.setVisibility(View.VISIBLE);
                sendRequest();
            }
        });
        return view;
    }
    private void sendRequest()
    {
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
                                final DocumentReference pendingMealRequestCRef = db.collection("PendingVolunteerRequests").document(""+documentSnapshot.getString("phoneNumber"));
                                pendingMealRequestCRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                                {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot)
                                    {
                                        if(documentSnapshot.exists())
                                        {
                                            Toast.makeText(getActivity(),"Wait for Previous request's completion!", Toast.LENGTH_SHORT).show();
                                            quantity.setText("");
                                            progressbar.setVisibility(View.GONE);
                                        }
                                        else
                                        {
                                            if(quantity.getText().toString().isEmpty())
                                            {
                                                Toast.makeText(getActivity(),"Enter number of servings", Toast.LENGTH_SHORT).show();
                                                quantity.requestFocus();
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
                                                            String serving = quantity.getText().toString();
                                                            PendingMealsRequest pendingMealsRequest = new PendingMealsRequest(name,serving,phn,"meal",false,pickupLocation);
                                                            pendingMealRequestCRef.set(pendingMealsRequest);
                                                            Toast.makeText(getActivity(),"Wait for Volunteer's approval", Toast.LENGTH_SHORT).show();
                                                            quantity.setText("");
                                                            startActivity(new Intent(getActivity(),HomeActivity.class));
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

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, 1);
    }
    
}
