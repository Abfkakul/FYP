package com.example.android.insaniyat;

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
    private Toast toast;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.getSupportActionBar().setTitle("User Login");
        this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkblue)));

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId= findViewById(R.id.loginEmail);
        password=findViewById(R.id.loginPassword);
        signinButton=findViewById(R.id.loginSubmit);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        mFirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser!=null && mFirebaseUser.isEmailVerified())
        {
            Toast.makeText(LoginActivity.this,""+mFirebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
            DocumentReference drefUser = db.collection("Users").document(""+mFirebaseUser.getEmail());
            drefUser.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
            {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot)
                {
                    if(documentSnapshot.exists())
                    {
                        Toast.makeText(LoginActivity.this,"User Logged in", Toast.LENGTH_SHORT).show();
                        Intent intToHome= new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intToHome);
                    }
                }
            }).addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    Toast.makeText(LoginActivity.this,"Error Occurred : "+e, Toast.LENGTH_SHORT).show();
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
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    mFirebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            progressBar.setVisibility(View.GONE);
                            if(task.isSuccessful())
                            {
                                if(mFirebaseAuth.getCurrentUser().isEmailVerified())
                                {
                                    final DocumentReference dref = db.collection("Users").document(""+email);
                                    dref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>()
                                    {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot)
                                        {
                                            if(documentSnapshot.exists())
                                            {
                                                Toast.makeText(LoginActivity.this,documentSnapshot.getString("name")+" Login Successful!", Toast.LENGTH_SHORT).show();

                                                Intent intToHome= new Intent(LoginActivity.this, HomeActivity.class);
                                                switchActivity(intToHome);
                                            }
                                            else
                                            {
                                                Toast.makeText(LoginActivity.this,documentSnapshot.getString("name")+"Not a registered User", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(LoginActivity.this,"Error : "+e, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else
                                {
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(LoginActivity.this,"Please Verify Email!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this,"Incorrect Email or Password, Please Login Again ", Toast.LENGTH_SHORT).show();
                                FirebaseAuth.getInstance().signOut();
                                password.setError("Incorrect Email or Password");
                                password.requestFocus();
                                password.setText("");
                                emailId.setError("Incorrect Email or Password");
                                emailId.requestFocus();
                            }
                        }
                    });
                }
            }
        });
    }

    private void switchActivity(Intent i)
    {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
    }
}