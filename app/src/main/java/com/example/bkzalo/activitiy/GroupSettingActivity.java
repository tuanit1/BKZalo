package com.example.bkzalo.activitiy;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.asynctasks.ExecuteQueryAsync;
import com.example.bkzalo.asynctasks.LoadGetRoomAsync;
import com.example.bkzalo.listeners.ExecuteQueryListener;
import com.example.bkzalo.listeners.GetRoomListener;
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.Room;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;
import com.example.bkzalo.utils.PathUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.RequestBody;

public class GroupSettingActivity extends AppCompatActivity {

    private int ROOM_ID;
    private String mType;
    private ImageView iv_back, iv_user_image;
    private TextView tv_group_name, tv_img_name_group;
    private LinearLayout ll_other_all, ll_private_all, ll_group_all;
    private RelativeLayout ll_gr_edit_name, ll_gr_add_member, ll_gr_view_member, ll_gr_leave, ll_gr_edit_image;
    private RelativeLayout ll_pr_profile, ll_pr_block;
    private RelativeLayout ll_oth_search_msg, ll_oth_see_image, ll_oth_change_bgrd, ll_oth_change_nickname, ll_oth_delete_msg;
    private CardView cv_group_icon;
    private Methods methods;
    private Participant mParticipant;
    private final int PICK_IMAGE_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_setting);

        methods = new Methods(this);

        Intent intent = getIntent();
        if(intent != null){
            ROOM_ID = intent.getIntExtra("room_id", 0);
            mType = intent.getStringExtra("type");
        }

        AnhXa();
        LoadGetRoom();
    }

    private void AnhXa(){
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();;
            }
        });
        cv_group_icon = findViewById(R.id.cv_group_icon);
        iv_user_image = findViewById(R.id.iv_user_image);
        tv_group_name = findViewById(R.id.tv_group_name);
        tv_img_name_group = findViewById(R.id.tv_img_name_group);
        ll_group_all = findViewById(R.id.ll_group_all);
        ll_private_all = findViewById(R.id.ll_private_all);
        ll_other_all = findViewById(R.id.ll_other_all);
        ll_gr_edit_image = findViewById(R.id.ll_gr_edit_image);
        ll_gr_edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mType.equals("group")){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_PICK);
                    startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_IMAGE_CODE);
                }
            }
        });
        ll_gr_edit_name = findViewById(R.id.ll_gr_edit_name);
        ll_gr_edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mType.equals("group")){
                    openDialogText("Đổi tên nhóm");
                }

            }
        });
        ll_gr_add_member = findViewById(R.id.ll_gr_add_member);
        ll_gr_view_member = findViewById(R.id.ll_gr_view_member);
        ll_gr_leave = findViewById(R.id.ll_gr_leave);
        ll_pr_profile = findViewById(R.id.ll_pr_profile);
        ll_pr_block = findViewById(R.id.ll_pr_block);
        ll_oth_search_msg = findViewById(R.id.ll_oth_search_msg);
        ll_oth_see_image = findViewById(R.id.ll_oth_see_image);
        ll_oth_change_bgrd = findViewById(R.id.ll_oth_change_bgrd);
        ll_oth_change_nickname = findViewById(R.id.ll_oth_change_nickname);
        ll_oth_delete_msg = findViewById(R.id.ll_oth_delete_msg);
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
                        mParticipant = participant;
                        FirstSetup(room);
                    }else {
                        Toast.makeText(GroupSettingActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(GroupSettingActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        LoadGetRoomAsync async = new LoadGetRoomAsync(requestBody, listener);
        async.execute();
    }

    private void FirstSetup(Room room){

        String image = room.getImage();
        String name = room.getName();

        tv_group_name.setText(name);

        if(mType.equals("private")){

            cv_group_icon.setVisibility(View.GONE);
            iv_user_image.setVisibility(View.VISIBLE);
            ll_private_all.setVisibility(View.VISIBLE);
            ll_group_all.setVisibility(View.GONE);

            String image_path = Constant.SERVER_URL + "image/image_user/" + image;

            Picasso.get()
                    .load(image_path)
                    .placeholder(R.drawable.message_placeholder_ic)
                    .into(iv_user_image);
        }else {

            ll_private_all.setVisibility(View.GONE);
            ll_group_all.setVisibility(View.VISIBLE);

            if(!image.equals("")){
                //group with image
                cv_group_icon.setVisibility(View.GONE);
                iv_user_image.setVisibility(View.VISIBLE);

                String image_path = Constant.SERVER_URL + "image/image_room/" + image;

                Picasso.get()
                        .load(image_path)
                        .placeholder(R.drawable.message_placeholder_ic)
                        .into(iv_user_image);
            }else {
                //group with default image
                cv_group_icon.setVisibility(View.VISIBLE);
                iv_user_image.setVisibility(View.GONE);

                String[] arrayList_txt = name.toUpperCase().split(" ");

                if(arrayList_txt.length == 1){
                    String[] first = arrayList_txt[0].split("");
                    String first_char = first[0];

                    tv_group_name.setText(first_char);
                }else {
                    String[] first = arrayList_txt[0].split("");
                    String first_char = first[0];

                    String[] second = arrayList_txt[1].split("");
                    String second_char = second[0];

                    tv_img_name_group.setText(first_char+second_char);
                }
            }
        }

        ll_gr_add_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mType.equals("group")){
                    if(mParticipant.isAdmin()){
                        Intent intent = new Intent(GroupSettingActivity.this, AddMemberActivity.class);
                        intent.putExtra("room_id", ROOM_ID);
                        startActivityForResult(intent, 2);
                    }else {
                        Toast.makeText(GroupSettingActivity.this, "Chỉ quản trị viên có thể sử dụng chức năng này!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

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

                ChangeGroupImage(file);

            }
        }else if(requestCode == 2){

            if(resultCode == RESULT_OK){
                LoadGetRoom();
            }

        }
    }

    private void ChangeGroupImage(File file){
        Bundle bundle = new Bundle();
        bundle.putInt("room_id", ROOM_ID);
        bundle.putInt("user_id", Constant.UID);

        RequestBody requestBody = methods.getRequestBody("method_change_group_image", bundle, file);

        ExecuteQueryListener listener = new ExecuteQueryListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        LoadGetRoom();
                    }else {
                        Toast.makeText(GroupSettingActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(GroupSettingActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExecuteQueryAsync async = new ExecuteQueryAsync(requestBody, listener);
        async.execute();
    }


    private void openDialogText(String name) {
        Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_inputtext);
        EditText edt_dialog = dialog.findViewById(R.id.edt_dialog);
        TextView tv_dialog = dialog.findViewById(R.id.tv_dialog);
        Button btn_ok = dialog.findViewById(R.id.btn_ok);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edt_dialog.getText().toString().isEmpty()){

                    ChangeGroupName(edt_dialog.getText().toString());

                    dialog.dismiss();

                }else {
                    edt_dialog.setError("Vui lòng nhập đầy đủ!");
                }
            }
        });

        tv_dialog.setText(name);

        dialog.show();
    }

    private void ChangeGroupName(String name){
        Bundle bundle = new Bundle();
        bundle.putInt("room_id", ROOM_ID);
        bundle.putString("name", name);
        bundle.putInt("user_id", Constant.UID);

        RequestBody requestBody = methods.getRequestBody("method_change_group_name", bundle, null);

        ExecuteQueryListener listener = new ExecuteQueryListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        LoadGetRoom();
                    }else {
                        Toast.makeText(GroupSettingActivity.this, "Lỗi server!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(GroupSettingActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
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