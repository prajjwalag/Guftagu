package com.prajjwal.guftagu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button logoutBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);

         if(item.getItemId() == R.id.menu_logout) {
             FirebaseAuth.getInstance().signOut();
             sendToStart();
         }

         if(item.getItemId() == R.id.menu_profile) {
             Intent settingsIntent = new Intent(MainActivity.this, ProfileSettings.class);
             startActivity(settingsIntent);
         }

         return true;
    }

    public void onStart() {

        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null) {
            sendToStart();
        }

        SharedPreferences getProfile =getSharedPreferences("setupProfile", MODE_PRIVATE);
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