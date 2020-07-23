package com.example.bpmapp;

public class ChatClass {
    private String full_name;
    private String status;
    private String image;

    public ChatClass() {
    }

    public ChatClass(String full_name, String status, String image) {
        this.full_name = full_name;
        this.status = status;
        this.image = image;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }
}
