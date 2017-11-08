package com.example.nabeel.postandcommenttutorial.models;

import java.io.Serializable;

/**
 * Created by Nabeel on 11/7/2017.
 */

public class inbox_model implements Serializable {
    private String Sender_name;
    private String Sender_email;
    private String sender_image_url;
    private String message;
    private String message_id;
    private long SendingtimeStamp;

    public inbox_model(String sender_name, String sender_email, String sender_image_url, String message, long sendingtimeStamp, String message_id) {
        this.Sender_name = sender_name;
        this.Sender_email = sender_email;
        this.sender_image_url = sender_image_url;
        this.message = message;
        this.SendingtimeStamp = sendingtimeStamp;
        this.message_id = message_id;
    }

    public inbox_model() {
    }

    public String getSender_name() {
        return Sender_name;
    }

    public void setSender_name(String sender_name) {
        Sender_name = sender_name;
    }

    public String getSender_email() {
        return Sender_email;
    }

    public void setSender_email(String sender_email) {
        Sender_email = sender_email;
    }

    public String getSender_image_url() {
        return sender_image_url;
    }

    public void setSender_image_url(String sender_image_url) {
        this.sender_image_url = sender_image_url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getSendingtimeStamp() {
        return SendingtimeStamp;
    }

    public void setSendingtimeStamp(long sendingtimeStamp) {
        SendingtimeStamp = sendingtimeStamp;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }
}
