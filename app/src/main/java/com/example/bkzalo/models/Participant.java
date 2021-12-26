/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:16 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 10/27/21, 9:47 PM
 * /
 */

package com.example.bkzalo.models;

import java.util.Date;

public class Participant {
    private int user_id;
    private int room_id;
    private String nickname;
    private boolean isAdmin;
    private boolean isHide;
    private Date timestamp;

    public Participant(int user_id, int room_id, String nickname, boolean isAdmin, boolean isHide, Date timestamp) {
        this.user_id = user_id;
        this.room_id = room_id;
        this.nickname = nickname;
        this.isAdmin = isAdmin;
        this.isHide = isHide;
        this.timestamp = timestamp;
    }


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getRoom_id() {
        return room_id;
    }

    public void setRoom_id(int room_id) {
        this.room_id = room_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
