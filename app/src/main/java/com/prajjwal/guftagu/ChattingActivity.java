package com.prajjwal.guftagu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ChattingActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView chatRecyclerView;
    ImageView chatSenderImage;
    TextView chatSenderName, chatSenderOnline;
    EditText messageBox;
    ImageButton sendBtn;


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
    }
}