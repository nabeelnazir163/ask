package com.zillion.nabeel.postandcommenttutorial.models;

/**
 * Created by Nabeel on 10/30/2017.
 */

public class Notification_model {

    private String imageurl;
    private String name;
    private String notification;

    public Notification_model() {
    }

    public Notification_model(String imageurl, String name, String notification) {
        this.imageurl = imageurl;
        this.name = name;
        this.notification = notification;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }
}
