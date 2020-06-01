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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class WelfareMealDonationFragment extends Fragment implements AdapterView.OnItemSelectedListener
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Button submit;
    private Spinner dastarkhwanSpinner;
    private EditText servings;
    private ProgressBar progressbar;
    private String selected="";
    private double latitude,longitude;
    private FusedLocationProviderClient client;
    
    private String mParam1;
    private String mParam2;

    public WelfareMealDonationFragment() 
    {
        // Required empty public constructor
    }
    
    public static WelfareMealDonationFragment newInstance(String param1, String param2) 
    {
        WelfareMealDonationFragment fragment = new WelfareMealDonationFragment();
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
        View view = inflater.inflate(R.layout.fragment_welfare_meal_donation, container, false);

        submit = view.findViewById(R.id.dastarkhwanSubmit);
        dastarkhwanSpinner = view.findViewById(R.id.dastarkhwanSpinner);
        servings = view.findViewById(R.id.quantity);
        progressbar = view.findViewById(R.id.progressbar);
        progressbar.setVisibility(View.GONE);

        client = LocationServices.getFusedLocationProviderClient(getActivity());

//        final ArrayList<CustomSpinnerItems> customList = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("Dastarkhwans");

        final ArrayList<String> list = new ArrayList<>();
        list.add("Select Dastarkhwan Location");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
        {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots)
            {
                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots)
                {
                    list.add(documentSnapshot.getString("name"));
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {

            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dastarkhwanSpinner.setAdapter(adapter);
        CustomBloodGroupSpinnerAdapter customAdapter = new CustomBloodGroupSpinnerAdapter(getActivity(), list);
        if (dastarkhwanSpinner!= null)
        {
            dastarkhwanSpinner.setAdapter(customAdapter);
            dastarkhwanSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    selected=(String) parent.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });
        }

        submit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                progressbar.setVisibility(View.VISIBLE);
                sendRequest(selected);
            }
        });
                
        return view;
    }

    private void sendRequest(final String selected)
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
                                final DocumentReference pendingDastarkhwanRequestCRef = db.collection("PendingWelfareRequests").document(""+documentSnapshot.getString("phoneNumber"));
                                pendingDastarkhwanRequestCRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                                {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot)
                                    {
                                        if(documentSnapshot.exists())
                                        {
                                            Toast.makeText(getActivity(),"Wait for Previous request's completion!", Toast.LENGTH_SHORT).show();
                                            servings.setText("");
                                            progressbar.setVisibility(View.GONE);
                                        }
                                        else
                                        {
                                            if(servings.getText().toString().isEmpty())
                                            {
                                                Toast.makeText(getActivity(),"Enter number of servings", Toast.LENGTH_SHORT).show();
                                                servings.requestFocus();
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
                                                            String serving = servings.getText().toString();
                                                            DastarkhwanRequest DastarkhwanRequest = new DastarkhwanRequest(name,serving,selected,phn,"meal",false,pickupLocation);
                                                            pendingDastarkhwanRequestCRef.set(DastarkhwanRequest);
                                                            Toast.makeText(getActivity(),"Wait for Admin's approval", Toast.LENGTH_SHORT).show();
                                                            servings.setText("");
                                                            startActivity(new Intent(getActivity(),HomeActivity.class));
                                                            getActivity().finish();
                                                            progressbar.setVisibility(View.GONE);
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
                    Toast.makeText(getActivity(), "Turn on GPS Or give permissions" + location, Toast.LENGTH_SHORT).show();
                    progressbar.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(getActivity(), "Location not retrieved, ERROR!", Toast.LENGTH_SHORT).show();
                progressbar.setVisibility(View.GONE);
            }
        });

        progressbar.setVisibility(View.GONE);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        CustomSpinnerItems items = (CustomSpinnerItems) parent.getSelectedItem();
        selected = items.getSpinnerText();
        Toast.makeText(getActivity(), "Selected : "+selected, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
}
