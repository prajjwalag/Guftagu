package com.prajjwal.guftagu.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.prajjwal.guftagu.ChattingActivity;
import com.prajjwal.guftagu.Models.ChatListModel;
import com.prajjwal.guftagu.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListHolder> {

    Context context;
    List<ChatListModel> userList;
    DatabaseReference userReference, MessageReference;

    public ChatListAdapter(Context context, List<ChatListModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ChatListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, parent, false);
        return new ChatListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListHolder holder, int position) {
        String userUID = userList.get(position).getId();
        String currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        MessageReference = FirebaseDatabase.getInstance().getReference().child("messages").child(currentUID).child(userUID);
        MessageReference.addValueEventListener(new ValueEventListener() {
            String lastMessage, messageTimeStamp, receiver;
            int count=0;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count=0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    lastMessage = dataSnapshot.child("message").getValue().toString();
                    messageTimeStamp = dataSnapshot.child("timestamp").getValue().toString();
                    receiver = dataSnapshot.child("receiver").getValue().toString();
                    String seen = dataSnapshot.child("isSeen").getValue().toString();
                    if(seen.equals("false") && receiver.equals(currentUID)) {
                        count++;
                    }
                }
                holder.chatListLastMessage.setText(lastMessage);
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(Long.parseLong(messageTimeStamp));
                String date = DateFormat.format("dd/MM/yyyy", cal).toString();
                String time = DateFormat.format("hh:mm aa", cal).toString();

                holder.chatListMessageDate.setText(date);
                holder.chatListMessageTime.setText(time);
                String messageCount = String.valueOf(count);
                if(count!=0) {
                    holder.chatListMessageCount.setText(messageCount);
                    holder.chatListMessageCount.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String onlineStatus = snapshot.child("lastSeen").getValue().toString();
                String image = snapshot.child("image").getValue().toString();
                String typingTo = snapshot.child("typingTo").getValue().toString();

                holder.chatListName.setText(name);
                if(onlineStatus.equals("online")) {
                    holder.chatListOnline.setImageResource(R.drawable.circle_online);
                    holder.chatListOnline.setVisibility(View.VISIBLE);
                }
                else {
                    holder.chatListOnline.setImageResource(R.drawable.circle_offline);
                    holder.chatListOnline.setVisibility(View.VISIBLE);
                }


                if(!image.equals("default")) {
                    Picasso.get().load(image).placeholder(R.drawable.avatar).into(holder.chatListProfileImage);
                }

                if(typingTo.equals(currentUID)) {
                    holder.chatListLastMessage.setText("typing...");
                    holder.chatListLastMessage.setTextColor(context.getResources().getColor(R.color.color_primary));
                }
                else {
                    holder.chatListLastMessage.setTextColor(context.getResources().getColor(R.color.grey));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent chatIntent = new Intent(context, ChattingActivity.class);
                chatIntent.putExtra("uid", userUID);
                holder.chatListMessageCount.setVisibility(View.GONE);
                holder.chatListMessageCount.setText("0");
                context.startActivity(chatIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ChatListHolder extends RecyclerView.ViewHolder {

        CircleImageView chatListProfileImage, chatListOnline;
        TextView chatListName, chatListLastMessage, chatListMessageDate, chatListMessageTime, chatListMessageCount;

        public ChatListHolder(@NonNull View itemView) {
            super(itemView);
            chatListProfileImage = itemView.findViewById(R.id.chatList_profileImage);
            chatListOnline = itemView.findViewById(R.id.chatList_user_online);
            chatListName = itemView.findViewById(R.id.chatList_userName);
            chatListLastMessage = itemView.findViewById(R.id.chatList_user_lastMessage);
            chatListMessageDate = itemView.findViewById(R.id.chat_list_message_date);
            chatListMessageTime = itemView.findViewById(R.id.chatList_message_time);
            chatListMessageCount = itemView.findViewById(R.id.chatList_unreadMessages);
        }
    }
}
