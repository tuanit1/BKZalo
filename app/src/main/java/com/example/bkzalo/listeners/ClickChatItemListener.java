package com.example.bkzalo.listeners;

import com.example.bkzalo.models.Message;

public interface ClickChatItemListener {
    void onRemove(int message_id);
    void onImageClick(Message message);
}
