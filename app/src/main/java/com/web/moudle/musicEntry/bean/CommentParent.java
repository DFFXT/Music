package com.web.moudle.musicEntry.bean;

public class CommentParent {
    private String com_id;
    private String comment;
    private int status;
    private CommentAuthor author;

    public CommentAuthor getAuthor() {
        return author;
    }

    public void setAuthor(CommentAuthor author) {
        this.author = author;
    }

    public String getCom_id() {
        return com_id;
    }

    public void setCom_id(String com_id) {
        this.com_id = com_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
