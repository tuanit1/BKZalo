/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:15 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 11/29/21, 6:15 PM
 * /
 */

package com.example.bkzalo.asynctasks;

import android.os.AsyncTask;

import com.example.bkzalo.listeners.GetUserRelationshipListener;
import com.example.bkzalo.models.Relationship;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.JsonUtils;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class GetUserRelationshipAsync extends AsyncTask<Void, String, Boolean> {

    private RequestBody requestBody;
    private GetUserRelationshipListener listener;
    private Relationship relationship;

    public GetUserRelationshipAsync(RequestBody requestBody, GetUserRelationshipListener listener) {
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
        try {
            String api_url = Constant.SERVER_URL+"api.php";

            //result is json_string
            String result = JsonUtils.okhttpPost(api_url, requestBody);

            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray_relate = jsonObject.getJSONArray("array_relation");

            if(jsonArray_relate.length() != 0){
                JSONObject obj = jsonArray_relate.getJSONObject(0);

                int user_id1 = obj.getInt("user_id1");
                int user_id2 = obj.getInt("user_id2");

                int requester = 0;
                int blocker = 0;

                if(!obj.getString("requester").equals("null")){
                    requester = obj.getInt("requester");
                }

                if(!obj.getString("blocker").equals("null")){
                    blocker = obj.getInt("blocker");
                }

                String status = obj.getString("status");

                relationship = new Relationship(user_id1, user_id2, requester, blocker, status);
            }else {
                relationship = new Relationship(0, 0, 0, 0, "stranger");
            }



            return true;

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean status) {
        listener.onEnd(status, relationship);
        super.onPostExecute(status);
    }
}
