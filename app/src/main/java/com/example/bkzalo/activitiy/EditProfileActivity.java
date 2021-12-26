/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:13 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 11/29/21, 10:20 PM
 * /
 */

package com.example.bkzalo.activitiy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.asynctasks.ExcecuteQueryAsyncHuong;
import com.example.bkzalo.asynctasks.GetProfileUserAsync;
import com.example.bkzalo.fragments.FragmentProfile;
import com.example.bkzalo.listeners.ExecuteQueryListenerHuong;
import com.example.bkzalo.listeners.GetProfileUserListener;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;
import com.example.bkzalo.utils.PathUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.RequestBody;

public class EditProfileActivity extends AppCompatActivity {

    private Button btn_cancel;
    private Button btn_change_image;
    private Button btn_ok;
    private ImageView iv_user_image;
    private EditText et_user_name;
    private EditText et_user_birth;
    private EditText et_user_phone;
    private EditText et_user_bio;
    private Methods methods;
    private Date date_temp;
    private DatePickerDialog.OnDateSetListener DataSetListener;
    private final int PICK_IMAGE_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        methods = new Methods(this);
        AnhXa();
        GetUserProfile();
    }

    private void GetUserProfile() {
        int uid = Constant.UID;

        Bundle bundle = new Bundle();
        bundle.putInt("uid", uid);

        RequestBody requestBody = methods.getRequestBody("method_get_profile_user", bundle, null);

        GetProfileUserListener listener = new GetProfileUserListener() {
            @Override
            public void onStart() {
                //hiện progressbar
            }

            @Override
            public void onEnd(boolean status, User user) {
                if(methods.isNetworkConnected()){
                    if(status){
                        UpdateUI(user);
                    }else {
                        Toast.makeText(EditProfileActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(EditProfileActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetProfileUserAsync async = new GetProfileUserAsync(requestBody, listener);
        async.execute();
    }

    private void UpdateUI(User user) {
        et_user_name.setText(user.getName());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        et_user_birth.setText(sdf.format(user.getBirthday()));
        et_user_phone.setText(user.getPhone());
        et_user_bio.setText(user.getBio());

        date_temp = user.getBirthday();

        //String im = "http://192.168.1.9/bkzalo/image/image_user/tuan.jpg";
        String image_url = Constant.SERVER_URL + "image/image_user/" + user.getImage();

        Picasso.get()
                .load(image_url)
                .into(iv_user_image);
    }

    private void AnhXa(){
        iv_user_image = findViewById(R.id.iv_user_image);
        et_user_name = findViewById(R.id.et_user_name);
        et_user_birth = findViewById(R.id.et_user_birth);
        et_user_phone = findViewById(R.id.et_user_phone);
        et_user_bio = findViewById(R.id.et_user_bio);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_ok = findViewById(R.id.btn_ok);
        btn_change_image = findViewById(R.id.btn_change_image);
        btn_change_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_CODE);
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_user_name.getText().toString();
                String phone = et_user_phone.getText().toString();
                String bio = et_user_bio.getText().toString();
                String date = "";
                try{
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    date = sdf.format(date_temp);
                }catch (Exception e){
                    Log.e("error", e.getMessage());
                }

                EditProfile(name, phone, bio, date);

                EditProfileActivity.super.onBackPressed();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileActivity.super.onBackPressed();
            }
        });
        et_user_birth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        EditProfileActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        DataSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        DataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
               month = month + 1;
                String date = month + "/" + day + "/" + year;
                et_user_birth.setText(date);

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);

                date_temp = calendar.getTime();
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){

            switch (requestCode){
                case PICK_IMAGE_CODE:

                    Uri uri = data.getData();

                    File file;

                    try{
                        String filePath = PathUtil.getPath(this, uri);
                        file = new File(filePath);

                        UpdateImage(file, uri);

                    }catch (Exception e){
                        Toast.makeText(this, "Không thể sử dụng ảnh này, vui lòng chọn lại!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    break;
            }

        }
    }

    private void UpdateImage(File file, Uri uri) {

        Bundle bundle = new Bundle();
        bundle.putInt("uid", Constant.UID);

        RequestBody requestBody = methods.getRequestBody("method_update_profile_image", bundle, file);

        ExecuteQueryListenerHuong listener = new ExecuteQueryListenerHuong() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Toast.makeText(EditProfileActivity.this, "Cập nhập ảnh thành công!", Toast.LENGTH_SHORT).show();

                        Picasso.get()
                                .load(uri)
                                .into(iv_user_image);

                    }else {
                        Toast.makeText(EditProfileActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(EditProfileActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExcecuteQueryAsyncHuong async = new ExcecuteQueryAsyncHuong(requestBody, listener);
        async.execute();

    }

    private void EditProfile(String name, String phone, String bio, String date) {

        Bundle bundle = new Bundle();
        bundle.putInt("uid", Constant.UID);
        bundle.putString("phone", phone);
        bundle.putString("name", name);
        bundle.putString("bio", bio);
        bundle.putString("date", date);

        RequestBody requestBody = methods.getRequestBody("method_update_profile", bundle, null);

        ExecuteQueryListenerHuong listener = new ExecuteQueryListenerHuong() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Toast.makeText(EditProfileActivity.this, "Cập nhập thành công!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(EditProfileActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(EditProfileActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExcecuteQueryAsyncHuong async = new ExcecuteQueryAsyncHuong(requestBody, listener);
        async.execute();
    }
}