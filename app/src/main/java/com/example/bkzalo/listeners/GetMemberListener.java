package com.example.bkzalo.listeners;

import com.example.bkzalo.models.Participant;
import com.example.bkzalo.models.User;

import java.util.ArrayList;

public interface GetMemberListener {
    void onStart();
    void onEnd(boolean status, ArrayList<User> array_user, ArrayList<Participant> array_participant);
}
