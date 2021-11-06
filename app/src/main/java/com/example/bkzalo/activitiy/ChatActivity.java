package com.example.bkzalo.activitiy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.adapters.MessageAdapter;
import com.example.bkzalo.asynctasks.ExecuteQueryAsync;
import com.example.bkzalo.asynctasks.LoadGetRoomAsync;
import com.example.bkzalo.asynctasks.LoadMessagesAsync;
import com.example.bkzalo.asynctasks.SendMessageAsync;
import com.example.bkzalo.listeners.ClickChatItemListener;
import com.example.bkzalo.listeners.ExecuteQueryListener;
import com.example.bkzalo.listeners.GetRoomListener;
import com.example.bkzalo.listeners.LoadMessagesListener;
import com.example.bkzalo.listeners.SendMessageListener;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.Room;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;
import com.example.bkzalo.utils.PathUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.io.File;
import java.util.ArrayList;

import okhttp3.RequestBody;

public class ChatActivity extends AppCompatActivity {

    private ImageView iv_back, iv_room_image, iv_option, iv_send_media, iv_send_btn;
    private CardView cv_group_icon;
    private RecyclerView rv_chat;
    private TextView tv_name, tv_name_group;
    private EditText edt_message;
    private Methods methods;
    private ArrayList<Message> arrayList_message;
    private MessageAdapter adapter;
    private ProgressBar progressBar;
    private Socket socket;
    private int ROOM_ID;
    private String mType;
    private final int PICK_IMAGE_CODE = 1;

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
        }

        LoadGetRoom();
        InitSocketIO();
        LoadMessages();

    }

    private void AnhXa() {
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
                startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_IMAGE_CODE);
            }
        });
        iv_send_btn = findViewById(R.id.iv_send_btn);
        iv_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage("text", null);
            }
        });

        rv_chat = findViewById(R.id.rv_chat);

        tv_name = findViewById(R.id.tv_name);
        tv_name_group = findViewById(R.id.tv_name_group);
        edt_message = findViewById(R.id.edt_message);
        cv_group_icon = findViewById(R.id.cv_group_icon);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                Uri uri = data.getData();

                File file;

                try{
                    String filePath = PathUtil.getPath(this, uri);
                    file = new File(filePath);
                }catch (Exception e){
                    Toast.makeText(this, "Không thể sử dụng ảnh này, vui lòng chọn lại!", Toast.LENGTH_SHORT).show();
                    return;
                }

                SendMessage("image", file);

            }
        }else if(requestCode == 2){

            if(resultCode == RESULT_OK){
                LoadGetRoom();
                LoadMessages();
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

            String image_path = Constant.SERVER_URL + "image/image_user/" + mImage;

            Picasso.get()
                    .load(image_path)
                    .placeholder(R.drawable.message_placeholder_ic)
                    .into(iv_room_image);
        }else {

            if(!mImage.equals("")){
                //group with image
                cv_group_icon.setVisibility(View.GONE);
                iv_room_image.setVisibility(View.VISIBLE);

                String image_path = Constant.SERVER_URL + "image/image_room/" + mImage;

                Picasso.get()
                        .load(image_path)
                        .placeholder(R.drawable.message_placeholder_ic)
                        .into(iv_room_image);
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

                Intent intent1 = new Intent(ChatActivity.this, GroupSettingActivity.class);
                intent1.putExtra("room_id", ROOM_ID);
                intent1.putExtra("type", mType);

                startActivityForResult(intent1, 2);

            }
        });
    }

    private void InitSocketIO(){
        try {
            socket = IO.socket("http://192.168.1.11:3000/");
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on("onNewMessage", onNewMessage);
        socket.on("onRemoveMessage", onRemoveMessage);
        socket.on("onSeenMessage", onSeenMessage);

        socket.connect();

    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String text = String.valueOf(args[0]);
                    Toast.makeText(ChatActivity.this, text, Toast.LENGTH_SHORT).show();
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

    private void SendMessage(String type, File file){

        if(!(edt_message.getText().toString().isEmpty() && type.equals("text"))){
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", Constant.UID);
            bundle.putInt("room_id",ROOM_ID);
            bundle.putString("type", type);
            bundle.putString("message", edt_message.getText().toString());

            if(type.equals("image")){
                bundle.putString("is_send_image", "true");
            }else {
                bundle.putString("is_send_image", "false");
            }

            RequestBody requestBody = methods.getRequestBody("method_send_message", bundle, file);

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

    private void LoadMessages(){

        Bundle bundle = new Bundle();
        bundle.putInt("room_id", ROOM_ID);

        RequestBody requestBody = methods.getRequestBody("method_get_messages", bundle, null);

        LoadMessagesListener listener = new LoadMessagesListener() {
            @Override
            public void onStart() {
                arrayList_message.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEnd(boolean status, ArrayList<Message> array_message) {
                if(methods.isNetworkConnected()){
                    if(status){
                        arrayList_message.addAll(array_message);

                        progressBar.setVisibility(View.GONE);

                        SetAdapter();
                        FirstSeen();
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
            public void onRemove(int message_id) {
                RemoveMessage(message_id);
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

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
        super.onBackPressed();
    }

}