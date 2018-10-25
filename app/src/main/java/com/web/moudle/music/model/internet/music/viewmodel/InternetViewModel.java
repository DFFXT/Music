package com.web.moudle.music.model.internet.music.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.ItemKeyedDataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PageKeyedDataSource;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.web.common.bean.LiveDataWrapper;
import com.web.config.GetFiles;
import com.web.config.L;
import com.web.data.InternetMusic;
import com.web.moudle.music.model.control.adapter.InternetDataSource;
import com.web.moudle.music.model.internet.music.bean.InternetMusicWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 网络搜索
 */
public class InternetViewModel extends ViewModel {
    private InternetDataSource dataSource;
    private PagedList.Config config;
    private LiveData<PagedList<InternetMusic>> pagedListLiveData;
    private MutableLiveData<LiveDataWrapper> status;

    public InternetViewModel() {
        status=new MutableLiveData<>();
        dataSource=new InternetDataSource(status);
        config=new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(10)
                .setPrefetchDistance(10)
                .setEnablePlaceholders(false)
                .build();
    }

    public LiveData<LiveDataWrapper> getStatus() {
        return status;
    }

    public LiveData<PagedList<InternetMusic>> getMusicList(){
        if(pagedListLiveData==null)
            pagedListLiveData= new LivePagedListBuilder<>(new DataSource.Factory<String,InternetMusic>(){
                @Override
                public DataSource<String,InternetMusic> create() {
                    return dataSource;
                }
            },config).build();
        return pagedListLiveData;
    }
    public void setKeyWords(String keyWords){
        dataSource.setKeyWords(keyWords);
        pagedListLiveData=null;
    }
}
