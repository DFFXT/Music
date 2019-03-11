package com.web.moudle.musicDownload.ui;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.web.common.base.BaseActivity;
import com.web.common.tool.MToast;
import com.web.common.tool.Ticker;
import com.web.common.util.ResUtil;
import com.web.common.util.ViewUtil;
import com.web.data.InternetMusic;
import com.web.data.InternetMusicDetail;
import com.web.data.Music;
import com.web.misc.GapItemDecoration;
import com.web.misc.ToolsBar;
import com.web.misc.TopBarLayout;
import com.web.moudle.music.player.MusicPlay;
import com.web.moudle.musicDownload.adpter.DownloadViewAdapter;
import com.web.moudle.musicDownload.bean.DownloadMusic;
import com.web.moudle.musicEntry.ui.MusicDetailActivity;
import com.web.moudle.net.NetApis;
import com.web.moudle.service.FileDownloadService;
import com.web.web.R;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kotlinx.coroutines.Dispatchers;


public class MusicDownLoadActivity extends BaseActivity implements FileDownloadService.DownloadListener {
    private RecyclerView rv_download;
    private final List<DownloadMusic> dataList = new ArrayList<>();
    private DownloadViewAdapter adapter;

    private ServiceConnection serviceConnection;
    private FileDownloadService.Connect connect;
    ToolsBar toolsBar;

    private Ticker looper;
    private Runnable runnable = () -> {
        List<DownloadMusic> list = connect.getDownloadingMusic();
        if (list.size() == 0) {
            looper.stop();
        }
        for (int i = 0; i < list.size(); i++) {
            dataList.set(i + 1, list.get(i));
        }
        adapter.notifyItemRangeChanged(1, list.size());
    };


    @Override
    public int getLayoutId() {
        return R.layout.music_download;
    }

    @Override
    public void initView() {
        toolsBar = new ToolsBar(this);
        rv_download = findViewById(R.id.list);
        rv_download.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rv_download.addItemDecoration(new GapItemDecoration(10, 0, 10, 0));

        connect();
        TopBarLayout topBarLayout = findViewById(R.id.topBar_musicDownload);
        topBarLayout.setMainTitle(ResUtil.getString(R.string.downloadManager));
        ViewUtil.transparentStatusBar(getWindow());

        looper=new Ticker(500, Dispatchers.getMain(),()->{
            runnable.run();
            return null;
        });


    }

    private void connect() {
        Intent intent = new Intent(this, FileDownloadService.class);
        startService(intent);
        bindService(intent, serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                connect = (FileDownloadService.Connect) service;
                connect.setDownloadListener(MusicDownLoadActivity.this);
                connect.getDownloadList();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        }, BIND_AUTO_CREATE);
    }

    private void setAdapter() {//--设置适配器
        adapter = new DownloadViewAdapter(MusicDownLoadActivity.this, dataList);
        adapter.setItemClickListener((view, position) -> {
            InternetMusicDetail detail=dataList.get(position).getInternetMusicDetail();
            int id = detail.getId();
            int status = dataList.get(position).getStatus();

            switch (view.getId()){
                case R.id.downloadStatu:{
                    if (status == DownloadMusic.DOWNLOAD_DOWNLOADING) {
                        connect.pause(id);
                    } else {
                        connect.start(id);
                    }
                }break;
                case R.id.close:{
                    new AlertDialog.Builder(MusicDownLoadActivity.this)
                            .setTitle(ResUtil.getString(R.string.delete))
                            .setMessage("\n\n")
                            .setNegativeButton(ResUtil.getString(R.string.no), null)
                            .setPositiveButton(ResUtil.getString(R.string.yes), (dialog, witch) -> connect.delete(id)).create().show();
                }break;
                case R.id.iv_play:{
                    Music music=new Music(detail.getSongName(),detail.getArtistName(),detail.getPath());
                    if(Music.exist(music)){
                        MusicPlay.play(this,music);
                    }else{
                        MToast.showToast(this,R.string.fileNotFound);
                    }
                }break;
                case R.id.item_parent:{
                    MusicDetailActivity.actionStart(this,detail.getSongId());
                }break;
            }
        });
        adapter.setItemLongClickListener((v, position) -> {
            toolsBar.removeAllItem();
            toolsBar.addItem(1, ResUtil.getString(R.string.delete));
            toolsBar.setItemClick(id -> {
                switch (id) {
                    case 1: {
                        connect.delete(adapter.getSelectList((item,index)->item.getInternetMusicDetail().getId()));
                        adapter.setSelect(false);
                        toolsBar.close();
                    }
                }
                return null;
            });
            toolsBar.setBackClick(() -> {
                adapter.setSelect(false);
                return null;
            });
            toolsBar.show();
            toolsBar.setFitWindow(true);
            return true;
        });
        rv_download.setAdapter(adapter);
    }


    @Override
    public void progressChange(int id, long progress, long max) {
        looper.start();
    }

    @Override
    public void complete(InternetMusic music) {
    }

    @Override
    public void statusChange(int id, boolean isDownload) {
        int index = getIndex(id);
        if (id < 0) return;
        runOnUiThread(() -> adapter.notifyItemChanged(index));
    }

    @Override
    public void listChanged(List<DownloadMusic> downloadMusicList, List<DownloadMusic> completeList) {
        dataList.clear();
        dataList.add(new DownloadMusic(null, DownloadMusic.DOWNLOAD_DOWNLOADING_HEAD));
        dataList.addAll(downloadMusicList);
        dataList.add(new DownloadMusic(null, DownloadMusic.DOWNLOAD_COMPLETE_HEAD));
        dataList.addAll(completeList);
        runOnUiThread(() -> {
            if (adapter == null) {
                setAdapter();
            } else {
                adapter.notifyDataSetChanged();
            }
        });

    }

    private int getIndex(int id) {
        InternetMusicDetail m;
        for (int i = 0; i < dataList.size(); i++) {
            m = dataList.get(i).getInternetMusicDetail();
            if (m != null && id == m.getId()) {
                return i;
            }
        }
        return -1;
    }

    public void onDestroy() {
        super.onDestroy();
        looper.stop();
        if (serviceConnection != null) {
            unbindService(serviceConnection);
            if (connect != null)
                connect.removeDownloadListener(this);
        }
    }

    public static void actionStart(Context ctx) {
        Intent intent = new Intent(ctx, NetApis.Music.class);
        ctx.startActivity(intent);
    }
}
