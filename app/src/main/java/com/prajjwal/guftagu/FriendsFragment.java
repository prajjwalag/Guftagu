package com.prajjwal.guftagu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prajjwal.guftagu.Adapters.FriendAdapter;
import com.prajjwal.guftagu.Models.FriendsModel;

import java.util.ArrayList;
import java.util.List;

public class FriendsFragment extends Fragment {

    RecyclerView friendsRecyclerView;
    FriendAdapter friendAdapter;
    List<FriendsModel> friendsModelList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        friendsRecyclerView = view.findViewById(R.id.friends_recycler_view);

        friendsRecyclerView.setHasFixedSize(true);
        friendsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        friendsModelList = new ArrayList<>();
        
        getAllFriends();

        return view;
    }

    private void getAllFriends() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference friendsDataReference = FirebaseDatabase.getInstance().getReference().child("Friends").child(currentUser.getUid());

        friendsDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsModelList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    FriendsModel friendsModel = dataSnapshot.getValue(FriendsModel.class);
                    friendsModelList.add(friendsModel);
                    friendAdapter = new FriendAdapter(getActivity(), friendsModelList);
                    friendsRecyclerView.setAdapter(friendAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
