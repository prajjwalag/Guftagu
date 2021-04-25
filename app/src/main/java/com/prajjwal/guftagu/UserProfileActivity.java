package com.prajjwal.guftagu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

public class UserProfileActivity extends AppCompatActivity {

    ImageView viewProfileUserImage;
    TextView viewProfileUserName, viewProfileUserStatus;
    Button viewProfileAddFriendButton, viewProfileDeclineRequestButton;
    ImageButton viewProfileBackButton;

    DatabaseReference currentUserDatabase, userDatabase, friendRequestDatabase, friendDatabase;
    FirebaseUser currentUser;

    String connectionStatus;
    String userName, userImage, userStatus;
    String currentUserName, currentUserImage, currentUserStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().hide();

        String user_id = getIntent().getStringExtra("uid");

        viewProfileUserImage = findViewById(R.id.viewprofile_user_image);
        viewProfileUserName = findViewById(R.id.viewprofile_user_name);
        viewProfileUserStatus = findViewById(R.id.viewprofile_user_status);
        viewProfileAddFriendButton = findViewById(R.id.viewprofile_add_friend_button);
        viewProfileDeclineRequestButton = findViewById(R.id.viewprofile_decline_request_button);
        viewProfileBackButton = findViewById(R.id.viewProfile_back_button);
        connectionStatus = "stranger";

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        friendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        friendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Request");

        viewProfileBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserProfileActivity.this, MainActivity.class));
            }
        });

        currentUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUserName = snapshot.child("name").getValue().toString();
                currentUserImage = snapshot.child("image").getValue().toString();
                currentUserStatus= snapshot.child("status").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("name").getValue().toString();
                userStatus = snapshot.child("status").getValue().toString();
                userImage = snapshot.child("image").getValue().toString();

                viewProfileUserName.setText(userName);
                viewProfileUserStatus.setText(userStatus);
                if(!userImage.equals("default"));
                Picasso.get().load(userImage).placeholder(R.drawable.avatar).into(viewProfileUserImage);

                //Friend Request Feature
                friendRequestDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(user_id)) {
                            String req_type = snapshot.child(user_id).child("request_type").getValue().toString();
                            if(req_type.equals("received")) {
                                connectionStatus = "requestReceived";
                                viewProfileAddFriendButton.setText("Accept Friend Request");

                                viewProfileDeclineRequestButton.setVisibility(View.VISIBLE);
                                viewProfileDeclineRequestButton.setEnabled(true);
                            }
                            else if (req_type.equals("sent")) {
                                connectionStatus = "requested";
                                viewProfileAddFriendButton.setText("Cancel Request");
                            }
                        }
                        else  {
                            friendDatabase.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChild(user_id)) {
                                        connectionStatus = "friend";
                                        viewProfileAddFriendButton.setText("Unfriend");

                                        viewProfileDeclineRequestButton.setText("Send Message");
                                        viewProfileDeclineRequestButton.setVisibility(View.VISIBLE);
                                        viewProfileDeclineRequestButton.setEnabled(true);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        viewProfileAddFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewProfileAddFriendButton.setEnabled(false);

                // Stranger State

                if(connectionStatus.equals("stranger")) {

                    String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                    HashMap<String, String> sentRequestMap = new HashMap<>();
                    sentRequestMap.put("name", userName);
                    sentRequestMap.put("status", userStatus);
                    sentRequestMap.put("request_type", "sent");
                    sentRequestMap.put("image", userImage);
                    sentRequestMap.put("request_uid", user_id);
                    sentRequestMap.put("date", currentDate);


                    HashMap<String, String> receivedRequestMap = new HashMap<>();
                    receivedRequestMap.put("name", currentUserName);
                    receivedRequestMap.put("status", currentUserStatus);
                    receivedRequestMap.put("image", currentUserImage);
                    receivedRequestMap.put("request_type", "received");
                    receivedRequestMap.put("request_uid", currentUser.getUid());
                    receivedRequestMap.put("date", currentDate);


                    friendRequestDatabase.child(currentUser.getUid()).child(user_id)
                            .setValue(sentRequestMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                friendRequestDatabase.child(user_id).child(currentUser.getUid())
                                        .setValue(receivedRequestMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        connectionStatus = "requested";
                                        viewProfileAddFriendButton.setText("Cancel Request");
                                        Toast.makeText(UserProfileActivity.this, "Request Sent", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                            else {
                                Toast.makeText(UserProfileActivity.this, "Request Sent Successfully.", Toast.LENGTH_LONG).show();
                            }
                            viewProfileAddFriendButton.setEnabled(true);
                        }
                    });
                }

                //Cancel Request

                if(connectionStatus.equals("requested")) {

                    friendRequestDatabase.child(currentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendRequestDatabase.child(user_id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    viewProfileAddFriendButton.setEnabled(true);
                                    connectionStatus="stranger";
                                    viewProfileAddFriendButton.setText("ADD FRIEND");
                                }
                            });
                        }
                    });
                }

                //Request Received State
                if(connectionStatus.equals("requestReceived")) {


                    String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    HashMap<String, String> currentUserFriendMap = new HashMap<>();
                    currentUserFriendMap.put("name", userName);
                    currentUserFriendMap.put("status", userStatus);
                    currentUserFriendMap.put("image", userImage);
                    currentUserFriendMap.put("friend_uid", user_id);
                    currentUserFriendMap.put("date", currentDate);

                    HashMap<String, String> otherUserFriendMap = new HashMap<>();
                    otherUserFriendMap.put("name", currentUserName);
                    otherUserFriendMap.put("status", currentUserStatus);
                    otherUserFriendMap.put("image", currentUserImage);
                    otherUserFriendMap.put("friend_uid", currentUser.getUid());
                    otherUserFriendMap.put("date", currentDate);

                    friendDatabase.child(currentUser.getUid()).child(user_id).setValue(currentUserFriendMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendDatabase.child(user_id).child(currentUser.getUid()).setValue(otherUserFriendMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    friendRequestDatabase.child(user_id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            friendRequestDatabase.child(currentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    viewProfileAddFriendButton.setEnabled(true);
                                                    connectionStatus = "friend";
                                                    viewProfileAddFriendButton.setText("Unfriend");
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }

                //friends state
                if(connectionStatus.equals("friend")) {
                    friendDatabase.child(currentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendDatabase.child(user_id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    connectionStatus = "stranger";
                                    viewProfileAddFriendButton.setEnabled(true);
                                    viewProfileAddFriendButton.setText("Add friend");
                                    viewProfileDeclineRequestButton.setVisibility(View.INVISIBLE);
                                    viewProfileDeclineRequestButton.setText("SEND MESSAGE");
                                    viewProfileDeclineRequestButton.setEnabled(false);
                                }
                            });
                        }
                    });
                }
            }
        });

        viewProfileDeclineRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String decline_btn_txt = viewProfileDeclineRequestButton.getText().toString().toLowerCase();
                if(decline_btn_txt.equals("send message")){
                    Intent chatIntent = new Intent(UserProfileActivity.this, ChattingActivity.class);
                    chatIntent.putExtra("uid", user_id);
                    startActivity(chatIntent);
                }
                else if(decline_btn_txt.equals("decline request")) {
                    friendRequestDatabase.child(currentUser.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            friendRequestDatabase.child(user_id).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    viewProfileAddFriendButton.setEnabled(true);
                                    connectionStatus="stranger";
                                    viewProfileAddFriendButton.setText("ADD FRIEND");
                                    viewProfileDeclineRequestButton.setEnabled(false);
                                    viewProfileDeclineRequestButton.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    });
                }
            }
        });

    }
}