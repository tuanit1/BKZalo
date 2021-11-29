package com.example.bkzalo.activitiy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
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

public class ProfileUserActivity extends AppCompatActivity {

    private LinearLayout ll1;
    private LinearLayout ll2;
    private LinearLayout ll_search_list;
    private RecyclerView rv_friend_list;
    private ArrayList<User> array_user;
    private TextView tv_user_bio;
    private TextView tv_user_name;
    private ImageView iv_user_image;
    private EditText et_tim_kiem;
    private Button btn_friend;
    private Button btn_acpt;
    private Button btn_refu;
    private Button btn_mess;
    private LinearLayout view_friend;
    private LinearLayout request_friend;
    private int CURRENT_MODE;
    private final int REQUEST_MODE = 1;
    private final int REQUESTED_MODE = 2;
    private final int FRIEND_MODE = 3;
    private final int STRANGER_MODE = 4;
    private Methods methods;
    private int mUID;
    private String Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);

        array_user = new ArrayList<>();
        methods = new Methods(this);
        AnhXa();

        Intent intent = getIntent();

        if(intent != null){
            mUID = intent.getIntExtra("uid", 0);
            CURRENT_MODE = intent.getIntExtra("mode", 0);
        }

        GetUserProfile();

    }

    private void AnhXa() {
        rv_friend_list = findViewById(R.id.rv_friend_list);
        ll1 = findViewById(R.id.ll1);
        ll2 = findViewById(R.id.ll2);
        ll_search_list = findViewById(R.id.ll_search_list);
        tv_user_bio = findViewById(R.id.tv_user_bio);
        tv_user_name = findViewById(R.id.tv_user_name);
        et_tim_kiem = findViewById(R.id.et_tim_kiem);
        iv_user_image = findViewById(R.id.iv_user_image);
        request_friend = findViewById(R.id.request_friend);
        view_friend = findViewById(R.id.view_friend);
        btn_friend = findViewById(R.id.btn_friend);
        btn_acpt = findViewById(R.id.btn_acpt);
        btn_mess = findViewById(R.id.btn_mess);
        btn_refu = findViewById(R.id.btn_refu);

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

        rv_friend_list.setLayoutManager(new LinearLayoutManager(ProfileUserActivity.this, RecyclerView.VERTICAL, false));
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
                        Toast.makeText(ProfileUserActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ProfileUserActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
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

                        Intent intent = new Intent(ProfileUserActivity.this, ProfileUserActivity.class);

                        intent.putExtra("uid", muid);

                        switch (Status){
                            case "friend":
                                intent.putExtra("mode", FRIEND_MODE);
                                break;
                            case "stranger":
                                intent.putExtra("mode", STRANGER_MODE);
                                break;
                            case "request":
                                if(relationship.getRequseter() == Constant.UID){
                                    intent.putExtra("mode", REQUESTED_MODE);
                                }else {
                                    intent.putExtra("mode", REQUEST_MODE);
                                }
                                break;
                        }

                        startActivity(intent);

                        Toast.makeText(ProfileUserActivity.this, "Lấy status thành công!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ProfileUserActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ProfileUserActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetRelationshipAsync async = new GetRelationshipAsync(requestBody, listener);
        async.execute();
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
                        Toast.makeText(ProfileUserActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ProfileUserActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
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

        switch (CURRENT_MODE){
            case REQUEST_MODE:
                request_friend.setVisibility(View.VISIBLE);
                view_friend.setVisibility(View.GONE);

                btn_acpt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UpdateRelationship(mUID);

                        CURRENT_MODE = FRIEND_MODE;
                        SetUIFRIEND();
                    }
                });

                btn_refu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RefuseRelationship(mUID);

                        CURRENT_MODE = STRANGER_MODE;
                        SetUIStranger();
                    }
                });
                break;
            case REQUESTED_MODE:
                SetUIRequested();
                break;
            case FRIEND_MODE:
                SetUIFRIEND();
                break;
            case STRANGER_MODE:
                SetUIStranger();
                break;
        }

        btn_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (CURRENT_MODE){
                    case REQUESTED_MODE:
                        CancelFriend(mUID);

                        CURRENT_MODE = STRANGER_MODE;
                        SetUIStranger();

                        break;
                    case FRIEND_MODE:
                        UnFriend(mUID);

                        CURRENT_MODE = STRANGER_MODE;
                        SetUIStranger();

                        break;
                    case STRANGER_MODE:
                        AddFriend(mUID);

                        CURRENT_MODE = REQUESTED_MODE;
                        SetUIRequested();

                        break;
                }
            }
        });
    }

    private void SetUIRequested(){
        request_friend.setVisibility(View.GONE);
        view_friend.setVisibility(View.VISIBLE);
        btn_friend.setText("Hủy lời mời");
        btn_friend.setBackground(this.getResources().getDrawable(R.drawable.botron_button));
        btn_friend.setTextColor(Color.WHITE);
    }

    private void SetUIFRIEND(){
        request_friend.setVisibility(View.GONE);
        view_friend.setVisibility(View.VISIBLE);
        btn_friend.setText("Bạn bè");
        btn_friend.setBackground(this.getResources().getDrawable(R.drawable.botron_edittext));
        btn_friend.setTextColor(Color.parseColor("#3D5A80"));
    }

    private void SetUIStranger(){
        request_friend.setVisibility(View.GONE);
        view_friend.setVisibility(View.VISIBLE);
        btn_friend.setText("Kết bạn");
        btn_friend.setBackground(this.getResources().getDrawable(R.drawable.botron_button));
        btn_friend.setTextColor(Color.WHITE);
    }

    private void AddFriend(int muid){
        Bundle bundle = new Bundle();
        bundle.putInt("uid", Constant.UID);
        bundle.putInt("uid2", muid);

        RequestBody requestBody = methods.getRequestBody("method_add_friend", bundle, null);

        ExecuteQueryListenerHuong listener = new ExecuteQueryListenerHuong() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Toast.makeText(ProfileUserActivity.this, "Đã gửi lời mời kết bạn!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ProfileUserActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ProfileUserActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExcecuteQueryAsyncHuong async = new ExcecuteQueryAsyncHuong(requestBody, listener);
        async.execute();
    }

    private void CancelFriend(int muid){
        Bundle bundle = new Bundle();
        bundle.putInt("uid", Constant.UID);
        bundle.putInt("uid2", muid);

        RequestBody requestBody = methods.getRequestBody("method_cancel_friend", bundle, null);

        ExecuteQueryListenerHuong listener = new ExecuteQueryListenerHuong() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Toast.makeText(ProfileUserActivity.this, "Đã thu hồi lời mời kết bạn!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ProfileUserActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ProfileUserActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExcecuteQueryAsyncHuong async = new ExcecuteQueryAsyncHuong(requestBody, listener);
        async.execute();
    }

    private void UnFriend(int muid){
        Bundle bundle = new Bundle();
        bundle.putInt("uid", Constant.UID);
        bundle.putInt("uid2", muid);

        RequestBody requestBody = methods.getRequestBody("method_unfriend", bundle, null);

        ExecuteQueryListenerHuong listener = new ExecuteQueryListenerHuong() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Toast.makeText(ProfileUserActivity.this, "Hủy kết bạn thành công!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ProfileUserActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ProfileUserActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExcecuteQueryAsyncHuong async = new ExcecuteQueryAsyncHuong(requestBody, listener);
        async.execute();
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
                        Toast.makeText(ProfileUserActivity.this, "Kết bạn thành công!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ProfileUserActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ProfileUserActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ProfileUserActivity.this, "Từ chối thành công!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(ProfileUserActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ProfileUserActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExcecuteQueryAsyncHuong async = new ExcecuteQueryAsyncHuong(requestBody, listener);
        async.execute();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
}