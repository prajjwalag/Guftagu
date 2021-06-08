package com.prajjwal.guftagu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onStart() {

        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser==null) {
            sendToStart();
        }
        else {
            Intent changeUIIntent = new Intent(MainActivity.this, UserAccessActivity.class);
            startActivity(changeUIIntent);
            finish();
        }
        SharedPreferences getProfile = getSharedPreferences("setupProfile", MODE_PRIVATE);
        boolean isProfileSetup = getProfile.getBoolean("ProfileSetup", false);
        if(currentUser!=null && !isProfileSetup) {
            Intent setProfileIntent = new Intent(MainActivity.this, SetupProfile.class);
            startActivity(setProfileIntent);
            finish();
        }
    }

    private void sendToStart() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}