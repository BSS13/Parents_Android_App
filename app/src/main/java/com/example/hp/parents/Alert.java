package com.example.hp.parents;



public class Alert {

    String message;
    Double lat;
    Double lng;
    String date;
    String time;

    public Alert(String message, Double lat, Double lng, String date, String time) {
        this.message = message;
        this.lat = lat;
        this.lng = lng;
        this.date = date;
        this.time = time;
    }
}
