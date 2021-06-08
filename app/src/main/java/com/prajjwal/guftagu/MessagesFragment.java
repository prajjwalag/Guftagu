package com.prajjwal.guftagu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prajjwal.guftagu.Adapters.ChatListAdapter;
import com.prajjwal.guftagu.Adapters.FriendAdapter;
import com.prajjwal.guftagu.Models.ChatListModel;
import com.prajjwal.guftagu.Models.ChatModel;
import com.prajjwal.guftagu.Models.FriendsModel;

import java.util.ArrayList;
import java.util.List;

public class MessagesFragment extends Fragment {


    RecyclerView chatListRecyclerView;
    List<ChatListModel> chatListModels;
    String currentUID;
    DatabaseReference chatListReference;
    ChatListAdapter chatListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_messages, container, false);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            chatListRecyclerView = view.findViewById(R.id.recyclerView_allMessages);

            chatListModels = new ArrayList<>();

            chatListReference = FirebaseDatabase.getInstance().getReference().child("ChatList").child(currentUID);
            chatListReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatListModels.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ChatListModel chatListModel = dataSnapshot.getValue(ChatListModel.class);
                        chatListModels.add(chatListModel);
                        chatListAdapter = new ChatListAdapter(getActivity(), chatListModels);
                        chatListRecyclerView.setAdapter(chatListAdapter);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        return view;
    }

}
