/**
  * Copyright 2019 bejson.com 
  */
package com.web.moudle.billboard.bean;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

/**
 * Auto-generated: 2019-02-15 15:11:7
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Content {

    private String name;
    private int type;
    private int count;
    private String comment;
    private String web_url;
    private String pic_s192;
    private String pic_s444;
    private String pic_s260;
    private String pic_s210;
    @JSONField(name = "content")
    private List<BillBoardMusic> musicList;
    private String color;
    private String bg_color;
    private String bg_pic;
    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }

    public void setType(int type) {
         this.type = type;
     }
     public int getType() {
         return type;
     }

    public void setCount(int count) {
         this.count = count;
     }
     public int getCount() {
         return count;
     }

    public void setComment(String comment) {
         this.comment = comment;
     }
     public String getComment() {
         return comment;
     }

    public void setWeb_url(String web_url) {
         this.web_url = web_url;
     }
     public String getWeb_url() {
         return web_url;
     }

    public void setPic_s192(String pic_s192) {
         this.pic_s192 = pic_s192;
     }
     public String getPic_s192() {
         return pic_s192;
     }

    public void setPic_s444(String pic_s444) {
         this.pic_s444 = pic_s444;
     }
     public String getPic_s444() {
         return pic_s444;
     }

    public void setPic_s260(String pic_s260) {
         this.pic_s260 = pic_s260;
     }
     public String getPic_s260() {
         return pic_s260;
     }

    public void setPic_s210(String pic_s210) {
         this.pic_s210 = pic_s210;
     }
     public String getPic_s210() {
         return pic_s210;
     }

    public void setMusicList(List<BillBoardMusic> musicList) {
         this.musicList = musicList;
     }
     public List<BillBoardMusic> getMusicList() {
         return musicList;
     }

    public void setColor(String color) {
         this.color = color;
     }
     public String getColor() {
         return color;
     }

    public void setBg_color(String bg_color) {
         this.bg_color = bg_color;
     }
     public String getBg_color() {
         return bg_color;
     }

    public void setBg_pic(String bg_pic) {
         this.bg_pic = bg_pic;
     }
     public String getBg_pic() {
         return bg_pic;
     }

}