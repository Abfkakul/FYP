package com.example.android.insaniyat;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity
{
    private FirebaseAuth mFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mFirebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        final boolean loggedIn;

        if(mFirebaseUser!=null && mFirebaseUser.isEmailVerified())
        {
            loggedIn=true;
        }
        else
        {
            loggedIn=false;
        }

        Thread myThread = new Thread()
        {
            @Override
            public void run()
            {
                try
                {
                    sleep(300);
                    if(loggedIn)
                    {
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }
                    finish();
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                super.run();
            }
        };
        myThread.start();
    }
}