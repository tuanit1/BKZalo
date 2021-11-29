package com.example.bkzalo.listeners;

import com.example.bkzalo.models.User;

import java.util.ArrayList;

public interface GetProfileUserListListener {
    void onStart();
    void onEnd(boolean status, ArrayList<User> arrayList_user);
}
