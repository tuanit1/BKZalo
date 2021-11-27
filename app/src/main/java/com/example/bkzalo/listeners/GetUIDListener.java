package com.example.bkzalo.listeners;

import com.example.bkzalo.models.User;

import java.util.ArrayList;

public interface GetUIDListener {
    void onStart();
    void onEnd(boolean status, User user);
}
