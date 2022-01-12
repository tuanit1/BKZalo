/*
 * Copyright (c) 2022.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 1/12/22, 11:59 PM
 *  Copyright (c) 2022 . All rights reserved.
 *  Last modified 1/12/22, 11:59 PM
 * /
 */

package com.example.bkzalo.asynctasks;

import android.os.AsyncTask;

import com.example.bkzalo.listeners.ExecuteQueryListener_Nhi;
import com.example.bkzalo.listeners.GetSignInIDListener;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.JsonUtils;

import okhttp3.RequestBody;

/**
 * Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 1/12/2022.
 */
public class GetSignInIDAsync extends AsyncTask<Void, String, Boolean> {
    private RequestBody requestBody;
    private GetSignInIDListener listener;
    private int id = 0;

    public GetSignInIDAsync(RequestBody requestBody, GetSignInIDListener listener) {
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

            if(result.equals("Failed")){
                return false;
            }else {

                id = Integer.parseInt(result);

                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean status) {
        listener.onEnd(status, id);
        super.onPostExecute(status);
    }
}
