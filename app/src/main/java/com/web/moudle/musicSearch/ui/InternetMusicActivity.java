package com.web.moudle.musicSearch.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import com.web.common.base.BaseActivity;
import com.web.common.base.BaseFragment;
import com.web.common.base.BaseFragmentPagerAdapter;
import com.web.common.util.ViewUtil;
import com.web.misc.TopBarLayout;
import com.web.moudle.musicSearch.adapter.InternetMusicAdapter;
import com.web.moudle.search.SearchActivity;
import com.web.web.R;

import java.util.ArrayList;

public class InternetMusicActivity extends BaseActivity {
    private final static int RESULT_CODE_SEARCH = 1;
    public final static String KEYWORD = "keyword";

    private ArrayList<BaseSearchFragment> pageList=new ArrayList<>();
    private TopBarLayout topBarLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String keyWords;
    private InternetMusicAdapter adapter;

    private void initData() {
        keyWords = getIntent().getStringExtra(KEYWORD);

    }

    @Override
    public int getLayoutId() {
        return R.layout.music_internet;
    }

    @Override
    public void initView() {
        initData();
        tabLayout=findViewById(R.id.tabLayout);
        viewPager=findViewById(R.id.viewPager);
        //recyclerView = findViewById(R.id.internetMusic);
        //smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        topBarLayout = findViewById(R.id.topBar);
        topBarLayout.setEndImageListener(v -> {
            SearchActivity.actionStart(this, RESULT_CODE_SEARCH);
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        //recyclerView.setLayoutManager(manager);

        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ViewUtil.getDrawable(R.drawable.recycler_divider));
        //recyclerView.addItemDecoration(decoration);
        adapter = new InternetMusicAdapter(this);
        //vm = ViewModelProviders.of(this).get(InternetViewModel.class);

        //recyclerView.setAdapter(adapter);
        //adapter.setListener(this::downloadConsider);


        /*smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setEnableOverScrollDrag(true);
        smartRefreshLayout.setEnableLoadMore(true);
        smartRefreshLayout.setRefreshFooter(new ClassicsFooter(this));*/


        /*init();
        search(keyWords);*/



        pageList.add(new MusicFragment());
        pageList.add(new ArtistFragment());
        pageList.add(new AlbumFragment());
        pageList.add(new SheetFragment());
        Bundle b=new Bundle();
        b.putString(MusicFragment.keyword,keyWords);
        for(BaseSearchFragment f:pageList){
            f.setArguments(b);
        }
        viewPager.setAdapter(new BaseFragmentPagerAdapter(getSupportFragmentManager(),pageList));
        viewPager.setOffscreenPageLimit(Integer.MAX_VALUE);
        tabLayout.setTabIndicatorFullWidth(false);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        tabLayout.getTabAt(0).setText(getText(R.string.music));
        tabLayout.getTabAt(1).setText(getText(R.string.singer));
        tabLayout.getTabAt(2).setText(getText(R.string.albumEntry_albumList));
        tabLayout.getTabAt(3).setText(getText(R.string.songSheet));




    }

    /*private void init() {
        vm.observerMusicDetail().observe(this, detailList -> {
            if (detailList == null) return;
            downloadConsider(detailList.getSongList().get(0));
        });
        vm.getStatus().observe(this, wrapper -> {
            if (wrapper == null) return;
            switch (wrapper.getCode()) {
                case InternetViewModel.CODE_OK: {
                    smartRefreshLayout.finishLoadMore();
                }
                break;
                case InternetViewModel.CODE_NO_DATA: {
                    smartRefreshLayout.setNoMoreData(true);
                    //MToast.showToast(this, ResUtil.getString(R.string.noMoreData));
                }
                break;
                case InternetViewModel.CODE_JSON_ERROR: {
                    MToast.showToast(this, ResUtil.getString(R.string.dataAnalyzeError));
                }
                break;
                case InternetViewModel.CODE_URL_ERROR: {
                    MToast.showToast(this, ResUtil.getString(R.string.urlAnalyzeError));
                }
                break;
                case InternetViewModel.CODE_NET_ERROR: {
                    MToast.showToast(this, ResUtil.getString(R.string.noInternet));
                }
                break;
                case InternetViewModel.CODE_ERROR: {
                    MToast.showToast(this, ResUtil.getString(R.string.unkownError));
                }
            }
        });
    }




    *//**
     * 外部调用搜索
     *
     * @param keyword 关键词
     *//*
    public void search(String keyword) {
        smartRefreshLayout.setNoMoreData(false);
        vm.setKeyWords(keyword);
        vm.getMusicList().observe(this, pl -> {
            adapter.submitList(pl);
        });
        this.keyWords = keyword;
    }

    *//**
     * 网络音乐点击
     *
     * //@param music p
     *//*
    @SuppressLint("SetTextI18n")
    private void downloadConsider(InternetMusicDetail music) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.create();
        builder.setPositiveButton("取消", (arg0, arg1) -> {

        });
        builder.setNeutralButton("在线试听", (dialog, which) -> new Thread(() -> {
            InternetMusicForPlay info = new InternetMusicForPlay();
            info.setMusicName(Shortcut.validatePath(music.getSongName()));
            info.setSinger(Shortcut.validatePath(music.getArtistName()));
            info.setPath(music.getSongLink());
            info.setImgAddress(music.getSingerIconSmall());
            info.setLrcLink(music.getLrcLink());
            MusicPlay.play(InternetMusicActivity.this,info);

        }).start());
        builder.setNegativeButton("下载(" + ResUtil.getFileSize(music.getSize()) + ")", (arg0, arg1) -> {
            //**网络获取的时间以秒为单位、后面需要毫秒(媒体库里面的单位为毫秒)
            music.setDuration(music.getDuration() * 1000);
            FileDownloadService.addTask(InternetMusicActivity.this, music);
        });

        builder.setTitle(music.getSongName());
        TextView tv_songName = new TextView(this);
        tv_songName.setTextColor(this.getResources().getColor(R.color.white, this.getTheme()));
        builder.setMessage("歌手：" + music.getArtistName() +
                "\n时长：" + ResUtil.timeFormat("mm:ss", music.getDuration() * 1000) +
                "\n大小：" + ResUtil.getFileSize(music.getSize()));
        builder.show();
    }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK) return;
        if (data == null) return;
        if (requestCode == RESULT_CODE_SEARCH) {
            keyWords=data.getStringExtra(SearchActivity.INPUT_DATA);
            viewPager.setCurrentItem(0);

        }
    }

    public static void actionStart(Context ctx, String word) {
        Intent intent = new Intent(ctx, InternetMusicActivity.class);
        intent.putExtra(KEYWORD, word);
        ctx.startActivity(intent);
    }
}
