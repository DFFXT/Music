/**
  * Copyright 2019 bejson.com 
  */
package com.web.moudle.songSheetEntry.bean;
import java.util.List;

/**
 * Auto-generated: 2019-01-23 20:53:21
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Result {

    private String comment_num;
    private Info info;
    private int iscollect;
    private int song_num;
    private String share_url;
    private int have_more;
    private long listen_num;
    private int share_num;
    private List<Collector> collector;
    private int collect_num;
    private List<Songlist> songlist;
    public void setComment_num(String comment_num) {
         this.comment_num = comment_num;
     }
     public String getComment_num() {
         return comment_num;
     }

    public void setInfo(Info info) {
         this.info = info;
     }
     public Info getInfo() {
         return info;
     }

    public void setIscollect(int iscollect) {
         this.iscollect = iscollect;
     }
     public int getIscollect() {
         return iscollect;
     }

    public void setSong_num(int song_num) {
         this.song_num = song_num;
     }
     public int getSong_num() {
         return song_num;
     }

    public void setShare_url(String share_url) {
         this.share_url = share_url;
     }
     public String getShare_url() {
         return share_url;
     }

    public void setHave_more(int have_more) {
         this.have_more = have_more;
     }
     public int getHave_more() {
         return have_more;
     }

    public void setListen_num(long listen_num) {
         this.listen_num = listen_num;
     }
     public long getListen_num() {
         return listen_num;
     }

    public void setShare_num(int share_num) {
         this.share_num = share_num;
     }
     public int getShare_num() {
         return share_num;
     }

    public void setCollector(List<Collector> collector) {
         this.collector = collector;
     }
     public List<Collector> getCollector() {
         return collector;
     }

    public void setCollect_num(int collect_num) {
         this.collect_num = collect_num;
     }
     public int getCollect_num() {
         return collect_num;
     }

    public void setSonglist(List<Songlist> songlist) {
         this.songlist = songlist;
     }
     public List<Songlist> getSonglist() {
         return songlist;
     }

}