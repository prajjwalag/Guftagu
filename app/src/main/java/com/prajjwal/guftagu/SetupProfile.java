package com.prajjwal.guftagu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupProfile extends AppCompatActivity {

    private DatabaseReference userDatabase;

    private EditText name, username;
    private Button createProfileBtn;

    private ProgressBar progressBar;

    private static final int GALLERY_PIC = 1;

    SharedPreferences setProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);

        name = findViewById(R.id.sp_name_field);
        username = findViewById(R.id.sp_username_field);
        createProfileBtn = findViewById(R.id.create_profile_btn);
        progressBar = findViewById(R.id.progress_bar_sp);

        createProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProfileBtn.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                String displayName = name.getText().toString();
                String profileUsername = username.getText().toString();

                if(!TextUtils.isEmpty(displayName) && !TextUtils.isEmpty(profileUsername)) {
                    registerUser(displayName, profileUsername);
                }
                else {
                    Toast.makeText(SetupProfile.this, "Please Enter all details", Toast.LENGTH_LONG).show();
                    createProfileBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void registerUser(String displayName, String profileUsername) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String UID = currentUser.getUid();

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(UID);
        HashMap<String, String> userMap = new HashMap<>();
        userMap.put("name", displayName);
        userMap.put("username", profileUsername);
        userMap.put("phone", currentUser.getPhoneNumber());
        userMap.put("status", "Let's do some Guftagu");
        userMap.put("image", "default");
        userMap.put("thumb_image", "default");
        userMap.put("uid", UID);

        userDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    setProfile = getSharedPreferences("setupProfile", MODE_PRIVATE);
                    SharedPreferences.Editor editor = setProfile.edit();
                    editor.putBoolean("ProfileSetup", true);
                    editor.commit();

                    Intent mainIntent = new Intent(SetupProfile.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();
                }
                else {
                    Toast.makeText(SetupProfile.this, "Some error occurred. Please try again", Toast.LENGTH_LONG).show();
                    createProfileBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}