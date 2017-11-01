package com.example.nabeel.postandcommenttutorial.models;

import java.io.Serializable;

/**
 * Created by Nabeel on 10/5/2017.
 */

public class Answer implements Serializable {

    private User user = new User();
    private String answerId;
    private long timeCreated;
    private String answer;
    private String audio;
    private String answerImgUrl;
    private long numLikes;

    public Answer() {
    }

    public Answer(User user, String answerId, long timeCreated, String answer , String answerAudiUrl, long numLikes, String answerImgUrl) {

        this.user = user;
        this.answerId = answerId;
        this.timeCreated = timeCreated;
        this.answer = answer;
        this.audio = answerAudiUrl;
        this.numLikes = numLikes;
        this.answerImgUrl = answerImgUrl;
    }

    public String getAnswerImgUrl() {
        return answerImgUrl;
    }

    public void setAnswerImgUrl(String answerImgUrl) {
        this.answerImgUrl = answerImgUrl;
    }

    public long getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(long numLikes) {
        this.numLikes = numLikes;
    }

    public String getAudio() { return audio; }

    public void setAudio(String answerAudiUrl) { this.audio = answerAudiUrl; }

    public User getUser() { return user; }

    public void setUser(User user) {
        this.user = user;
    }

    public String getanswerId() {
        return answerId;
    }

    public void setanswerId(String answerId) { this.answerId = answerId; }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getanswer() {
        return answer;
    }

    public void setanswer(String answer) {
        this.answer = answer;
    }
}