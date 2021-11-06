package com.example.bkzalo.activitiy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.adapters.SelectUserApdater;
import com.example.bkzalo.asynctasks.ExecuteQueryAsync;
import com.example.bkzalo.asynctasks.LoadListFriendAsync;
import com.example.bkzalo.listeners.ExecuteQueryListener;
import com.example.bkzalo.listeners.LoadListFriendListener;
import com.example.bkzalo.listeners.UserSelectListener;
import com.example.bkzalo.models.User;
import com.example.bkzalo.models.UserSelect;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;
import com.google.gson.Gson;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class AddMemberActivity extends AppCompatActivity {

    private ImageView iv_back;
    private TextView tv_xacnhan;
    private EditText edt_search;
    private RecyclerView rv_friend;
    private Methods methods;
    private ArrayList<User> arrayList_friend;
    private ArrayList<UserSelect> arrayList_checklist;
    private int ROOM_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        methods = new Methods(this);
        arrayList_friend = new ArrayList<>();
        arrayList_checklist = new ArrayList<>();

        Intent intent = getIntent();
        if(intent != null){
            ROOM_ID = intent.getIntExtra("room_id", 0);
        }

        AnhXa();
        LoadListFriend();
    }

    private void AnhXa(){
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMemberActivity.super.onBackPressed();
            }
        });
        tv_xacnhan = findViewById(R.id.tv_xacnhan);
        tv_xacnhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMember();
            }
        });
        edt_search = findViewById(R.id.edt_search);
        rv_friend = findViewById(R.id.rv_friend);
    }

    private void AddMember() {

        ArrayList<UserSelect> arrayList_selected = new ArrayList<>();

        for(UserSelect u : arrayList_checklist){
            if(u.isChecked()){
                arrayList_selected.add(u);
            }
        }

        if(arrayList_selected.size() < 1){
            Toast.makeText(this, "Vui lòng chọn tối thiểu 1 thành viên", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Integer> arrayList_id = new ArrayList<>();

        for(UserSelect u : arrayList_selected){
            arrayList_id.add(u.getId());
        }

        String json_member_id = new Gson().toJson(arrayList_id);

        Bundle bundle = new Bundle();
        bundle.putString("json_member_id", json_member_id);
        bundle.putInt("user_id", Constant.UID);
        bundle.putInt("room_id", ROOM_ID);

        RequestBody requestBody = methods.getRequestBody("method_add_member", bundle, null);

        ExecuteQueryListener listener = new ExecuteQueryListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(status){
                    Toast.makeText(AddMemberActivity.this, "Thêm thành viên thành công!", Toast.LENGTH_SHORT).show();
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    finish();
                }else {
                    Toast.makeText(AddMemberActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExecuteQueryAsync async = new ExecuteQueryAsync(requestBody, listener);
        async.execute();
    }

    private void LoadListFriend(){
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", Constant.UID);
        bundle.putInt("room_id", ROOM_ID);

        RequestBody requestBody = methods.getRequestBody("method_get_group_unmember", bundle, null);

        LoadListFriendListener listener = new LoadListFriendListener() {
            @Override
            public void onStart() {
                arrayList_friend.clear();
            }

            @Override
            public void onEnd(boolean status, ArrayList<User> array_user) {
                if(methods.isNetworkConnected()){
                    if(status){
                        arrayList_friend.addAll(array_user);
                        SetAdapter();
                    }else {
                        Toast.makeText(AddMemberActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AddMemberActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        LoadListFriendAsync async = new LoadListFriendAsync(requestBody, listener);
        async.execute();
    }

    private void SetAdapter() {

        for(User u : arrayList_friend){
            arrayList_checklist.add(new UserSelect(u.getId(), u.getName(), u.getPhone(), u.getImage(), false));
        }

        UserSelectListener listener = new UserSelectListener() {
            @Override
            public void onClick(int user_id, boolean isChecked) {
                for(UserSelect u : arrayList_checklist){
                    if(u.getId() == user_id){
                        u.setChecked(isChecked);
                    }
                }
            }
        };

        SelectUserApdater adapter = new SelectUserApdater(arrayList_checklist, listener,this);

        rv_friend.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv_friend.setHasFixedSize(true);
        rv_friend.setAdapter(adapter);

        ArrayList<UserSelect> arrayList_search = new ArrayList<>();

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                arrayList_search.clear();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for(UserSelect u : arrayList_checklist){
                    if(u.getName().toLowerCase().contains(s.toString().toLowerCase())
                            || u.getPhone().contains(s.toString())){
                        arrayList_search.add(u);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.setAdapterData(arrayList_search);
                adapter.notifyDataSetChanged();
            }
        });
    }
}