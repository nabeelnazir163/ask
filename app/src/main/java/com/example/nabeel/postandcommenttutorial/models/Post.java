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
    private String email;
    private long numSeen;
    private long numLikes;
    private long numComments;
    private long timeCreated;
    private long numAnswers;
//    private String FCM_TOKEN;

    public Post() {
    }

    public Post(String postText, String postImageUrl, String postId, String email, long numSeen, long numLikes, long numComments, long timeCreated, long numAnswers) {
        this.postText = postText;
        this.postImageUrl = postImageUrl;
        this.postId = postId;
        this.email = email;
        this.numSeen = numSeen;
        this.numLikes = numLikes;
        this.numComments = numComments;
        this.timeCreated = timeCreated;
        this.numAnswers = numAnswers;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getNumSeen() {
        return numSeen;
    }

    public void setNumSeen(long numSeen) {
        this.numSeen = numSeen;
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

    public long getNumAnswers() {
        return numAnswers;
    }

    public void setNumAnswers(long numAnswers) {
        this.numAnswers = numAnswers;
    }
}