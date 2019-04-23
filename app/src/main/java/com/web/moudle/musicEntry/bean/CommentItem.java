package com.web.moudle.musicEntry.bean;

public class CommentItem {
    private int isAuthor;
    private int islike;
    private CommentParent com_parent;
    private String comment;
    private String com_id;
    private int zan_num;
    private long ctime;
    private CommentAuthor author;

    public int getIsAuthor() {
        return isAuthor;
    }

    public void setIsAuthor(int isAuthor) {
        this.isAuthor = isAuthor;
    }

    public int getIslike() {
        return islike;
    }

    public void setIslike(int islike) {
        this.islike = islike;
    }

    public CommentParent getCom_parent() {
        return com_parent;
    }

    public void setCom_parent(CommentParent com_parent) {
        this.com_parent = com_parent;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCom_id() {
        return com_id;
    }

    public void setCom_id(String com_id) {
        this.com_id = com_id;
    }

    public int getZan_num() {
        return zan_num;
    }

    public void setZan_num(int zan_num) {
        this.zan_num = zan_num;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public CommentAuthor getAuthor() {
        return author;
    }

    public void setAuthor(CommentAuthor author) {
        this.author = author;
    }
}
