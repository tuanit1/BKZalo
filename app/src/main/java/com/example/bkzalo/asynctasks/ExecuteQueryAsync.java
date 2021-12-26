/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:15 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 11/11/21, 12:17 AM
 * /
 */

package com.example.bkzalo.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.bkzalo.listeners.ExecuteQueryListener;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.JsonUtils;

import okhttp3.RequestBody;

public class ExecuteQueryAsync extends AsyncTask<Void, String, Boolean> {

    private RequestBody requestBody;
    private ExecuteQueryListener listener;

    public ExecuteQueryAsync(RequestBody requestBody, ExecuteQueryListener listener) {
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

            if(result.equals("success")){
                return true;
            }else {
                return false;
            }
        }catch (Exception e){
            Log.e("ThongBao", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean status) {
        listener.onEnd(status);
        super.onPostExecute(status);
    }
}
