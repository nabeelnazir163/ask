package com.example.nabeel.postandcommenttutorial.models;

import java.io.Serializable;

/**
 * Created by Nabeel on 9/28/2017.
 */

public class reply implements Serializable {

    private String email;
    private String replyId;
    private long timeCreated;
    private String reply_text;

    public reply() {
    }

    public reply(String email, String replyId, long timeCreated, String reply_text) {
        this.email = email;
        this.replyId = replyId;
        this.timeCreated = timeCreated;
        this.reply_text = reply_text;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getReply_text() {
        return reply_text;
    }

    public void setReply_text(String reply_text) {
        this.reply_text = reply_text;
    }
}
