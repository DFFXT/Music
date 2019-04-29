package com.web.moudle.home.mainFragment.subFragment.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


//**数据中key包含中文
public class MusicTagBox {
    private int error_code;
    @JSONField(name = "taglist")
    private String tagList;
    private List<String> tags;

    private HashMap<String,List<MusicTag>> tagMap;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public void setTagList(String tagList) {
        this.tagList = tagList;
    }

    public String getTagList() {
        return tagList;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Map<String, List<MusicTag>> getTagMap() {
        if(tagMap==null){
            tagMap=new HashMap<>();
            JSONObject json=JSONObject.parseObject(tagList);
            for(int i=0;i<tags.size();i++){
                tagMap.put(tags.get(i),JSON.parseArray(json.getString(tags.get(i)),MusicTag.class));
            }
        }
        return tagMap;
    }

    public void setTagMap(HashMap<String, List<MusicTag>> tagMap) {
        this.tagMap = tagMap;
    }
}
