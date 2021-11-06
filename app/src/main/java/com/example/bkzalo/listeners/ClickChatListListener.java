package com.example.bkzalo.listeners;

import com.example.bkzalo.models.Room;
import com.example.bkzalo.models.User;

public interface ClickChatListListener {
    void onClick(int room_id, String type, String room_image, String room_name);
    void unHide(int room_id);
}
