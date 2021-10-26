package com.example.bkzalo.listeners;

import com.example.bkzalo.models.User;

import java.util.ArrayList;

public interface ChatListListener {
    void onStart();
    void onEnd(boolean status, ArrayList<User> arrayList);
}
