package com.example.bkzalo.activitiy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class GetOTP_Activity extends AppCompatActivity {

    private  static final String TAG = GetOTP_Activity.class.getName();

    private PinView pin_view;
    private Button btn_next_otp;
    private ImageView imv;
    private TextView tv_otp_again;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    String name, phone, email, password, birthday, whatToDo, gender, verificationID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_otp);

        AnhXa();

        //VerifyPhoneNumber(phone);
        //sendOTPToUser(phone);
    }

    private void AnhXa() {

        mAuth = FirebaseAuth.getInstance();
        pin_view = findViewById(R.id.pin_view);

        phone = getIntent().getStringExtra("phone");
        verificationID = getIntent().getStringExtra("verificationID");

        //name = getIntent().getStringExtra("name");
        //password = getIntent().getStringExtra("password");
        //email = getIntent().getStringExtra("email");
        //birthday = getIntent().getStringExtra("birthday");
        //gender = getIntent().getStringExtra("gender");
        //whatToDo = getIntent().getStringExtra("whatToDo");

        btn_next_otp = findViewById(R.id.btn_next_OTP);
        btn_next_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String otp = pin_view.getText().toString().trim();
                sendOTP(otp);

//
            }
        });

        tv_otp_again = findViewById(R.id.tv_otp_again);
        tv_otp_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOTPAgain();
            }
        });

//        imv = findViewById(R.id.imv_back_otp);
//        imv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(GetOTP_Activity.this, ForgotpassActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    private void sendOTP(String otp) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void sendOTPAgain() {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)
                        .setForceResendingToken(forceResendingToken)// Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onCodeSent(@NonNull String mverificationID, @NonNull PhoneAuthProvider.ForceResendingToken mforceResendingToken)
                            {
                                super.onCodeSent(mverificationID, forceResendingToken);
                                verificationID = mverificationID;
                                forceResendingToken = mforceResendingToken;
                            }

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(GetOTP_Activity.this, "Xác thực không thành công", Toast.LENGTH_LONG).show();
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
                                Toast.makeText(GetOTP_Activity.this,"OTP không hợp lệ",Toast.LENGTH_LONG).show();
                            }
                        }
                    }


                });
    }

    private void gotoResetPassActivity(String phone) {
        Intent intent = new Intent(GetOTP_Activity.this, ResetPassActivity.class);
        intent.putExtra("phone", phone);
        startActivity(intent);
    }


    private void saveNewUser()
    {
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("Users");

       //User user = new User(id, phone, name, email, password);

       // reference.child(phone).setValue(user);
    }


    private void goToLoginActivity(String phoneNumber) {
        Intent intent = new Intent(GetOTP_Activity.this, LoginActivity.class);
      startActivity(intent);

    }

}