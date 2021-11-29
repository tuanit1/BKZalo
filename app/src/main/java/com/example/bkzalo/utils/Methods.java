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
