/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:15 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 11/29/21, 10:20 PM
 * /
 */

package com.example.bkzalo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bkzalo.R;
import com.example.bkzalo.activitiy.BlockActivity;
import com.example.bkzalo.activitiy.EditProfileActivity;
import com.example.bkzalo.activitiy.ProfileUserActivity;
import com.example.bkzalo.adapters.BlockListAdapter;
import com.example.bkzalo.adapters.RequestListAdapter;
import com.example.bkzalo.adapters.UserListAdapter;
import com.example.bkzalo.asynctasks.ExcecuteQueryAsyncHuong;
import com.example.bkzalo.asynctasks.GetProfileBlockListAsync;
import com.example.bkzalo.asynctasks.GetProfileRequestListAsync;
import com.example.bkzalo.asynctasks.GetProfileUserAsync;
import com.example.bkzalo.asynctasks.GetProfileUserListAsync;
import com.example.bkzalo.asynctasks.GetRelationshipAsync;
import com.example.bkzalo.listeners.ClickItemBlockListener;
import com.example.bkzalo.listeners.ClickItemRequestListener;
import com.example.bkzalo.listeners.ClickItemUserListener;
import com.example.bkzalo.listeners.ExecuteQueryListenerHuong;
import com.example.bkzalo.listeners.GetProfileBlockListener;
import com.example.bkzalo.listeners.GetProfileRequestListener;
import com.example.bkzalo.listeners.GetProfileUserListListener;
import com.example.bkzalo.listeners.GetProfileUserListener;
import com.example.bkzalo.listeners.GetRelationshipListener;
import com.example.bkzalo.models.Relationship;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;
import com.example.bkzalo.utils.PathUtil;
import com.example.bkzalo.utils.PathUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import okhttp3.RequestBody;

public class FragmentProfile extends Fragment{

    private View view;
    private LinearLayout ll1;
    private LinearLayout ll2;
    private LinearLayout ll_search_list;
    private Button btn_editprofile;
    private TextView tv_user_bio;
    private TextView tv_user_name;
    private ImageView iv_user_image;
    private RecyclerView rv_friend;
    private RecyclerView rv_friend_list;
    private EditText et_tim_kiem;
    private BottomNavigationView menu_user_profile;
    private ArrayList<User> array_user;
    private ArrayList<User> array_request;
    private ArrayList<User> array_block;
    private Methods methods;
    private String Status;
    private final int RELOAD_REQUEST_LIST_CODE = 10;
    private final int RELOAD_BLOCK_LIST_CODE = 12;
    private final int REQUEST_MODE = 1;
    private final int REQUESTED_MODE = 2;
    private final int FRIEND_MODE = 3;
    private final int STRANGER_MODE = 4;
    private final  int PICK_IMAGE_CODE = 13;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        methods = new Methods(getContext());

        array_user = new ArrayList<>();
        array_request = new ArrayList<>();
        array_block = new ArrayList<>();

        AnhXa();

        GetUser();

        GetRequestList();

        return view;
    }

    private void GetUser() {

        //requestbody + listener = asynctask

        int uid = Constant.UID;

        Bundle bundle = new Bundle();
        bundle.putInt("uid", uid);

        RequestBody requestBody = methods.getRequestBody("method_get_profile_user", bundle, null);

        GetProfileUserListener listener = new GetProfileUserListener() {
            @Override
            public void onStart() {
                //hiện progressbar
            }

            @Override
            public void onEnd(boolean status, User user) {
                if(methods.isNetworkConnected()){
                    if(status){
                        UpdateUI(user);
                    }else {
                        Toast.makeText(getContext(), "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetProfileUserAsync async = new GetProfileUserAsync(requestBody, listener);
        async.execute();

    }

    private void UpdateUI(User user) {
        tv_user_name.setText(user.getName());
        tv_user_bio.setText(user.getBio());

        String image_path = user.getImage_url();

        if(!image_path.isEmpty()){
            Picasso.get()
                    .load(image_path)
                    .placeholder(R.drawable.image_user_holder)
                    .error(R.drawable.message_placeholder_ic)
                    .into(iv_user_image);
        }else{
            Picasso.get()
                    .load(R.drawable.message_placeholder_ic)
                    .into(iv_user_image);
        }

    }

    private void AnhXa(){
        ll1 = view.findViewById(R.id.ll1);
        ll2 = view.findViewById(R.id.ll2);
        ll_search_list = view.findViewById(R.id.ll_search_list);
        rv_friend = view.findViewById(R.id.rv_friend);
        rv_friend_list = view.findViewById(R.id.rv_friend_list);
        tv_user_bio = view.findViewById(R.id.tv_user_bio);
        tv_user_name = view.findViewById(R.id.tv_user_name);
        iv_user_image = view.findViewById(R.id.iv_user_image);
//        iv_user_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_PICK);
//                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_CODE);
//            }
//        });

        et_tim_kiem = view.findViewById(R.id.et_tim_kiem);

//        et_tim_kiem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ll1.setVisibility(View.INVISIBLE);
//                ll2.setVisibility(View.INVISIBLE);
//                GetUserList();
//            }
//        });

        et_tim_kiem.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    GetUserList();
                }else {
                    array_user.clear();
                }
            }
        });

        et_tim_kiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(s.toString().isEmpty()){
                    ll1.setVisibility(View.VISIBLE);
                    ll2.setVisibility(View.VISIBLE);
                    ll_search_list.setVisibility(View.GONE);
                }else {
                    ll1.setVisibility(View.GONE);
                    ll2.setVisibility(View.GONE);
                    ll_search_list.setVisibility(View.VISIBLE);

                    filter(s.toString());
                }
            }
        });
        //anh xa cho cai btn tu cai id ben xml
        btn_editprofile = view.findViewById(R.id.btn_editproflie);
        //gio tao su kien cho no
        btn_editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //roi viet hanh dong mo man hinh kia len
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        menu_user_profile = view.findViewById(R.id.menu_user_profile);
        menu_user_profile.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();

                switch (id){
                    case R.id.user_friend:

                        GetRequestList();
                        break;
                    case R.id.user_block:
                        GetBlockList();
                        break;
                }

                return true;
            }
        });
    }

    private void filter(String s){
        ArrayList<User> filteredList = new ArrayList<>();

        for (User user : array_user) {
            if(tryParseInt(s)) {
                if(user.getPhone().toLowerCase().contains(s.toLowerCase())){
                    filteredList.add(user);
                }
            }
            else{
                if(user.getName().toLowerCase().contains(s.toLowerCase())){
                    filteredList.add(user);
                }
            }
        }

        ClickItemUserListener listener = new ClickItemUserListener() {
            @Override
            public void onClick(int uid) {

                GetRelationship(uid);
            }
        };

        UserListAdapter adapter = new UserListAdapter(filteredList, listener);

        rv_friend_list.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rv_friend_list.setAdapter(adapter);
    }

    private boolean tryParseInt(String s) {
        try{
            int n = Integer.parseInt(s);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void GetRequestList(){
        Bundle bundle = new Bundle();
        bundle.putInt("uid", Constant.UID);

        RequestBody requestBody = methods.getRequestBody("method_get_request_list", bundle, null);

        GetProfileRequestListener listener = new GetProfileRequestListener() {
            @Override
            public void onStart() {
                array_request.clear();
            }

            @Override
            public void onEnd(boolean status, ArrayList<User> arrayList_user) {
                if(methods.isNetworkConnected()){
                    if(status){

                        array_request.addAll(arrayList_user);

                        SetAdapter();
                    }else {
                        Toast.makeText(getContext(), "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetProfileRequestListAsync async = new GetProfileRequestListAsync(requestBody, listener);
        async.execute();
    }

    private void GetBlockList(){
        Bundle bundle = new Bundle();
        bundle.putInt("uid", Constant.UID);

        RequestBody requestBody = methods.getRequestBody("method_get_block_list", bundle, null);

        GetProfileBlockListener listener = new GetProfileBlockListener() {
            @Override
            public void onStart() {
                array_block.clear();
            }

            @Override
            public void onEnd(boolean status, ArrayList<User> arrayList_user) {
                if(methods.isNetworkConnected()){
                    if(status){

                        array_block.addAll(arrayList_user);

                        SetAdapterBlock();
                    }else {
                        Toast.makeText(getContext(), "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetProfileBlockListAsync async = new GetProfileBlockListAsync(requestBody, listener);
        async.execute();
    }

    private void GetUserList(){
        Bundle bundle = new Bundle();
        bundle.putInt("uid", Constant.UID);

        RequestBody requestBody = methods.getRequestBody("method_get_user_list", bundle, null);

        GetProfileUserListListener listener = new GetProfileUserListListener() {
            @Override
            public void onStart() {
                array_user.clear();
            }

            @Override
            public void onEnd(boolean status, ArrayList<User> arrayList_user) {
                if(methods.isNetworkConnected()){
                    if(status){
                        array_user.addAll(arrayList_user);
                    }else {
                        Toast.makeText(getContext(), "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetProfileUserListAsync async = new GetProfileUserListAsync(requestBody, listener);
        async.execute();
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

                        if(relationship != null){
                            Status = relationship.getStatus();
                        }else {
                            Status = "stranger";
                        }

                        Intent intent = new Intent(getContext(), ProfileUserActivity.class);

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

                        startActivityForResult(intent, RELOAD_REQUEST_LIST_CODE);

                        Toast.makeText(getContext(), "Lấy status thành công!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetRelationshipAsync async = new GetRelationshipAsync(requestBody, listener);
        async.execute();
    }


    private void SetAdapter() {

        ClickItemRequestListener listener = new ClickItemRequestListener() {
            @Override
            public void onClick(int uid) {
                Intent intent = new Intent(getContext(), ProfileUserActivity.class);

                intent.putExtra("uid", uid);
                intent.putExtra("mode", REQUEST_MODE);

                startActivityForResult(intent, RELOAD_REQUEST_LIST_CODE);
            }
            public void onAccept(int uid){
                UpdateRelationship(uid);
                GetRequestList();
            }
            public void onRefuse(int uid){
                RefuseRelationship(uid);
                GetRequestList();
            }
        };

        RequestListAdapter adapter = new RequestListAdapter(array_request, listener);

        rv_friend.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rv_friend.setAdapter(adapter);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){

            switch (requestCode){
                case PICK_IMAGE_CODE:

                    Uri uri = data.getData();

                    File file;

                    try{
                        String filePath = PathUtil.getPath(getContext(), uri);
                        file = new File(filePath);

                        UpdateImage(file, uri);

                    }catch (Exception e){
                        Toast.makeText(getContext(), "Không thể sử dụng ảnh này, vui lòng chọn lại!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    break;
                case RELOAD_BLOCK_LIST_CODE:

                    GetBlockList();

                    break;
                case RELOAD_REQUEST_LIST_CODE:

                    GetRequestList();

                    break;
            }

        }

    }

    private void UpdateImage(File file, Uri uri) {

        Bundle bundle = new Bundle();
        bundle.putInt("uid", Constant.UID);

        RequestBody requestBody = methods.getRequestBody("method_update_profile_image", bundle, file);

        ExecuteQueryListenerHuong listener = new ExecuteQueryListenerHuong() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Toast.makeText(getContext(), "Cập nhập ảnh thành công!", Toast.LENGTH_SHORT).show();

                        Picasso.get()
                                .load(uri)
                                .into(iv_user_image);

                    }else {
                        Toast.makeText(getContext(), "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExcecuteQueryAsyncHuong async = new ExcecuteQueryAsyncHuong(requestBody, listener);
        async.execute();

    }

    private void SetAdapterBlock() {

        ClickItemBlockListener listener = new ClickItemBlockListener() {
            @Override
            public void onClick(int uid) {
                Intent intent = new Intent(getContext(), BlockActivity.class);
                intent.putExtra("uid", uid);
                startActivityForResult(intent, RELOAD_BLOCK_LIST_CODE);
            }

            public  void onUnBlock(int uid) {
                UnBlockRelationship(uid);
                GetBlockList();
            }
        };

        BlockListAdapter adapter = new BlockListAdapter(array_block, listener);

        rv_friend.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rv_friend.setAdapter(adapter);

    }
    private void UpdateRelationship(int muid) {
        Bundle bundle = new Bundle();
        bundle.putInt("uid", Constant.UID);
        bundle.putInt("uid2", muid);

        RequestBody requestBody = methods.getRequestBody("method_accept_friend", bundle, null);

        ExecuteQueryListenerHuong listener = new ExecuteQueryListenerHuong() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Toast.makeText(getContext(), "Kết bạn thành công!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExcecuteQueryAsyncHuong async = new ExcecuteQueryAsyncHuong(requestBody, listener);
        async.execute();
    }

    private void RefuseRelationship(int muid) {
        Bundle bundle = new Bundle();
        bundle.putInt("uid", Constant.UID);
        bundle.putInt("uid2", muid);

        RequestBody requestBody = methods.getRequestBody("method_refuse_friend", bundle, null);

        ExecuteQueryListenerHuong listener = new ExecuteQueryListenerHuong() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Toast.makeText(getContext(), "Từ chối thành công!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExcecuteQueryAsyncHuong async = new ExcecuteQueryAsyncHuong(requestBody, listener);
        async.execute();
    }

    private void UnBlockRelationship(int muid) {
        Bundle bundle = new Bundle();
        bundle.putInt("uid", Constant.UID);
        bundle.putInt("uid2", muid);

        RequestBody requestBody = methods.getRequestBody("method_unblock_friend", bundle, null);

        ExecuteQueryListenerHuong listener = new ExecuteQueryListenerHuong() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Toast.makeText(getContext(), "Bỏ chặn thành công!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExcecuteQueryAsyncHuong async = new ExcecuteQueryAsyncHuong(requestBody, listener);
        async.execute();
    }

}