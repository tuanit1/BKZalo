package com.example.bkzalo.models;

public class Room {
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
