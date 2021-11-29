package com.example.bkzalo.activitiy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.adapters.UserListAdapter;
import com.example.bkzalo.asynctasks.ExcecuteQueryAsyncHuong;
import com.example.bkzalo.asynctasks.GetProfileUserAsync;
import com.example.bkzalo.asynctasks.GetProfileUserListAsync;
import com.example.bkzalo.asynctasks.GetRelationshipAsync;
import com.example.bkzalo.listeners.ClickItemUserListener;
import com.example.bkzalo.listeners.ExecuteQueryListenerHuong;
import com.example.bkzalo.listeners.GetProfileUserListListener;
import com.example.bkzalo.listeners.GetProfileUserListener;
import com.example.bkzalo.listeners.GetRelationshipListener;
import com.example.bkzalo.models.Relationship;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class BlockActivity extends AppCompatActivity {
    
    private final int REQUEST_MODE = 1;
    private final int REQUESTED_MODE = 2;
    private final int FRIEND_MODE = 3;
    private final int STRANGER_MODE = 4;
    private LinearLayout ll1;
    private LinearLayout ll2;
    private LinearLayout ll_search_list;
    private RecyclerView rv_friend_list;
    private EditText et_tim_kiem;
    private ArrayList<User> array_user;
    private TextView tv_user_bio;
    private TextView tv_user_name;
    private ImageView iv_user_image;
    private Button btn_unblock;
    private Methods methods;
    private int mUID = 0;
    private String Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);

        array_user = new ArrayList<>();

        AnhXa();

        Intent intent = getIntent();

        if(intent != null){
            mUID = intent.getIntExtra("uid", 0);
        }

        methods = new Methods(this);
        GetUserProfile();
    }

    private void GetUserProfile() {
        int uid = mUID;

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
                        Toast.makeText(BlockActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(BlockActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetProfileUserAsync async = new GetProfileUserAsync(requestBody, listener);
        async.execute();
    }

    private void UpdateUI(User user) {
        tv_user_name.setText(user.getName());
        tv_user_bio.setText(user.getBio());

        //String im = "http://192.168.1.9/bkzalo/image/image_user/tuan.jpg";
        String image_url = Constant.SERVER_URL + "image/image_user/" + user.getImage();

        Picasso.get()
                .load(image_url)
                .into(iv_user_image);
    }
    private void AnhXa(){
        rv_friend_list = findViewById(R.id.rv_friend_list);
        ll1 = findViewById(R.id.ll1);
        ll2 = findViewById(R.id.ll2);
        ll_search_list = findViewById(R.id.ll_search_list);
        tv_user_bio = findViewById(R.id.tv_user_bio);
        tv_user_name = findViewById(R.id.tv_user_name);
        iv_user_image = findViewById(R.id.iv_user_image);

        btn_unblock = findViewById(R.id.btn_unblock);
        btn_unblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UnBlockRelationship(mUID);

                Intent intent_reload = new Intent();
                setResult(RESULT_OK, intent_reload);

                Intent intent = new Intent(BlockActivity.this, ProfileUserActivity.class);
                intent.putExtra("uid", mUID);
                intent.putExtra("mode", STRANGER_MODE);

                startActivity(intent);
                finish();
            }
        });

        et_tim_kiem = findViewById(R.id.et_tim_kiem);
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

        rv_friend_list.setLayoutManager(new LinearLayoutManager(BlockActivity.this, RecyclerView.VERTICAL, false));
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
                        Toast.makeText(BlockActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(BlockActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetProfileUserListAsync async = new GetProfileUserListAsync(requestBody, listener);
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
                        Toast.makeText(BlockActivity.this, "Bỏ chặn thành công!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(BlockActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(BlockActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExcecuteQueryAsyncHuong async = new ExcecuteQueryAsyncHuong(requestBody, listener);
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

                        Intent intent = new Intent(BlockActivity.this, ProfileUserActivity.class);

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

                        Toast.makeText(BlockActivity.this, "Lấy status thành công!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(BlockActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(BlockActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetRelationshipAsync async = new GetRelationshipAsync(requestBody, listener);
        async.execute();
    }
}