package com.prajjwal.guftagu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class login extends AppCompatActivity {

    private CountryCodePicker ccp;
    private EditText editTextCarrierNumber;
    private Button sendOTP;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ccp = findViewById(R.id.ccp);
        editTextCarrierNumber = findViewById(R.id.editText_carrierNumber);
        sendOTP = findViewById(R.id.verify_OTP_btn);
        progressBar = findViewById(R.id.progress_bar_login);
        ccp.registerCarrierNumberEditText(editTextCarrierNumber);

        editTextCarrierNumber.requestFocus();

        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ccp.isValidFullNumber()) {
                    Toast.makeText(login.this, "Please enter a valid mobile number", Toast.LENGTH_LONG).show();
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                sendOTP.setVisibility(View.INVISIBLE);

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        ccp.getFullNumberWithPlus(),
                        60, TimeUnit.SECONDS,
                        login.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                progressBar.setVisibility(View.INVISIBLE);
                                sendOTP.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressBar.setVisibility(View.GONE);
                                sendOTP.setVisibility(View.VISIBLE);
                                Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                progressBar.setVisibility(View.INVISIBLE);
                                sendOTP.setVisibility(View.VISIBLE);
                                Intent intent = new Intent(getApplicationContext(), VerifyOTP.class);
                                intent.putExtra("mobile", ccp.getFullNumberWithPlus());
                                intent.putExtra("verificationID", verificationID);
                                startActivity(intent);
                            }
                        }
                );
            }
        });
    }
}