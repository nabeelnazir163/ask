package com.zillion.android.askaalim.models;

public class Chat_model {

    private String message, from, from_image_url;
    private long sending_timeStamp;

    public Chat_model() {
    }

    public Chat_model(String message, long sending_timeStamp, String from, String from_image_url) {
        this.message = message;
        this.sending_timeStamp = sending_timeStamp;
        this.from = from;
        this.from_image_url = from_image_url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getSending_timeStamp() {
        return sending_timeStamp;
    }

    public void setSending_timeStamp(long sending_timeStamp) {
        this.sending_timeStamp = sending_timeStamp;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom_image_url() {
        return from_image_url;
    }

    public void setFrom_image_url(String from_image_url) {
        this.from_image_url = from_image_url;
    }
}
