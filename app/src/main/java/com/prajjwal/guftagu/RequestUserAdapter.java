package com.prajjwal.guftagu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

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
        String userImage = requestUserModelList.get(position).getImage();
        String userName = requestUserModelList.get(position).getName();
        String userStatus = requestUserModelList.get(position).getStatus();

        holder.requestUserName.setText(userName);
        holder.requestUserStatus.setText(userStatus);
        try
        {
            Picasso.get().load(userImage).placeholder(R.drawable.avatar).into(holder.requestUserImage);
        }
        catch (Exception e) {

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
