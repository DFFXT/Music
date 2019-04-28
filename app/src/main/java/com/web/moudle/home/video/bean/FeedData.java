package com.web.moudle.home.video.bean;

import java.util.List;

public class FeedData {
    private int feed_type;
    private int review_num;
    private int likes_num;
    private String user_id;
    private int is_liked;
    private String author;
    private int comment_num;
    private String share_pic;
    private List<String> img;
    private String method;
    private String share_url;
    private VideoContent content;
    private String title;
    private String url;
    private long time;
    private int has_claim;
    private int top_flag;
    private String feed_id;

    public int getFeed_type() {
        return feed_type;
    }

    public void setFeed_type(int feed_type) {
        this.feed_type = feed_type;
    }

    public int getReview_num() {
        return review_num;
    }

    public void setReview_num(int review_num) {
        this.review_num = review_num;
    }

    public int getLikes_num() {
        return likes_num;
    }

    public void setLikes_num(int likes_num) {
        this.likes_num = likes_num;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getIs_liked() {
        return is_liked;
    }

    public void setIs_liked(int is_liked) {
        this.is_liked = is_liked;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public String getShare_pic() {
        return share_pic;
    }

    public void setShare_pic(String share_pic) {
        this.share_pic = share_pic;
    }

    public List<String> getImg() {
        return img;
    }

    public void setImg(List<String> img) {
        this.img = img;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getShare_url() {
        return share_url;
    }

    public void setShare_url(String share_url) {
        this.share_url = share_url;
    }

    public VideoContent getContent() {
        return content;
    }

    public void setContent(VideoContent content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getHas_claim() {
        return has_claim;
    }

    public void setHas_claim(int has_claim) {
        this.has_claim = has_claim;
    }

    public int getTop_flag() {
        return top_flag;
    }

    public void setTop_flag(int top_flag) {
        this.top_flag = top_flag;
    }

    public String getFeed_id() {
        return feed_id;
    }

    public void setFeed_id(String feed_id) {
        this.feed_id = feed_id;
    }
}
