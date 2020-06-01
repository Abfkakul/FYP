package com.example.android.insaniyatvolunteer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity
{
    private Button login,signup;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        getSupportActionBar().setTitle("Insaniyat Volunteer");
        checkForPhoneCallPermission();

        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        mFirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if(mFirebaseUser!=null)
        {
            DocumentReference drefUser = db.collection("Volunteers").document(""+mFirebaseUser.getEmail());
            drefUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
            {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot)
                {
                    if(documentSnapshot.exists())
                    {
                        Toast.makeText(MainActivity.this,"Volunteer Logged in", Toast.LENGTH_SHORT).show();
                        Intent intToHome= new Intent(MainActivity.this, HomeActivity.class);
                        finish();
                        Intent intent =getIntent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intToHome);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,"Volunteer", Toast.LENGTH_SHORT).show();
                        //mFirebaseAuth.signOut();
                    }
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(MainActivity.this,"Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            login.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent i = new Intent(MainActivity.this,LoginActivity.class);
                    switchActivity(i);
                }
            });

            signup.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent i = new Intent(MainActivity.this,SignupActivity.class);
                    switchActivity(i);
                }
            });
        }
    }

    private void checkForPhoneCallPermission()
    {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    1);
        }
    }

    public void switchActivity(Intent i)
    {
        finish();
        startActivity(i);
    }
}