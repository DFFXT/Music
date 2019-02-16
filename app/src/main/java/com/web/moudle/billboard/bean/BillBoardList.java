/**
  * Copyright 2019 bejson.com 
  */
package com.web.moudle.billboard.bean;
import java.util.List;

/**
 * Auto-generated: 2019-02-15 15:11:7
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class BillBoardList {

    private List<Content> content;
    private int error_code;
    public void setContent(List<Content> content) {
         this.content = content;
     }
     public List<Content> getContent() {
         return content;
     }

    public void setError_code(int error_code) {
         this.error_code = error_code;
     }
     public int getError_code() {
         return error_code;
     }

}