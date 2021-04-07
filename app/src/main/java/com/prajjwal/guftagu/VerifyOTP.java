package com.prajjwal.guftagu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class VerifyOTP extends AppCompatActivity {

    private OtpTextView otpTextView;
    private TextView changeNumber, phoneNumber;
    private String verificationID;
    private ProgressBar progressBar;

    SharedPreferences boolSetupProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_o_t_p);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        verificationID = getIntent().getStringExtra("verificationID");

        otpTextView = (OtpTextView) findViewById(R.id.otp_view);
        changeNumber = findViewById(R.id.changeNumber);
        progressBar = findViewById(R.id.progress_bar_verify);

        phoneNumber = findViewById(R.id.verifyPhoneNumber);
        phoneNumber.setText(getIntent().getStringExtra("mobile"));

        otpTextView.requestFocus();
        otpTextView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {

            }

            @Override
            public void onOTPComplete(String otp) {
                if(verificationID!=null) {
                    progressBar.setVisibility(View.VISIBLE);
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificationID, otpTextView.getOTP()
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    if (task.isSuccessful()) {

                                        boolSetupProfile = getSharedPreferences("setupProfile", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = boolSetupProfile.edit();
                                        editor.putBoolean("ProfileSetup", false);
                                        editor.commit();

                                        Intent intent = new Intent(getApplicationContext(), SetupProfile.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(VerifyOTP.this, "The Verification code entered was invalid", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        changeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent changeNumberIntent = new Intent(VerifyOTP.this, LoginActivity.class);
                startActivity(changeNumberIntent);
                finish();
            }
        });
    }
}