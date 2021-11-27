package com.example.bkzalo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.adapters.ChatListAdapter;
import com.example.bkzalo.asynctasks.ExecuteQueryAsync;
import com.example.bkzalo.asynctasks.LoadChatListAsync;
import com.example.bkzalo.listeners.ClickChatListListener;
import com.example.bkzalo.listeners.ExecuteQueryListener;
import com.example.bkzalo.listeners.LoadChatListListener;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.Room;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class HideListActivity extends AppCompatActivity {

    private ImageView iv_back;
    private RecyclerView rv_hidelist;
    private Methods methods;
    private ArrayList<Participant> arrayList_parti;
    private ArrayList<Room> arrayList_room;
    private ArrayList<User> arrayList_user;
    private ArrayList<Message> arrayList_message;
    private ChatListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_list);

        methods = new Methods(this);

        arrayList_parti = new ArrayList<>();
        arrayList_room = new ArrayList<>();
        arrayList_user = new ArrayList<>();
        arrayList_message = new ArrayList<>();

        AnhXa();
        LoadHideList();
    }

    private void AnhXa() {
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rv_hidelist = findViewById(R.id.rv_hidelist);
    }

    private void LoadHideList() {

        Bundle bundle = new Bundle();
        bundle.putInt("uid", Constant.UID);

        RequestBody requestBody = methods.getRequestBody("method_get_chat_list", bundle, null);

        LoadChatListListener listener = new LoadChatListListener() {
            @Override
            public void onStart() {
                arrayList_parti.clear();
                arrayList_room.clear();
                arrayList_user.clear();
                arrayList_message.clear();

            }

            @Override
            public void onEnd(boolean status, ArrayList<Participant> array_parti, ArrayList<Room> array_room , ArrayList<User> array_user, ArrayList<Message> array_message) {
                if(methods.isNetworkConnected()){
                    if(status){
                        arrayList_parti.addAll(array_parti);
                        arrayList_user.addAll(array_user);
                        arrayList_message.addAll(array_message);

                        for(Room r : array_room){
                            Participant par = GetParti(r);
                            if(par.isHide()){
                                arrayList_room.add(r);
                            }
                        }

                        SetAdapter();

                    }else {
                        Toast.makeText(HideListActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(HideListActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }


            }
        };

        LoadChatListAsync loadChatListAsync = new LoadChatListAsync(requestBody, listener);
        loadChatListAsync.execute();
    }

    private void SetAdapter() {
        adapter = new ChatListAdapter("hide_list", arrayList_parti, arrayList_room, arrayList_user, arrayList_message, HideListActivity.this, new ClickChatListListener() {
            @Override
            public void onClick(int room_id, int user_id, String type) {
                Intent intent = new Intent(HideListActivity.this, ChatActivity.class);

                intent.putExtra("room_id", room_id);
                intent.putExtra("type", type);
                intent.putExtra("user_id", user_id);

                startActivityForResult(intent, 1);
            }

            @Override
            public void unHide(int room_id) {
                UnhideAsync(room_id);
            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(HideListActivity.this, RecyclerView.VERTICAL, false);
        rv_hidelist.setLayoutManager(llm);
        rv_hidelist.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void UnhideAsync(int room_id) {
        Bundle bundle = new Bundle();
        bundle.putInt("room_id", room_id);
        bundle.putInt("user_id", Constant.UID);

        ExecuteQueryListener listener = new ExecuteQueryListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        LoadHideList();
                    }else {
                        Toast.makeText(HideListActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(HideListActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        RequestBody requestBody = methods.getRequestBody("method_unhide_room", bundle, null);

        ExecuteQueryAsync async = new ExecuteQueryAsync(requestBody, listener);
        async.execute();
    }

    public Participant GetParti(Room room){
        for(Participant participant : arrayList_parti){
            if(participant.getRoom_id() == room.getId() && participant.getUser_id() == Constant.UID){
                return participant;
            }
        }

        return null;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
        super.onBackPressed();
    }
}