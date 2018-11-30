package com.web.moudle.music.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.web.common.bean.LiveDataWrapper;
import com.web.data.InternetMusic;

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
