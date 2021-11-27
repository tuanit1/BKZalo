package com.example.bkzalo.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.bkzalo.listeners.ChatListListener;
import com.example.bkzalo.listeners.GetUIDListener;
import com.example.bkzalo.listeners.PhoneBookListListener;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.RequestBody;

public class GetUIDAsync extends AsyncTask<Void, String, Boolean> {
    private RequestBody requestBody;
    private GetUIDListener listener;
    private User user;

    public GetUIDAsync(RequestBody requestBody, GetUIDListener listener) {
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
            String api_url = Constant.SERVER_URL+"api.php";

            //result is json_string
            String result = JsonUtils.okhttpPost(api_url, requestBody);

            //json string -> json object
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray_user = jsonObject.getJSONArray("array_user");

            if (jsonArray_user.length()==0)
            {
                return false;
            }

            JSONObject obj = jsonArray_user.getJSONObject(0);

            int id = obj.getInt("id");

            String name = obj.getString("name");

            String image = obj.getString("image");

            String phone = obj.getString("phone");

            String bio = obj.getString("bio");

            String email = obj.getString("email");

            String date_string = obj.getString("birthday");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            boolean isOnline = obj.getInt("isOnline") == 1 ? true: false;


            if (date_string.equals("null"))
            {
                user = new User(id,  name,  image,  null,  phone,   bio,  email, isOnline);
            }
            else
            {
                Date birthday = sdf.parse(date_string);
                user = new User(id,  name,  image,  birthday,  phone,   bio,  email, isOnline);
            }

            return true;

        }catch (Exception e){
            Log.e("ThongBao", e.getMessage());
            e.printStackTrace();
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean status) {
        listener.onEnd(status, user);
        super.onPostExecute(status);
    }
}
