/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:12 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 12/25/21, 5:04 PM
 * /
 */

package com.example.bkzalo.activitiy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.asynctasks.ExecuteQueryAsync_Nhi;
import com.example.bkzalo.listeners.ExecuteQueryListener_Nhi;
import com.example.bkzalo.utils.Methods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import okhttp3.RequestBody;

public class CreatePF_Activity extends AppCompatActivity {

    EditText edt_phone, edt_name, edt_email, edt_password, edt_pw2;
    Button btn_next;
    String email, phone, name, password, pw2;
    Methods methods;
    FirebaseAuth mAuth;
    DatabaseReference Users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pf);

        methods = new Methods(this);

        AnhXa();
    }

    private void AnhXa()
    {
        mAuth = FirebaseAuth.getInstance();
        Users = FirebaseDatabase.getInstance().getReference().child("Users");
        edt_email = findViewById(R.id.edt_email_createpf);
        edt_name = findViewById(R.id.edt_name_createpf);
        edt_phone = findViewById(R.id.edt_phone_createpf);
        edt_password = findViewById(R.id.edt_pw1_createpf);
        edt_pw2 = findViewById(R.id.edt_pw2_createpf);

        btn_next = findViewById(R.id.next_createpf);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                if (validateItem())
                {
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
                                                            Toast.makeText(CreatePF_Activity.this, "Đăng ký thành công! Hãy kiểm tra và xác thực email.", Toast.LENGTH_SHORT).show();
                                                            Update();
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
                                    Toast.makeText(CreatePF_Activity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });

    }

    public boolean validateItem() {

        email = edt_email.getText().toString().trim();
        phone = edt_phone.getText().toString().trim();
        name = edt_name.getText().toString().trim();
        password = edt_password.getText().toString().trim();
        pw2 = edt_pw2.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            edt_email.setError("Hãy nhập email");
            edt_email.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(name)) {
            edt_name.setError("Hãy nhập họ tên");
            edt_name.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            edt_password.setError("Hãy nhập mật khẩu");
            edt_password.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(pw2)) {
            edt_pw2.setError("Hãy nhập lại mật khẩu");
            edt_pw2.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(phone)) {
            edt_phone.setError("Hãy nhập số điện thoại");
            edt_phone.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            edt_password.setError("Mật khẩu phải có trên 6 kí tự");
            return false;

        }
        if (pw2.length() < 6) {
            edt_pw2.setError("Mật khẩu phải có trên 6 kí tự");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edt_email.setError("Hãy nhập email hợp lệ");
            edt_email.requestFocus();
            return false;
        }
        return true;

    }

    private void Update()
    {
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
                        Intent intent = new Intent(CreatePF_Activity.this, Update_InfoActivity.class);
                        startActivity(intent);
                    }else{
                        Toast.makeText(CreatePF_Activity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(CreatePF_Activity.this, "Chưa kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExecuteQueryAsync_Nhi async = new ExecuteQueryAsync_Nhi(requestBody, listener);
        async.execute();
    }
}