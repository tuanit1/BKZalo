package com.example.bkzalo.listeners;

import com.example.bkzalo.models.Relationship;
import com.example.bkzalo.models.User;

public interface GetRelationshipListener {
    void onStart();
    void onEnd(boolean status, Relationship relationship);
}
