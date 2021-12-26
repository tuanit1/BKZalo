/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:12 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 12/25/21, 5:04 PM
 * /
 */

package com.example.bkzalo.activitiy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.adapters.NicknameAdapter;
import com.example.bkzalo.asynctasks.GetMemberListAsync;
import com.example.bkzalo.asynctasks.SendMessageAsync;
import com.example.bkzalo.listeners.GetMemberListener;
import com.example.bkzalo.listeners.SendMessageListener;
import com.example.bkzalo.listeners.UserSelectListener;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;
import com.google.gson.Gson;

import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.RequestBody;

public class ChangeNicknameActivity extends AppCompatActivity {

    private ImageView iv_back;
    private RecyclerView recyclerView;
    private int ROOM_ID;
    private ArrayList<User> arrayList_user;
    private ArrayList<Participant> arrayList_participant;
    private Socket socket;
    private Methods methods;
    private NicknameAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nickname);

        arrayList_user = new ArrayList<>();
        arrayList_participant = new ArrayList<>();

        methods = new Methods(this);

        Intent intent = getIntent();
        if(intent != null){
            ROOM_ID = intent.getIntExtra("room_id", 0);
        }

        AnhXa();
        InitSocketIO();
        LoadMemberList();
    }

    private void AnhXa(){
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView = findViewById(R.id.recyclerview);
    }

    private void InitSocketIO(){
        try {
            socket = IO.socket(Constant.SERVER_NODE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);

        socket.connect();

    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String text = String.valueOf(args[0]);
                    Toast.makeText(ChangeNicknameActivity.this, "Chưa khỏi động chat socket!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private void LoadMemberList(){
        Bundle bundle = new Bundle();
        bundle.putInt("room_id", ROOM_ID);

        RequestBody requestBody = methods.getRequestBody("method_get_memberlist", bundle, null);

        GetMemberListener listener = new GetMemberListener() {
            @Override
            public void onStart() {
                arrayList_user.clear();
                arrayList_participant.clear();
            }

            @Override
            public void onEnd(boolean status, ArrayList<User> array_user, ArrayList<Participant> array_participant) {
                if(methods.isNetworkConnected()){
                    if(status){
                        arrayList_user.addAll(array_user);
                        arrayList_participant.addAll(array_participant);

                        SetAdapter();
                    }else{
                        Toast.makeText(ChangeNicknameActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ChangeNicknameActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetMemberListAsync async = new GetMemberListAsync(requestBody, listener);
        async.execute();
    }

    private void SetAdapter(){
        adapter = new NicknameAdapter(arrayList_user, arrayList_participant, new UserSelectListener() {
            @Override
            public void onClick(int user_id, boolean isChecked) {
                openTextDialog(user_id);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
        super.onBackPressed();
    }

    private void openTextDialog(int user_id) {
        Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_inputtext);
        EditText edt_dialog = dialog.findViewById(R.id.edt_dialog);
        TextView tv_dialog = dialog.findViewById(R.id.tv_dialog);
        Button btn_ok = dialog.findViewById(R.id.btn_ok);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edt_dialog.getText().toString().isEmpty()){

                    ChangeNickname(user_id, edt_dialog.getText().toString());

                    dialog.dismiss();

                }else {
                    edt_dialog.setError("Vui lòng nhập đầy đủ!");
                }
            }
        });

        tv_dialog.setText("Đặt biệt danh mới");

        dialog.show();
    }

    private void ChangeNickname(int user_id, String nickname){
        Bundle bundle = new Bundle();
        bundle.putInt("admin_id", Constant.UID);
        bundle.putInt("user_id", user_id);
        bundle.putInt("room_id", ROOM_ID);
        bundle.putString("nickname", nickname);

        RequestBody requestBody = methods.getRequestBody("method_change_nickname", bundle, null);

        SendMessageListener listener = new SendMessageListener() {

            @Override
            public void onEnd(boolean status, Message message) {
                if(methods.isNetworkConnected()){
                    if(status){
                        String json = new Gson().toJson(message);
                        socket.emit("new message", json);

                        LoadMemberList();
                    }else {
                        Toast.makeText(ChangeNicknameActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ChangeNicknameActivity.this, "Vui lòng kết internet!", Toast.LENGTH_SHORT).show();
                }

            }
        };

        SendMessageAsync async = new SendMessageAsync(requestBody, listener);

        async.execute();
    }
}