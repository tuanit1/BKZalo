package com.example.bkzalo.models;

import java.util.Date;

public class Relationship {
    private int user_id1;
    private int user_id2;
    private int requseter;
    private int blocker;
    private String status;

    public Relationship(int user_id1, int user_id2, int requseter, int blocker, String status) {
        this.user_id1 = user_id1;
        this.user_id2 = user_id2;
        this.requseter = requseter;
        this.blocker = blocker;
        this.status = status;
    }

    public int getUser_id1() {
        return user_id1;
    }

    public int getUser_id2() {
        return user_id2;
    }

    public int getRequseter() {
        return requseter;
    }

    public int getBlocker() {
        return blocker;
    }

    public String getStatus() {
        return status;
    }
}
