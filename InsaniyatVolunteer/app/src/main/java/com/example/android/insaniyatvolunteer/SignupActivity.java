package com.example.android.insaniyatvolunteer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener
{
    private EditText name,phoneNumber, emailId,password;
    private Spinner bloodGroup;
    private Button signup;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mFirebaseAuth;
    private ProgressBar progressBar;
    private String selected="";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.getSupportActionBar().setTitle("Volunteer Signup");
        this.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkblue)));

        mFirebaseAuth = FirebaseAuth.getInstance();

        name = findViewById(R.id.signupVolunteerName);
        phoneNumber = findViewById(R.id.signupVolunteerPhone);
        emailId = findViewById(R.id.signupVolunteerEmail);
        password = findViewById(R.id.signupVolunteerPassword);
        bloodGroup = findViewById(R.id.signupVolunteerBloodGroupSpinner);
        signup=findViewById(R.id.signupVolunteerSubmit);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.GONE);

        final ArrayList<String> customList = new ArrayList<>();

        customList.add("Select Blood Group");
        customList.add("A+");
        customList.add("A-");
        customList.add("AB+");
        customList.add("AB-");
        customList.add("B+");
        customList.add("B-");
        customList.add("0+");
        customList.add("0-");

        CustomBloodGroupSpinnerAdapter customAdapter = new CustomBloodGroupSpinnerAdapter(this, customList);
        if (bloodGroup!= null)
        {
            bloodGroup.setAdapter(customAdapter);
            bloodGroup.setOnItemSelectedListener(this);
        }

        signup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addVolunteer(selected);
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        String items = (String) adapterView.getSelectedItem();
        selected = items;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView)
    {

    }

    private void addVolunteer(String blood)
    {
        String naam = name.getText().toString();
        String phone = phoneNumber.getText().toString();
        final String email = emailId.getText().toString();
        String pwd = password.getText().toString();

        final Volunteer volunteer = new Volunteer(naam,phone,email,pwd,blood);

        if(email.isEmpty() && pwd.isEmpty() && naam.isEmpty() && phone.isEmpty() && blood.isEmpty())
        {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            emailId.requestFocus();
            password.requestFocus();
            name.requestFocus();
            password.requestFocus();
            bloodGroup.requestFocus();
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
        else if(naam.isEmpty())
        {
            name.setError("Please Enter Name");
            name.requestFocus();
        }
        else if(phone.isEmpty())
        {
            phoneNumber.setError("Please Enter Phone Number");
            phoneNumber.requestFocus();
        }
        else if(blood.isEmpty())
        {
            Toast.makeText(this, "Please enter blood group.", Toast.LENGTH_SHORT).show();
            bloodGroup.requestFocus();
        }
        else if(email.isEmpty() && pwd.isEmpty())
        {
            Toast.makeText(SignupActivity.this,"Fields are empty",Toast.LENGTH_SHORT);
        }
        else if(!(email.isEmpty() && pwd.isEmpty()))
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
                                .addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(SignupActivity.this,
                                                    "Verify email!", //ADD THIS
                                                    Toast.LENGTH_SHORT).show();
                                            db.collection("Volunteers").document(""+email).set(volunteer);
                                            emailId.setText("");
                                            password.setText("");
                                            mFirebaseAuth.getInstance().signOut();
                                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        }
                                        else
                                        {
                                            Toast.makeText(SignupActivity.this,
                                                    "Signup unsuccessful : " + task.getException().getMessage(), //ADD THIS
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
        else
        {
            Toast.makeText(SignupActivity.this,"Error Occurred!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed()
    {
        finish();
        startActivity(new Intent(SignupActivity.this,MainActivity.class));
    }

}