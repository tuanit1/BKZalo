package com.example.bkzalo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.adapters.ImageMessageAdapter;
import com.example.bkzalo.adapters.SearchMessageAdapter;
import com.example.bkzalo.asynctasks.LoadMessagesAsync;
import com.example.bkzalo.listeners.ImageDetailListener;
import com.example.bkzalo.listeners.LoadMessagesListener;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.utils.Methods;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class MessageImageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView iv_back;
    private Methods methods;
    private int ROOM_ID;
    private ArrayList<Message> arrayList_message;
    private ImageMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_image);

        methods = new Methods(this);

        arrayList_message = new ArrayList<>();

        Intent intent = getIntent();

        if(intent != null){
            ROOM_ID = intent.getIntExtra("room_id", 0);
        }

        AnhXa();
        GetImageMessage();
    }

    private void AnhXa() {

        recyclerView = findViewById(R.id.recyclerview);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageImageActivity.super.onBackPressed();
            }
        });
    }

    private void GetImageMessage(){
        Bundle bundle = new Bundle();
        bundle.putInt("room_id", ROOM_ID);

        RequestBody requestBody = methods.getRequestBody("method_image_message", bundle, null);

        LoadMessagesListener listener = new LoadMessagesListener() {
            @Override
            public void onStart() {
                arrayList_message.clear();
            }

            @Override
            public void onEnd(boolean status, ArrayList<Message> array_message) {
                if(methods.isNetworkConnected()){
                    if(status){
                        arrayList_message.addAll(array_message);

                        SetAdapter();
                    }else {
                        Toast.makeText(MessageImageActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MessageImageActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        LoadMessagesAsync async = new LoadMessagesAsync(requestBody, listener);
        async.execute();
    }

    private void SetAdapter() {
        adapter = new ImageMessageAdapter(arrayList_message, new ImageDetailListener() {
            @Override
            public void onClick(Message message) {
                Intent intent = new Intent(MessageImageActivity.this, ImageDetailActivity.class);
                intent.putExtra("message", message);

                startActivity(intent);
            }
        });

        recyclerView.setLayoutManager( new GridLayoutManager(this, 3));
        recyclerView.setAdapter(adapter);
    }
}