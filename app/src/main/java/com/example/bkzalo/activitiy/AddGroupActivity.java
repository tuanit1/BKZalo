package com.example.bkzalo.activitiy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.example.bkzalo.asynctasks.LoadListFriendAsync;
import com.example.bkzalo.listeners.LoadListFriendListener;
import com.example.bkzalo.listeners.UserSelectListener;
import com.example.bkzalo.models.User;
import com.example.bkzalo.models.UserSelect;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;
import com.example.bkzalo.utils.PathUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import okhttp3.RequestBody;

public class AddGroupActivity extends AppCompatActivity {

    private ImageView iv_back, iv_image;
    private RecyclerView rv_friend;
    private EditText edt_name, edt_search;
    private TextView tv_xacnhan, tv_delete_img;
    private Methods methods;
    private ArrayList<User> arrayList_friend;
    private ArrayList<UserSelect> arrayList_checklist;
    private final int PICK_IMAGE_CODE = 1;
    private boolean isChangeImage = false;
    private Uri picked_image;
    private File file_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        AnhXa();

        methods = new Methods(this);

        arrayList_friend = new ArrayList<>();
        arrayList_checklist = new ArrayList<>();

        LoadListFriend();
    }

    private void AnhXa() {
        iv_image = findViewById(R.id.iv_image);
        iv_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_IMAGE_CODE);
            }
        });
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGroupActivity.super.onBackPressed();
            }
        });

        edt_name = findViewById(R.id.edt_name);
        edt_search = findViewById(R.id.edt_search);
        rv_friend = findViewById(R.id.rv_friend);
        tv_xacnhan = findViewById(R.id.tv_xacnhan);
        tv_xacnhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddGroup();
            }
        });
        tv_delete_img = findViewById(R.id.tv_delete_img);
        tv_delete_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChangeImage = false;

                Picasso.get()
                        .load(R.drawable.ic_image)
                        .into(iv_image);

                tv_delete_img.setVisibility(View.GONE);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                picked_image = data.getData();

                try{
                    String filePath = PathUtil.getPath(this, picked_image);
                    file_image = new File(filePath);
                }catch (Exception e){
                    Toast.makeText(this, "Không thể sử dụng ảnh này, vui lòng chọn lại!", Toast.LENGTH_SHORT).show();
                    return;
                }

                isChangeImage = true;

                Picasso.get()
                        .load(picked_image)
                        .into(iv_image);

                tv_delete_img.setVisibility(View.VISIBLE);
            }
        }
    }

    private void AddGroup() {

        if(edt_name.getText().toString().isEmpty()){
            edt_name.setError("Vui lòng nhập tên nhóm!");
            return;
        }

        ArrayList<UserSelect> arrayList_selected = new ArrayList<>();

        for(UserSelect u : arrayList_checklist){
            if(u.isChecked()){
                arrayList_selected.add(u);
            }
        }

        if(arrayList_selected.size() < 2){
            Toast.makeText(this, "Vui lòng chọn tối thiểu 2 thành viên", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<Integer> arrayList_id = new ArrayList<>();

        for(UserSelect u : arrayList_selected){
            arrayList_id.add(u.getId());
        }

        String json_member_id = new Gson().toJson(arrayList_id);

        Bundle bundle = new Bundle();
        bundle.putString("json_member_id", json_member_id);
        bundle.putString("group_name", edt_name.getText().toString());
        bundle.putBoolean("is_change_image", isChangeImage);
        bundle.putInt("admin_id", Constant.UID);

    }

    private void LoadListFriend(){
        Bundle bundle = new Bundle();
        bundle.putInt("uid", Constant.UID);

        RequestBody requestBody = methods.getRequestBody("method_get_list_friend", bundle, null);

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
                        Toast.makeText(AddGroupActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AddGroupActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
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