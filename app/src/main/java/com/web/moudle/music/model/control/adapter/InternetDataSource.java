package com.web.moudle.music.model.control.adapter;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.web.common.bean.LiveDataWrapper;
import com.web.config.GetFiles;
import com.web.data.InternetMusic;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class InternetDataSource extends PageKeyedDataSource<String,InternetMusic> {
    private String keyWords;
    private int page=1;
    private MutableLiveData<LiveDataWrapper> liveData;
    private LiveDataWrapper wrapper;
    public static final int CODE_JSON_ERROR=1;
    public static final int CODE_NET_ERROR=2;
    public static final int CODE_URL_ERROR=3;
    public static final int CODE_OK=4;
    public static final int CODE_NO_DATA=5;

    public InternetDataSource(@NonNull MutableLiveData<LiveDataWrapper> liveData){
        this.liveData=liveData;
        wrapper=new LiveDataWrapper();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull LoadInitialCallback<String, InternetMusic> callback) {
        List<InternetMusic> list;
        list=search(keyWords,page);
        callback.onResult(list,"","");
    }

    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, InternetMusic> callback) {
    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, InternetMusic> callback) {
        List<InternetMusic> list;
        list=search(keyWords,page+1);
        setPage(page+1);
        callback.onResult(list,"");
    }

    public List<InternetMusic> search(String keyword, final int page){
        List<InternetMusic> musicList=new ArrayList<>();
        if(TextUtils.isEmpty(keyword)){
            return musicList;
        }
        try {
            keyword= URLEncoder.encode(keyword,"utf-8");
            final String url="http://mobilecdn.kugou.com/api/v3/search/song?format=jsonp&keyword="+keyword+"&page="+page;
            String rowData = new GetFiles().readNetData(url);
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
                    music.setSqHash(obj.getString("sqhash"));
                    music.setDuration(obj.getInt("duration"));
                    music.set_320Hash(obj.getString("320hash"));
                    music.set_320FileSize(obj.getInt("320filesize"));
                    music.setSqFileSize(obj.getInt("sqfilesize"));
                    musicList.add(music);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(musicList.size()==0){
                wrapper.setCode(CODE_NO_DATA);
            }else {
                wrapper.setCode(CODE_OK);
            }
        }catch (JSONException e){
            wrapper.setCode(CODE_JSON_ERROR);
        }catch (MalformedURLException e){
            wrapper.setCode(CODE_URL_ERROR);
        }catch (IOException e){
            wrapper.setCode(CODE_NET_ERROR);
        }
        liveData.postValue(wrapper);
        return musicList;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
        setPage(1);
    }

    private void setPage(int page) {
        this.page = page;
    }



}
