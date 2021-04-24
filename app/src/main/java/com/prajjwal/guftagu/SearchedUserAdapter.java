package com.prajjwal.guftagu;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchedUserAdapter extends RecyclerView.Adapter<SearchedUserAdapter.SearchedUserHolder>{

    Context context;
    List<SearchedUsers> searchedUsersList;

    public SearchedUserAdapter(Context context, List<SearchedUsers> searchedUsersList) {
        this.context = context;
        this.searchedUsersList = searchedUsersList;
    }

    @NonNull
    @Override
    public SearchedUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.searched_user_row, parent, false);
        return new SearchedUserHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchedUserHolder holder, int position) {

        String user_uid = searchedUsersList.get(position).getUid();
        String image = searchedUsersList.get(position).getImage();
        String user_name = searchedUsersList.get(position).getName();
        String user_status = searchedUsersList.get(position).getStatus();

        holder.name.setText(user_name);
        holder.status.setText(user_status);
        try {
            Picasso.get().load(image).placeholder(R.drawable.avatar).into(holder.profileImage);
        }
        catch (Exception e) {

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(context, UserProfileActivity.class);
                profileIntent.putExtra("uid", user_uid);
                context.startActivity(profileIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchedUsersList.size();
    }

    class SearchedUserHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        TextView name, status;

        public SearchedUserHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.searched_user_image);
            name = itemView.findViewById(R.id.searched_user_name);
            status = itemView.findViewById(R.id.searched_user_status);
        }
    }
}
