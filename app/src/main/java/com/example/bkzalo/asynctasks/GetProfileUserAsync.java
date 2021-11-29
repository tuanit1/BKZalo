package com.example.bkzalo.asynctasks;

import android.os.AsyncTask;

import com.example.bkzalo.listeners.GetProfileUserListener;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.RequestBody;

public class GetProfileUserAsync extends AsyncTask<Void, String, Boolean> {

    private RequestBody requestBody;
    private GetProfileUserListener listener;
    private User user;

    public GetProfileUserAsync(RequestBody requestBody, GetProfileUserListener listener) {
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

            JSONObject object = jsonArray_user.getJSONObject(0);

            int id = object.getInt("id");
            String name = object.getString("name");
            String image = object.getString("image");

            //boolean table = object.getInt("boolean") == 1 ? true : false;

            String date_string = object.getString("birthday");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date birthday = sdf.parse(date_string);


            String phone = object.getString("phone");
            String email = object.getString("email");
            String bio = object.getString("bio");

            boolean isOnline = object.getInt("isOnline") == 1 ? true : false;

            user = new User(id, name, image, birthday, phone, email, bio, isOnline);

            return true;

        }catch (Exception e){
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean status) {
        listener.onEnd(status, user);
        super.onPostExecute(status);
    }
}
