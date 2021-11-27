package com.example.bkzalo.listeners;

import com.example.bkzalo.models.Message;

public interface SendMessageListener {
    void onEnd(boolean status, Message message);
}
