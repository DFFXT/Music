/**
  * Copyright 2019 bejson.com 
  */
package com.web.moudle.videoEntry.bean;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * Auto-generated: 2019-01-28 12:33:0
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class VideoInfoBox {

    private String share_url;
    private String video_type;
    private String max_definition;
    private String share_pic;
    @JSONField(name = "mv_info")
    private MvInfo mv_info;
    @JSONField(name = "video_info")
    private VideoInfo video_info;
    @JSONField(name = "files")
    private JSONObject rowJson;

    private FileInfo fileInfo;

    private String min_definition;
    public void setShare_url(String share_url) {
         this.share_url = share_url;
     }
     public String getShare_url() {
         return share_url;
     }

    public void setVideo_type(String video_type) {
         this.video_type = video_type;
     }
     public String getVideo_type() {
         return video_type;
     }

    public void setMax_definition(String max_definition) {
         this.max_definition = max_definition;
     }
     public String getMax_definition() {
         return max_definition;
     }

    public void setShare_pic(String share_pic) {
         this.share_pic = share_pic;
     }
     public String getShare_pic() {
         return share_pic;
     }

    public void setMv_info(MvInfo mv_info) {
         this.mv_info = mv_info;
     }
     public MvInfo getMv_info() {
         return mv_info;
     }

    public void setVideo_info(VideoInfo video_info) {
         this.video_info = video_info;
     }
     public VideoInfo getVideo_info() {
         return video_info;
     }

    public void setRowJson(JSONObject rowJson) {
         this.rowJson = rowJson;
     }
     public JSONObject getRowJson() {
         return rowJson;
     }

    public void setMin_definition(String min_definition) {
         this.min_definition = min_definition;
     }
     public String getMin_definition() {
         return min_definition;
     }


    public FileInfo getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }
}