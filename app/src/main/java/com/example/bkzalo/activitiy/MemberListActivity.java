/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:13 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 11/29/21, 10:47 PM
 * /
 */

package com.example.bkzalo.activitiy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.adapters.MemberAdapter;
import com.example.bkzalo.asynctasks.GetMemberListAsync;
import com.example.bkzalo.asynctasks.GetRelationshipAsync;
import com.example.bkzalo.asynctasks.SendMessageAsync;
import com.example.bkzalo.listeners.ClickMemberListener;
import com.example.bkzalo.listeners.GetMemberListener;
import com.example.bkzalo.listeners.GetRelationshipListener;
import com.example.bkzalo.listeners.SendMessageListener;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.Relationship;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;
import com.google.gson.Gson;

import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.RequestBody;

public class MemberListActivity extends AppCompatActivity {

    private ImageView iv_back;
    private RecyclerView recyclerView;
    private Methods methods;
    private int ROOM_ID;
    private boolean mIsAdmin = false;
    private ArrayList<User> arrayList_user;
    private ArrayList<Participant> arrayList_participant;
    private MemberAdapter adapter;
    private Socket socket;
    private final int REQUEST_MODE = 1;
    private final int REQUESTED_MODE = 2;
    private final int FRIEND_MODE = 3;
    private final int STRANGER_MODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

        methods = new Methods(this);

        Intent intent = getIntent();

        if(intent != null){
            ROOM_ID = intent.getIntExtra("room_id", 0);
            mIsAdmin = intent.getBooleanExtra("is_admin", false);
        }

        arrayList_user = new ArrayList<>();
        arrayList_participant = new ArrayList<>();

        InitSocketIO();
        AnhXa();
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
                    Toast.makeText(MemberListActivity.this, "Chưa khỏi động chat socket!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MemberListActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MemberListActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetMemberListAsync async = new GetMemberListAsync(requestBody, listener);
        async.execute();
    }

    private void SetAdapter(){
        adapter = new MemberAdapter(arrayList_user, arrayList_participant, new ClickMemberListener() {
            @Override
            public void onProfile(int user_id) {
                GetRelationship(user_id);
            }

            @Override
            public void onAdmin(int user_id, boolean isAdmin) {
                if(mIsAdmin){
                    OpenConfirmDialog("admin", user_id, isAdmin);
                }else {
                    Toast.makeText(MemberListActivity.this, "Chỉ quản trị viên có thể sử dụng chức năng này!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDelete(int user_id) {
                if(mIsAdmin){
                    OpenConfirmDialog("delete", user_id, false);
                }else {
                    Toast.makeText(MemberListActivity.this, "Chỉ quản trị viên có thể sử dụng chức năng này!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void GetRelationship(int muid) {
        Bundle bundle = new Bundle();
        bundle.putInt("uid", Constant.UID);
        bundle.putInt("uid2", muid);

        RequestBody requestBody = methods.getRequestBody("method_get_relationship", bundle, null);

        GetRelationshipListener listener = new GetRelationshipListener() {
            @Override
            public void onStart() {
                //hiện progressbar
            }

            @Override
            public void onEnd(boolean status, Relationship relationship) {
                if(methods.isNetworkConnected()){
                    if(status){

                        String Status = "";

                        if(relationship != null){
                            Status = relationship.getStatus();
                        }else {
                            Status = "stranger";
                        }

                        Intent intent = new Intent(MemberListActivity.this, ProfileUserActivity.class);

                        intent.putExtra("uid", muid);

                        switch (Status){
                            case "friend":
                                intent.putExtra("mode", FRIEND_MODE);
                                break;
                            case "stranger":
                                intent.putExtra("mode", STRANGER_MODE);
                                break;
                            case "request":
                                if(relationship.getRequester() == Constant.UID){
                                    intent.putExtra("mode", REQUESTED_MODE);
                                }else {
                                    intent.putExtra("mode", REQUEST_MODE);
                                }
                                break;
                        }

                        startActivity(intent);

                    }else {
                        Toast.makeText(MemberListActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MemberListActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetRelationshipAsync async = new GetRelationshipAsync(requestBody, listener);
        async.execute();
    }

    private void SetAdmin(int user_id, boolean isAdmin){
        Bundle bundle = new Bundle();
        bundle.putInt("admin_id", Constant.UID);
        bundle.putInt("user_id", user_id);
        bundle.putInt("room_id", ROOM_ID);
        bundle.putString("is_admin", isAdmin?"false":"true");

        RequestBody requestBody = methods.getRequestBody("method_set_group_admin", bundle, null);

        SendMessageListener listener = new SendMessageListener() {

            @Override
            public void onEnd(boolean status, Message message) {
                if(methods.isNetworkConnected()){
                    if(status){
                        String json = new Gson().toJson(message);
                        socket.emit("new message", json);

                        LoadMemberList();
                    }else {
                        Toast.makeText(MemberListActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MemberListActivity.this, "Vui lòng kết internet!", Toast.LENGTH_SHORT).show();
                }

            }
        };

        SendMessageAsync async = new SendMessageAsync(requestBody, listener);

        async.execute();
    }

    private void DeleteMember(int user_id){
        Bundle bundle = new Bundle();

        bundle.putInt("admin_id", Constant.UID);
        bundle.putInt("user_id", user_id);
        bundle.putInt("room_id", ROOM_ID);

        RequestBody requestBody = methods.getRequestBody("method_delete_group_member", bundle, null);

        SendMessageListener listener = new SendMessageListener() {

            @Override
            public void onEnd(boolean status, Message message) {
                if(methods.isNetworkConnected()){
                    if(status){
                        String msg_json = new Gson().toJson(message);

                        Bundle bundle1 = new Bundle();
                        bundle1.putString("user_id", String.valueOf(user_id));
                        bundle1.putString("room_id", String.valueOf(ROOM_ID));
                        bundle1.putString("msg_json", msg_json);

                        String json = new Gson().toJson(bundle1);

                        socket.emit("delete member", json);

                        LoadMemberList();
                    }else {
                        Toast.makeText(MemberListActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MemberListActivity.this, "Vui lòng kết internet!", Toast.LENGTH_SHORT).show();
                }

            }
        };

        SendMessageAsync async = new SendMessageAsync(requestBody, listener);

        async.execute();
    }

    protected void OpenConfirmDialog(String type, int user_id, boolean isAdmin){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_warning);
        builder.setTitle("Thông báo");

        switch (type){
            case "admin":
                builder.setMessage("Bạn thực sự muốn thay đổi quyền quản trị viên này?");
                break;
            case "delete":
                builder.setMessage("Bạn thực sự muốn xóa thành viên này khỏi nhóm?");
                break;
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog

                switch (type){
                    case "admin":
                        SetAdmin(user_id, isAdmin);
                        break;
                    case "delete":
                        DeleteMember(user_id);
                        break;
                }

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
        super.onBackPressed();
    }
}