/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:15 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 11/29/21, 4:56 PM
 * /
 */

package com.example.bkzalo.asynctasks;

import android.os.AsyncTask;

import com.example.bkzalo.listeners.GetMemberListener;
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.RequestBody;

public class GetMemberListAsync extends AsyncTask<Void, String, Boolean> {

    private RequestBody requestBody;
    private GetMemberListener listener;
    private ArrayList<User> arrayList_user;
    private ArrayList<Participant> arrayList_parti;

    public GetMemberListAsync(RequestBody requestBody, GetMemberListener listener) {
        this.requestBody = requestBody;
        this.listener = listener;
        arrayList_user = new ArrayList<>();
        arrayList_parti = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        listener.onStart();
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            String api_url = Constant.SERVER_URL;

            //result is json_string
            String result = JsonUtils.okhttpPost(api_url, requestBody);

            //json string -> json object
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray_parti = jsonObject.getJSONArray("array_participant");
            JSONArray jsonArray_user = jsonObject.getJSONArray("array_user");

            for(int i = 0; i < jsonArray_parti.length(); i++){
                JSONObject obj = jsonArray_parti.getJSONObject(i);

                int user_id = obj.getInt("user_id");

                int room_id = obj.getInt("room_id");

                String nickname = obj.getString("nickname");

                Boolean isAdmin = (obj.getInt("isAdmin") == 1)?true:false;

                Boolean isHide = (obj.getInt("isHide") == 1)?true:false;

                String date_string = obj.getString("timestamp");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date timestamp = sdf.parse(date_string);

                Participant ptcp =  new Participant(user_id, room_id, nickname, isAdmin, isHide, timestamp);

                arrayList_parti.add(ptcp);

            }

            for(int i = 0; i < jsonArray_user.length(); i++){
                JSONObject obj = jsonArray_user.getJSONObject(i);

                int id = obj.getInt("id");

                String name = obj.getString("name");

                String image = obj.getString("image");

                String image_url = obj.getString("image_url");

                String date_string = obj.getString("birthday");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date birthday = sdf.parse(date_string);

                String phone = obj.getString("phone");

                String email = obj.getString("email");

                String bio = obj.getString("bio");

                boolean isOnline = obj.getInt("isOnline")==0?false:true;

                User user = new User(id, name, image, image_url, birthday, phone, bio, email, isOnline);

                arrayList_user.add(user);

            }

            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean status) {
        listener.onEnd(status, arrayList_user, arrayList_parti);
        super.onPostExecute(status);
    }
}
