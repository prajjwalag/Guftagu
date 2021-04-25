package com.prajjwal.guftagu.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.prajjwal.guftagu.ChattingActivity;
import com.prajjwal.guftagu.Models.FriendsModel;
import com.prajjwal.guftagu.R;
import com.prajjwal.guftagu.UserProfileActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendHolder> {

    Context context;
    List<FriendsModel> friendsModelList;

    public FriendAdapter(Context context, List<FriendsModel> friendsModelList) {
        this.context = context;
        this.friendsModelList = friendsModelList;
    }

    @NonNull
    @Override
    public FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friends_row, parent, false);
        return new FriendHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendHolder holder, int position) {
        String friendUID = friendsModelList.get(position).getFriend_uid();
        String friendImage = friendsModelList.get(position).getImage();
        String friendName = friendsModelList.get(position).getName();
        String friendStatus = friendsModelList.get(position).getStatus();

        holder.friendName.setText(friendName);
        holder.friendStatus.setText(friendStatus);

        try {
            Picasso.get().load(friendImage).placeholder(R.drawable.avatar).into(holder.friendProfileImage);
        }
        catch (Exception e) {
        }

        holder.sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(context, ChattingActivity.class);
                chatIntent.putExtra("uid", friendUID);
                context.startActivity(chatIntent);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UserProfileActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendsModelList.size();
    }

    class FriendHolder extends RecyclerView.ViewHolder {

        CircleImageView friendProfileImage;
        TextView friendName, friendStatus;
        ImageButton sendMessageBtn;

        public FriendHolder(@NonNull View itemView) {
            super(itemView);

            friendProfileImage = itemView.findViewById(R.id.viewFriends_profile_image);
            friendName = itemView.findViewById(R.id.viewFriends_profile_name);
            friendStatus = itemView.findViewById(R.id.viewFriends_profile_status);
            sendMessageBtn = itemView.findViewById(R.id.viewFriends_send_msg_btn);
        }
    }
}
