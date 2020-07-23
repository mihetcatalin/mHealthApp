package com.example.bpmapp;

public class PressureClass {

    private String bloodpressure;
    private String heartrate;
    private String date;

    public PressureClass() {
    }

    public PressureClass(String bloodpressure, String heartrate, String date) {
        this.bloodpressure = bloodpressure;
        this.heartrate = heartrate;
        this.date = date;
    }

    public String getBloodpressure() {
        return bloodpressure;
    }

    public String getHeartrate() {
        return heartrate;
    }

    public String getDate(){
        return date;
    }
}
