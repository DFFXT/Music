package com.web.moudle.musicDownload.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.google.android.material.tabs.TabLayout;
import com.web.common.base.BaseActivity;
import com.web.common.base.BaseFragment;
import com.web.common.base.BaseFragmentPagerAdapter;
import com.web.common.base.BasePageChangeListener;
import com.web.common.tool.MToast;
import com.web.common.tool.Ticker;
import com.web.common.util.ResUtil;
import com.web.common.util.ViewUtil;
import com.web.data.InternetMusic;
import com.web.data.InternetMusicDetail;
import com.web.data.Music;
import com.web.misc.ConfirmDialog;
import com.web.misc.GapItemDecoration;
import com.web.misc.ToolsBar;
import com.web.misc.TopBarLayout;
import com.web.moudle.music.page.BaseMusicPage;
import com.web.moudle.music.player.MusicPlay;
import com.web.moudle.musicDownload.adpter.DownloadViewAdapter;
import com.web.moudle.musicDownload.bean.DownloadMusic;
import com.web.moudle.musicEntry.ui.MusicDetailActivity;
import com.web.moudle.service.FileDownloadService;
import com.web.web.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import kotlinx.coroutines.Dispatchers;


public class MusicDownLoadActivity extends BaseActivity {
    private ViewPager rv_download;

    private ServiceConnection serviceConnection;
    private FileDownloadService.Connect connect;
    private TabLayout tab;
    private TopBarLayout topBarLayout;
    private DownloadCompleteFragment completeFragment=new DownloadCompleteFragment();
    private DownloadingFragment downloadingFragment=new DownloadingFragment();
    private ArrayList<BaseDownloadFragment> fragments=new ArrayList<>();



    @Override
    public int getLayoutId() {
        return R.layout.music_download;
    }

    public TopBarLayout getTopBarLayout(){
        return topBarLayout;
    }

    @Override
    public void initView() {
        tab=findViewById(R.id.tabLayout);
        rv_download = findViewById(R.id.list);
        fragments.add(completeFragment);
        fragments.add(downloadingFragment);
        rv_download.setAdapter(new BaseFragmentPagerAdapter(getSupportFragmentManager(),fragments));
        tab.setupWithViewPager(rv_download);
        for(int i=0;i<fragments.size();i++){
            Objects.requireNonNull(tab.getTabAt(i)).setText(fragments.get(i).getTitle());
        }
        topBarLayout=findViewById(R.id.topBar);
        connect();
        ViewUtil.transparentStatusBar(getWindow());

        rv_download.addOnPageChangeListener(new BasePageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                for(int i=0;i<fragments.size();i++){
                    if(i==position){
                        fragments.get(position).onHiddenChanged(false);
                    }else{
                        fragments.get(position).onHiddenChanged(true);
                    }
                }
            }
        });
        rv_download.post(()-> fragments.get(0).onHiddenChanged(false));

    }

    private void connect() {
        Intent intent = new Intent(this, FileDownloadService.class);
        bindService(intent, serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                connect = (FileDownloadService.Connect) service;
                for(int i=0;i<fragments.size();i++){
                    fragments.get(i).setConnect(connect);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        }, BIND_AUTO_CREATE);
    }



    public void onDestroy() {
        super.onDestroy();
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
    }

    public static void actionStart(Context ctx) {
        Intent intent = new Intent(ctx, MusicDownLoadActivity.class);
        ctx.startActivity(intent);
    }
}
