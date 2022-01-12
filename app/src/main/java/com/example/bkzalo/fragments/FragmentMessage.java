/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:15 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 12/18/21, 3:07 AM
 * /
 */

package com.example.bkzalo.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.activitiy.AddGroupActivity;
import com.example.bkzalo.activitiy.ChatActivity;
import com.example.bkzalo.activitiy.EditProfileActivity;
import com.example.bkzalo.activitiy.HideListActivity;
import com.example.bkzalo.activitiy.MainActivity;
import com.example.bkzalo.adapters.ChatListAdapter;
import com.example.bkzalo.asynctasks.GetProfileUserAsync;
import com.example.bkzalo.asynctasks.LoadChatListAsync;
import com.example.bkzalo.listeners.ClickChatListListener;
import com.example.bkzalo.listeners.GetProfileUserListener;
import com.example.bkzalo.listeners.LoadChatListListener;
import com.example.bkzalo.models.Message;
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.Room;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Constant;
import com.example.bkzalo.utils.EndlessRecyclerViewScrollListener;
import com.example.bkzalo.utils.Methods;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;

public class FragmentMessage extends Fragment {

    private View view;
    private RecyclerView rv_chat;
    private EditText edt_search;
    private ImageView iv_add_group, iv_user;
    private LinearLayout empty_view;
    private Methods methods;
    private ArrayList<Participant> arrayList_parti;
    private ArrayList<Room> arrayList_room;
    private ArrayList<User> arrayList_user;
    private ArrayList<Message> arrayList_message;
    private ChatListAdapter adapter;
    private ProgressBar progressBar;
    private Socket socket;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);

        AnhXa();

        methods = new Methods(getContext());

        arrayList_parti = new ArrayList<>();
        arrayList_room = new ArrayList<>();
        arrayList_user = new ArrayList<>();
        arrayList_message = new ArrayList<>();

        SetAdapter();
        InitSocketIO();
        GetUserProfile();
        LoadChatList(false);

        return view;
    }

    private void AnhXa() {

        empty_view = view.findViewById(R.id.empty_view);
        rv_chat = view.findViewById(R.id.rv_chat);
        progressBar = view.findViewById(R.id.progressBar);
        iv_add_group = view.findViewById(R.id.iv_add_group);
        iv_add_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu menu = new PopupMenu(getContext(), iv_add_group);
                menu.inflate(R.menu.menu_msg_screen);

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        switch (id) {
                            case R.id.menu_add_group:
                                Intent intent = new Intent(getContext(), AddGroupActivity.class);
                                startActivityForResult(intent, 1);
                                break;
                            case R.id.menu_hide_list:
                                Intent intent1 = new Intent(getContext(), HideListActivity.class);
                                startActivityForResult(intent1, 1);
                                break;
                        }
                        return true;
                    }
                });

                menu.show();

            }
        });

        iv_user = view.findViewById(R.id.iv_user);
        iv_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaceFragment(new FragmentProfile(), "Profile");
                ((MainActivity)getActivity()).bottomNavigationView.setSelectedItemId(R.id.bottom_nav_canhan);
            }
        });

        edt_search = view.findViewById(R.id.edt_search);
    }

    private void InitSocketIO(){
        try {
            socket = IO.socket(Constant.SERVER_NODE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on("onNewMessage", onNewMessage);
        socket.on("onAddMember", onAddMember);
        socket.on("onDeleteMember", onDeleteMember);
        socket.on("onUpdateOnline", onUpdateOnline);

        socket.connect();

    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Chưa khởi động chat socket!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }
    };

    private Emitter.Listener onAddMember = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadChatList(true);
                    }
                });
            }

        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadChatList(true);
                    }
                });
            }

        }
    };

    private Emitter.Listener onDeleteMember = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LoadChatList(true);
                    }
                });
            }

        }
    };

    private Emitter.Listener onUpdateOnline = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String json = String.valueOf(args[0]);

                        Bundle bundle = new Gson().fromJson(json, Bundle.class);

                        int user_id = Integer.parseInt(bundle.getString("user_id"));
                        boolean status = bundle.getString("status").equals("1");

                        for(User u : arrayList_user){
                            if(u.getId() == user_id){
                                u.setOnline(status);
                                break;
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
            }

        }
    };

    private void GetUserProfile() {
        int uid = Constant.UID;

        Bundle bundle = new Bundle();
        bundle.putInt("uid", uid);

        RequestBody requestBody = methods.getRequestBody("method_get_profile_user", bundle, null);

        GetProfileUserListener listener = new GetProfileUserListener() {
            @Override
            public void onStart() {
                //hiện progressbar
            }

            @Override
            public void onEnd(boolean status, User user) {
                if(methods.isNetworkConnected()){
                    if(status){
                        if(!user.getImage_url().isEmpty()){
                            Picasso.get()
                                    .load(user.getImage_url())
                                    .placeholder(R.drawable.image_user_holder)
                                    .error(R.drawable.message_placeholder_ic)
                                    .into(iv_user);
                        }else{
                            Picasso.get()
                                    .load(R.drawable.message_placeholder_ic)
                                    .into(iv_user);
                        }
                    }else {
                        Toast.makeText(getContext(), "Lỗi Server", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(), "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        GetProfileUserAsync async = new GetProfileUserAsync(requestBody, listener);
        async.execute();
    }

    private void LoadChatList(boolean isReload) {

        Bundle bundle = new Bundle();
        bundle.putInt("uid", Constant.UID);


        RequestBody requestBody = methods.getRequestBody("method_get_chat_list", bundle, null);

        LoadChatListListener listener = new LoadChatListListener() {
            @Override
            public void onStart() {

                arrayList_parti.clear();
                arrayList_room.clear();
                arrayList_user.clear();
                arrayList_message.clear();

                if(!isReload){
                    progressBar.setVisibility(View.VISIBLE);
                }

                empty_view.setVisibility(View.GONE);

            }

            @Override
            public void onEnd(boolean status, ArrayList<Participant> array_parti, ArrayList<Room> array_room , ArrayList<User> array_user, ArrayList<Message> array_message) {
                if(methods.isNetworkConnected()){
                    if(status){

                        arrayList_parti.addAll(array_parti);
                        arrayList_user.addAll(array_user);
                        arrayList_message.addAll(array_message);

                        for(Room r : array_room){
                            Participant par = GetParti(r);
                            if(!par.isHide()){
                                arrayList_room.add(r);
                            }
                        }

                        SortByLatestMessage(arrayList_room);

                        if(!isReload){
                            progressBar.setVisibility(View.GONE);
                        }

                        adapter.notifyDataSetChanged();

                        if(arrayList_room.isEmpty()){
                            empty_view.setVisibility(View.VISIBLE);
                        }

                    }else {
                        Toast.makeText(getContext(), "Lỗi server!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(), "Vui lòng kết nối internet!", Toast.LENGTH_SHORT).show();
                }


            }
        };

        LoadChatListAsync loadChatListAsync = new LoadChatListAsync(requestBody, listener);
        loadChatListAsync.execute();
    }

    private void SetAdapter(){

        adapter = new ChatListAdapter("default", arrayList_parti, arrayList_room, arrayList_user, arrayList_message, getContext(), new ClickChatListListener() {
            @Override
            public void onClick(int room_id, int user_id, String type) {
                Intent intent = new Intent(getContext(), ChatActivity.class);

                intent.putExtra("room_id", room_id);
                intent.putExtra("type", type);
                intent.putExtra("user_id", user_id);

                startActivityForResult(intent, 1);
            }

            @Override
            public void unHide(int room_id) {

            }
        });

        LinearLayoutManager llm = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rv_chat.setLayoutManager(llm);
        rv_chat.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        ArrayList<Room> arrayList_search = new ArrayList<>();

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                arrayList_search.clear();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                for(Room room : arrayList_room){
                    if(room.getType().equals("private")){

                        User user = GetFriendByRoomId(room.getId());
                        String nickname = GetNicknameParti(room, user.getId());

                        if(user.getName().toLowerCase().contains(s.toString().toLowerCase())
                            || nickname.toLowerCase().contains(s.toString().toLowerCase())){
                            arrayList_search.add(room);
                        }
                    }else {
                        if(room.getName().toLowerCase().contains(s.toString().toLowerCase())){
                            arrayList_search.add(room);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.setAdapterData(arrayList_search);
                adapter.notifyDataSetChanged();
            }
        });

    }

    public void SortByLatestMessage(ArrayList<Room> arr) {
        for (int i = 0; i < arr.size(); i++) {

            int pos = i;
            for (int j = i; j < arr.size(); j++) {

                Room r = arr.get(j);
                Message m = GetMessage(r);

                Room r_pos = arr.get(pos);
                Message m_r = GetMessage(r_pos);

                if(m == null){
                    continue;

                }

                if(m_r == null){
                    pos = j;
                    continue;
                }

                if (m.getTime().compareTo(m_r.getTime()) > 0) {
                    pos = j;
                }
            }

            Room min = arr.get(pos);
            arr.set(pos, arr.get(i));
            arr.set(i, min);
        }
    }

    public Message GetMessage(Room room){
        for(Message m : arrayList_message){
            if(m.getRoom_id() == room.getId()){
                return m;
            }
        }
        return null;
    }

    public Participant GetParti(Room room){
        for(Participant participant : arrayList_parti){
            if(participant.getRoom_id() == room.getId() && participant.getUser_id() == Constant.UID){
                return participant;
            }
        }

        return null;
    }

    public String GetNicknameParti(Room room, int uid){
        for(Participant participant : arrayList_parti){
            if(participant.getRoom_id() == room.getId() && participant.getUser_id() == uid){
                return participant.getNickname();
            }
        }

        return null;
    }

    public User GetFriendByRoomId(int room_id){
        for(Participant p : arrayList_parti){
            if(p.getUser_id() != Constant.UID && p.getRoom_id() == room_id){
                for(User u : arrayList_user){
                    if(u.getId() == p.getUser_id()){
                        return u;
                    }
                }
            }
        }

        return null;
    }

    private void ReplaceFragment(Fragment fragment, String name){

        FragmentManager fm = getActivity().getSupportFragmentManager();
        int backstackCount = fm.getBackStackEntryCount();
        for (int i = 0; i < backstackCount; i++){
            int backStackId = fm.getBackStackEntryAt(i).getId();

            fm.popBackStack(backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.main_content, fragment, name);
        fragmentTransaction.commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if(resultCode == RESULT_OK){
                LoadChatList(true);
            }

        }
    }


}