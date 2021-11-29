package com.example.bkzalo.listeners;

import com.example.bkzalo.models.Relationship;

public interface GetUserRelationshipListener {
    void onStart();
    void onEnd(boolean status, Relationship relationship);
}
