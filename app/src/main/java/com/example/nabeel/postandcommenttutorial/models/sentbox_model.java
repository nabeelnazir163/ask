package com.example.nabeel.postandcommenttutorial.models;

/**
 * Created by Nabeel on 1/8/2018.
 */

public class sentbox_model {
    private String receiver_name;
    private String receiver_email;
    private String receiver_image_url;
    private String message;
    private String message_id;
    private long sending_timeStamp;

    public sentbox_model(String receiver_name, String receiver_email, String receiver_image_url, String message, String message_id, long sending_timeStamp) {
        this.receiver_name = receiver_name;
        this.receiver_email = receiver_email;
        this.receiver_image_url = receiver_image_url;
        this.message = message;
        this.message_id = message_id;
        this.sending_timeStamp = sending_timeStamp;
    }

    public sentbox_model() {
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public String getReceiver_email() {
        return receiver_email;
    }

    public void setReceiver_email(String receiver_email) {
        this.receiver_email = receiver_email;
    }

    public String getReceiver_image_url() {
        return receiver_image_url;
    }

    public void setReceiver_image_url(String receiver_image_url) {
        this.receiver_image_url = receiver_image_url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public long getSending_timeStamp() {
        return sending_timeStamp;
    }

    public void setSending_timeStamp(long sending_timeStamp) {
        this.sending_timeStamp = sending_timeStamp;
    }
}
