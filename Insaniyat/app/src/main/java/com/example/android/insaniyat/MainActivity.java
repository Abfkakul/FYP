package com.example.android.insaniyat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
        this.getSupportActionBar().hide();
        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser!=null && mFirebaseUser.isEmailVerified())
        {
            Toast.makeText(MainActivity.this,""+mFirebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
            DocumentReference drefUser = db.collection("Users").document(""+mFirebaseUser.getEmail());
            drefUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
            {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot)
                {
                    if(documentSnapshot.exists())
                    {
                        Toast.makeText(MainActivity.this,"User Logged in", Toast.LENGTH_SHORT).show();
                        Intent intToHome= new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intToHome);
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
            signup.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent i = new Intent(MainActivity.this,SignupActivity.class);
                    startActivity(i);
                }
            });

            login.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent i = new Intent(MainActivity.this,LoginActivity.class);
                    switchActivity(i);
                }
            });

        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(new Intent(this,MainActivity.class));
    }

    private void switchActivity(Intent i)
    {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(i);
    }
}