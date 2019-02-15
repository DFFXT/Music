package com.web.moudle.musicSearch.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import com.web.common.bean.LiveDataWrapper;
import com.web.data.InternetMusicDetailList;
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo;
import com.web.moudle.musicSearch.model.InternetDataSource;

/**
 * 网络搜索
 */
public class InternetViewModel extends ViewModel {


    private InternetDataSource dataSource;
    private PagedList.Config config;
    private LiveData<PagedList<SimpleMusicInfo>> pagedListLiveData;
    private MutableLiveData<InternetMusicDetailList> musicDetail;
    private MutableLiveData<LiveDataWrapper<Integer>> status;

    public InternetViewModel() {
        status = new MutableLiveData<>();
        musicDetail = new MutableLiveData<>();
        dataSource = new InternetDataSource(status);
        config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(10)
                .setPageSize(10)
                .setPrefetchDistance(10)
                .setEnablePlaceholders(false)
                .build();
    }

    public MutableLiveData<InternetMusicDetailList> observerMusicDetail() {
        return musicDetail;
    }

    public LiveData<LiveDataWrapper<Integer>> getStatus() {
        return status;
    }

    public LiveData<PagedList<SimpleMusicInfo>> getMusicList() {
        if (pagedListLiveData == null)
            pagedListLiveData = new LivePagedListBuilder<>(new DataSource.Factory<String, SimpleMusicInfo>() {
                @Override
                public DataSource<String, SimpleMusicInfo> create() {
                    return dataSource;
                }
            }, config).build();
        return pagedListLiveData;
    }

    public void setKeyWords(String keyWords) {
        dataSource.setKeyWords(keyWords);
        pagedListLiveData = null;
    }


}
