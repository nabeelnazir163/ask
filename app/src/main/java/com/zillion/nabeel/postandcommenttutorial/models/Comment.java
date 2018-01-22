package com.zillion.nabeel.postandcommenttutorial.models;
import java.io.Serializable;

/**
 * Created by brad on 2017/02/05.
 */

public class Comment implements Serializable {
    private String email;
    private String commentId;
    private long timeCreated;
    private String comment;
    private long numReply;
   /* private long numCommentLikes;
    private long numReplies;*/
    public Comment() {
    }

    public Comment(String email, String commentId, long timeCreated, String comment, long numReply) {
        this.email = email;
        this.commentId = commentId;
        this.timeCreated = timeCreated;
        this.comment = comment;
        this.numReply = numReply;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

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

    public long getNumReply() {
        return numReply;
    }

    public void setNumReply(long numReply) {
        this.numReply = numReply;
    }
}
