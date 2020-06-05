package com.example.android.insaniyat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;

//For map integration
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

// classes to calculate a route
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import android.util.Log;




import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Use the LocationComponent to easily add a device location "puck" to a Mapbox map.
 */
public class OpenMap extends AppCompatActivity implements
        OnMapReadyCallback, PermissionsListener {

    //for sms
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    //For mapbox view
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;

    //Logout logic
    public Boolean indicator = true;

    //firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFireStore;


    //new
    //latitude and longitude are representing user location
    public double latitude;
    public double longitutde;
    private FusedLocationProviderClient client;


    //newcode
    public Object docID;
    public Object docName;
    public Object docLat;
    public Object docLong;
    public double diffBetweenTwoLoc;
    public double minimumDist=100.0;
    //newcode end

    //fetching phone numbers and names
    public String DriverPhoneNumber;
    public  String UserPhoneNumber;
    public  String UserName;
    public  String DriverName;

    //for cancelling ride
    //private String driverDocID;
    private String assignedDocID;

    //navigation variables start
    //latitude and longitude are representing ambulance driver location
    public double Dlatitude;
    public double Dlongitude;
    //new end

    // variables for calculating and drawing a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;

    //refreshing activity
    private ListenerRegistration noteListener;
    //navigation ends


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //hide title bar
        getSupportActionBar().hide();

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, getString(R.string.access_token));

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_open_map);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        //Authentication
        //final FirebaseFirestore db = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();


        //Firestore
        mFireStore =FirebaseFirestore.getInstance();


        //get phone number of user and driver

        //Toggle button starting the service of update location
        ToggleButton toggle = findViewById(R.id.toggle);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    //logic for signout
                    indicator = false;

                    //uses gps
                    FINDLOC();



                    //following querries/code is for assigning closest driver to user
                    //setting assigned rider status to "Unavailable"
                    //makeing a new collection by the name "AssignedRides"

                    //newcode
                    mFireStore.collection("Ambulance Drivers")
                            .whereEqualTo("status", "Available")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("1st Search querry :","I am Searching Available drivers");
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Double lat = new Double(document.getData().get("latitude").toString());
                                            Double lon = new Double(document.getData().get("longitude").toString());
                                            String Name = document.getData().get("name").toString();

                                            //latitude and longitude are representing user location
                                            Log.d("MyLatitude:",""+latitude);
                                            Log.d("MyLongitude:",""+longitutde);
                                            Log.d("minDiff:",""+minimumDist);
                                            diffBetweenTwoLoc = distance(latitude,longitutde,lat,lon);
                                            if(diffBetweenTwoLoc < minimumDist){
                                                minimumDist = diffBetweenTwoLoc;
                                                docID = document.getId();
                                                docLat = lat;
                                                docLong = lon;
                                                docName = Name;
                                                //Toast.makeText(MainActivity.this, ""+docID, Toast.LENGTH_SHORT).show();
                                            }
                                            Log.d("LatitudeResult",document.getId() + " => " + document.getData().get("latitude"));
                                            Log.d("LongitudeResult",document.getId() + " => " + document.getData().get("longitude"));
                                            Log.d("Diff", ""+diffBetweenTwoLoc);

                                        }


                                        //new if condition
                                        if(docID != null){

                                            //get phoneNumbers of user and driver, and other logics
                                            runMultipleQuerries();


                                            /*//new code2 start

                                            // Set "Status" to "Unavailable" of booked driver
                                            DocumentReference washingtonRef = mFireStore.collection("Ambulance Drivers")
                                                    .document(docID.toString());

                                            washingtonRef
                                                    .update("status", "Unavailable")
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            //Toast.makeText(OpenMap.this, "DocumentSnapshot successfully updated!", Toast.LENGTH_SHORT).show();
                                                            Log.d("status:::","Updated to unavailable");


                                                            //newcode
                                                            Log.d("in !null ID",".."+docID);

                                                            // maybe need to put it in else condition
                                                            Toast.makeText(OpenMap.this, "Your Ambulance driver ID is: "+docID, Toast.LENGTH_SHORT).show();


                                                            //making a new collection
                                                            //store data of booked driver and customer in new collection
                                                            Map<String, Object> Ride = new HashMap<>();
                                                            Ride.put("AmbulanceDriverID", docID);
                                                            Ride.put("U_Latitude",latitude );
                                                            Ride.put("U_Longitude",longitutde);
                                                            Ride.put("D_Latitude",docLat);
                                                            Ride.put("D_Longitude",docLong);
                                                            Ride.put("D_Name",docName);

                                                            mFireStore.collection("AssignedRides")
                                                                    .add(Ride)
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                            Log.d("Data added::","new colection of assigned drivers");
                                                                            //Toast.makeText(OpenMap.this, "DocumentSnapshot successfully written!", Toast.LENGTH_SHORT).show();

                                                                            //newcode4
                                                                            //StartNavigation();
                                                                            //newcode4 end

                                                                            //for listener
                                                                            //attachListener();

                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Log.d("Data added::","error writing document in new colection of assigned drivers");
                                                                            //Toast.makeText(OpenMap.this, "Error writing document "+e, Toast.LENGTH_SHORT).show();

                                                                        }
                                                                    });


                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                            //Toast.makeText(OpenMap.this, "Error updating document", Toast.LENGTH_SHORT).show();
                                                            Log.d("status:::","Error updating document in unavailable");

                                                        }
                                                    });

                                            //new code2 end*/

                                        }

                                        else{
                                            // When no driver is Online (not sure)
                                            Log.d("else:...... ","No Driver Available");
                                            Toast.makeText(OpenMap.this, "Sorry, No driver is available", Toast.LENGTH_SHORT).show();
                                            restartActivity();
                                            return;
                                        }

                                        //new if condition ends





                                    }
                                    //task successful ends here


                                    else {

                                        Log.d("QuerryError", "Error getting documents: ", task.getException());
                                    }



                                    //to have accurate minimum distance next time
                                    minimumDist=100.01;
                                    //newcode3 end


                                    //Message code starts here
                                   /*

                                    //For user
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(""+UserPhoneNumber, null, "Your ambulance has been booked. Driver Name is "+DriverName+".\n You can contact Ambulance driver on this number "+DriverPhoneNumber+".", null, null);

                                    //For Driver

                                    smsManager.sendTextMessage(""+DriverPhoneNumber, null, ""+UserName+" has booked the ambulance.\nYou can contact on this number "+UserPhoneNumber+".", null, null);
                                    Toast.makeText(OpenMap.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();

                                    */
                                    //Message code ends here


                                    //newcode4
                                    StartNavigation();
                                    //newcode4 end

                                    //for listener
                                    attachListener();



                                }
                            });
                    //newcode end
                    //end



                }



                else {
                    // The toggle is disabled
                    //logic for signout
                    indicator = true;

                    //following querries code to get document id of assigned dreiver in "AssignedRides"
                    //deleting the assigned driver document
                    //assigning the status of driver to "Available" in the end



                    //Start of (End ride logic)

                    //getting document id for assigned ride
                    mFireStore.collection("AssignedRides")
                            .whereEqualTo("AmbulanceDriverID", ""+docID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                            assignedDocID = document.getId();
                                            //driverDocID = document.getData().get("AmbulanceDriverID").toString();

                                            Log.d("Status","Fetched doc Id");
                                            Log.d("Status",""+document.getId() + " => " + document.getData());

                                        }

                                        //querry 2
                                        //deleting document after ride is ended
                                        mFireStore.collection("AssignedRides").document(assignedDocID)
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("Status","Document deleted "+assignedDocID);
                                                        Log.d("Status","DocumentSnapshot successfully deleted!");


                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("Status","Document could not be deleted");
                                                        Log.d("Status","Error deleting document"+e);
                                                    }
                                                });
                                        //delete end
                                        //querry 2 end



                                    } else {
                                        Log.d("Status","Could not fetched doc Id");
                                        Log.d("Status","Error getting documents: "+task.getException());
                                    }
                                }
                            });
                    //end getting

                    restartActivity();

                    detachListener();
                    //End of (End ride logic)

                }
            }
        });



        //log out logic
        Button LogOut = findViewById(R.id.LogOut);
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(indicator.equals(true)){
                    signOut();
                }
                else{
                    Toast.makeText(OpenMap.this, "First Go Offline", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    //onCreate ends

    //My customize function starts //////////////////////////////////

    public void FINDLOC(){

        if(ActivityCompat.checkSelfPermission(OpenMap.this , ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        client = LocationServices.getFusedLocationProviderClient(this);
        client.getLastLocation().addOnSuccessListener(OpenMap.this ,new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    longitutde = location.getLongitude();
                    latitude = location.getLatitude();
                    Log.d("MyLatitude:::::>",""+latitude);
                    Log.d("MyLongitude:::::>",""+longitutde);
                    //Toast.makeText(OpenMap.this, "Show Location "+latitude+" "+longitutde, Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(OpenMap.this, "Turn on GPS", Toast.LENGTH_LONG).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Exception.........",""+e);
                Toast.makeText(OpenMap.this, "Location not retrieved, ERROR!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        // "haversine" great circle distance approximation

        Double R = 6371.0; // Radius of the earth in km
        Double dLat = deg2rad(lat2-lat1);  // deg2rad below
        Double dLon = deg2rad(lon2-lon1);
        Double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        Double d = R * c; // Distance in km
        return d;

    }

    private double deg2rad(double deg) {

        return (deg * Math.PI / 180.0);
    }

    public void StartNavigation(){

        Dlatitude =  (Double) docLat;
        Dlongitude = (Double) docLong;
        Log.d("Chk input, before navi",""+latitude+" "+longitutde+"  "+Dlatitude+" "+Dlongitude);
        //Toast.makeText(this, ""+UserLong+" "+UserLat+"  "+AmbulanceDriverLat+" "+AmbulanceDriverLong, Toast.LENGTH_SHORT).show();

        Point origin = Point.fromLngLat(longitutde,latitude);
        Point destination = Point.fromLngLat(Dlongitude,Dlatitude);
        getRoute(origin, destination);
        Log.d("Nav: ","Navigation about to start");
        Toast.makeText(this, "Navigation about to start", Toast.LENGTH_SHORT).show();

    }


    void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        //for debugging
                        Log.d("Route",""+currentRoute);


                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                            Log.d("end: ","yoyooyo");
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    public void attachListener(){
        noteListener= mFireStore.collection("AssignedRides")
                .whereEqualTo("AmbulanceDriverID", docID.toString())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("Listening 1 :>", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    //DO NOTHING
                                    //Start navigation for driver
                                    //StartNavigation();
                                    break;

                                        /*case MODIFIED:
                                            Log.d("Data modified", "is  : " + dc.getDocument().getData());
                                            break;*/

                                case REMOVED:
                                    restartActivity();
                                    Log.d("Data removed", "Driver Removed is : " + dc.getDocument().getData());
                                    break;
                            }
                        }


                    }
                });
    }

    public void detachListener(){
        noteListener.remove();
        Log.d("Listener: ","Removed");

    }

    public void runMultipleQuerries(){

        final FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        //Log.d("User Id: ",""+mFirebaseUser.getEmail());

        //get user phone number and name
        mFireStore.collection("Users")
                .whereEqualTo("email", ""+mFirebaseUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                //Log.d("USer data",""+document.getData());
                                UserPhoneNumber= document.getData().get("phoneNumber").toString();
                                UserName = document.getData().get("name").toString();
                                Log.d("User number: ",""+UserPhoneNumber);
                                Log.d("User name: ",""+UserName);

                            }

                            //get driver phone number and name

                            mFireStore.collection("Ambulance Drivers")
                                    .whereEqualTo("email", ""+docID)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {

                                                    DriverPhoneNumber= document.getData().get("phoneNumber").toString();
                                                    DriverName = document.getData().get("name").toString();
                                                    Log.d("Driver number: ",""+DriverPhoneNumber);
                                                    Log.d("Driver name: ",""+DriverName);


                                                }


                                                //new new

                                                //new code2 start

                                                // Set "Status" to "Unavailable" of booked driver
                                                DocumentReference washingtonRef = mFireStore.collection("Ambulance Drivers")
                                                        .document(docID.toString());

                                                washingtonRef
                                                        .update("status", "Unavailable")
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                //Toast.makeText(OpenMap.this, "DocumentSnapshot successfully updated!", Toast.LENGTH_SHORT).show();
                                                                Log.d("status:::","Updated to unavailable");


                                                                //newcode
                                                                Log.d("in !null ID",".."+docID);

                                                                // maybe need to put it in else condition
                                                                Toast.makeText(OpenMap.this, "Your Ambulance driver ID is: "+docID, Toast.LENGTH_SHORT).show();


                                                                //making a new collection
                                                                //store data of booked driver and customer in new collection
                                                                Map<String, Object> Ride = new HashMap<>();
                                                                Ride.put("AmbulanceDriverID", docID);
                                                                Ride.put("U_Latitude",latitude );
                                                                Ride.put("U_Longitude",longitutde);
                                                                Ride.put("D_Latitude",docLat);
                                                                Ride.put("D_Longitude",docLong);
                                                                Ride.put("D_Name",docName);
                                                                Ride.put("D_PhoneNumber",DriverPhoneNumber);
                                                                Ride.put("D_Name",DriverName);
                                                                Ride.put("U_PhoneNumber",UserPhoneNumber);
                                                                Ride.put("U_Name",UserName);



                                                                mFireStore.collection("AssignedRides")
                                                                        .add(Ride)
                                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentReference documentReference) {
                                                                                Log.d("Data added::","new colection of assigned drivers");
                                                                                //Toast.makeText(OpenMap.this, "DocumentSnapshot successfully written!", Toast.LENGTH_SHORT).show();

                                                                                //newcode4
                                                                                //StartNavigation();
                                                                                //newcode4 end

                                                                                //for listener
                                                                                //attachListener();

                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Log.d("Data added::","error writing document in new colection of assigned drivers");
                                                                                //Toast.makeText(OpenMap.this, "Error writing document "+e, Toast.LENGTH_SHORT).show();

                                                                            }
                                                                        });


                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                                //Toast.makeText(OpenMap.this, "Error updating document", Toast.LENGTH_SHORT).show();
                                                                Log.d("status:::","Error updating document in unavailable");

                                                            }
                                                        });

                                                //new code2 end

                                                //new new





                                            } else {
                                                Log.d("Status","Could not fetch Driver Phone number");
                                                Log.d("Status","Error getting documents: "+task.getException());
                                            }
                                        }
                                    });



                        } else {
                            Log.d("Status","Could not fetch User phone number");
                            Log.d("Status","Error getting documents: "+task.getException());
                        }
                    }
                });
    }



    public void restartActivity(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void signOut(){
        mFirebaseAuth.signOut();
        openMain();
    }

    public void openMain(){
        Intent i = new Intent(OpenMap.this, MainActivity.class);
        startActivity(i);
    }

    public void openHome(){
        Intent i = new Intent(OpenMap.this, HomeActivity.class);
        startActivity(i);
    }


    //My customize functions end //////////////////////////////////






    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        OpenMap.this.mapboxMap = mapboxMap;

        mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/cjerxnqt3cgvp2rmyuxbeqme7"),
                new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                    }
                });
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        if(indicator.equals(true)){
            openHome();
        }
        else{
            //do nothing
            Toast.makeText(OpenMap.this, "You need to stay on this page", Toast.LENGTH_SHORT).show();
        }
    }
}