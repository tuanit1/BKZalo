/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:16 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 11/29/21, 4:56 PM
 * /
 */

package com.example.bkzalo.models;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private int id;
    private String name;
    private String image;
    private String image_url;
    private Date birthday;
    private String phone;
    private String bio;
    private String email;
    private String password;
    private boolean isOnline;

    public User(int id, String name, String phone, String email)
    {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public User(String name, String phone, String email, String password) {
        this.phone = phone;
        this.name = name;
        this.email = email;
        this.password = password;
    }


    public User(int id, String name, String image, String image_url, Date birthday, String phone, String bio, String email, boolean isOnline) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.image_url = image_url;
        this.birthday = birthday;
        this.phone = phone;
        this.bio = bio;
        this.email=email;
        this.isOnline = isOnline;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }
}
