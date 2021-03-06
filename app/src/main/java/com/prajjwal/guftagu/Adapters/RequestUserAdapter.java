package com.prajjwal.guftagu.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prajjwal.guftagu.R;
import com.prajjwal.guftagu.Models.RequestUserModel;
import com.prajjwal.guftagu.UserProfileActivity;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RequestUserAdapter extends RecyclerView.Adapter<RequestUserAdapter.RequestHolder> {

    Context context;
    List<RequestUserModel> requestUserModelList;

    public RequestUserAdapter(Context context, List<RequestUserModel> requestUserModelList) {
        this.context = context;
        this.requestUserModelList = requestUserModelList;
    }

    @NonNull
    @Override
    public RequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.requests_row_user, parent, false);
        return new RequestHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestHolder holder, int position) {

        String requestUID = requestUserModelList.get(position).getRequest_uid();

        DatabaseReference requestDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(requestUID);
        requestDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String requestUserName = snapshot.child("name").getValue().toString();
                String requestUserStatus = snapshot.child("status").getValue().toString();
                String requestUserImage = snapshot.child("image").getValue().toString();

                holder.requestUserName.setText(requestUserName);
                holder.requestUserStatus.setText(requestUserStatus);
                try
                {
                    Picasso.get().load(requestUserImage).placeholder(R.drawable.avatar).into(holder.requestUserImage);
                }
                catch (Exception e) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        String requestType = requestUserModelList.get(position).getRequest_type();
        if(requestType.equals("sent")){
            holder.requestAcceptBtn.setVisibility(View.GONE);
            holder.requestDeclineBtn.setVisibility(View.GONE);
            holder.requestCancelBtn.setVisibility(View.VISIBLE);
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference friendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Request");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(context, UserProfileActivity.class);
                profileIntent.putExtra("uid", requestUID);
                context.startActivity(profileIntent);
            }
        });

        holder.requestCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference friendRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_Request");
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                friendRequestDatabase.child(currentUser.getUid()).child(requestUID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        friendRequestDatabase.child(requestUID).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Request Cancelled", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

        holder.requestDeclineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendRequestDatabase.child(currentUser.getUid()).child(requestUID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        friendRequestDatabase.child(requestUID).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(context, "Request Declined", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        });

        holder.requestAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                DatabaseReference friendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
                HashMap<String, String> currentUserFriendMap = new HashMap<>();
                currentUserFriendMap.put("friend_uid", requestUID);
                currentUserFriendMap.put("date", currentDate);

                HashMap<String, String> otherUserFriendMap = new HashMap<>();
                otherUserFriendMap.put("friend_uid", currentUser.getUid());
                otherUserFriendMap.put("date", currentDate);

                friendDatabase.child(currentUser.getUid()).child(requestUID).setValue(currentUserFriendMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        friendDatabase.child(requestUID).child(currentUser.getUid()).setValue(otherUserFriendMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                friendRequestDatabase.child(requestUID).child(currentUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        friendRequestDatabase.child(currentUser.getUid()).child(requestUID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "Request Accepted", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestUserModelList.size();
    }

    class RequestHolder extends RecyclerView.ViewHolder {

        ImageView requestUserImage;
        TextView requestUserName, requestUserStatus;
        Button requestDeclineBtn, requestAcceptBtn, requestCancelBtn;


        public RequestHolder(@NonNull View itemView) {
            super(itemView);

            requestUserImage = itemView.findViewById(R.id.viewRequest_user_image);
            requestUserName = itemView.findViewById(R.id.viewRequest_user_name);
            requestUserStatus = itemView.findViewById(R.id.viewRequest_user_status);
            requestAcceptBtn = itemView.findViewById(R.id.viewRequest_accept_btn);
            requestDeclineBtn = itemView.findViewById(R.id.viewRequest_decline_btn);
            requestCancelBtn = itemView.findViewById(R.id.viewRequest_cancel_btn);
        }
    }
}
