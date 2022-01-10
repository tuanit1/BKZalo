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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.adapters.MessageAdapter;
import com.example.bkzalo.asynctasks.ExecuteQueryAsync;
import com.example.bkzalo.asynctasks.GetUserRelationshipAsync;
import com.example.bkzalo.asynctasks.LoadGetRoomAsync;
import com.example.bkzalo.asynctasks.LoadMessagesAsync;
import com.example.bkzalo.asynctasks.SendMessageAsync;
import com.example.bkzalo.listeners.ClickChatItemListener;
import com.example.bkzalo.listeners.ExecuteQueryListener;
import com.example.bkzalo.listeners.GetRoomListener;
import com.example.bkzalo.listeners.GetUserRelationshipListener;
import com.example.bkzalo.listeners.LoadMessagesListener;
import com.example.bkzalo.listeners.SendMessageListener;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.Relationship;
import com.example.bkzalo.models.Room;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.EndlessRecyclerViewScrollListener;
import com.example.bkzalo.utils.Methods;
import com.example.bkzalo.utils.PathUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import okhttp3.RequestBody;

public class ChatActivity extends AppCompatActivity {

    private ImageView iv_back, iv_room_image, iv_option, iv_send_media, iv_send_btn;
    private CardView cv_group_icon, cv_send;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout ll_for_blocker, ll_for_blocked;
    private RecyclerView rv_chat;
    private TextView tv_name, tv_name_group, tv_unblock;
    private EditText edt_message;
    private Methods methods;
    private ArrayList<Message> arrayList_message;
    private MessageAdapter adapter;
    private ProgressBar progressBar;
    private Socket socket;
    private int ROOM_ID;
    private int USER_ID;
    private String mType;
    private String image_url = "";
    private final int PICK_IMAGE_CODE = 1;
    private int page = 0;
    private int step = 20;
    private int crr_add = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        methods = new Methods(this);
        arrayList_message = new ArrayList<>();

        AnhXa();

        Intent intent = getIntent();

        if(intent != null){
            ROOM_ID = intent.getIntExtra("room_id", 0);
            mType = intent.getStringExtra("type");
            USER_ID = intent.getIntExtra("user_id", 0);

        }

        if(mType.equals("private")){
            InitBlock();
        }

        SetAdapter();
        LoadGetRoom();
        InitSocketIO();
        LoadMessages(true);

    }

    private void AnhXa() {

        cv_send = findViewById(R.id.send);
        ll_for_blocked = findViewById(R.id.ll_for_blocked);
        ll_for_blocker = findViewById(R.id.ll_for_blocker);
        tv_unblock = findViewById(R.id.tv_unblock);
        tv_unblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mType.equals("private")){
                    UnblockConfirmDialog();
                }
            }
        });

        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressBar = findViewById(R.id.progressBar);
        iv_room_image = findViewById(R.id.iv_user_image);
        iv_option = findViewById(R.id.iv_option);
        iv_send_media = findViewById(R.id.iv_send_media);
        iv_send_media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_CODE);
            }
        });
        iv_send_btn = findViewById(R.id.iv_send_btn);
        iv_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mType.equals("private")){
                    CheckBlock("text", 0);
                }else {
                    SendMessage("text");
                }

            }
        });

        rv_chat = findViewById(R.id.rv_chat);

        tv_name = findViewById(R.id.tv_name);
        tv_name_group = findViewById(R.id.tv_name_group);
        edt_message = findViewById(R.id.edt_message);
        cv_group_icon = findViewById(R.id.cv_group_icon);

        swipeRefreshLayout = findViewById(R.id.swipelayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadMessages(false);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                Uri imageUri = data.getData();

                Random rnd = new Random();
                int rand = 100000 + rnd.nextInt(900000);

                String file_name = methods.getFileName(imageUri);
                String image_name = "IMG_MSG_" + rand + "_" +file_name;
                StorageReference filePath = FirebaseStorage.getInstance().getReference().child("message_image").child(image_name);

                filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                image_url = uri.toString();

                                if(mType.equals("private")){
                                    CheckBlock("image", 0);
                                }else {
                                    SendMessage("image");
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        String err = e.getMessage();
                        Toast.makeText(ChatActivity.this, "Đã có lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }else if(requestCode == 2){

            if(resultCode == RESULT_OK){
                if(mType.equals("private")){
                    InitBlock();
                }
                LoadGetRoom();
                page = 0;
                crr_add = 0;
                LoadMessages(false);
            }

        }
    }

    private void LoadGetRoom(){
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", Constant.UID);
        bundle.putInt("room_id", ROOM_ID);
        bundle.putString("type", mType);

        RequestBody requestBody = methods.getRequestBody("method_get_room", bundle, null);

        GetRoomListener listener = new GetRoomListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status, Room room, Participant participant) {
                if(methods.isNetworkConnected()){
                    if(status){
                        FirstSetup(room);
                    }else {
                        Toast.makeText(ChatActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ChatActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        LoadGetRoomAsync async = new LoadGetRoomAsync(requestBody, listener);
        async.execute();
    }

    private void FirstSetup(Room room){

        String mImage = room.getImage();
        String mName = room.getName();

        tv_name.setText(mName);

        if(mType.equals("private")){

            cv_group_icon.setVisibility(View.GONE);
            iv_room_image.setVisibility(View.VISIBLE);

            if(!room.getImage_url().isEmpty()){
                Picasso.get()
                        .load(room.getImage_url())
                        .placeholder(R.drawable.image_user_holder)
                        .error(R.drawable.message_placeholder_ic)
                        .into(iv_room_image);
            }else{
                Picasso.get()
                        .load(R.drawable.message_placeholder_ic)
                        .into(iv_room_image);
            }

        }else {

            if(!mImage.equals("")){
                //group with image
                cv_group_icon.setVisibility(View.GONE);
                iv_room_image.setVisibility(View.VISIBLE);

                if(!room.getImage_url().isEmpty()){
                    Picasso.get()
                            .load(room.getImage_url())
                            .placeholder(R.drawable.image_user_holder)
                            .error(R.drawable.message_placeholder_ic)
                            .into(iv_room_image);
                }else{
                    Picasso.get()
                            .load(R.drawable.message_placeholder_ic)
                            .into(iv_room_image);
                }
            }else {
                //group with default image
                cv_group_icon.setVisibility(View.VISIBLE);
                iv_room_image.setVisibility(View.GONE);

                String[] arrayList_txt = mName.toUpperCase().split(" ");

                if(arrayList_txt.length == 1){
                    String[] first = arrayList_txt[0].split("");
                    String first_char = first[0];

                    tv_name_group.setText(first_char);
                }else {
                    String[] first = arrayList_txt[0].split("");
                    String first_char = first[0];

                    String[] second = arrayList_txt[1].split("");
                    String second_char = second[0];

                    tv_name_group.setText(first_char+second_char);
                }
            }
        }

        iv_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mType.equals("private")){
                    CheckBlock("option", 0);
                }else {
                    Intent intent1 = new Intent(ChatActivity.this, GroupSettingActivity.class);
                    intent1.putExtra("room_id", ROOM_ID);
                    intent1.putExtra("type", mType);
                    intent1.putExtra("user_id", USER_ID);

                    startActivityForResult(intent1, 2);
                }


            }
        });
    }

    private void InitSocketIO(){
        try {
            socket = IO.socket(Constant.SERVER_NODE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on("onNewMessage", onNewMessage);
        socket.on("onRemoveMessage", onRemoveMessage);
        socket.on("onSeenMessage", onSeenMessage);
        socket.on("onAddMember", onAddMember);
        socket.on("onDeleteMember", onDeleteMember);

        socket.connect();

    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String text = String.valueOf(args[0]);
                    Toast.makeText(ChatActivity.this, "Chưa khỏi động chat socket!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private Emitter.Listener onDeleteMember = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String json = String.valueOf(args[0]);
                    Bundle bundle = new Gson().fromJson(json, Bundle.class);

                    int user_id = Integer.parseInt(bundle.getString("user_id"));
                    int room_id = Integer.parseInt(bundle.getString("room_id"));

                    String msg_json = bundle.getString("msg_json");

                    Message message = new Gson().fromJson(msg_json, Message.class);

                    if(user_id == Constant.UID && room_id == message.getRoom_id()){

                        Toast.makeText(ChatActivity.this, "Bạn đã bị xóa khỏi nhóm!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                        startActivity(intent);

                    }else {
                        if(message.getRoom_id() == ROOM_ID){
                            arrayList_message.add(message);
                            adapter.notifyDataSetChanged();
                            rv_chat.smoothScrollToPosition(arrayList_message.size());
                            crr_add++;

                            SeenMessage(message);
                        }
                    }
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String json = String.valueOf(args[0]);
                    Message message = new Gson().fromJson(json, Message.class);

                    if(message.getRoom_id() == ROOM_ID){
                        arrayList_message.add(message);
                        adapter.notifyDataSetChanged();
                        rv_chat.smoothScrollToPosition(arrayList_message.size());

                        crr_add++;

                        SeenMessage(message);
                    }
                }
            });
        }
    };

    private Emitter.Listener onAddMember = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String json = String.valueOf(args[0]);
                    ArrayList<Message> arrayList = new Gson().fromJson(json, new TypeToken<ArrayList<Message>>(){}.getType());

                    for(Message message : arrayList){
                        arrayList_message.add(message);
                        adapter.notifyDataSetChanged();
                        rv_chat.smoothScrollToPosition(arrayList_message.size());

                        crr_add++;

                        SeenMessage(message);
                    }

                }
            });
        }
    };

    private void SeenMessage(Message message){
        if(message.getUser_id() != Constant.UID && !message.isSeen() && !this.isDestroyed()){
            Bundle bundle = new Bundle();
            bundle.putInt("message_id", message.getId());

            RequestBody requestBody = methods.getRequestBody("method_seen_message", bundle, null);

            ExecuteQueryListener listener = new ExecuteQueryListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(boolean status) {
                    if(methods.isNetworkConnected()){
                        if(status){

                            Bundle bundle1 = new Bundle();
                            bundle1.putString("message_id", String.valueOf(message.getId()));
                            bundle1.putString("room_id", String.valueOf(ROOM_ID));

                            String json = new Gson().toJson(bundle1);

                            socket.emit("seen message", json);

                        }else {
                            Toast.makeText(ChatActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(ChatActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                    }
                }
            };

            ExecuteQueryAsync async = new ExecuteQueryAsync(requestBody, listener);
            async.execute();
        }
    }

    private Emitter.Listener onRemoveMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String json = String.valueOf(args[0]);
                    Bundle bundle = new Gson().fromJson(json, Bundle.class);

                    int room_id = Integer.parseInt(bundle.getString("room_id"));
                    int message_id = Integer.parseInt(bundle.getString("message_id"));

                    if(room_id == ROOM_ID){
                        for(int i = 0; i < arrayList_message.size(); i++){
                            if(arrayList_message.get(i).getId() == message_id){
                                arrayList_message.get(i).setRemove(true);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
            });
        }
    };
    private Emitter.Listener onSeenMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String json = String.valueOf(args[0]);
                    Bundle bundle = new Gson().fromJson(json, Bundle.class);

                    int room_id = Integer.parseInt(bundle.getString("room_id"));
                    int message_id = Integer.parseInt(bundle.getString("message_id"));

                    if(room_id == ROOM_ID){
                        for(int i = 0; i < arrayList_message.size(); i++){
                            if(arrayList_message.get(i).getId() == message_id){
                                arrayList_message.get(i).setSeen(true);
                                adapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
            });
        }
    };

    private void SendMessage(String type){

        if(!(edt_message.getText().toString().isEmpty() && type.equals("text"))){
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", Constant.UID);
            bundle.putInt("room_id",ROOM_ID);
            bundle.putString("type", type);

            if(type.equals("image")){
                bundle.putString("message", image_url);
            }else {
                bundle.putString("message", edt_message.getText().toString());
            }

            RequestBody requestBody = methods.getRequestBody("method_send_message", bundle, null);

            SendMessageListener listener = new SendMessageListener() {

                @Override
                public void onEnd(boolean status, Message message) {
                    if(methods.isNetworkConnected()){
                        if(status){
                            String json = new Gson().toJson(message);
                            socket.emit("new message", json);
                            edt_message.setText("");
                        }else {
                            Toast.makeText(ChatActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(ChatActivity.this, "Vui lòng kết internet!", Toast.LENGTH_SHORT).show();
                    }

                }
            };

            SendMessageAsync async = new SendMessageAsync(requestBody, listener);

            async.execute();
        }

    }

    private void LoadMessages(boolean isFirstSeen){

        Bundle bundle = new Bundle();
        bundle.putInt("room_id", ROOM_ID);
        bundle.putInt("user_id", Constant.UID);
        bundle.putInt("step", step);
        bundle.putInt("page", page);
        bundle.putInt("crr_add", crr_add);

        RequestBody requestBody = methods.getRequestBody("method_get_messages", bundle, null);

        LoadMessagesListener listener = new LoadMessagesListener() {
            @Override
            public void onStart() {
                if(isFirstSeen){
                    progressBar.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onEnd(boolean status, ArrayList<Message> array_message) {
                if(methods.isNetworkConnected()){
                    if(status){
                        if(!array_message.isEmpty()){
                            arrayList_message.addAll(0,array_message);
                            adapter.notifyItemRangeInserted(0, step);

                            page++;

                            if(isFirstSeen){
                                FirstSeen();
                            }
                        }else{
                            Toast.makeText(ChatActivity.this, "Không còn tin nhắn nào!", Toast.LENGTH_SHORT).show();
                        }

                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);

                    }else {
                        Toast.makeText(ChatActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ChatActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        LoadMessagesAsync async = new LoadMessagesAsync(requestBody, listener);
        async.execute();
    }



    private void SetAdapter() {

        adapter = new MessageAdapter(arrayList_message, new ClickChatItemListener() {

            @Override
            public void onImageClick(Message zmessage) {
                Intent intent = new Intent(ChatActivity.this, ImageDetailActivity.class);
                intent.putExtra("message", zmessage);

                startActivity(intent);
            }

            @Override
            public void onRemove(int message_id) {
                if(mType.equals("private")){
                    CheckBlock("remove", message_id);
                }else {
                    RemoveMessage(message_id);
                }

            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(ChatActivity.this);
        llm.setStackFromEnd(true);
        rv_chat.setLayoutManager(llm);
        rv_chat.setAdapter(adapter);
    }

    private void FirstSeen(){
        for(Message m : arrayList_message){
            if(!m.isSeen()){
                SeenMessage(m);
            }
        }
    }

    private void InitBlock(){
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", USER_ID);
        bundle.putInt("admin_id", Constant.UID);

        RequestBody requestBody = methods.getRequestBody("method_get_user_relationship", bundle, null);

        GetUserRelationshipListener listener = new GetUserRelationshipListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status, Relationship relationship) {
                if(methods.isNetworkConnected()){
                    if(status){

                        if(relationship.getStatus().equals("block")){
                            if(relationship.getBlocker() == Constant.UID){
                                cv_send.setVisibility(View.GONE);
                                ll_for_blocker.setVisibility(View.VISIBLE);
                                ll_for_blocked.setVisibility(View.GONE);
                            }else {
                                cv_send.setVisibility(View.GONE);
                                ll_for_blocker.setVisibility(View.GONE);
                                ll_for_blocked.setVisibility(View.VISIBLE);
                            }
                        }else {
                            cv_send.setVisibility(View.VISIBLE);
                            ll_for_blocker.setVisibility(View.GONE);
                            ll_for_blocked.setVisibility(View.GONE);
                        }

                    }else {
                        Toast.makeText(ChatActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ChatActivity.this, "Vui lòng kết internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetUserRelationshipAsync async = new GetUserRelationshipAsync(requestBody, listener);
        async.execute();
    }

    private void CheckBlock(String type, int message_id){
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", USER_ID);
        bundle.putInt("admin_id", Constant.UID);

        RequestBody requestBody = methods.getRequestBody("method_get_user_relationship", bundle, null);

        GetUserRelationshipListener listener = new GetUserRelationshipListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status, Relationship relationship) {
                if(methods.isNetworkConnected()){
                    if(status){

                        if(relationship.getStatus().equals("block")){
                            if(relationship.getBlocker() == Constant.UID){
                                Toast.makeText(ChatActivity.this, "Bạn đã chặn người dùng này!", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ChatActivity.this, "Người dùng này đã chặn bạn!", Toast.LENGTH_SHORT).show();
                            }
                        }else {

                            switch (type){
                                case "text":
                                case "image":
                                    SendMessage(type);
                                    break;
                                case "option":
                                    Intent intent1 = new Intent(ChatActivity.this, GroupSettingActivity.class);
                                    intent1.putExtra("room_id", ROOM_ID);
                                    intent1.putExtra("type", mType);
                                    intent1.putExtra("user_id", USER_ID);

                                    startActivityForResult(intent1, 2);
                                    break;
                                case "remove":
                                    RemoveMessage(message_id);
                                    break;
                            }


                        }

                    }else {
                        Toast.makeText(ChatActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ChatActivity.this, "Vui lòng kết internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetUserRelationshipAsync async = new GetUserRelationshipAsync(requestBody, listener);
        async.execute();
    }

    private void RemoveMessage(int message_id){

        Bundle bundle = new Bundle();
        bundle.putInt("message_id", message_id);

        RequestBody requestBody = methods.getRequestBody("method_remove_message", bundle, null);

        ExecuteQueryListener listener = new ExecuteQueryListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){

                        Bundle bundle1 = new Bundle();
                        bundle1.putString("message_id", String.valueOf(message_id));
                        bundle1.putString("room_id", String.valueOf(ROOM_ID));

                        String json = new Gson().toJson(bundle1);

                        socket.emit("remove message", json);

                    }else {
                        Toast.makeText(ChatActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ChatActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExecuteQueryAsync async = new ExecuteQueryAsync(requestBody, listener);
        async.execute();
    }

    private void Unblock(){
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", USER_ID);
        bundle.putInt("admin_id", Constant.UID);

        RequestBody requestBody = methods.getRequestBody("method_msg_unblock", bundle, null);

        ExecuteQueryListener listener = new ExecuteQueryListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Toast.makeText(ChatActivity.this, "Đã bỏ chặn người dùng này!", Toast.LENGTH_SHORT).show();

                        cv_send.setVisibility(View.VISIBLE);
                        ll_for_blocked.setVisibility(View.GONE);
                        ll_for_blocker.setVisibility(View.GONE);
                    }else {
                        Toast.makeText(ChatActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ChatActivity.this, "Vui lòng kết internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExecuteQueryAsync async = new ExecuteQueryAsync(requestBody, listener);
        async.execute();
    }

    protected void UnblockConfirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_warning);
        builder.setTitle("Thông báo");
        builder.setMessage("Bạn thật sự muốn bỏ chặn người dùng này");


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                Unblock();
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