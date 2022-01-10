/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:15 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 11/11/21, 12:18 AM
 * /
 */

package com.example.bkzalo.asynctasks;

import android.os.AsyncTask;

import com.example.bkzalo.listeners.GetRoomListener;
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.Room;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.RequestBody;

public class LoadGetRoomAsync extends AsyncTask<Void, String, Boolean> {

    private RequestBody requestBody;
    private GetRoomListener listener;
    private Room room;
    private Participant participant;

    public LoadGetRoomAsync(RequestBody requestBody, GetRoomListener listener) {
        this.requestBody = requestBody;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        listener.onStart();
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try{
            String api_url = Constant.SERVER_URL;

            //result is json_string
            String result = JsonUtils.okhttpPost(api_url, requestBody);

            //json string -> json object
            JSONObject jsonObject = new JSONObject(result);

            //room
            JSONArray jsonArray_room = jsonObject.getJSONArray("array_room");

            JSONObject obj = jsonArray_room.getJSONObject(0);

            int id = obj.getInt("id");

            String name = obj.getString("name");

            String image = obj.getString("image");

            String image_url = obj.getString("image_url");

            String background = obj.getString("background");

            String type = obj.getString("type");

            room = new Room(id, name, image, image_url, background, type);

            //participant
            JSONArray jsonArray_par = jsonObject.getJSONArray("array_participant");

            JSONObject obj1 = jsonArray_par.getJSONObject(0);

            int user_id = obj1.getInt("user_id");

            int room_id = obj1.getInt("room_id");

            String nickname = obj1.getString("nickname");

            Boolean isAdmin = (obj1.getInt("isAdmin") == 1)?true:false;

            Boolean isHide = (obj1.getInt("isHide") == 1)?true:false;

            String date_string = obj1.getString("timestamp");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date timestamp = sdf.parse(date_string);

            participant =  new Participant(user_id, room_id, nickname, isAdmin, isHide, timestamp);

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean status) {
        listener.onEnd(status, room, participant);
        super.onPostExecute(status);
    }
}
