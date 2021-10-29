package com.example.bkzalo.listeners;

import com.example.bkzalo.models.User;

import java.util.ArrayList;

public interface LoadListFriendListener {
    void onStart();
    void onEnd(boolean status, ArrayList<User> array_user);
}
