package com.web.model.internet.music.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

import com.web.config.GetFiles;
import com.web.data.InternetMusic;
import com.web.model.internet.music.bean.InternetMusicWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 网络搜索
 */
public class InternetViewModel extends ViewModel {
    private MutableLiveData<InternetMusicWrapper> data=new MutableLiveData<>();
    private boolean isSearch;

    public MutableLiveData<InternetMusicWrapper> getData() {
        return data;
    }

    public void search(String keyword, final int page){
        if(TextUtils.isEmpty(keyword)||isSearch){
            return;
        }
        try {//---中文转url
            keyword= URLEncoder.encode(keyword,"utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();}
        final String url="http://mobilecdn.kugou.com/api/v3/search/song?format=jsonp&keyword="+keyword+"&page="+page;
        new Thread(() -> {
            isSearch = true;
            String rowData = new GetFiles().readNetData(url);
            InternetMusicWrapper wrapper=data.getValue();
            if(wrapper==null){
                wrapper=new InternetMusicWrapper();
            }else
                wrapper.getList().clear();
            try {
                if (rowData == null) {
                    throw new JSONException("");
                }
                rowData = rowData.substring(1, rowData.length() - 1);//--网络数据有括号需要清除

                JSONObject jsonObject = new JSONObject(rowData);
                JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("info");
                for (int index = 0; index < jsonArray.length(); index++) {
                    try {
                        InternetMusic music = new InternetMusic();
                        JSONObject obj = jsonArray.getJSONObject(index);
                        music.setMusicName(obj.getString("songname"));
                        music.setSingerName(obj.getString("singername"));
                        music.setFullSize(obj.getInt("filesize"));
                        music.setHash(obj.getString("hash"));
                        wrapper.getList().add(music);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                wrapper.setCode(InternetMusicWrapper.OK);
            } catch (JSONException e) {
                wrapper.setCode(InternetMusicWrapper.ERROR);
                e.printStackTrace();
            }
            isSearch = false;
            data.postValue(wrapper);
            }).start();
    }
}
