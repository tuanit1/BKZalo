package com.example.bkzalo.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.bkzalo.listeners.SendMessageListener;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.RequestBody;

public class SendMessageAsync extends AsyncTask<Void, String, Boolean> {
    private RequestBody requestBody;
    private SendMessageListener listener;
    private Message returnMessage;

    public SendMessageAsync(RequestBody requestBody, SendMessageListener listener) {
        this.requestBody = requestBody;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
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
            JSONArray jsonArray_message = jsonObject.getJSONArray("array_message");

            JSONObject obj = jsonArray_message.getJSONObject(0);

            int id = obj.getInt("id");
            int user_id = obj.getInt("user_id");
            int room_id = obj.getInt("room_id");
            String type = obj.getString("type");
            String message = obj.getString("message");

            String date_string = obj.getString("time");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date time = sdf.parse(date_string);

            boolean isRemove = obj.getInt("isRemove") == 1;
            boolean isSeen = obj.getInt("isSeen") == 1;
            String name = obj.getString("name");
            String image = obj.getString("image");
            String nickname = obj.getString("nickname");

            returnMessage = new Message(id, user_id, room_id, type, message, time, isRemove, isSeen, name, image, nickname);

            return  true;

        }catch (Exception e){
            Log.e("ThongBao", e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean status) {
        listener.onEnd(status, returnMessage);
        super.onPostExecute(status);
    }
}
