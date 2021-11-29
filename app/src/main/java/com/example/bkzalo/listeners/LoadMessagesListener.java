package com.example.bkzalo.listeners;

import com.example.bkzalo.models.Message;

import java.util.ArrayList;

public interface LoadMessagesListener {
    void onStart();
    void onEnd(boolean status, ArrayList<Message> array_message);
}
