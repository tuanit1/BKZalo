package com.example.bkzalo.listeners;

public interface ClickItemRequestListener {
    void onClick(int uid);
    void onAccept(int uid);
    void onRefuse(int uid);
}
