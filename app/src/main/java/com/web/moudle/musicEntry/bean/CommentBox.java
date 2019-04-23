package com.web.moudle.musicEntry.bean;

import java.util.ArrayList;

public class CommentBox {
    private ArrayList<CommentItem> commentlist_last;
    private ArrayList<CommentItem> commentlist_hot;
    private int commentlist_last_nums;
    private CommentBaseInfo baseInfo;

    public ArrayList<CommentItem> getCommentlist_last() {
        return commentlist_last;
    }

    public void setCommentlist_last(ArrayList<CommentItem> commentlist_last) {
        this.commentlist_last = commentlist_last;
    }

    public ArrayList<CommentItem> getCommentlist_hot() {
        return commentlist_hot;
    }

    public void setCommentlist_hot(ArrayList<CommentItem> commentlist_hot) {
        this.commentlist_hot = commentlist_hot;
    }

    public int getCommentlist_last_nums() {
        return commentlist_last_nums;
    }

    public void setCommentlist_last_nums(int commentlist_last_nums) {
        this.commentlist_last_nums = commentlist_last_nums;
    }

    public CommentBaseInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(CommentBaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }
}
