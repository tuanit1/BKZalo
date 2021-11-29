package com.example.bkzalo.activitiy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.adapters.SearchMessageAdapter;
import com.example.bkzalo.asynctasks.LoadMessagesAsync;
import com.example.bkzalo.listeners.LoadMessagesListener;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.utils.Methods;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class SearchMessageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView iv_back;
    private TextView tv_ketqua;
    private String mSearchText;
    private int ROOM_ID;
    private Methods methods;
    private ArrayList<Message> arrayList_message;
    private SearchMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_message);

        arrayList_message = new ArrayList<>();

        methods = new Methods(this);

        Intent intent = getIntent();

        if(intent != null){
            mSearchText = intent.getStringExtra("search_text");
            ROOM_ID = intent.getIntExtra("room_id", 0);
        }

        AnhXa();
        GetSearchMessage();
    }

    private void AnhXa(){
        recyclerView = findViewById(R.id.recyclerview);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchMessageActivity.super.onBackPressed();
            }
        });
        tv_ketqua = findViewById(R.id.tv_ketqua);
    }

    private void GetSearchMessage(){
        Bundle bundle = new Bundle();
        bundle.putString("text", mSearchText);
        bundle.putInt("room_id", ROOM_ID);

        RequestBody requestBody = methods.getRequestBody("method_search_message", bundle, null);

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

                        String text = arrayList_message.size() + " kết quả liên quan đến '" + mSearchText + "'";
                        tv_ketqua.setText(text);

                        SetAdapter();
                    }else {
                        Toast.makeText(SearchMessageActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(SearchMessageActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        LoadMessagesAsync async = new LoadMessagesAsync(requestBody, listener);
        async.execute();
    }

    private void SetAdapter() {
        adapter = new SearchMessageAdapter(arrayList_message);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}