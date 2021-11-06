package com.example.bkzalo.models;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private int id;
    private String name;
    private String image;
    private Date birthday;
    private String phone;
    private String password;
    private String bio;

    public User(int id, String name, String image, Date birthday, String phone, String password, String bio) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.birthday = birthday;
        this.phone = phone;
        this.password = password;
        this.bio = bio;
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
}
