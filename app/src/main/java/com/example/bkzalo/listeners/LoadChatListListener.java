package com.example.bkzalo.listeners;

import com.example.bkzalo.models.Message;
import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.Room;
import com.example.bkzalo.models.User;

import java.util.ArrayList;

public interface LoadChatListListener {
    void onStart();
    void onEnd(boolean status, ArrayList<Participant> array_parti, ArrayList<Room> array_room , ArrayList<User> array_user, ArrayList<Message> array_message);
}
