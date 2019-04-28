package com.web.moudle.home.video.bean;

import java.util.List;

public class VideoRecommendBox {
    private List<FeedData> feed_data;
    private int error_code;
    private List<String> keyword;

    public List<FeedData> getFeed_data() {
        return feed_data;
    }

    public void setFeed_data(List<FeedData> feed_data) {
        this.feed_data = feed_data;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public List<String> getKeyword() {
        return keyword;
    }

    public void setKeyword(List<String> keyword) {
        this.keyword = keyword;
    }
}
