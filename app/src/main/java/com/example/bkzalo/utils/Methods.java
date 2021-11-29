package com.example.bkzalo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.google.gson.JsonObject;

import java.io.File;
import java.lang.reflect.Method;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Methods {

    private Context context;
    private boolean isImageChange = false;

    public Methods(Context context){
        this.context = context;
    }

    private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");


    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    public RequestBody getRequestBody(String method_name, Bundle bundle, File file){

        JsonObject postObj = new JsonObject();

        postObj.addProperty("method_name", method_name);

        if(method_name.equals("method_get_chat_list")){
            int uid = bundle.getInt("uid");
            postObj.addProperty("uid", uid);
        }

        if(method_name.equals("method_get_phonebook_list")){
            int uid = bundle.getInt("uid");
            postObj.addProperty("uid", uid);
        }

        if(method_name.equals("method_delete_phonebook")){
            int uid1 = bundle.getInt("uid1");
            postObj.addProperty("uid1", uid1);
            int uid2 = bundle.getInt("uid2");
            postObj.addProperty("uid2", uid2);
        }

        if(method_name.equals("method_block_phonebook")){
            int uid1 = bundle.getInt("uid1");
            postObj.addProperty("uid1", uid1);
            int uid2 = bundle.getInt("uid2");
            postObj.addProperty("uid2", uid2);
        }

        if(method_name.equals("method_signup")){
            String name = bundle.getString("name");
            String email = bundle.getString("email");
            String phone = bundle.getString("phone");

            postObj.addProperty("name", name);
            postObj.addProperty("email", email);
            postObj.addProperty("phone", phone);
        }

        if(method_name.equals("method_get_user")){
            String email = bundle.getString("email");
            postObj.addProperty("email", email);
        }

        if(method_name.equals("method_update_info")){
            String birthday = bundle.getString("birthday");
            String image = bundle.getString("image");
            int uid = bundle.getInt("uid");
            postObj.addProperty("uid", uid);
            postObj.addProperty("birthday", birthday);
            postObj.addProperty("image", image);
        }

        if(method_name.equals("method_get_list_friend")){
            int uid = bundle.getInt("uid");
            postObj.addProperty("uid", uid);
        }

        if(method_name.equals("method_add_group")){
            isImageChange = bundle.getBoolean("is_change_image");
            int admin_id = bundle.getInt("admin_id");
            String json_member_id = bundle.getString("json_member_id");
            String group_name = bundle.getString("group_name");

            postObj.addProperty("admin_id", admin_id);
            postObj.addProperty("json_member_id", json_member_id);
            postObj.addProperty("group_name", group_name);
            postObj.addProperty("is_change_image", isImageChange?"true":"false");
        }

        if(method_name.equals("method_get_messages")){
            int room_id = bundle.getInt("room_id");
            int user_id = bundle.getInt("user_id");

            postObj.addProperty("room_id", room_id);
            postObj.addProperty("user_id", user_id);
        }

        if(method_name.equals("method_send_message")){
            int user_id = bundle.getInt("user_id");
            int room_id = bundle.getInt("room_id");
            String type = bundle.getString("type");
            String message = bundle.getString("message");
            String is_send_image = bundle.getString("is_send_image");

            if(is_send_image.equals("true")){
                isImageChange = true;
            }

            postObj.addProperty("user_id", user_id);
            postObj.addProperty("room_id", room_id);
            postObj.addProperty("type", type);
            postObj.addProperty("message", message);
            postObj.addProperty("is_send_image", is_send_image);
        }

        if(method_name.equals("method_remove_message")){
            int message_id = bundle.getInt("message_id");

            postObj.addProperty("message_id", message_id);
        }

        if(method_name.equals("method_seen_message")){
            int message_id = bundle.getInt("message_id");

            postObj.addProperty("message_id", message_id);
        }

        if(method_name.equals("method_unhide_room")){
            int room_id = bundle.getInt("room_id");
            int user_id = bundle.getInt("user_id");

            postObj.addProperty("room_id", room_id);
            postObj.addProperty("user_id", user_id);
        }

        if(method_name.equals("method_change_group_name")){
            String name = bundle.getString("name");
            int room_id = bundle.getInt("room_id");
            int user_id = bundle.getInt("user_id");

            postObj.addProperty("name", name);
            postObj.addProperty("room_id", room_id);
            postObj.addProperty("user_id", user_id);
        }

        if(method_name.equals("method_get_room")){
            String type = bundle.getString("type");
            int room_id = bundle.getInt("room_id");
            int user_id = bundle.getInt("user_id");

            postObj.addProperty("type", type);
            postObj.addProperty("room_id", room_id);
            postObj.addProperty("user_id", user_id);
        }

        if(method_name.equals("method_change_group_image")){
            isImageChange = true;
            int room_id = bundle.getInt("room_id");
            int user_id = bundle.getInt("user_id");

            postObj.addProperty("room_id", room_id);
            postObj.addProperty("user_id", user_id);
        }

        if(method_name.equals("method_get_group_unmember")){
            int room_id = bundle.getInt("room_id");
            int user_id = bundle.getInt("user_id");

            postObj.addProperty("user_id", user_id);
            postObj.addProperty("room_id", room_id);
        }

        if(method_name.equals("method_add_member")){
            String json_member_id = bundle.getString("json_member_id");
            int user_id = bundle.getInt("user_id");
            int room_id = bundle.getInt("room_id");

            postObj.addProperty("json_member_id", json_member_id);
            postObj.addProperty("user_id", user_id);
            postObj.addProperty("room_id", room_id);
        }

        if(method_name.equals("method_get_memberlist")){
            int room_id = bundle.getInt("room_id");

            postObj.addProperty("room_id", room_id);
        }

        if(method_name.equals("method_set_group_admin")){
            int admin_id = bundle.getInt("admin_id");
            int user_id = bundle.getInt("user_id");
            int room_id = bundle.getInt("room_id");
            String is_admin = bundle.getString("is_admin");

            postObj.addProperty("admin_id", admin_id);
            postObj.addProperty("user_id", user_id);
            postObj.addProperty("room_id", room_id);
            postObj.addProperty("is_admin", is_admin);
        }

        if(method_name.equals("method_delete_group_member")){
            int admin_id = bundle.getInt("admin_id");
            int user_id = bundle.getInt("user_id");
            int room_id = bundle.getInt("room_id");

            postObj.addProperty("admin_id", admin_id);
            postObj.addProperty("user_id", user_id);
            postObj.addProperty("room_id", room_id);
        }

        if(method_name.equals("method_leave_room")){
            int user_id = bundle.getInt("user_id");
            int room_id = bundle.getInt("room_id");

            postObj.addProperty("user_id", user_id);
            postObj.addProperty("room_id", room_id);
        }

        if(method_name.equals("method_check_last_admin")){
            int room_id = bundle.getInt("room_id");

            postObj.addProperty("room_id", room_id);
        }

        if(method_name.equals("method_search_message")){
            int room_id = bundle.getInt("room_id");
            String text = bundle.getString("text");

            postObj.addProperty("room_id", room_id);
            postObj.addProperty("text", text);
        }

        if(method_name.equals("method_image_message")){
            int room_id = bundle.getInt("room_id");

            postObj.addProperty("room_id", room_id);
        }

        if(method_name.equals("method_change_nickname")){
            int admin_id = bundle.getInt("admin_id");
            int user_id = bundle.getInt("user_id");
            int room_id = bundle.getInt("room_id");
            String nickname = bundle.getString("nickname");

            postObj.addProperty("admin_id", admin_id);
            postObj.addProperty("user_id", user_id);
            postObj.addProperty("room_id", room_id);
            postObj.addProperty("nickname", nickname);
        }

        if(method_name.equals("method_delete_history")){
            int room_id = bundle.getInt("room_id");
            int user_id = bundle.getInt("user_id");

            postObj.addProperty("room_id", room_id);
            postObj.addProperty("user_id", user_id);
        }

        if(method_name.equals("method_msg_block_user")){
            int admin_id = bundle.getInt("admin_id");
            int user_id = bundle.getInt("user_id");

            postObj.addProperty("admin_id", admin_id);
            postObj.addProperty("user_id", user_id);
        }

        if(method_name.equals("method_get_user_relationship")){
            int admin_id = bundle.getInt("admin_id");
            int user_id = bundle.getInt("user_id");

            postObj.addProperty("admin_id", admin_id);
            postObj.addProperty("user_id", user_id);
        }

        if(method_name.equals("method_msg_unblock")){
            int admin_id = bundle.getInt("admin_id");
            int user_id = bundle.getInt("user_id");

            postObj.addProperty("admin_id", admin_id);
            postObj.addProperty("user_id", user_id);
        }

        if(method_name.equals("method_hide_room")){
            int room_id = bundle.getInt("room_id");
            int user_id = bundle.getInt("user_id");

            postObj.addProperty("room_id", room_id);
            postObj.addProperty("user_id", user_id);
        }

        if(method_name.equals("method_update_online")){
            int user_id = bundle.getInt("user_id");
            int status = bundle.getInt("status");

            postObj.addProperty("user_id", user_id);
            postObj.addProperty("status", status);
        }

        if(method_name.equals("method_get_private_room")){
            int uid = bundle.getInt("uid");
            int friend_id = bundle.getInt("friend_id");

            postObj.addProperty("uid", uid);
            postObj.addProperty("friend_id", friend_id);
        }

        if(method_name.equals("method_get_profile_user")){
            int uid = bundle.getInt("uid");
            postObj.addProperty("uid", uid);
        }

        if(method_name.equals("method_update_profile")){
            String name = bundle.getString("name");
            String phone = bundle.getString("phone");
            String bio = bundle.getString("bio");
            String date = bundle.getString("date");
            int uid = bundle.getInt("uid");

            postObj.addProperty("name", name);
            postObj.addProperty("phone", phone);
            postObj.addProperty("bio", bio);
            postObj.addProperty("date", date);
            postObj.addProperty("uid", uid);

        }

        if(method_name.equals("method_get_request_list")){
            int uid = bundle.getInt("uid");
            postObj.addProperty("uid", uid);
        }

        if(method_name.equals("method_get_block_list")){
            int uid = bundle.getInt("uid");
            postObj.addProperty("uid", uid);
        }

        if(method_name.equals("method_accept_friend")){
            int uid = bundle.getInt("uid");
            int uid2 = bundle.getInt("uid2");

            postObj.addProperty("uid", uid);
            postObj.addProperty("uid2", uid2);
        }

        if(method_name.equals("method_refuse_friend")){
            int uid = bundle.getInt("uid");
            int uid2 = bundle.getInt("uid2");

            postObj.addProperty("uid", uid);
            postObj.addProperty("uid2", uid2);
        }

        if(method_name.equals("method_unblock_friend")){
            int uid = bundle.getInt("uid");
            int uid2 = bundle.getInt("uid2");

            postObj.addProperty("uid", uid);
            postObj.addProperty("uid2", uid2);
        }

        if(method_name.equals("method_unfriend")){
            int uid = bundle.getInt("uid");
            int uid2 = bundle.getInt("uid2");

            postObj.addProperty("uid", uid);
            postObj.addProperty("uid2", uid2);
        }

        if(method_name.equals("method_add_friend")){
            int uid = bundle.getInt("uid");
            int uid2 = bundle.getInt("uid2");

            postObj.addProperty("uid", uid);
            postObj.addProperty("uid2", uid2);
        }

        if(method_name.equals("method_cancel_friend")){
            int uid = bundle.getInt("uid");
            int uid2 = bundle.getInt("uid2");

            postObj.addProperty("uid", uid);
            postObj.addProperty("uid2", uid2);
        }

        if(method_name.equals("method_get_user_list")){
            int uid = bundle.getInt("uid");
            postObj.addProperty("uid", uid);
        }

        if(method_name.equals("method_get_relationship")){
            int uid = bundle.getInt("uid");
            int uid2 = bundle.getInt("uid2");

            postObj.addProperty("uid", uid);
            postObj.addProperty("uid2", uid2);
        }

        if(method_name.equals("method_update_profile_image")){
            isImageChange = true;
            int uid = bundle.getInt("uid");
            postObj.addProperty("uid", uid);
        }

        String post_data = postObj.toString();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("data", post_data);

        if(isImageChange){
            if(file != null) {
                builder.addFormDataPart("image", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
            }
        }

        return builder.build();
    }
}
