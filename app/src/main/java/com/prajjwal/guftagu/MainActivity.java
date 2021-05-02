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
    BottomNavigationView bottomNavigationView;
    String currentUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MessagesFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.menu_messages: selectedFragment = new MessagesFragment();
                                        break;
                case R.id.menu_requests: selectedFragment = new RequestsFragment();
                                        break;
                case R.id.menu_friends: selectedFragment = new FriendsFragment();
                                        break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return  true;
        }
    };

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
         if(item.getItemId() == R.id.add_friend_btn) {
             startActivity(new Intent(MainActivity.this, SearchUserActivity.class));
         }

         return true;
    }

    public void onStart() {

        super.onStart();

        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference lastSeenReference = FirebaseDatabase.getInstance().
                getReference().child("Users").child(currentUID).child("lastSeen");
        lastSeenReference.setValue("online");
        currentUser = mAuth.getCurrentUser();
        if(currentUser==null) {
            sendToStart();
        }

        if(currentUser!=null) {
            currentUID = currentUser.getUid();
            SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USRID", currentUID);
            editor.apply();
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


    @Override
    protected void onPause() {
        super.onPause();

        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String timestamp = String.valueOf(System.currentTimeMillis());

        DatabaseReference lastSeenReference = FirebaseDatabase.getInstance().
                getReference().child("Users").child(currentUID).child("lastSeen");
        lastSeenReference.setValue(timestamp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference lastSeenReference = FirebaseDatabase.getInstance().
                getReference().child("Users").child(currentUID).child("lastSeen");
        lastSeenReference.setValue("online");
    }
}