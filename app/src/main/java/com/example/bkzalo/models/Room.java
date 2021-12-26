/*
 * Copyright (c) 2021.
 *  /**
 *  Created by DoThanhTuan-LuuYenNhi-LeThiThuHuong on 12/25/21, 5:16 PM
 *  Copyright (c) 2021 . All rights reserved.
 *  Last modified 10/31/21, 4:24 PM
 * /
 */

package com.example.bkzalo.models;

import java.io.Serializable;

public class Room implements Serializable {
    private int id;
    private String name;
    private String image;
    private String background;
    private String type;

    public Room(int id, String name, String image, String background, String type) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.background = background;
        this.type = type;
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

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
