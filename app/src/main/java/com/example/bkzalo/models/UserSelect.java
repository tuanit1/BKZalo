package com.example.bkzalo.models;

public class UserSelect {
    private int id;
    private String name;
    private String image;
    private boolean isChecked;

    public UserSelect(int id, String name, String image, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.isChecked = isChecked;
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
