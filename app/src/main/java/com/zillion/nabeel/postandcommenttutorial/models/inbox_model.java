package com.zillion.nabeel.postandcommenttutorial.models;

import java.io.Serializable;

/**
 * Created by Nabeel on 11/7/2017.
 */

public class inbox_model implements Serializable {
    private String Sender_name;
    private String sender_email;
    private String sender_image_url;
    private String message;
    private String receiver_email;
    private String receiver_name;
    private long sending_timeStamp;
    private String receiver_image_uri;
    private String message_id;

    public inbox_model(String sender_name, String sender_email, String sender_image_url, String message, String receiver_email, String receiver_name, long sending_timeStamp, String receiver_image_uri, String message_id) {
        Sender_name = sender_name;
        this.sender_email = sender_email;
        this.sender_image_url = sender_image_url;
        this.message = message;
        this.receiver_email = receiver_email;
        this.receiver_name = receiver_name;
        this.sending_timeStamp = sending_timeStamp;
        this.receiver_image_uri = receiver_image_uri;
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
        return sender_email;
    }

    public void setSender_email(String sender_email) {
        this.sender_email = sender_email;
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

    public String getReceiver_email() {
        return receiver_email;
    }

    public void setReceiver_email(String receiver_email) {
        this.receiver_email = receiver_email;
    }

    public String getReceiver_name() {
        return receiver_name;
    }

    public void setReceiver_name(String receiver_name) {
        this.receiver_name = receiver_name;
    }

    public long getSending_timeStamp() {
        return sending_timeStamp;
    }

    public void setSending_timeStamp(long sending_timeStamp) {
        this.sending_timeStamp = sending_timeStamp;
    }

    public String getReceiver_image_uri() {
        return receiver_image_uri;
    }

    public void setReceiver_image_uri(String receiver_image_uri) {
        this.receiver_image_uri = receiver_image_uri;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }
}
