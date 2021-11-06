package com.example.bkzalo.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.bkzalo.listeners.LoadChatListListener;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.Room;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.RequestBody;

public class LoadChatListAsync extends AsyncTask<Void, String, Boolean> {

    private RequestBody requestBody;
    private LoadChatListListener listener;
    private ArrayList<Participant> arrayList_participant;
    private ArrayList<Room> arrayList_room;
    private ArrayList<User> arrayList_user;
    private ArrayList<Message> arrayList_message;

    public LoadChatListAsync(RequestBody requestBody, LoadChatListListener listener) {
        this.requestBody = requestBody;
        this.listener = listener;
        arrayList_participant = new ArrayList<>();
        arrayList_room = new ArrayList<>();
        arrayList_user = new ArrayList<>();
        arrayList_message = new ArrayList<>();
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
            JSONArray jsonArray_parti = jsonObject.getJSONArray("array_participant");
            JSONArray jsonArray_room = jsonObject.getJSONArray("array_room");
            JSONArray jsonArray_user = jsonObject.getJSONArray("array_user");
            JSONArray jsonArray_message = jsonObject.getJSONArray("array_message");

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

                arrayList_participant.add(ptcp);

            }

            for(int i = 0; i < jsonArray_room.length(); i++){
                JSONObject obj = jsonArray_room.getJSONObject(i);

                int id = obj.getInt("id");

                String name = obj.getString("name");

                String image = obj.getString("image");

                String background = obj.getString("background");

                String type = obj.getString("type");

                Room room = new Room(id, name, image, background, type);

                arrayList_room.add(room);
            }

            for(int i = 0; i < jsonArray_user.length(); i++){
                JSONObject obj = jsonArray_user.getJSONObject(i);

                int id = obj.getInt("id");

                String name = obj.getString("name");

                String image = obj.getString("image");

                String date_string = obj.getString("birthday");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date birthday = sdf.parse(date_string);

                String phone = obj.getString("phone");

                String password = obj.getString("password");

                String bio = obj.getString("bio");

                User user = new User(id, name, image, birthday, phone, password, bio);

                arrayList_user.add(user);
            }

            for(int i = 0; i < jsonArray_message.length(); i++){
                JSONObject obj = jsonArray_message.getJSONObject(i);

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

                Message m = new Message(id, user_id, room_id, type, message, time, isRemove, isSeen, name, image, nickname);
                arrayList_message.add(m);
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
        listener.onEnd(status, arrayList_participant, arrayList_room,  arrayList_user, arrayList_message);
        super.onPostExecute(status);
    }
}
