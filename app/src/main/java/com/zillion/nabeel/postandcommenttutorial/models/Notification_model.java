package com.zillion.nabeel.postandcommenttutorial.models;

public class Notification_model {

    private String image;
    private String name;
    private String notification;
    private long time;
    private Post post;

    public Notification_model() {
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Notification_model(String imageurl, String name, String notification, long time, Post post) {
        this.image = imageurl;
        this.name = name;
        this.notification = notification;
        this.time = time;
        this.post = post;

    }



    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
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
