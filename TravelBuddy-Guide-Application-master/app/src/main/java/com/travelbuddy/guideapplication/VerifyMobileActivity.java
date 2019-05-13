package com.travelbuddy.guideapplication;

import android.arch.core.executor.TaskExecutor;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyMobileActivity extends AppCompatActivity {

    private EditText otpCode;
    private Button btn;
    private ProgressBar progressBar;
    private String verificationId;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);

        otpCode = findViewById(R.id.input_otp);
        btn = findViewById(R.id.signin_btn);
        progressBar = findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.INVISIBLE);
        String phonenumber = getIntent().getStringExtra("mob_number");
        phonenumber = "+91" + phonenumber;
        mAuth = FirebaseAuth.getInstance();
        //now send Verification code to mobile number OKAY
        sendVerificationCode(phonenumber);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                btn.setVisibility(View.INVISIBLE);
                String code = otpCode.getText().toString().trim();

                if (code.isEmpty() || code.length() < 6) {

                    otpCode.setError("Enter code...");
                    otpCode.requestFocus();
                    progressBar.setVisibility(View.INVISIBLE);
                    btn.setVisibility(View.VISIBLE);

                    return;
                }
                else {
                    verifyCode(code);
                }
            }
        });


    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);

    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendVerificationCode(String phonenumber) {
        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phonenumber,60,TimeUnit.SECONDS,this,mCallback
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s,forceResendingToken);
            verificationId = s;

        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code!=null)
            {
                otpCode.setText(code);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

        }
    };
}
