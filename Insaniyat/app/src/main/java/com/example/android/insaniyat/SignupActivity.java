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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity
{
    private EditText name,phoneNumber, emailId,password;
    private Button signup;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mFirebaseAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mFirebaseAuth = FirebaseAuth.getInstance();
        this.getSupportActionBar().setTitle("User signup");
        this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkblue)));

        name=findViewById(R.id.signupName);
        phoneNumber=findViewById(R.id.signupPhone);
        emailId=findViewById(R.id.signupEmail);
        password=findViewById(R.id.signupPassword);
        signup=findViewById(R.id.signupSubmit);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        signup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addUser();
            }
        });
    }

    void addUser()
    {
        String naam = name.getText().toString();
        String phone = phoneNumber.getText().toString();
        final String email = emailId.getText().toString();
        String pwd = password.getText().toString();
        final User user = new User(naam,phone,email,pwd);

        if(email.isEmpty() && pwd.isEmpty())
        {
            Toast.makeText(SignupActivity.this,"Fields are empty",Toast.LENGTH_SHORT);
            emailId.requestFocus();
            password.requestFocus();
        }
        else if(email.isEmpty())
        {
            emailId.setError("Please enter email id");
            emailId.requestFocus();
        }
        else if(pwd.isEmpty())
        {
            password.setError("Please enter password!");
            password.requestFocus();
        }
        else
        {
            progressBar.setVisibility(View.VISIBLE);
            mFirebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    progressBar.setVisibility(View.GONE);
                    if(task.isSuccessful())
                    {
                        mFirebaseAuth.getCurrentUser().sendEmailVerification()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(SignupActivity.this,
                                                    "Verify email!", //ADD THIS
                                                    Toast.LENGTH_SHORT).show();
                                            db.collection("Users").document(""+email).set(user);
                                            emailId.setText("");
                                            password.setText("");
                                            FirebaseAuth.getInstance().signOut();
                                            Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                                            switchActivity(i);
                                        }
                                        else
                                        {
                                            Toast.makeText(SignupActivity.this,
                                                    "Signup unsuccessful : " + task.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else
                    {
                        Toast.makeText(SignupActivity.this,
                                "Signup unsuccessful: " + task.getException().getMessage(), //ADD THIS
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void switchActivity(Intent i)
    {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(i);
    }

    @Override
    public void onBackPressed()
    {
        finish();
        startActivity(new Intent(SignupActivity.this,MainActivity.class));
    }

}