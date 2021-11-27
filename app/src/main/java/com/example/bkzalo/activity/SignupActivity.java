package com.example.bkzalo.activity;

import static com.example.bkzalo.R.color.main_blue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.asynctasks.ExecuteQueryAsync_Nhi;
import com.example.bkzalo.listeners.ExecuteQueryListener_Nhi;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Methods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import okhttp3.RequestBody;

public class SignupActivity extends AppCompatActivity {

    private EditText edt_phone_signup, edt_email_signup, edt_name_signup, edt_pw1_signup, edt_pw2_signup;
    private TextView tv_back;
    private Button btn_signup;
    private String email, phone, name, password, pw2;
    private Methods methods;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        methods = new Methods(this);

        AnhXa();
        ChangeStatusBarColor();


    }

    private void AnhXa() {

        mAuth = FirebaseAuth.getInstance();

        edt_email_signup = findViewById(R.id.edt_email_signup);
        edt_phone_signup = findViewById(R.id.edt_phone_signup);
        edt_name_signup = findViewById(R.id.edt_name_signup);
        edt_pw1_signup = findViewById(R.id.edt_pw1_signup);
        edt_pw2_signup = findViewById(R.id.edt_pw2_signup);


        tv_back = findViewById(R.id.tv_back_signup);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        btn_signup = findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateItem()) {

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(phone);
                                        HashMap<String, Object> hashMap = new HashMap<>();

                                        hashMap.put("phone", phone);
                                        hashMap.put("name", name);
                                        hashMap.put("email", email);
                                        hashMap.put("password", password);

                                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                mAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(@NonNull Void unused) {
                                                        Toast.makeText(SignupActivity.this, "Đăng ký thành công! Hãy kiểm tra và xác thực email.", Toast.LENGTH_SHORT).show();
                                                        SignUp(name, email, phone);
                                                        mAuth.signOut();
                                                        SignupActivity.super.onBackPressed();

                                                    }

                                                });
                                            }
                                            else
                                            {
                                                //Toast.makeText(SignupActivity.this, "Đăng ký chưa thành công", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });;

                                    }
                                    else
                                    {
                                        //Toast.makeText(SignupActivity.this, "Đăng ký chưa thành công", Toast.LENGTH_LONG).show();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                    //server



                }


            }
        });
    }

        public boolean validateItem() {

            email = edt_email_signup.getText().toString().trim();
            phone = edt_phone_signup.getText().toString().trim();
            name = edt_name_signup.getText().toString().trim();
            password = edt_pw1_signup.getText().toString().trim();
            pw2 = edt_pw2_signup.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                edt_email_signup.setError("Hãy nhập email");
                edt_email_signup.requestFocus();
                return false;
            }
            if (TextUtils.isEmpty(name)) {
                edt_name_signup.setError("Hãy nhập họ tên");
                edt_name_signup.requestFocus();
                return false;
            }
            if (TextUtils.isEmpty(phone)) {
                edt_phone_signup.setError("Hãy nhập số điện thoại");
                edt_phone_signup.requestFocus();
                return false;
            }
            if (TextUtils.isEmpty(password)) {
                edt_pw1_signup.setError("Hãy nhập password");
                edt_pw1_signup.requestFocus();
                return false;
            }
            if (TextUtils.isEmpty(pw2)) {
                edt_pw2_signup.setError("Hãy nhập lại password");
                edt_pw2_signup.requestFocus();
                return false;
            }
            if (password.length() < 6) {
                edt_pw1_signup.setError("Password phải có trên 6 kí tự");
                return false;

            }
            if (pw2.length() < 6) {
                edt_pw2_signup.setError("Password phải có trên 6 kí tự");
                return false;

            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edt_email_signup.setError("Hãy nhập email hợp lệ");
                edt_email_signup.requestFocus();
                return false;
            }

            if (!password.equals(pw2))
            {
                edt_pw2_signup.setError("Mật khẩu không trùng khớp");
                return false;
            }

            return true;

        }



    private void SignUp(String name, String email, String phone){

        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("email", email);
        bundle.putString("phone", phone);

        RequestBody requestBody = methods.getRequestBody("method_signup", bundle, null);

        ExecuteQueryListener_Nhi listener = new ExecuteQueryListener_Nhi() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        //SignupActivity.super.onBackPressed();
                    }else{
                        Toast.makeText(SignupActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SignupActivity.this, "Chưa kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExecuteQueryAsync_Nhi async = new ExecuteQueryAsync_Nhi(requestBody, listener);
        async.execute();
    }





    private void ChangeStatusBarColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(main_blue));

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        }
    }
}