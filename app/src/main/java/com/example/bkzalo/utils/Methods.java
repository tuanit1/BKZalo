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

        if(method_name.equals("method_get_list_friend")){
            int uid = bundle.getInt("uid");
            postObj.addProperty("uid", uid);
        }

        if(method_name.equals("method_add_group")){
            isImageChange = bundle.getBoolean("is_change_image");
            int admin_id = bundle.getInt("admin_in");
            String json_member_id = bundle.getString("json_member_id");
            String group_name = bundle.getString("group_name");

            postObj.addProperty("admin_id", admin_id);
            postObj.addProperty("json_member_id", json_member_id);
            postObj.addProperty("group_name", group_name);
            postObj.addProperty("is_change_image", isImageChange?"true":"false");
        }


        String post_data = postObj.toString();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("data", post_data);

        if(isImageChange){
            builder.addFormDataPart("image", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
        }

        return builder.build();
    }
}
