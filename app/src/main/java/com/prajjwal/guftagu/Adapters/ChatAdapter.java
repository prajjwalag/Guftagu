package com.prajjwal.guftagu.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prajjwal.guftagu.Models.ChatModel;
import com.prajjwal.guftagu.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends  RecyclerView.Adapter<ChatAdapter.ChatHolder> {


    private static final int MSG_TYPE_REC = 0;
    private static final int MSG_TYPE_SEND = 1;

    Context context;
    List<ChatModel> chatModelList;

    FirebaseUser currentUser;

    public ChatAdapter(Context context, List<ChatModel> chatModelList) {
        this.context = context;
        this.chatModelList = chatModelList;
    }

    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == MSG_TYPE_SEND)
        {
            view = LayoutInflater.from(context).inflate(R.layout.row_chat_send, parent, false);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.row_chat_received, parent, false);
        }
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {

        String message = chatModelList.get(position).getMessage();
        String timeStamp = chatModelList.get(position).getTimestamp();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        holder.chatMessage.setText(message);
        holder.chatTime.setText(dateTime);


        if(position == chatModelList.size()-1) {
            String seen = String.valueOf(chatModelList.get(position).isSeen());
            if(seen.equals("true")) {
                holder.chatSeenStatus.setText("seen");
            }
            else {
                holder.chatSeenStatus.setText("Delivered");
            }
        }
        else  {
            holder.chatSeenStatus.setVisibility(View.GONE);
        }

    }


    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatModelList.get(position).getSender().equals(currentUser.getUid())) {
            return MSG_TYPE_SEND;
        } else {
            return MSG_TYPE_REC;
        }
    }

    class ChatHolder extends RecyclerView.ViewHolder {

        TextView chatMessage, chatTime, chatSeenStatus;
        LinearLayout messageLayout;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);

            messageLayout = itemView.findViewById(R.id.messageLayout);
            chatMessage = itemView.findViewById(R.id.chatMessage);
            chatTime = itemView.findViewById(R.id.chatMessageTime);
            chatSeenStatus = itemView.findViewById(R.id.chatSeenStatus);
        }
    }
}
