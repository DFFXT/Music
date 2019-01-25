package com.web.moudle.musicSearch.model;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.web.common.base.BaseObserver;
import com.web.common.bean.LiveDataWrapper;
import com.web.config.GetFiles;
import com.web.data.InternetMusic;
import com.web.moudle.musicSearch.bean.SearchWrapper0;
import com.web.moudle.musicSearch.bean.next.SearchMusicWrapper1;
import com.web.moudle.musicSearch.bean.next.next.next.SimpleMusicInfo;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static com.web.moudle.musicSearch.viewModel.InternetViewModel.CODE_JSON_ERROR;
import static com.web.moudle.musicSearch.viewModel.InternetViewModel.CODE_NET_ERROR;
import static com.web.moudle.musicSearch.viewModel.InternetViewModel.CODE_NO_DATA;
import static com.web.moudle.musicSearch.viewModel.InternetViewModel.CODE_OK;
import static com.web.moudle.musicSearch.viewModel.InternetViewModel.CODE_URL_ERROR;

public class InternetDataSource extends PageKeyedDataSource<String,SimpleMusicInfo> {
    private String keyWords;
    private int page=1;
    private int pageSize=10;
    private MutableLiveData<LiveDataWrapper> liveData;
    private LiveDataWrapper wrapper;
    private InternetMusicModel model=new InternetMusicModel();


    public InternetDataSource(@NonNull MutableLiveData<LiveDataWrapper> liveData){
        this.liveData=liveData;
        wrapper=new LiveDataWrapper();
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
        /*model.search(keyWords,page,pageSize).subscribe(new BaseObserver<SearchResultBd>() {
            @Override
            public void onNext(SearchResultBd rowMusicData) {
                callback.onResult(rowMusicData.getSong_list(),"");
            }

            @Override
            public void error(@NotNull Throwable e) {
                wrapper.setCode(CODE_JSON_ERROR);
                liveData.postValue(wrapper);
            }
        });*/

    }

    private void load(@NonNull Object callback,int flg){
        model.getSimpleMusic(keyWords,page).subscribe(new BaseObserver<SearchWrapper0<SearchMusicWrapper1>>() {
            @Override
            public void onNext(SearchWrapper0<SearchMusicWrapper1> res) {
                ArrayList<SimpleMusicInfo> list=res.getResult().getSearchSongWrapper2().getSongList();
                if(list.size()==0){
                    wrapper.setCode(LiveDataWrapper.CODE_NO_DATA);
                    liveData.postValue(wrapper);
                }else{
                    if(flg==0){
                        ((LoadInitialCallback)callback).onResult(list,"","");
                    }else if(flg==1){
                        ((LoadCallback)callback).onResult(list,"");
                    }
                }

            }
            @Override
            public void error(@NotNull Throwable e) {
                wrapper.setCode(LiveDataWrapper.CODE_ERROR);
                liveData.postValue(wrapper);
            }
        });
        //***
        /*model.search(keyWords,page,pageSize).subscribe(new BaseObserver<SearchResultBd>() {
            @Override
            public void onNext(SearchResultBd rowMusicData) {
                StringBuilder builder=new StringBuilder();
                for(InternetMusic m:rowMusicData.getSong_list()){
                    builder.append(m.getHash());
                    builder.append(",");
                }
                model.getMusicDetail(builder.substring(0,builder.length()-1))
                        .subscribe(new BaseObserver<InternetMusicDetailList>() {
                            @Override
                            public void onNext(InternetMusicDetailList internetMusicDetailList) {
                                if(flg==0){
                                    ((LoadInitialCallback)callback).onResult(internetMusicDetailList.getSongList(),"","");
                                }else if(flg==1){
                                    ((LoadCallback)callback).onResult(internetMusicDetailList.getSongList(),"");
                                }
                            }
                            @Override
                            public void error(@NotNull Throwable e) {
                                e.printStackTrace();
                            }
                        });

            }

            @Override
            public void error(@NotNull Throwable e) {
                e.printStackTrace();
            }
        });*/
    }
    /**
     *  搜索
     * @param keyword key
     * @param page page
     * @deprecated use InternetMusicModel.search instead
     * @return List
     */
    @Deprecated
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
