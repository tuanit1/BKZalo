package com.example.bkzalo.listeners;

import com.example.bkzalo.models.Room;
import com.example.bkzalo.models.User;

public interface ClickChatListListener {
    void onClick(int room_id, int user_id, String type);
    void unHide(int room_id);
}
