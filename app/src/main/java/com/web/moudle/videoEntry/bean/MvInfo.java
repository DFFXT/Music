/**
  * Copyright 2019 bejson.com 
  */
package com.web.moudle.videoEntry.bean;
import java.util.Date;
import java.util.List;

/**
 * Auto-generated: 2019-01-28 12:33:0
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class MvInfo {

    private String mv_id;
    private String all_artist_id;
    private String thumbnail;
    private Date publishtime;
    private String provider;
    private String del_status;
    private String artist_id;
    private String artist;
    private String thumbnail3;
    private String title;
    private String thumbnail2;
    private List<Artist_list> artist_list;
    private int play_nums;
    private String subtitle;
    private String aliastitle;
    public void setMv_id(String mv_id) {
         this.mv_id = mv_id;
     }
     public String getMv_id() {
         return mv_id;
     }

    public void setAll_artist_id(String all_artist_id) {
         this.all_artist_id = all_artist_id;
     }
     public String getAll_artist_id() {
         return all_artist_id;
     }

    public void setThumbnail(String thumbnail) {
         this.thumbnail = thumbnail;
     }
     public String getThumbnail() {
         return thumbnail;
     }

    public void setPublishtime(Date publishtime) {
         this.publishtime = publishtime;
     }
     public Date getPublishtime() {
         return publishtime;
     }

    public void setProvider(String provider) {
         this.provider = provider;
     }
     public String getProvider() {
         return provider;
     }

    public void setDel_status(String del_status) {
         this.del_status = del_status;
     }
     public String getDel_status() {
         return del_status;
     }

    public void setArtist_id(String artist_id) {
         this.artist_id = artist_id;
     }
     public String getArtist_id() {
         return artist_id;
     }

    public void setArtist(String artist) {
         this.artist = artist;
     }
     public String getArtist() {
         return artist;
     }

    public void setThumbnail3(String thumbnail3) {
         this.thumbnail3 = thumbnail3;
     }
     public String getThumbnail3() {
         return thumbnail3;
     }

    public void setTitle(String title) {
         this.title = title;
     }
     public String getTitle() {
         return title;
     }

    public void setThumbnail2(String thumbnail2) {
         this.thumbnail2 = thumbnail2;
     }
     public String getThumbnail2() {
         return thumbnail2;
     }

    public void setArtist_list(List<Artist_list> artist_list) {
         this.artist_list = artist_list;
     }
     public List<Artist_list> getArtist_list() {
         return artist_list;
     }

    public void setPlay_nums(int play_nums) {
         this.play_nums = play_nums;
     }
     public int getPlay_nums() {
         return play_nums;
     }

    public void setSubtitle(String subtitle) {
         this.subtitle = subtitle;
     }
     public String getSubtitle() {
         return subtitle;
     }

    public void setAliastitle(String aliastitle) {
         this.aliastitle = aliastitle;
     }
     public String getAliastitle() {
         return aliastitle;
     }

}