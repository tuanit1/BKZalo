/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:15 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 11/29/21, 4:56 PM
 * /
 */

package com.example.bkzalo.asynctasks;

import android.net.sip.SipSession;
import android.os.AsyncTask;

import com.example.bkzalo.listeners.GetProfileUserListener;
import com.example.bkzalo.listeners.GetRelationshipListener;
import com.example.bkzalo.models.Relationship;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.RequestBody;

public class GetRelationshipAsync extends AsyncTask<Void, String, Boolean> {

    private RequestBody requestBody;
    private GetRelationshipListener listener;
    private Relationship relationship;

    public GetRelationshipAsync(RequestBody requestBody, GetRelationshipListener listener) {
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

            JSONArray jsonArray_user = jsonObject.getJSONArray("array_relationship");

            if(jsonArray_user.length() != 0){
                JSONObject object = jsonArray_user.getJSONObject(0);

                int use_id1 = object.getInt("user_id1");
                int use_id2 = object.getInt("user_id2");
                int requester = 0;

                if(!object.getString("requester").equals("null")){
                    requester = object.getInt("requester");
                }

                int blocker = 0;

                if(!object.getString("blocker").equals("null")){
                    blocker = object.getInt("blocker");
                }

                String status = object.getString("status");

                relationship = new Relationship(use_id1, use_id2, requester, blocker, status);
            }else {
                relationship = null;
            }



            return true;

        }catch (Exception e){
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean status) {
        listener.onEnd(status, relationship);
        super.onPostExecute(status);
    }
}
