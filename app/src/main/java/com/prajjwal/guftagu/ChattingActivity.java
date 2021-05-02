package com.prajjwal.guftagu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prajjwal.guftagu.Adapters.ChatAdapter;
import com.prajjwal.guftagu.Models.ChatModel;
import com.prajjwal.guftagu.Models.SearchedUsers;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChattingActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView chatRecyclerView;
    ImageView chatSenderImage;
    TextView chatSenderName, chatSenderOnline;
    EditText messageBox;
    ImageButton sendBtn;

    String senderUID, currentUID;
    boolean notify = false;

    DatabaseReference usersDatabaseReference, userRefForSeen, otherUserRefForSeen;

    List<ChatModel> chatModelList;
    ChatAdapter chatAdapter;

    ValueEventListener seenListener, otherUserSeenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        chatRecyclerView = findViewById(R.id.chat_recycler_view);
        chatSenderImage = findViewById(R.id.chat_sender_image);
        chatSenderName = findViewById(R.id.chat_sender_name);
        chatSenderOnline = findViewById(R.id.chat_sender_online);
        messageBox = findViewById(R.id.chat_message_box);
        sendBtn = findViewById(R.id.chat_send_btn);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);


        senderUID = getIntent().getStringExtra("uid");
        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        usersDatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(senderUID);

        usersDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String senderImage = snapshot.child("image").getValue().toString();
                String senderName = snapshot.child("name").getValue().toString();
                String lastSeen = snapshot.child("lastSeen").getValue().toString();
                String typingStatus = snapshot.child("typingTo").getValue().toString();

                if(typingStatus.equals(currentUID)) {
                    chatSenderOnline.setText("typing...");
                }
                else {
                    if(lastSeen.equals("online")) {
                        chatSenderOnline.setText("online");
                    }
                    else {
                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(Long.parseLong(lastSeen));
                        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
                        chatSenderOnline.setText("Last seen at " + dateTime);
                    }
                }

                chatSenderName.setText(senderName);

                if(!senderImage.equals("default")) {
                    Picasso.get().load(senderImage).placeholder(R.drawable.avatar).into(chatSenderImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;

                String message = messageBox.getText().toString().trim();

                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(ChattingActivity.this, "Cannot send empty message", Toast.LENGTH_SHORT).show();
                }
                else {
                    sendMessage(message);
                }

                messageBox.setText("");
            }
        });

        messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    checkTypingStatus("none");
                }
                else {
                    checkTypingStatus(senderUID);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        readMessages();
        seenMessages();

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
    protected void onPause() {
        super.onPause();
        userRefForSeen.removeEventListener(seenListener);
        otherUserRefForSeen.removeEventListener(otherUserSeenListener);

        String timestamp = String.valueOf(System.currentTimeMillis());

        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
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

    private void seenMessages() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference().child("messages").child(currentUID).child(senderUID);
        otherUserRefForSeen = FirebaseDatabase.getInstance().getReference().child("messages").child(senderUID).child(currentUID);
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    ChatModel chat = dataSnapshot.getValue(ChatModel.class);
                    if (chat.getReceiver().equals(currentUID) && chat.getSender().equals(senderUID)) {
                        HashMap<String, Object> hasSeenMap = new HashMap<>();
                        hasSeenMap.put("isSeen", true);
                        dataSnapshot.getRef().updateChildren(hasSeenMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        otherUserSeenListener = otherUserRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    ChatModel chat = dataSnapshot.getValue(ChatModel.class);
                    if (chat.getReceiver().equals(currentUID) && chat.getSender().equals(senderUID)) {
                        HashMap<String, Object> hasSeenMap = new HashMap<>();
                        hasSeenMap.put("isSeen", true);
                        dataSnapshot.getRef().updateChildren(hasSeenMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessages() {
        chatModelList = new ArrayList<>();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("messages").child(currentUID).child(senderUID);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatModelList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    ChatModel chat = dataSnapshot.getValue(ChatModel.class);
                    chatModelList.add(chat);
                }
                chatAdapter = new ChatAdapter(ChattingActivity.this, chatModelList);
                chatAdapter.notifyDataSetChanged();

                chatRecyclerView.setAdapter(chatAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void sendMessage(String message) {
        DatabaseReference messageReference =  FirebaseDatabase.getInstance().getReference();
        String timestamp = String.valueOf(System.currentTimeMillis());

        HashMap<String, Object> messageMap= new HashMap<>();
        messageMap.put("sender", currentUID);
        messageMap.put("receiver", senderUID);
        messageMap.put("message", message);
        messageMap.put("timestamp", timestamp);
        messageMap.put("isSeen", false);

        messageReference.child("messages").child(currentUID).child(senderUID).push().setValue(messageMap);
        messageReference.child("messages").child(senderUID).child(currentUID).push().setValue(messageMap);

        DatabaseReference myChatListReference = FirebaseDatabase.getInstance().getReference().child("ChatList").child(currentUID).child(senderUID);
        DatabaseReference senderChatListReference = FirebaseDatabase.getInstance().getReference().child("ChatList").child(senderUID).child(currentUID);

        myChatListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    myChatListReference.child("id").setValue(senderUID);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        senderChatListReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    senderChatListReference.child("id").setValue(currentUID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkTypingStatus(String typing) {
        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference typingReference = FirebaseDatabase.getInstance().
                getReference().child("Users").child(currentUID).child("typingTo");
        typingReference.setValue(typing);
    }
}