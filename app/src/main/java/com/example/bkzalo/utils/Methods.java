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

        String post_data = postObj.toString();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("data", post_data);

        return builder.build();
    }
}
