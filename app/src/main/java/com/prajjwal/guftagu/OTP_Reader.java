package com.prajjwal.guftagu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.EditText;

import in.aabhasjindal.otptextview.OtpTextView;

public class OTP_Reader extends BroadcastReceiver {
    private static OtpTextView otp;

    public void setOtp (OtpTextView otp) {
        OTP_Reader.otp = otp;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

        for (SmsMessage sms : messages) {
            String message = sms.getMessageBody();
        }
    }
}
