package com.web.data;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MusicGroup extends DataSupport {
    private String groupName;
    private int id;
    private List<Music> music=new ArrayList<>();//***外表

    public void setId(int groupId) {
        this.id = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getId() {
        return id;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
