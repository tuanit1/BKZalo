package com.example.bkzalo.models;

public class Relationship {
    private int user_id1;
    private int user_id2;
    private int requester;
    private int blocker;
    private String status;

    public Relationship(int user_id1, int user_id2, int requester, int blocker, String status) {
        this.user_id1 = user_id1;
        this.user_id2 = user_id2;
        this.requester = requester;
        this.blocker = blocker;
        this.status = status;
    }

    public int getUser_id1() {
        return user_id1;
    }

    public void setUser_id1(int user_id1) {
        this.user_id1 = user_id1;
    }

    public int getUser_id2() {
        return user_id2;
    }

    public void setUser_id2(int user_id2) {
        this.user_id2 = user_id2;
    }

    public int getRequester() {
        return requester;
    }

    public void setRequester(int requester) {
        this.requester = requester;
    }

    public int getBlocker() {
        return blocker;
    }

    public void setBlocker(int blocker) {
        this.blocker = blocker;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
