package com.zillion.nabeel.postandcommenttutorial.models;

import java.io.Serializable;

public class Answer implements Serializable {

//    private User user = new User();
    private String answerId;
    private long timeCreated;
    private String answer;
    private String audio;
    private String answerImgUrl;
    private String email;
    private long numLikes;

    public Answer() {
    }

    public Answer(String answerId, long timeCreated, String answer, String audio, String answerImgUrl, String email, long numLikes) {
        this.answerId = answerId;
        this.timeCreated = timeCreated;
        this.answer = answer;
        this.audio = audio;
        this.answerImgUrl = answerImgUrl;
        this.email = email;
        this.numLikes = numLikes;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getAnswerImgUrl() {
        return answerImgUrl;
    }

    public void setAnswerImgUrl(String answerImgUrl) {
        this.answerImgUrl = answerImgUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(long numLikes) {
        this.numLikes = numLikes;
    }
}