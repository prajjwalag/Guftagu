package com.prajjwal.guftagu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ProfileSettings extends AppCompatActivity {

    CircleImageView profileImage;
    ImageButton editImage, editName, editUsername, editStatus;
    TextView profileName, profileUsername, profileStatus;

    DatabaseReference mUserDatabase;
    FirebaseUser mCurrentUser;
    StorageReference mImageStorage;

    String currentUID;

    ProgressDialog progressDialog;

    private static final int GALLERY_PIC = 1;

    //TODO : Edit

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile Settings");

        progressDialog = new ProgressDialog(ProfileSettings.this);
        progressDialog.setTitle("Retrieving Profile");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        profileImage = findViewById(R.id.profile_image_settings);
        editImage = findViewById(R.id.edit_profile_image_btn);
        editName = findViewById(R.id.edit_profile_name_btn);
        editUsername = findViewById(R.id.edit_profile_username_button);
        editStatus = findViewById(R.id.edit_profile_status_btn);
        profileName = findViewById(R.id.profile_name_settings);
        profileUsername = findViewById(R.id.profile_username_settings);
        profileStatus = findViewById(R.id.profile_user_status);

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = mCurrentUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        mUserDatabase.keepSynced(true);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String image = snapshot.child("image").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                String username = snapshot.child("username").getValue().toString();

                profileName.setText(name);
                profileStatus.setText(status);
                profileUsername.setText(username);
                if (!image.equals("default")) {
                    Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.avatar).into(profileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(image).placeholder(R.drawable.avatar).into(profileImage);
                        }
                    });
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog editNameDialog = new BottomSheetDialog(ProfileSettings.this);
                editNameDialog.setContentView(R.layout.edit_name_dialog);
                editNameDialog.show();
                editNameDialog.setCanceledOnTouchOutside(false);

                EditText etName = editNameDialog.findViewById(R.id.dialogEditProfileName);
                TextView cancelText = editNameDialog.findViewById(R.id.edit_name_cancel_button);
                TextView saveText = editNameDialog.findViewById(R.id.edit_name_save_button);

                etName.setText(profileName.getText().toString());

                cancelText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editNameDialog.dismiss();
                    }
                });

                saveText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.setTitle("Saving Changes");
                        progressDialog.setMessage("Please wait while we save the changes");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        String editedName = etName.getText().toString();
                        mUserDatabase.child("name").setValue(editedName).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    editNameDialog.dismiss();
                                    Toast.makeText(ProfileSettings.this, "Name changed", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        });

        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog editUsernameDialog = new BottomSheetDialog(ProfileSettings.this);
                editUsernameDialog.setContentView(R.layout.edit_username_dialog);
                editUsernameDialog.setTitle("Enter Username");
                editUsernameDialog.show();
                editUsernameDialog.setCanceledOnTouchOutside(false);

                EditText etUsername = editUsernameDialog.findViewById(R.id.dialogEditProfileUserName);
                TextView cancelText = editUsernameDialog.findViewById(R.id.edit_username_cancel_button);
                TextView saveText = editUsernameDialog.findViewById(R.id.edit_username_save_button);

                etUsername.setText(profileUsername.getText().toString());

                cancelText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editUsernameDialog.dismiss();
                    }
                });

                saveText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.setTitle("Saving Changes");
                        progressDialog.setMessage("Please wait while we save the changes");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        String editedUsername = etUsername.getText().toString();
                        mUserDatabase.child("username").setValue(editedUsername).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    editUsernameDialog.dismiss();
                                    Toast.makeText(ProfileSettings.this, "Username changed", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        });

        editStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog editStatusDialog = new BottomSheetDialog(ProfileSettings.this);
                editStatusDialog.setContentView(R.layout.edit_status_dialog);
                editStatusDialog.setTitle("Enter Status");
                editStatusDialog.show();
                editStatusDialog.setCanceledOnTouchOutside(false);

                EditText etUsername = editStatusDialog.findViewById(R.id.dialogEditProfileStatus);
                TextView cancelText = editStatusDialog.findViewById(R.id.edit_status_cancel_button);
                TextView saveText = editStatusDialog.findViewById(R.id.edit_status_save_button);

                etUsername.setText(profileStatus.getText().toString());

                cancelText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editStatusDialog.dismiss();
                    }
                });

                saveText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.setTitle("Saving Changes");
                        progressDialog.setMessage("Please wait while we save the changes");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();

                        String editedUsername = etUsername.getText().toString();
                        mUserDatabase.child("status").setValue(editedUsername).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    editStatusDialog.dismiss();
                                    Toast.makeText(ProfileSettings.this, "Username changed", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        });

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), 2);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

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

    @Override
    protected void onStart() {
        super.onStart();

        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference lastSeenReference = FirebaseDatabase.getInstance().
                getReference().child("Users").child(currentUID).child("lastSeen");
        lastSeenReference.setValue("online");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            uploadToFirebase(imageUri);
        }
    }

    private void uploadToFirebase(Uri uri) {
        StorageReference imgRef = mImageStorage.child(mCurrentUser.getUid() + ".jpg");
        imgRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mUserDatabase.child("image").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileSettings.this, "Profile Picture Changed Successfully.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                progressDialog.setTitle("Uploading Image");
                progressDialog.setMessage("Please Wait");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(ProfileSettings.this, "Image Uploading Failed.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String timestamp = String.valueOf(System.currentTimeMillis());

        DatabaseReference lastSeenReference = FirebaseDatabase.getInstance().
                getReference().child("Users").child(currentUID).child("lastSeen");
        lastSeenReference.setValue(timestamp);
    }
}