package com.example.bkzalo.listeners;

import com.example.bkzalo.models.User;

public interface ClickMemberListener {
    void onProfile(int user_id);
    void onAdmin(int user_id, boolean isAdmin);
    void onDelete(int user_id);
}
