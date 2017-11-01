package com.example.nabeel.postandcommenttutorial.models;
import java.io.Serializable;

/**
 * Created by brad on 2017/02/05.
 */

public class Comment implements Serializable {
    private User user = new User();
    private String commentId;
    private long timeCreated;
    private String comment;
   /* private long numCommentLikes;
    private long numReplies;*/
    public Comment() {
    }

    public Comment(User user, String commentId, long timeCreated, String comment/*,  long numCLikes, long numreply*/) {

        this.user = user;
        this.commentId = commentId;
        this.timeCreated = timeCreated;
        this.comment = comment;
        /*this.numCommentLikes = numCLikes;
        this.numReplies = numreply;*/
    }

   /* public long getNumLikes() { return numCommentLikes; }

    public void setNumLikes(long numLikes) { this.numCommentLikes = numLikes; }

    public long getNumComments() { return numReplies; }

    public void setNumComments(long numComments) { this.numReplies = numComments; }*/

    public User getUser() { return user; }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) { this.commentId = commentId; }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}