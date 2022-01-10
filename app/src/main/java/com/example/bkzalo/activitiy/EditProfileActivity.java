/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:13 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 11/29/21, 10:20 PM
 * /
 */

package com.example.bkzalo.activitiy;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

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
    private String crr_image = "";

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

        String image_path = user.getImage_url();
        crr_image = user.getImage();

        if(!image_path.isEmpty()){
            Picasso.get()
                    .load(image_path)
                    .placeholder(R.drawable.image_user_holder)
                    .error(R.drawable.message_placeholder_ic)
                    .into(iv_user_image);
        }else{
            Picasso.get()
                    .load(R.drawable.message_placeholder_ic)
                    .into(iv_user_image);
        }
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

                    Uri imageUri = data.getData();

                    Random rnd = new Random();
                    int rand = 100000 + rnd.nextInt(900000);

                    String file_name = methods.getFileName(imageUri);
                    String image_name = "IMG_USER_" + rand + "_" +file_name;
                    StorageReference filePath = FirebaseStorage.getInstance().getReference().child("user_image").child(image_name);

                    Toast.makeText(this, "Đang xử lý, vui lòng kiên nhẫn!", Toast.LENGTH_SHORT).show();

                    filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                            firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    DeleteExistImage();
                                    UpdateImage(image_name, uri.toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            String err = e.getMessage();
                            Toast.makeText(EditProfileActivity.this, "Đã có lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;
            }

        }
    }

    private void DeleteExistImage() {
        StorageReference filePath = FirebaseStorage.getInstance().getReference().child("user_image").child(crr_image);
        filePath.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.e("firebasestorage", "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.e("firebasestorage", "onFailure: did not delete file");
            }
        });
    }

    private void UpdateImage(String image_name, String image_url) {

        Bundle bundle = new Bundle();
        bundle.putInt("uid", Constant.UID);
        bundle.putString("image", image_name);
        bundle.putString("image_url", image_url);

        RequestBody requestBody = methods.getRequestBody("method_update_profile_image", bundle, null);

        ExecuteQueryListenerHuong listener = new ExecuteQueryListenerHuong() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Toast.makeText(EditProfileActivity.this, "Cập nhập ảnh thành công!", Toast.LENGTH_SHORT).show();

                        crr_image = image_name;

                        StorageReference filePath = FirebaseStorage.getInstance().getReference().child("user_image").child(image_name);
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUrl) {
                                Picasso.get()
                                        .load(downloadUrl.toString())
                                        .placeholder(R.drawable.image_user_holder)
                                        .error(R.drawable.message_placeholder_ic)
                                        .into(iv_user_image);
                            }
                        });


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