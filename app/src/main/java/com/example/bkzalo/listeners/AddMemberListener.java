package com.example.bkzalo.listeners;

import com.example.bkzalo.models.Message;

import java.util.ArrayList;

public interface AddMemberListener {
    void onStart();
    void onEnd(boolean status, ArrayList<Message> arrayList);
}
