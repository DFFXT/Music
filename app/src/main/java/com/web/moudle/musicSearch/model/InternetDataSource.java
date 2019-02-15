package com.web.moudle.musicSearch.model;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import com.web.common.base.BaseObserver;
import com.web.common.bean.LiveDataWrapper;
import com.web.moudle.musicSearch.bean.next.SearchMusicWrapper1;
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class InternetDataSource extends PageKeyedDataSource<String,SimpleMusicInfo> {
    private String keyWords;
    private int page=1;
    private int total=0;
    private MutableLiveData<LiveDataWrapper<Integer>> liveData;
    private LiveDataWrapper<Integer> wrapper;
    private InternetMusicModel model=new InternetMusicModel();


    public InternetDataSource(@NonNull MutableLiveData<LiveDataWrapper<Integer>> liveData){
        this.liveData=liveData;
        wrapper=new LiveDataWrapper<>();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<String> params, @NonNull LoadInitialCallback<String, SimpleMusicInfo> callback) {
        load(callback,0);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, SimpleMusicInfo> callback) {
    }

    @Override
    public void loadAfter(@NonNull LoadParams<String> params, @NonNull LoadCallback<String, SimpleMusicInfo> callback) {
        setPage(page+1);
        load(callback,1);

    }

    private void load(@NonNull Object callback,int flg) {
        model.getSimpleMusic(keyWords, page).subscribe(new BaseObserver<SearchMusicWrapper1>() {
            @Override
            public void onNext(SearchMusicWrapper1 res) {
                ArrayList<SimpleMusicInfo> list = res.getSearchSongWrapper2().getSongList();
                if (list.size() == 0&&res.getSearchSongWrapper2().getTotal()!=0) {
                    wrapper.setCode(LiveDataWrapper.CODE_NO_DATA);
                    liveData.postValue(wrapper);
                } else {
                    if (flg == 0) {
                        ((LoadInitialCallback) callback).onResult(list, "", "");
                    } else if (flg == 1) {
                        ((LoadCallback) callback).onResult(list, "");
                    }
                    if (total != res.getSearchSongWrapper2().getTotal()) {
                        total = res.getSearchSongWrapper2().getTotal();
                        wrapper.setCode(LiveDataWrapper.CODE_OK);
                        wrapper.setValue(total);
                        liveData.postValue(wrapper);
                    }
                }

            }

            @Override
            public void error(@NotNull Throwable e) {
                wrapper.setCode(LiveDataWrapper.CODE_ERROR);
                liveData.postValue(wrapper);
            }
        });
    }
    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
        setPage(1);
    }

    private void setPage(int page) {
        this.page = page;
    }



}
