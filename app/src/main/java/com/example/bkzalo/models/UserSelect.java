/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:16 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 10/29/21, 10:42 PM
 * /
 */

package com.example.bkzalo.models;

public class UserSelect {
    private int id;
    private String name;
    private String phone;
    private String image;
    private boolean isChecked;

    public UserSelect(int id, String name, String phone, String image, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.isChecked = isChecked;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
