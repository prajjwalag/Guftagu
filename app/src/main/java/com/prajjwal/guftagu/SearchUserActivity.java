package com.prajjwal.guftagu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prajjwal.guftagu.Adapters.SearchedUserAdapter;
import com.prajjwal.guftagu.Models.SearchedUsers;

import java.util.ArrayList;
import java.util.List;

public class SearchUserActivity extends AppCompatActivity {

    RecyclerView searchedUserRecycler;
    SearchedUserAdapter searchedUserAdapter;
    List<SearchedUsers> searchedUsersList;
    String currentUID;

    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchedUserRecycler = findViewById(R.id.search_user_recyclerView);
        searchedUserRecycler.setHasFixedSize(true);
        searchedUserRecycler.setLayoutManager(new LinearLayoutManager(this));

        searchedUsersList = new ArrayList<>();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(!TextUtils.isEmpty(query.trim())) {
                    searchUsers(query);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText.trim())) {
                    searchUsers(newText);
                }
                return false;
            }
        });

        return true;
    }

    private void searchUsers(String newText) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference searchUserRef = FirebaseDatabase.getInstance().getReference("Users");

        searchUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                searchedUsersList.clear();
                for (DataSnapshot dataSnap : snapshot.getChildren()) {
                    SearchedUsers searchedUsers = dataSnap.getValue(SearchedUsers.class);

                    if(!searchedUsers.getPhone().equals(currentUser.getPhoneNumber())) {
                        if(searchedUsers.getName().toLowerCase().contains(newText.toLowerCase()) || searchedUsers.getPhone().contains(newText)) {
                            searchedUsersList.add(searchedUsers);
                        }
                    }

                    searchedUserAdapter = new SearchedUserAdapter(SearchUserActivity.this, searchedUsersList);
                    searchedUserAdapter.notifyDataSetChanged();
                    searchedUserRecycler.setAdapter(searchedUserAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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