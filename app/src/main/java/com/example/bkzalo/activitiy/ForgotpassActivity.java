package com.example.bkzalo.activitiy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ForgotpassActivity extends AppCompatActivity {

    private  static final String TAG = ForgotpassActivity.class.getName();
    private Button btn_next;
    private TextView tv_back;
    private EditText edt_phone;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);
        AnhXa();
    }
    private void AnhXa() {

        mAuth = FirebaseAuth.getInstance();

        edt_phone = findViewById(R.id.edt_phone_forgot);
        String phone = edt_phone.getText().toString().trim();

        btn_next = findViewById(R.id.btn_next_forgot);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String phone = edt_phone.getText().toString().trim();

                FirebaseAuth auth = FirebaseAuth.getInstance();

                auth.sendPasswordResetEmail(phone)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotpassActivity.this, "Hãy kiểm tra email của bạn", Toast.LENGTH_SHORT).show();

                                }
                                else
                                {
                                    Toast.makeText(ForgotpassActivity.this, "Không thành công. Vui lòng nhập lại email.", Toast.LENGTH_LONG);
                                }
                            }
                        });

            }
        });

        tv_back = findViewById(R.id.tv_back_forgot);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotpassActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void VerifyPhoneNumber(String phone) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onCodeSent(@NonNull String verificationID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken)
                            {
                                super.onCodeSent(verificationID, forceResendingToken);
                                gotoGetOTPActivity(phone, verificationID);
                            }

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(ForgotpassActivity.this, "Xác thực không thành công", Toast.LENGTH_LONG).show();
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();


        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            gotoResetPassActivity(user.getPhoneNumber());
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(ForgotpassActivity.this,"OTP không hợp lệ",Toast.LENGTH_LONG).show();
                            }
                        }
                    }


                });
    }

    private void gotoResetPassActivity(String phone) {
        Intent intent = new Intent(ForgotpassActivity.this, ResetPassActivity.class);
        intent.putExtra("phone", phone);
        startActivity(intent);
    }

    private void gotoGetOTPActivity(String phone, String verificationID) {
        Intent intent = new Intent(ForgotpassActivity.this, GetOTP_Activity.class);
        intent.putExtra("phone", phone);
        intent.putExtra("verificationID", verificationID);
        startActivity(intent);
    }

//    private void FindPhone(String phone)
//    {
//        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phone").equalTo(phone);
//        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists())
//                {
//                    edt_phone.setError(null);
//
//                    Intent intent = new Intent(ForgotpassActivity.this, Forgot_ResetpassActivity.class);
//                    intent.putExtra("phone", phone);
//                    intent.putExtra("whatToDo", "updateData");
//                    startActivity(intent);
//                    finish();
//                }
//                else
//                {
//                    edt_phone.setError("Tài khoản không tồn tại");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
}