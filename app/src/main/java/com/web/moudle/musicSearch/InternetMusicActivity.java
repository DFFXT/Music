package com.web.moudle.musicSearch;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.web.common.base.BaseActivity;
import com.web.common.tool.MToast;
import com.web.common.util.ResUtil;
import com.web.common.util.ViewUtil;
import com.web.config.Shortcut;
import com.web.data.InternetMusicDetail;
import com.web.data.InternetMusicForPlay;
import com.web.misc.TopBarLayout;
import com.web.moudle.musicSearch.model.InternetViewModel;
import com.web.moudle.musicSearch.adapter.InternetMusicAdapter;
import com.web.moudle.music.player.MusicPlay;
import com.web.moudle.musicDownload.service.FileDownloadService;
import com.web.moudle.search.SearchActivity;
import com.web.web.R;

public class InternetMusicActivity extends BaseActivity {
    private final static int RESULT_CODE_SEARCH = 1;
    public final static String KEYWORD = "keyword";
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    private InternetViewModel vm;
    private TopBarLayout topBarLayout;
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
        recyclerView = findViewById(R.id.internetMusic);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        topBarLayout = findViewById(R.id.topBar);
        topBarLayout.setEndImageListener(v -> {
            SearchActivity.actionStart(this, RESULT_CODE_SEARCH);
        });
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ViewUtil.getDrawable(R.drawable.recycler_divider));
        recyclerView.addItemDecoration(decoration);
        adapter = new InternetMusicAdapter(this);
        vm = ViewModelProviders.of(this).get(InternetViewModel.class);

        recyclerView.setAdapter(adapter);
        adapter.setListener(this::downloadConsider);


        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setEnableOverScrollDrag(true);
        smartRefreshLayout.setEnableLoadMore(true);
        smartRefreshLayout.setRefreshFooter(new ClassicsFooter(this));


        init();
        search(keyWords);
    }

    private void init() {
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

    private void closeKeyBord() {//--close键盘
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
        }
    }




    /**
     * 外部调用搜索
     *
     * @param keyword 关键词
     */
    public void search(String keyword) {
        smartRefreshLayout.setNoMoreData(false);
        closeKeyBord();
        vm.setKeyWords(keyword);
        vm.getMusicList().observe(this, pl -> {
            adapter.submitList(pl);
        });
        this.keyWords = keyword;
    }

    /**
     * 网络音乐点击
     *
     * @param music p
     */
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
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK) return;
        if (data == null) return;
        if (requestCode == RESULT_CODE_SEARCH) {
            search(data.getStringExtra(SearchActivity.INPUT_DATA));
        }
    }

    public static void actionStart(Context ctx, String word) {
        Intent intent = new Intent(ctx, InternetMusicActivity.class);
        intent.putExtra(KEYWORD, word);
        ctx.startActivity(intent);
    }
}
