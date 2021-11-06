package com.example.bkzalo.listeners;

import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.Room;

public interface GetRoomListener {
    void onStart();
    void onEnd(boolean status, Room room, Participant participant);
}
