/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:16 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 11/9/21, 1:16 AM
 * /
 */

package com.example.bkzalo.models;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private int id;
    private int user_id;
    private int room_id;
    private String type;
    private String message;
    private Date time;
    private boolean isRemove;
    private boolean isSeen;
    private String name;
    private String image;
    private String nickname;

    public Message(int id, int user_id, int room_id, String type, String message, Date time, boolean isRemove, boolean isSeen, String name, String image, String nickname) {
        this.id = id;
        this.user_id = user_id;
        this.room_id = room_id;
        this.type = type;
        this.message = message;
        this.time = time;
        this.isRemove = isRemove;
        this.isSeen = isSeen;
        this.name = name;
        this.image = image;
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isRemove() {
        return isRemove;
    }

    public void setRemove(boolean remove) {
        isRemove = remove;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
