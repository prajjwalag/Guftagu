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
import com.prajjwal.guftagu.Adapters.RequestUserAdapter;
import com.prajjwal.guftagu.Models.RequestUserModel;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends Fragment {

    RecyclerView requestRecyclerView;
    RequestUserAdapter requestUserAdapter;
    List<RequestUserModel> requestUserModelList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        requestRecyclerView = view.findViewById(R.id.requests_recycler_view);
        requestRecyclerView.setHasFixedSize(true);
        requestRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        requestUserModelList =  new ArrayList<>();

        getAllUsers();

        return view;
    }

    //TODO : Edit requests

    private void getAllUsers() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String cur_uid = currentUser.getUid();

        DatabaseReference requestReference = FirebaseDatabase.getInstance().getReference().child("Friend_Request").child(cur_uid);
        requestReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestUserModelList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    RequestUserModel requestUserModel = dataSnapshot.getValue(RequestUserModel.class);

                    requestUserModelList.add(requestUserModel);
                    requestUserAdapter = new RequestUserAdapter(getActivity(), requestUserModelList);
                    requestUserAdapter.notifyDataSetChanged();
                    requestRecyclerView.setAdapter(requestUserAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
