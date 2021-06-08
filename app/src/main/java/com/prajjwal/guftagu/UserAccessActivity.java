package com.prajjwal.guftagu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserAccessActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;
    String currentUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_access);

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
            Intent settingsIntent = new Intent(UserAccessActivity.this, ProfileSettings.class);
            startActivity(settingsIntent);
        }
        if(item.getItemId() == R.id.add_friend_btn) {
            startActivity(new Intent(UserAccessActivity.this, SearchUserActivity.class));
        }

        return true;
    }

    private void sendToStart() {

        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String timestamp = String.valueOf(System.currentTimeMillis());

        DatabaseReference lastSeenReference = FirebaseDatabase.getInstance().
                getReference().child("Users").child(currentUID).child("lastSeen");
        lastSeenReference.setValue(timestamp);

        FirebaseAuth.getInstance().signOut();

        Intent loginIntent = new Intent(UserAccessActivity.this, LoginActivity.class);
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

    @Override
    protected void onStart() {
        super.onStart();

        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference lastSeenReference = FirebaseDatabase.getInstance().
                getReference().child("Users").child(currentUID).child("lastSeen");
        lastSeenReference.setValue("online");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String timestamp = String.valueOf(System.currentTimeMillis());

        DatabaseReference lastSeenReference = FirebaseDatabase.getInstance().
                getReference().child("Users").child(currentUID).child("lastSeen");
        lastSeenReference.setValue(timestamp);
    }
}