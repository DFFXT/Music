/**
  * Copyright 2019 bejson.com 
  */
package com.web.moudle.billboradDetail.bean;
import com.alibaba.fastjson.annotation.JSONField;
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo;

import java.util.List;

/**
 * Auto-generated: 2019-02-18 9:42:45
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class RecommendMusicBox {

    private String bg_color;
    @JSONField(name = "list")
    private List<SimpleMusicInfo> list;
    private int total;
    private long date;
    private String color;
    private String bg_pic;
    private int havemore;
    public void setBg_color(String bg_color) {
         this.bg_color = bg_color;
     }
     public String getBg_color() {
         return bg_color;
     }

    public void setList(List<SimpleMusicInfo> list) {
         this.list = list;
     }
     public List<SimpleMusicInfo> getList() {
         return list;
     }

    public void setTotal(int total) {
         this.total = total;
     }
     public int getTotal() {
         return total;
     }

    public void setDate(long date) {
         this.date = date;
     }
     public long getDate() {
         return date;
     }

    public void setColor(String color) {
         this.color = color;
     }
     public String getColor() {
         return color;
     }

    public void setBg_pic(String bg_pic) {
         this.bg_pic = bg_pic;
     }
     public String getBg_pic() {
         return bg_pic;
     }

    public void setHavemore(int havemore) {
         this.havemore = havemore;
     }
     public int getHavemore() {
         return havemore;
     }

}