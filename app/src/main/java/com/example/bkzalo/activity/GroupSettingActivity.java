package com.example.bkzalo.activity;

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
import com.example.bkzalo.asynctasks.SendMessageAsync;
import com.example.bkzalo.listeners.ExecuteQueryListener;
import com.example.bkzalo.listeners.GetRoomListener;
import com.example.bkzalo.listeners.SendMessageListener;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.Room;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.Methods;
import com.example.bkzalo.utils.PathUtil;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.RequestBody;

public class GroupSettingActivity extends AppCompatActivity {

    private int ROOM_ID;
    private int USER_ID;
    private String mType;
    private ImageView iv_back, iv_user_image;
    private TextView tv_group_name, tv_img_name_group;
    private LinearLayout ll_other_all, ll_private_all, ll_group_all;
    private RelativeLayout ll_gr_edit_name, ll_gr_add_member, ll_gr_view_member, ll_gr_leave, ll_gr_edit_image;
    private RelativeLayout ll_pr_profile, ll_pr_block;
    private RelativeLayout ll_oth_search_msg, ll_oth_see_image, ll_oth_hide, ll_oth_change_nickname, ll_oth_delete_msg;
    private CardView cv_group_icon;
    private Methods methods;
    private Participant mParticipant;
    private final int PICK_IMAGE_CODE = 1;
    private Socket socket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_setting);

        methods = new Methods(this);

        Intent intent = getIntent();
        if(intent != null){
            ROOM_ID = intent.getIntExtra("room_id", 0);
            mType = intent.getStringExtra("type");
            USER_ID = intent.getIntExtra("user_id", 0);
        }


        AnhXa();
        InitSocketIO();
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
        ll_gr_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mParticipant.isAdmin()){
                    CheckLastAdmin();
                }else {
                    OpenConfirmDialog("leave");
                }

            }
        });
        ll_pr_profile = findViewById(R.id.ll_pr_profile);
        ll_pr_block = findViewById(R.id.ll_pr_block);
        ll_pr_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mType.equals("private")) {
                    OpenConfirmDialog("block");
                }
            }
        });
        ll_oth_search_msg = findViewById(R.id.ll_oth_search_msg);
        ll_oth_search_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearchDialog();
            }
        });
        ll_oth_see_image = findViewById(R.id.ll_oth_see_image);
        ll_oth_see_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupSettingActivity.this, MessageImageActivity.class);
                intent.putExtra("room_id", ROOM_ID);

                startActivity(intent);
            }
        });
        ll_oth_hide = findViewById(R.id.ll_oth_hide);
        ll_oth_hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenConfirmDialog("hide");
            }
        });
        ll_oth_change_nickname = findViewById(R.id.ll_oth_change_nickname);
        ll_oth_change_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupSettingActivity.this, ChangeNicknameActivity.class);
                intent.putExtra("room_id", ROOM_ID);

                startActivityForResult(intent, 2);
            }
        });
        ll_oth_delete_msg = findViewById(R.id.ll_oth_delete_msg);
        ll_oth_delete_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenConfirmDialog("history");
            }
        });
    }

    private void HideRoom() {
        Bundle bundle = new Bundle();
        bundle.putInt("room_id", ROOM_ID);
        bundle.putInt("user_id", Constant.UID);

        RequestBody requestBody = methods.getRequestBody("method_hide_room", bundle, null);

        ExecuteQueryListener listener = new ExecuteQueryListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        if(mType.equals("private")){
                            Toast.makeText(GroupSettingActivity.this, "Đã ẩn người dùng này!", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(GroupSettingActivity.this, "Đã ẩn nhóm này!", Toast.LENGTH_SHORT).show();
                        }

                        Intent intent = new Intent(GroupSettingActivity.this, MainActivity.class);
                        startActivity(intent);

                    }else {
                        Toast.makeText(GroupSettingActivity.this, "Lỗi Server!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(GroupSettingActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExecuteQueryAsync async = new ExecuteQueryAsync(requestBody, listener);
        async.execute();
    }

    private void InitSocketIO(){
        try {
            socket = IO.socket(Constant.SERVER_NODE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on("onDeleteMember", onDeleteMember);

        socket.connect();

    }

    private void CheckLastAdmin(){
        Bundle bundle = new Bundle();
        bundle.putInt("room_id", ROOM_ID);

        RequestBody requestBody = methods.getRequestBody("method_check_last_admin", bundle, null);

        ExecuteQueryListener listener = new ExecuteQueryListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        OpenConfirmDialog("leave");
                    }else {
                        Toast.makeText(GroupSettingActivity.this, "Bạn là quản trị viên duy nhất, nhóm yêu cầu ít nhất một quản trị viên!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(GroupSettingActivity.this, "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExecuteQueryAsync async = new ExecuteQueryAsync(requestBody, listener);
        async.execute();
    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String text = String.valueOf(args[0]);
                    Toast.makeText(GroupSettingActivity.this, "Chưa khỏi động chat socket!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private Emitter.Listener onDeleteMember = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String json = String.valueOf(args[0]);
                    Bundle bundle = new Gson().fromJson(json, Bundle.class);

                    int user_id = Integer.parseInt(bundle.getString("user_id"));
                    int room_id = Integer.parseInt(bundle.getString("room_id"));

                    String msg_json = bundle.getString("msg_json");

                    Message message = new Gson().fromJson(msg_json, Message.class);

                    if(user_id == Constant.UID && room_id == ROOM_ID){

                        Toast.makeText(GroupSettingActivity.this, "Bạn đã bị xóa khỏi nhóm!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(GroupSettingActivity.this, MainActivity.class);
                        startActivity(intent);

                    }
                }
            });
        }
    };

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

        ll_gr_view_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mType.equals("group")){
                    Intent intent = new Intent(GroupSettingActivity.this, MemberListActivity.class);
                    intent.putExtra("room_id", ROOM_ID);
                    intent.putExtra("is_admin", mParticipant.isAdmin());
                    startActivityForResult(intent, 2);
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

        SendMessageListener listener = new SendMessageListener() {

            @Override
            public void onEnd(boolean status, Message message) {
                if(methods.isNetworkConnected()){
                    if(status){
                        String json = new Gson().toJson(message);
                        socket.emit("new message", json);
                    }else {
                        Toast.makeText(GroupSettingActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(GroupSettingActivity.this, "Vui lòng kết internet!", Toast.LENGTH_SHORT).show();
                }

            }
        };

        SendMessageAsync async = new SendMessageAsync(requestBody, listener);

        async.execute();
    }

    private void LeaveRoom() {
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", Constant.UID);
        bundle.putInt("room_id", ROOM_ID);

        RequestBody requestBody = methods.getRequestBody("method_leave_room", bundle, null);

        SendMessageListener listener = new SendMessageListener() {

            @Override
            public void onEnd(boolean status, Message message) {
                if(methods.isNetworkConnected()){
                    if(status){
                        String json = new Gson().toJson(message);
                        socket.emit("new message", json);

                        Intent intent = new Intent(GroupSettingActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(GroupSettingActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(GroupSettingActivity.this, "Vui lòng kết internet!", Toast.LENGTH_SHORT).show();
                }

            }
        };

        SendMessageAsync async = new SendMessageAsync(requestBody, listener);

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

    private void openSearchDialog() {
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

                    Intent intent = new Intent(GroupSettingActivity.this, SearchMessageActivity.class);
                    intent.putExtra("search_text", edt_dialog.getText().toString());
                    intent.putExtra("room_id", ROOM_ID);

                    startActivity(intent);

                    dialog.dismiss();

                }else {
                    edt_dialog.setError("Vui lòng nhập đầy đủ!");
                }
            }
        });

        tv_dialog.setText("Tìm kiếm tin nhắn");

        dialog.show();
    }

    private void ChangeGroupName(String name){
        Bundle bundle = new Bundle();
        bundle.putInt("room_id", ROOM_ID);
        bundle.putString("name", name);
        bundle.putInt("user_id", Constant.UID);

        RequestBody requestBody = methods.getRequestBody("method_change_group_name", bundle, null);

        SendMessageListener listener = new SendMessageListener() {

            @Override
            public void onEnd(boolean status, Message message) {
                if(methods.isNetworkConnected()){
                    if(status){
                        String json = new Gson().toJson(message);
                        socket.emit("new message", json);

                        LoadGetRoom();
                    }else {
                        Toast.makeText(GroupSettingActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(GroupSettingActivity.this, "Vui lòng kết internet!", Toast.LENGTH_SHORT).show();
                }

            }
        };

        SendMessageAsync async = new SendMessageAsync(requestBody, listener);

        async.execute();

    }

    private void DeleteHistory(){
        Bundle bundle = new Bundle();
        bundle.putInt("room_id", ROOM_ID);
        bundle.putInt("user_id", Constant.UID);

        RequestBody requestBody = methods.getRequestBody("method_delete_history", bundle, null);

        ExecuteQueryListener listener = new ExecuteQueryListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Toast.makeText(GroupSettingActivity.this, "Đã xóa lịch sử trò chuyện", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(GroupSettingActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(GroupSettingActivity.this, "Vui lòng kết internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        ExecuteQueryAsync async = new ExecuteQueryAsync(requestBody, listener);
        async.execute();
    }

    protected void OpenConfirmDialog(String type){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_warning);
        builder.setTitle("Thông báo");

        switch (type){
            case "leave":
                builder.setMessage("Bạn thật sự muốn rời nhóm?");
                break;
            case "history":
                builder.setMessage("Bạn sẽ không thể lấy lại những tin nhắn đã xóa, thực sự muốn tiếp tục?");
                break;
            case "block":
                builder.setMessage("Bạn thật sự muốn chặn người dùng này?");
                break;
            case "hide":
                builder.setMessage("Bạn thật sự muốn ẩn?");
                break;
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog

                switch (type){
                    case "leave":
                        LeaveRoom();
                        break;
                    case "history":
                        DeleteHistory();
                        break;
                    case "block":
                        BlockUser();
                        break;
                    case "hide":
                        HideRoom();
                        break;
                }

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void BlockUser() {
        Bundle bundle = new Bundle();
        bundle.putInt("user_id", USER_ID);
        bundle.putInt("admin_id", Constant.UID);

        RequestBody requestBody = methods.getRequestBody("method_msg_block_user", bundle, null);

        ExecuteQueryListener listener = new ExecuteQueryListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(boolean status) {
                if(methods.isNetworkConnected()){
                    if(status){
                        Toast.makeText(GroupSettingActivity.this, "Đã chặn người dùng này!", Toast.LENGTH_SHORT).show();

                        onBackPressed();
                    }else {
                        Toast.makeText(GroupSettingActivity.this, "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(GroupSettingActivity.this, "Vui lòng kết internet!", Toast.LENGTH_SHORT).show();
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