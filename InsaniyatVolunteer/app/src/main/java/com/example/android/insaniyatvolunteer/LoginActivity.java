package com.example.android.insaniyatvolunteer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity
{
    private EditText emailId,password;
    private Button signinButton;
    private FirebaseAuth mFirebaseAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.getSupportActionBar().setTitle("Volunteer Login");
        this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkblue)));

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId= findViewById(R.id.loginVolunteerEmail);
        password=findViewById(R.id.loginVolunteerPassword);
        signinButton=findViewById(R.id.loginVolunteerSubmit);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        mFirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser!=null && mFirebaseUser.isEmailVerified())
        {
            Toast.makeText(LoginActivity.this,""+mFirebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
            DocumentReference drefUser = db.collection("Volunteers").document(""+mFirebaseUser.getEmail());
            drefUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
            {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot)
                {
                    if(documentSnapshot.exists())
                    {
                        Toast.makeText(LoginActivity.this,"Volunteer Login successful!", Toast.LENGTH_SHORT).show();
                        Intent intToHome= new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intToHome);
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"Not a registered Volunteer!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(LoginActivity.this,"Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        signinButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String email= emailId.getText().toString();
                String pwd= password.getText().toString();
                if(email.isEmpty())
                {
                    emailId.setError("Please enter email id");
                    emailId.requestFocus();
                }
                else if(pwd.isEmpty())
                {
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else if(email.isEmpty() && pwd.isEmpty())
                {
                    Toast.makeText(LoginActivity.this,"Feilds are Empty", Toast.LENGTH_SHORT).show();
                }
                else if(!(email.isEmpty() && pwd.isEmpty()))
                {
                    progressBar.setVisibility(View.VISIBLE);
                    mFirebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {   progressBar.setVisibility(View.GONE);
                            if(!task.isSuccessful())
                            {
                                Toast.makeText(LoginActivity.this,"Incorrect Email or Password, Please Login Again ", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {  if(mFirebaseAuth.getCurrentUser().isEmailVerified())
                                {
                                    DocumentReference dref = db.collection("Volunteers").document(""+email);
                                    dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                                    {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot)
                                        {
                                            if(documentSnapshot.exists())
                                            {
                                                Toast.makeText(LoginActivity.this,"Login Successful", Toast.LENGTH_SHORT).show();
                                                Intent intToHome= new Intent(LoginActivity.this, HomeActivity.class);
                                                finish();
                                                Intent intent =getIntent();
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intToHome);
                                            }
                                            else
                                            {
                                                Toast.makeText(LoginActivity.this,"Not a registered Volunteer", Toast.LENGTH_SHORT).show();
                                                emailId.setText("");
                                                password.setText("");
                                                FirebaseAuth.getInstance().signOut();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener()
                                    {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            Toast.makeText(LoginActivity.this,"Error Occurred!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else
                                {
                                 FirebaseAuth.getInstance().signOut();
                                 Toast.makeText(LoginActivity.this,"Please Verify Email!", Toast.LENGTH_SHORT).show();
                                 }
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(LoginActivity.this,"Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        finish();
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
    }

}