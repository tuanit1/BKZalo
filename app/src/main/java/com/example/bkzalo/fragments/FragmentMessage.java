package com.example.bkzalo.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bkzalo.R;
import com.example.bkzalo.adapters.ChatListAdapter;
import com.example.bkzalo.asynctasks.LoadChatListAsync;
import com.example.bkzalo.listeners.ChatListListener;
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.Room;
import com.example.bkzalo.models.User;
import com.example.bkzalo.utils.Methods;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class FragmentMessage extends Fragment {

    private View view;
    private RecyclerView rv_chat;
    private Methods methods;
    private ArrayList<Participant> arrayList_parti;
    private ArrayList<Room> arrayList_room;
    private ArrayList<User> arrayList_user;
    private ChatListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);

        AnhXa();

        methods = new Methods(getContext());

        arrayList_parti = new ArrayList<>();
        arrayList_room = new ArrayList<>();
        arrayList_user = new ArrayList<>();

        LoadChatList();

        return view;
    }

    private void AnhXa() {
        rv_chat = view.findViewById(R.id.rv_chat);
    }

    private void LoadChatList() {

        int uid = 1;

        Bundle bundle = new Bundle();
        bundle.putInt("uid", uid);

        RequestBody requestBody = methods.getRequestBody("method_get_chat_list", bundle, null);

        ChatListListener listener = new ChatListListener() {
            @Override
            public void onStart() {
                arrayList_user.clear();
            }

            @Override
            public void onEnd(boolean status, ArrayList<Participant> array_parti, ArrayList<Room> array_room , ArrayList<User> array_user) {
                if(methods.isNetworkConnected()){
                    if(status){
                        arrayList_parti.addAll(array_parti);
                        arrayList_room.addAll(array_room);
                        arrayList_user.addAll(array_user);
                        SetAdapter();
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

        adapter = new ChatListAdapter(arrayList_user, getContext());

        LinearLayoutManager llm = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rv_chat.setLayoutManager(llm);
        rv_chat.setAdapter(adapter);

    }
}