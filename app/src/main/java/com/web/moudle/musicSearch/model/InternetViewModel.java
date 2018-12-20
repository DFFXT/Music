package com.web.moudle.musicSearch.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.web.common.base.BaseObserver;
import com.web.common.bean.LiveDataWrapper;
import com.web.data.InternetMusicDetail;
import com.web.data.InternetMusicDetailList;

import org.jetbrains.annotations.NotNull;

/**
 * 网络搜索
 */
public class InternetViewModel extends ViewModel {

    public static final int CODE_JSON_ERROR=1;
    public static final int CODE_NET_ERROR=2;
    public static final int CODE_URL_ERROR=3;
    public static final int CODE_OK=4;
    public static final int CODE_NO_DATA=5;
    public static final int CODE_ERROR=6;

    private InternetDataSource dataSource;
    private PagedList.Config config;
    private LiveData<PagedList<InternetMusicDetail>> pagedListLiveData;
    private MutableLiveData<InternetMusicDetailList> musicDetail;
    private MutableLiveData<LiveDataWrapper> status;
    private LiveDataWrapper wrapper;
    private InternetMusicModel model=new InternetMusicModel();

    public InternetViewModel() {
        wrapper=new LiveDataWrapper();
        status=new MutableLiveData<>();
        musicDetail=new MutableLiveData<>();
        dataSource=new InternetDataSource(status);
        config=new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(10)
                .setPrefetchDistance(10)
                .setEnablePlaceholders(false)
                .build();
    }

    public MutableLiveData<InternetMusicDetailList> observerMusicDetail() {
        return musicDetail;
    }

    public LiveData<LiveDataWrapper> getStatus() {
        return status;
    }

    public LiveData<PagedList<InternetMusicDetail>> getMusicList(){
        if(pagedListLiveData==null)
            pagedListLiveData= new LivePagedListBuilder<>(new DataSource.Factory<String,InternetMusicDetail>(){
                @Override
                public DataSource<String,InternetMusicDetail> create() {
                    return dataSource;
                }
            },config).build();
        return pagedListLiveData;
    }
    public void setKeyWords(String keyWords){
        dataSource.setKeyWords(keyWords);
        pagedListLiveData=null;
    }
    public void getMusicDetail(String songIds){
        model.getMusicDetailAsync(songIds)
                .subscribe(new BaseObserver<InternetMusicDetailList>() {
                    @Override
                    public void onNext(InternetMusicDetailList internetMusicDetailList) {
                        musicDetail.setValue(internetMusicDetailList);
                    }

                    @Override
                    public void error(@NotNull Throwable e) {
                        e.printStackTrace();
                        wrapper.setCode(CODE_ERROR);
                        status.postValue(wrapper);
                    }
                });
    }
}
