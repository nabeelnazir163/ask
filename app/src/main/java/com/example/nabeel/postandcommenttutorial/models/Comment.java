package com.example.nabeel.postandcommenttutorial.models;
import java.io.Serializable;

/**
 * Created by brad on 2017/02/05.
 */

public class Comment implements Serializable {
    private String Email;
    private String commentId;
    private long timeCreated;
    private String comment;
    private long numReply;
   /* private long numCommentLikes;
    private long numReplies;*/
    public Comment() {
    }

    public Comment(String email, String commentId, long timeCreated, String comment/*,  long numCLikes,*/, long numreply) {

        this.Email = email;
        this.commentId = commentId;
        this.timeCreated = timeCreated;
        this.comment = comment;
        this.numReply = numreply;
        /*this.numCommentLikes = numCLikes;
        this.numReplies = numreply;*/
    }

   /* public long getNumLikes() { return numCommentLikes; }

    public void setNumLikes(long numLikes) { this.numCommentLikes = numLikes; }

    public long getNumComments() { return numReplies; }

    public void setNumComments(long numComments) { this.numReplies = numComments; }*/

    public long getNumReply() {
        return numReply;
    }

    public void setNumReply(long numReply) {
        this.numReply = numReply;
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

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }
}