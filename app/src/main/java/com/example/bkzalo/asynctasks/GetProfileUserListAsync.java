package com.example.bkzalo.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.bkzalo.listeners.GetProfileUserListListener;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.RequestBody;

public class GetProfileUserListAsync extends AsyncTask<Void, String, Boolean> {


    private RequestBody requestBody;
    private GetProfileUserListListener listener;
    private ArrayList<User> arrayList_user;

    public GetProfileUserListAsync(RequestBody requestBody, GetProfileUserListListener listener) {
        this.requestBody = requestBody;
        this.listener = listener;
        arrayList_user = new ArrayList<>();
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

            for(int i = 0; i < jsonArray_user.length(); i++){
                JSONObject obj = jsonArray_user.getJSONObject(i);

                int id = obj.getInt("id");

                String name = obj.getString("name");

                String image = obj.getString("image");

                String date_string = obj.getString("birthday");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date birthday = sdf.parse(date_string);

                String phone = obj.getString("phone");

                String email = obj.getString("email");

                String bio = obj.getString("bio");

                boolean isOnline = obj.getInt("isOnline") == 1 ? true : false;

                User user = new User(id, name, image, birthday, phone, email, bio, isOnline);

                arrayList_user.add(user);
            }

            return  true;

        }catch (Exception e){
            Log.e("ThongBao", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean status) {
        listener.onEnd(status, arrayList_user);
        super.onPostExecute(status);
    }
}
