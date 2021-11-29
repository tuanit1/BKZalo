package com.example.bkzalo.models;

import java.util.Date;

public class User {
    private int id;
    private String name;
    private String image;
    private Date birthday;
    private String phone;
    private String email;
    private String bio;
    private boolean isOnline;

    public User(int id, String name, String image, Date birthday, String phone, String email, String bio, boolean isOnline) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.birthday = birthday;
        this.phone = phone;
        this.email = email;
        this.bio = bio;
        this.isOnline = isOnline;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
