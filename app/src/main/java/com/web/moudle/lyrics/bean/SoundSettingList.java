package com.web.moudle.lyrics.bean;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class SoundSettingList extends DataSupport {
    private String title;
    private List<SoundInfo> soundInfoList;

    public SoundSettingList(String title, List<SoundInfo> soundInfoList) {
        this.title = title;
        this.soundInfoList = soundInfoList;
    }
    public SoundSettingList() { }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SoundInfo> getSoundInfoList() {
        return soundInfoList==null?new ArrayList<>():soundInfoList;
    }

    public void setSoundInfoList(List<SoundInfo> soundInfoList) {
        this.soundInfoList = soundInfoList;
    }

    /**
     * 删除相关联的数据
     * @return effect col
     */
    @Override
    public synchronized int delete() {
        int col=super.delete();
        if(soundInfoList==null)return col;
        for(int i=0;i<soundInfoList.size();i++){
            soundInfoList.get(i).delete();
        }
        return col;
    }
}
