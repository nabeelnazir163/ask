package com.example.nabeel.postandcommenttutorial.models;


import java.io.Serializable;

/**
 * Created by brad on 2017/02/05.
 */

public class Post implements Serializable {
    private String postText;
    private String postImageUrl;
    private String postId;
//    private User user;
    private String Email;
    private long numSeen;
    private long numLikes;
    private long numComments;
    private long timeCreated;
    private long numAnswers;
//    private String FCM_TOKEN;

    public Post() {
    }

    public Post(String email,/*User user,*/ String postText, String postImageUrl, String postId, long numLikes, long numComments, long timeCreated, long numAnswers, long numSeen/*, String FCM_TOKEN*/) {

        this.Email = email;
        this.postText = postText;
        this.postImageUrl = postImageUrl;
        this.postId = postId;
        this.numLikes = numLikes;
        this.numComments = numComments;
        this.timeCreated = timeCreated;
        this.numAnswers = numAnswers;
        this.numSeen = numSeen;
//        this.user = user;
//        this.FCM_TOKEN = FCM_TOKEN;
    }

    public long getNumAnswers() {
        return numAnswers;
    }

    public void setNumAnswers(long numAnswers) {
        this.numAnswers = numAnswers;
    }

    public long getNumSeen() {
        return numSeen;
    }

    public void setNumSeen(long numSeen) {
        this.numSeen = numSeen;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public long getNumLikes() {
        return numLikes;
    }

    public void setNumLikes(long numLikes) {
        this.numLikes = numLikes;
    }

    public long getNumComments() {
        return numComments;
    }

    public void setNumComments(long numComments) {
        this.numComments = numComments;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

//    public User getUser() {
//        return user;
//    }
//
//    public void setUser(User user) {
//        this.user = user;
//    }
}