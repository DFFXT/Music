package com.web.data;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class MusicGroup extends DataSupport {
    private String groupName;
    private int groupId;
    private List<Music> music=new ArrayList<>();//***外表

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
