/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:13 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 12/25/21, 2:08 AM
 * /
 */

package com.example.bkzalo.activitiy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.asynctasks.ExecuteQueryAsync_Nhi;
import com.example.bkzalo.asynctasks.LoadGetRoomAsync;
import com.example.bkzalo.listeners.ExecuteQueryListener_Nhi;
import com.example.bkzalo.listeners.GetRoomListener;
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.Room;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.Manifest.permission.CALL_PHONE;

import okhttp3.RequestBody;

public class Profile_PB_Activity extends AppCompatActivity {

    private ImageView imv_phonecall_pf, imv_mess_pf, imv_del_pf, imv_back_pf, imv_block_pf;
    private RoundedImageView imv_user_pf;
    private TextView tv_phone_pf, tv_name_pf, tv_email_pf, tv_bday_pf;
    private Methods methods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pb);
        methods = new Methods(this);

        AnhXa();
        GetData();
        Call();
        Click_Delete();
        Click_Block();
        BackButton();
    }

    private void AnhXa()
    {
        imv_phonecall_pf = findViewById(R.id.imv_phonecall_pf);
        imv_mess_pf = findViewById(R.id.imv_mess_pf);
        imv_mess_pf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenMessage();
            }
        });
        imv_del_pf = findViewById(R.id.imv_del_pf);
        imv_back_pf = findViewById(R.id.imv_back_pf);
        imv_block_pf = findViewById(R.id.imv_block_pf);
        imv_user_pf = findViewById(R.id.imv_user_pf);

        tv_name_pf = findViewById(R.id.tv_name_pf);
        tv_phone_pf = findViewById(R.id.tv_phone_pf);
        tv_email_pf = findViewById(R.id.tv_email_pf);
        tv_bday_pf = findViewById(R.id.tv_bday_pf);
    }

    private void GetData()
    {
        Bundle bundle = getIntent().getExtras();
        if (bundle==null)
        {
            return;
        }

        try {
            User user = (User) bundle.get("user");
            tv_name_pf.setText(user.getName());
            tv_phone_pf.setText(user.getPhone());
            tv_email_pf.setText(user.getEmail());

            String image_path = user.getImage_url();

            if(!image_path.isEmpty()){
                Picasso.get()
                        .load(image_path)
                        .placeholder(R.drawable.image_user_holder)
                        .error(R.drawable.message_placeholder_ic)
                        .into(imv_user_pf);
            }else{
                Picasso.get()
                        .load(R.drawable.message_placeholder_ic)
                        .into(imv_user_pf);
            }

            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            Date bday = user.getBirthday();
            String b_day = df.format(bday);
            tv_bday_pf.setText(b_day);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void OpenMessage(){
        Bundle bundle = getIntent().getExtras();
        User user = (User) bundle.get("user");

        Bundle bundle1 = new Bundle();
        bundle1.putInt("uid", Constant.UID);
        bundle1.putInt("friend_id", user.getId());

        RequestBody requestBody = methods.getRequestBody("method_open_chat_activity", bundle1, null);

        LoadGetRoomAsync async = new LoadGetRoomAsync(requestBody, new GetRoomListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status, Room room, Participant participant) {

                if(room != null){
                    Intent intent = new Intent(Profile_PB_Activity.this, ChatActivity.class);

                    intent.putExtra("room_id", room.getId());
                    intent.putExtra("type", "private");
                    intent.putExtra("user_id", user.getId());

                    startActivity(intent);
                }else {
                    Toast.makeText(Profile_PB_Activity.this, "Chat room không tồn tại!", Toast.LENGTH_SHORT).show();
                }


            }
        });

        async.execute();
    }

    private void Call()
    {
        imv_phonecall_pf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = tv_phone_pf.getText().toString();
                String s = "tel:" + phone;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(s));
                if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                } else {
                    requestPermissions(new String[]{CALL_PHONE}, 1);
                }
            }
        });
    }

    private void Click_Delete()
    {
        imv_del_pf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Bundle bundle = getIntent().getExtras();
                                if (bundle == null) {
                                    return;
                                }
                                User user = (User) bundle.get("user");
                                int uid1 = Constant.UID;
                                int uid2 = user.getId();

                                Delete_PB(uid1, uid2);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Profile_PB_Activity.this);
                builder.setMessage("Bạn có chắc chắn muốn hủy kết bạn?").setPositiveButton("Có", dialogClickListener)
                        .setNegativeButton("Không", dialogClickListener).show();
            }
        });
    }

    private void Delete_PB (int uid1, int uid2)
    {

        Bundle bundle = new Bundle();
        bundle.putInt("uid1", uid1);
        bundle.putInt("uid2", uid2);

        RequestBody requestBody = methods.getRequestBody("method_delete_phonebook", bundle, null);

        ExecuteQueryListener_Nhi listener = new ExecuteQueryListener_Nhi() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Toast.makeText(Profile_PB_Activity.this, "Hủy kết bạn thành công", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }else{
                        Toast.makeText(Profile_PB_Activity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(Profile_PB_Activity.this, "Chưa kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExecuteQueryAsync_Nhi async = new ExecuteQueryAsync_Nhi(requestBody, listener);
        async.execute();
    }

    private void Click_Block()
    {
        imv_block_pf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                Bundle bundle = getIntent().getExtras();
                                if (bundle == null) {
                                    return;
                                }
                                User user = (User) bundle.get("user");
                                int uid1 = Constant.UID;
                                int uid2 = user.getId();

                                Block_PB(uid1, uid2);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(Profile_PB_Activity.this);
                builder.setMessage("Bạn có chắc chắn muốn chặn người này?").setPositiveButton("Có", dialogClickListener)
                        .setNegativeButton("Không", dialogClickListener).show();
            }
        });
    }

    private void Block_PB (int uid1, int uid2)
    {

        Bundle bundle = new Bundle();
        bundle.putInt("uid1", uid1);
        bundle.putInt("uid2", uid2);

        RequestBody requestBody = methods.getRequestBody("method_block_phonebook", bundle, null);

        ExecuteQueryListener_Nhi listener = new ExecuteQueryListener_Nhi() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Toast.makeText(Profile_PB_Activity.this, "Đã chặn thành công", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }else{
                        Toast.makeText(Profile_PB_Activity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(Profile_PB_Activity.this, "Chưa kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExecuteQueryAsync_Nhi async = new ExecuteQueryAsync_Nhi(requestBody, listener);
        async.execute();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
        super.onBackPressed();
    }

    private void BackButton()
    {
        imv_back_pf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}