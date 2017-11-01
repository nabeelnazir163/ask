package com.example.nabeel.postandcommenttutorial.models;

/**
 * Created by Nabeel on 10/18/2017.
 */

public class Bookmark {

    private String postText;
    private String postImageUrl;
    private String postId;
    private long numLikes;
    private long numComments;
    private long timeCreated;
    private long numAnswers;
    private String userImage;

    public Bookmark() {
    }

    public Bookmark(String postText, String postImageUrl, String postId, long numLikes, long numComments, long timeCreated, long numAnswers, String userImage, String userName) {
        this.postText = postText;
        this.postImageUrl = postImageUrl;
        this.postId = postId;
        this.numLikes = numLikes;
        this.numComments = numComments;
        this.timeCreated = timeCreated;
        this.numAnswers = numAnswers;
        this.userImage = userImage;
        this.userName = userName;
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

    public long getNumAnswers() {
        return numAnswers;
    }

    public void setNumAnswers(long numAnswers) {
        this.numAnswers = numAnswers;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;

}
