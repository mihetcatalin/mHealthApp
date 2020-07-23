package com.example.bpmapp;

import android.graphics.Bitmap;

public class AddChatClass {
    private String full_name;
    private String status;
    private String image;

    public AddChatClass(){
        //empty constructor
    }

    public AddChatClass(String name, String status, String image){
        this.full_name = name;
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
