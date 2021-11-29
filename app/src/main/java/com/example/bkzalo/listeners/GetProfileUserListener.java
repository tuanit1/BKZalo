package com.example.bkzalo.listeners;

import com.example.bkzalo.models.User;

public interface GetProfileUserListener {
    void onStart();
    void onEnd(boolean status, User user);
}
