package com.web.moudle.music.page.local;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.web.common.base.BaseActivity;
import com.web.common.constant.Constant;
import com.web.common.tool.MToast;
import com.web.common.util.ResUtil;
import com.web.config.Shortcut;
import com.web.data.Music;
import com.web.data.MusicList;
import com.web.data.PlayerConfig;
import com.web.moudle.lyrics.LyricsActivity;
import com.web.moudle.music.page.BaseMusicPage;
import com.web.moudle.music.page.local.control.interf.ListSelectListener;
import com.web.moudle.music.page.local.control.ui.SelectorListAlert;
import com.web.moudle.music.page.recommend.RecommendPage;
import com.web.moudle.music.player.MusicPlay;
import com.web.moudle.musicDownload.ui.MusicDownLoadActivity;
import com.web.moudle.musicEntry.ui.PlayerObserver;
import com.web.moudle.musicSearch.ui.InternetMusicActivity;
import com.web.moudle.preference.SP;
import com.web.moudle.search.SearchActivity;
import com.web.moudle.setting.ui.SettingActivity;
import com.web.web.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MusicActivity extends BaseActivity implements OnClickListener {

    private int RESULT_CODE_SEARCH = 1;

    private TextView songname, singer, tv_musicOrigin;//**音乐信息
    private SeekBar bar;//--进度条
    private ImageView iv_singerIcon;
    private ImageView pre, pause, next, musicplay_type;//--各种图标

    private Toolbar toolbar;

    private DrawerLayout drawer;
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private MusicListLPage musicListLPage;
    private MusicPlay.Connect connect;
    private List<MusicList<Music>> groupList;
    private int groupIndex = -1;
    private List<BaseMusicPage> pageList = new ArrayList<>();

    private SelectorListAlert listAlert;
    private PlayerObserver observer=new PlayerObserver(){
        @Override
        public void load(int groupIndex, int childIndex, Music music, int maxTime) {
            bar.setMax(maxTime / 1000);
            if (music == null) {
                songname.setText(null);
                singer.setText(null);
            } else {
                songname.setText(music.getMusicName());
                singer.setText(music.getSinger());
            }
            if (connect.getConfig().getBitmap() == null) {
                iv_singerIcon.setImageResource(R.drawable.singer_default_icon);
            } else {
                iv_singerIcon.setImageBitmap(connect.getConfig().getBitmap());
            }

            play();
            musicListLPage.loadMusic(groupIndex, childIndex);
        }

        @Override
        public void play() {
            pause.setImageResource(R.drawable.icon_play_black);
            if (listAlert != null && connect.getConfig().getMusicOrigin() == PlayerConfig.MusicOrigin.WAIT) {
                listAlert.setIndex(connect.getWaitIndex());
            }
        }

        @Override
        public void pause() {
            pause.setImageResource(R.drawable.icon_pause_black);
        }

        @Override
        public void playTypeChanged(PlayerConfig.PlayType playType) {
            showPlayType(playType);
        }

        @Override
        public void currentTime(int group, int child, int time) {
            runOnUiThread(() -> bar.setProgress(time / 1000));
        }

        @Override
        public void musicListChange(int group, List<MusicList<Music>> list) {
            runOnUiThread(() -> {

                if (list == null || list.size() == 0 || list.get(0).size() == 0) {
                    if (!SP.INSTANCE.getBoolean(Constant.spName, Constant.SpKey.noNeedScan,false)) {
                        new AlertDialog.Builder(MusicActivity.this)
                                .setMessage(ResUtil.getString(R.string.musicMain_noMusicAlert))
                                .setNegativeButton(ResUtil.getString(R.string.no), null)
                                .setPositiveButton(ResUtil.getString(R.string.yes), (dialog, which) -> connect.scanLocalMusic()).create().show();
                    } else if (list != null) {
                        musicListLPage.setData(group, list.get(group));
                        groupList = list;
                        groupIndex = group;

                        getCurrentPage().setTitle(tv_title);
                    }
                } else {
                    musicListLPage.setData(group, list.get(group));
                    groupList = list;
                    groupIndex = group;
                    getCurrentPage().setTitle(tv_title);
                }
            });
        }

        @Override
        public void bufferingUpdate(int percent) {
            bar.setSecondaryProgress((int) (percent*bar.getMax()/100f));
        }

        /***
         * 音乐源发生了变化
         * @param origin origin
         */
        @SuppressLint("SetTextI18n")
        @Override
        public void musicOriginChanged(PlayerConfig.MusicOrigin origin) {
            runOnUiThread(() -> {
                switch (origin) {
                    case LOCAL: {
                        tv_musicOrigin.setText("LOCAL");
                    }
                    break;
                    case INTERNET: {
                        tv_musicOrigin.setText("INTERNET");
                    }
                    break;
                    case WAIT: {
                        tv_musicOrigin.setText("LIST");
                    }
                    break;
                    case STORAGE: {
                        tv_musicOrigin.setText("STORAGE");
                    }
                    break;
                }
            });

        }
    };

    public int getLayoutId() {//---活动启动入口
        return R.layout.restruct_music_layout;
    }

    @Override
    public void initView() {
        findID();
        setToolbar();
        musicListLPage = new MusicListLPage();
        pageList.add(new RecommendPage());
        pageList.add(musicListLPage);
        musicListLPage.setTitle(tv_title);
        setAdapter();
        startService(new Intent(this, MusicPlay.class));
        setListener();
        connect();
        iv_singerIcon.setClipToOutline(true);
        iv_singerIcon.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), view.getWidth() / 10f);
            }
        });
        //iv_singerIcon.setOnClickListener(v-> LyricsActivity.actionStart(this));
    }

    @SuppressLint("RestrictedApi")
    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(ResUtil.getString(R.string.page_local));
        tv_title = (TextView) toolbar.getChildAt(0);
        tv_title.setCompoundDrawableTintMode(PorterDuff.Mode.ADD);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.three_h_line);
        }
        toolbar.setNavigationOnClickListener(v -> drawer.openDrawer(GravityCompat.START));
        tv_title.setOnClickListener(v -> {
            switch (pageList.get(viewPager.getCurrentItem()).getPageName()) {
                case MusicListLPage.pageName: {
                    SelectorListAlert listAlert = new SelectorListAlert(MusicActivity.this, ResUtil.getString(R.string.songSheet));
                    List<String> list = new ArrayList<>();
                    for (MusicList ml : groupList) {
                        list.add(ml.getTitle());
                    }
                    listAlert.setList(list);
                    listAlert.setIndex(groupIndex);
                    listAlert.setCanTouchRemove(false);
                    listAlert.setListSelectListener(new ListSelectListener() {
                        @Override
                        public void select(View v, int position) {
                            connect.getList(position);
                            listAlert.cancel();
                        }

                        @Override
                        public void remove(View v, int position) {
                        }
                    });
                    listAlert.build();
                    listAlert.show();
                }
                break;
            }
        });
    }

    private TextView tv_title;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            SearchActivity.actionStart(MusicActivity.this, RESULT_CODE_SEARCH);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu m) {
        getMenuInflater().inflate(R.menu.music_toolbar_item, m);
        return true;
    }

    private boolean noSuchPage(String pageName) {
        for (BaseMusicPage page : pageList) {
            if (pageName.equals(page.getPageName())) return false;
        }
        return true;
    }

    /**
     * 获取传输的数据
     */
    private void getIntentData() {
        Intent intent = getIntent();
        if (intent.getData() != null) {
            String path = intent.getData().getPath();
            File file;
            if (path != null) {
                file = new File(path);
                String name = file.getName();
                String out[] = new String[2];
                Shortcut.getName(out, name);
                Music music = new Music(out[0], out[1], path);
                connect.playLocal(music);
            }
        }

    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    private void findID() {
        drawer = findViewById(R.id.drawer);
        viewPager = findViewById(R.id.viewPager);

        View v = findViewById(R.id.musicControlBox);
        v.setOnClickListener(view->LyricsActivity.actionStart(this));
        bar = v.findViewById(R.id.bar);
        Drawable d = getDrawable(R.drawable.icon_seekbar_dot_pressed);
        if (d != null) {
            d.setTint(getColor(R.color.themeColor));
            bar.setThumb(d);
        }
        iv_singerIcon = v.findViewById(R.id.iv_singerIcon);
        songname = v.findViewById(R.id.songname);
        singer = v.findViewById(R.id.singer);
        pre = v.findViewById(R.id.pre);
        pause = v.findViewById(R.id.pause);
        next = v.findViewById(R.id.next);
        musicplay_type = v.findViewById(R.id.musicplay_type);

        //***音乐控制View
        bar = findViewById(R.id.bar);
        songname = findViewById(R.id.songname);
        singer = findViewById(R.id.singer);
        tv_musicOrigin = findViewById(R.id.musicOrigin);
        pre = findViewById(R.id.pre);
        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        musicplay_type = findViewById(R.id.musicplay_type);


        v = findViewById(R.id.leftDrawer);
        v.findViewById(R.id.scanLocalMusic).setOnClickListener(this);
        v.findViewById(R.id.goDownload).setOnClickListener(this);
        v.findViewById(R.id.item_setting1).setOnClickListener(this);
        tv_musicOrigin.setOnClickListener(this);


    }

    private ServiceConnection serviceConnection;

    /**
     * 连接服务
     */
    private void connect() {
        Intent intent = new Intent(this, MusicPlay.class);
        intent.setAction(MusicPlay.BIND);

        bindService(intent, serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                connect = (MusicPlay.Connect) service;
                connect.addObserver(MusicActivity.this,observer);
                connect.getList(0);
                getIntentData();//*************8获取输入数据
                for (BaseMusicPage page : pageList) {
                    page.setConnect(connect);
                }
                connect.getPlayerInfo();//**获取播放器状态
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                connect = null;
            }
        }, BIND_AUTO_CREATE);
    }

    private void setAdapter() {//--设置本地适配器
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return pageList.get(position);
            }

            @Override
            public int getCount() {
                return pageList.size();
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                return POSITION_NONE;
            }
        });

    }

    /**
     * 给控件绑定事件
     */
    private void setListener() {
        pre.setOnClickListener(this);
        pause.setOnClickListener(this);
        next.setOnClickListener(this);
        musicplay_type.setOnClickListener(this);

        bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {//--进度条拖动
            public void onStopTrackingTouch(SeekBar arg0) {
                int progress = arg0.getProgress();
                connect.seekTo(progress * 1000);
                //currentTime(-1,-1,arg0.getProgress()*100);
            }

            public void onStartTrackingTouch(SeekBar arg0) {
            }

            public void onProgressChanged(SeekBar v, int now, boolean tf) {//--发送进度条信息
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Shortcut.closeKeyBord(MusicActivity.this, viewPager);
                pageList.get(position).setTitle(tv_title);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void onClick(View v) {//--点击事件
        switch (v.getId()) {
            case R.id.goDownload: {
                Intent intent = new Intent(this, MusicDownLoadActivity.class);
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);
            }
            break;

            case R.id.download: {
                MusicDownLoadActivity.actionStart(this);
                drawer.closeDrawer(GravityCompat.START);
            }
            break;
            case R.id.pre: {
                connect.pre();
            }
            break;
            case R.id.pause: {
                connect.changePlayerPlayingStatus();
            }
            break;
            case R.id.next: {
                connect.next();
            }
            break;
            case R.id.musicplay_type: {
                changePlayType();
            }
            break;
            case R.id.subSettingBox:
                break;
            case R.id.item_setting1: {
                SettingActivity.actionStart(this);
                drawer.closeDrawer(GravityCompat.START);
                return;
            }
            case R.id.scanLocalMusic: {//--扫描本地文件
                SP.INSTANCE.putValue(Constant.spName, Constant.SpKey.clearAll, false);
                connect.scanLocalMusic();
                MToast.showToast(this, ResUtil.getString(R.string.musicIsScanning));
                drawer.closeDrawer(GravityCompat.START);
            }
            break;
            case R.id.musicOrigin: {
                if (connect.getConfig().getMusicOrigin() != PlayerConfig.MusicOrigin.WAIT) return;
                if (listAlert == null) {
                    listAlert = new SelectorListAlert(this, ResUtil.getString(R.string.playList));
                }
                listAlert.setListSelectListener(new ListSelectListener() {
                    @Override
                    public void select(View v, int position) {
                        connect.playWait(position);
                    }

                    @Override
                    public void remove(View v, int position) {
                        connect.removeWait(position);
                        if (connect.getWaitMusic().size() == 0) {
                            listAlert.cancel();
                            return;
                        }
                        listAlert.setIndex(connect.getWaitIndex());
                    }
                });
                List<String> list = new ArrayList<>();
                for (Music m : connect.getWaitMusic()) {
                    list.add(m.getMusicName());
                }
                listAlert.setCanTouchRemove(true);
                listAlert.setList(list);
                listAlert.setIndex(connect.getWaitIndex());
                listAlert.build();
                listAlert.show();

            }
            break;
        }
    }

    private BaseMusicPage getCurrentPage() {
        return pageList.get(viewPager.getCurrentItem());
    }

    /**
     * 设置相应playType【循环】
     */
    private void changePlayType() {
        PlayerConfig config = connect.getConfig();
        switch (config.getPlayType()) {
            case ALL_LOOP: {
                config.setPlayType(PlayerConfig.PlayType.ONE_LOOP);
            }
            break;
            case ONE_LOOP: {
                config.setPlayType(PlayerConfig.PlayType.ALL_ONCE);
            }
            break;
            case ALL_ONCE: {
                config.setPlayType(PlayerConfig.PlayType.ONE_ONCE);
            }
            break;
            case ONE_ONCE: {
                config.setPlayType(PlayerConfig.PlayType.ALL_LOOP);
            }
            break;
        }
        showPlayType(config.getPlayType());
    }

    //**根据playType显示图标
    private void showPlayType(PlayerConfig.PlayType playType) {
        switch (playType) {
            case ALL_LOOP: {
                musicplay_type.setImageResource(R.drawable.music_type_all_loop);
            }
            break;
            case ONE_LOOP: {
                musicplay_type.setImageResource(R.drawable.music_type_one_loop);
            }
            break;
            case ALL_ONCE: {
                musicplay_type.setImageResource(R.drawable.music_type_all_once);
            }
            break;
            case ONE_ONCE: {
                musicplay_type.setImageResource(R.drawable.music_type_one_once);
            }
            break;
        }
    }




    public void onDestroy() {
        if (serviceConnection != null) {
            unbindService(serviceConnection);
        }
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK) return;
        if (data == null) return;
        if (requestCode == RESULT_CODE_SEARCH) {
            InternetMusicActivity.actionStart(this, data.getStringExtra(SearchActivity.INPUT_DATA));
        }
    }

}

