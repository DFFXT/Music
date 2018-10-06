package com.web.subWeb;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.web.adpter.PlayInterface;
import com.web.config.Shortcut;
import com.web.data.Music;
import com.web.data.MusicList;
import com.web.data.PlayerConfig;
import com.web.model.control.interf.WaitMusicListener;
import com.web.model.control.ui.ListAlert;
import com.web.page.InternetMusicPage;
import com.web.page.LyricPage;
import com.web.page.MusicListLPage;
import com.web.service.MusicPlay;
import com.web.util.BaseActivity;
import com.web.util.StrUtil;
import com.web.web.R;

import org.litepal.LitePal;

import java.io.File;
import java.util.List;

public class MusicPlayer_main_restruct extends BaseActivity implements OnClickListener,PlayInterface{

    private TextView songname,singer,tv_musicOrigin;//**音乐信息
	private SeekBar bar;//--进度条
	private ImageView pre,pause,next,musicplay_type;//--各种图标

	private Toolbar toolbar;
	private SearchView searchView;

	private DrawerLayout drawer;
	private ViewPager viewPager;
	private InternetMusicPage internetMusicPage;
	private MusicListLPage musicListLPage;
	private LyricPage lyricPage;
	private List<MusicList<Music>> localMusicList;
	private MusicPlay.Connect connect;




	public void onCreate(Bundle b){//---活动启动入口

		super.onCreate(b);
		setContentView(R.layout.restruct_music_layout);

		setToolbar();
		findID();
		setAdapter();
		startService(new Intent(this,MusicPlay.class));
		setListener();


	}
	@SuppressLint("RestrictedApi")
	private void setToolbar(){
	    toolbar=findViewById(R.id.toolbar);
	    toolbar.setTitle(StrUtil.getString(R.string.page_local));
	    setSupportActionBar(toolbar);
		if(getSupportActionBar()!=null){
			getSupportActionBar().setHomeButtonEnabled(true);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeAsUpIndicator(R.drawable.three_h_line);
		}
		toolbar.setNavigationOnClickListener(v -> drawer.openDrawer(Gravity.START));

    }

    public boolean onCreateOptionsMenu(Menu m){
		getMenuInflater().inflate(R.menu.music_toolbar_item,m);
		searchView= (SearchView) m.findItem(R.id.search).getActionView();
		if(internetMusicPage!=null){
			internetMusicPage.setSearchView(searchView);
		}
		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				viewPager.setCurrentItem(0);
				internetMusicPage.search(query,1);
				searchView.clearFocus();
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		return true;
	}

	/**
	 * 获取传输的数据
	 */
	private void getIntentData(){
		Intent intent=getIntent();
		if(intent.getData()!=null){
			String path=intent.getData().getPath();
			File file=new File(path);
			String name=file.getName();
			String out[]=Shortcut.getName(name);
			Music music=new Music(out[0],out[1],path);
			if(connect!=null){
				connect.playLocal(music);
			}
		}

	}

	private void findID(){
		drawer=findViewById(R.id.drawer);
		viewPager=findViewById(R.id.viewPager);

		View v=findViewById(R.id.musicControlBox);
		bar=v.findViewById(R.id.bar);
		songname=v.findViewById(R.id.songname);
		singer=v.findViewById(R.id.singer);
		pre=v.findViewById(R.id.pre);
		pause=v.findViewById(R.id.pause);
		next=v.findViewById(R.id.next);
		musicplay_type=v.findViewById(R.id.musicplay_type);

		//***音乐控制View
		bar=findViewById(R.id.bar);
		songname=findViewById(R.id.songname);
		singer=findViewById(R.id.singer);
		tv_musicOrigin=findViewById(R.id.musicOrigin);
		pre=findViewById(R.id.pre);
		pause=findViewById(R.id.pause);
		next=findViewById(R.id.next);
		musicplay_type=findViewById(R.id.musicplay_type);



		v=findViewById(R.id.leftDrawer);
		v.findViewById(R.id.scanLocalMusic).setOnClickListener(this);
		v.findViewById(R.id.goDownload).setOnClickListener(this);
		v.findViewById(R.id.item_setting1).setOnClickListener(this);
		tv_musicOrigin.setOnClickListener(this);
		singer.setOnClickListener(this);
		songname.setOnClickListener(this);


	}

	private ServiceConnection serviceConnection;
	/**
	 * 连接服务
	 */
	private void connect(){
		Intent intent = new Intent(this,MusicPlay.class);
		bindService(intent,serviceConnection= new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				connect=(MusicPlay.Connect) service;
				connect.setPlayInterface(MusicPlayer_main_restruct.this);
				connect.getList();
				getIntentData();//*************8获取输入数据
				if(musicListLPage!=null)
					musicListLPage.setConnect(connect);
				if(lyricPage!=null)
					lyricPage.setConnect(connect);
				if(internetMusicPage!=null){
					internetMusicPage.setConnect(connect);
				}
				connect.getPlayerInfo();//**获取播放器状态
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				connect=null;
			}
		},BIND_AUTO_CREATE);
	}

	private void setAdapter(){//--设置本地适配器

		viewPager.setAdapter(new PagerAdapter() {
			@Override
			public int getCount() {
				return 3;
			}
			@NonNull
			@Override
			public Object instantiateItem(@NonNull ViewGroup parent, int index){
				View v;
				if(index==0){
					if(internetMusicPage==null){
                        internetMusicPage=new InternetMusicPage(parent,MusicPlayer_main_restruct.this,connect);
                    }
				    v=internetMusicPage.getView();
                }
				else if(index==1){
					if(musicListLPage==null){
						musicListLPage=new MusicListLPage(MusicPlayer_main_restruct.this,parent,connect);
						musicListLPage.setData(localMusicList);
						new Thread(()-> connect()).start();
					}

					v=musicListLPage.getView();
				}else {
					if(lyricPage==null){
						lyricPage=new LyricPage(parent,connect);
						lyricPage.loadLyrics();
					}
					v=lyricPage.getView();
				}
				parent.addView(v);
				return v;
			}

			@Override
			public void destroyItem(@NonNull ViewGroup parent, int position, @NonNull Object object){
				switch (position){
					case 0:{
						parent.removeView(internetMusicPage.getView());
						//internetMusicPage=null;
					}break;
					case 1:{
						parent.removeView(musicListLPage.getView());
						//musicListLPage=null;
					}break;
					case 2:{
						parent.removeView(lyricPage.getView());
						//lyricPage=null;
					}break;
				}
			}

			@Override
			public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
				return view.equals(object);
			}
		});
		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
			@Override
			public void onPageSelected(int position) {
				Shortcut.closeKeyBord(MusicPlayer_main_restruct.this,viewPager);
				if(searchView!=null&&position!=0)
					searchView.onActionViewCollapsed();
			    if(position==0){
					toolbar.setTitle(StrUtil.getString(R.string.page_Internet));
			        internetMusicPage.setConnect(connect);
			        if(internetMusicPage.getKeyword()!=null){
			        	searchView.onActionViewExpanded();
						searchView.setQuery(internetMusicPage.getKeyword(),false);
						searchView.clearFocus();
					}
                }
				else if (position==1){
					if(lyricPage!=null) {
						toolbar.setTitle(StrUtil.getString(R.string.page_local));
						lyricPage.stop();
					}
				}
				else if(position==2){
					toolbar.setTitle(StrUtil.getString(R.string.page_lyrics));
					lyricPage.show();
				}

			}
			@Override
			public void onPageScrollStateChanged(int state) { }
		});
		viewPager.setCurrentItem(1);

	}

	/**
	 * 给控件绑定事件
	 */
	private void setListener(){
		pre.setOnClickListener(this);
		pause.setOnClickListener(this);
		next.setOnClickListener(this);
		musicplay_type.setOnClickListener(this);

		bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {//--进度条拖动
			public void onStopTrackingTouch(SeekBar arg0) {
				int progress=arg0.getProgress();
				connect.seekTo(progress*1000);
				lyricPage.lyricsRun(progress*10);
				//currentTime(-1,-1,arg0.getProgress()*100);
			}
			public void onStartTrackingTouch(SeekBar arg0) {
			}
			public void onProgressChanged(SeekBar v, int now, boolean tf) {//--发送进度条信息
			}
		});
	}

	public void onClick(View v){//--点击事件
		switch(v.getId()){
			case R.id.goDownload:{
				Intent intent=new Intent(this,MusicDownLoad.class);
				startActivity(intent);
			}break;

			case R.id.download:{
			    goDownloadInfo();
                drawer.closeDrawer(Gravity.START);
            }break;
			case R.id.pre:{
				connect.pre();
			}break;
			case R.id.pause:{
				connect.changePlayerPlayingStatus();
			}break;
			case R.id.next:{
				connect.next();

			}
			case R.id.musicplay_type:{
				changePlayType();
			}break;
			case R.id.subSettingBox:break;
			case R.id.item_setting1:{
				Intent intent=new Intent(this,Settings.class);
				startActivity(intent);
				drawer.closeDrawer(Gravity.START);
				return;
			}
			case R.id.scanLocalMusic:{//--扫描本地文件
				connect.scanLocalMusic();
				show("音乐扫描中");
				drawer.closeDrawer(Gravity.START);
			}break;
			case R.id.songname:
			case R.id.singer:
			case R.id.musicOrigin:{
				if(connect.getConfig().getMusicOrigin()!= PlayerConfig.MusicOrigin.WAIT)return;
				ListAlert listAlert=new ListAlert(this);
				listAlert.setMusicList(connect.getWaitMusic());
				listAlert.setIndex(connect.getWaitIndex());
				listAlert.setWaitMusicListener(new WaitMusicListener() {
                    @Override
                    public void select(View v, int position) {
                        connect.playWait(position);
                        listAlert.setIndex(position);
                        listAlert.getAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void remove(View v, int position) {
                        connect.removeWait(position);
                        if(connect.getWaitMusic().size()==0){
                        	listAlert.cancel();
                        	return;
						}
                        listAlert.setIndex(connect.getWaitIndex());
                        listAlert.getAdapter().notifyDataSetChanged();
                    }
                });
				listAlert.build();
				listAlert.show();

			}break;
		}
	}

	/**
	 * 设置相应playType【循环】
	 */
	private void changePlayType(){
		PlayerConfig config=connect.getConfig();
		switch (config.getPlayType()){
			case ALL_LOOP:{
				config.setPlayType(PlayerConfig.PlayType.ONE_LOOP);
			}break;
			case ONE_LOOP:{
				config.setPlayType(PlayerConfig.PlayType.ALL_ONCE);
			}break;
			case ALL_ONCE:{
				config.setPlayType(PlayerConfig.PlayType.ONE_ONCE);
			}break;
			case ONE_ONCE:{
				config.setPlayType(PlayerConfig.PlayType.ALL_LOOP);
			}break;
		}
		showPlayType(config.getPlayType());
	}
	//**根据playType显示图标
	private void showPlayType(PlayerConfig.PlayType playType){
		switch (playType){
			case ALL_LOOP:{
				musicplay_type.setImageResource(R.drawable.music_type_all_loop);
			}break;
			case ONE_LOOP:{
				musicplay_type.setImageResource(R.drawable.music_type_one_loop);
			}break;
			case ALL_ONCE:{
				musicplay_type.setImageResource(R.drawable.music_type_all_once);
			}break;
			case ONE_ONCE:{
				musicplay_type.setImageResource(R.drawable.music_type_one_once);
			}break;
		}
	}
	public boolean onKeyDown(int keyCode,KeyEvent event){//--按键处理

		return super.onKeyDown(keyCode, event);//--调用父级方法
	}

	
	private void show(String str){//---Toast提示
		Toast.makeText(MusicPlayer_main_restruct.this, str, Toast.LENGTH_SHORT).show();
	}


	@Override
	public void load(String musicName,String singerName,int maxTime){
		bar.setMax(maxTime/1000);
		songname.setText(musicName);
		singer.setText(singerName);
		play(musicName,singerName,maxTime);
		if(lyricPage!=null){
			lyricPage.setConnect(connect);
			lyricPage.loadLyrics();
		}
	}

	@Override
	public void play(String musicName,String singerName,int maxTime) {
		pause.setImageResource(R.drawable.play);
	}

	@Override
	public void pause() {
		pause.setImageResource(R.drawable.pause);
	}

	@Override
	public void playTypeChanged(PlayerConfig.PlayType playType){
		showPlayType(playType);
	}

	@Override
	public void currentTime(int group, int child,int time) {
		runOnUiThread(() -> {
            bar.setProgress(time/1000);
            if(lyricPage!=null){
                lyricPage.lyricsRun(time/100);
            }
        });
	}

	@Override
	public void musicListChange(List<MusicList<Music>> list) {
	    runOnUiThread(() -> {
            localMusicList=list;
            if(musicListLPage!=null)
                musicListLPage.setData(list);
        });
	}


    /***
     * 音乐源发生了变化
     * @param origin origin
     */
	@SuppressLint("SetTextI18n")
	@Override
	public void musicOriginChanged(PlayerConfig.MusicOrigin origin){
		runOnUiThread(() -> {
            switch (origin){
                case LOCAL:{
                    tv_musicOrigin.setText("LOCAL");
                }break;
                case INTERNET:{
                    tv_musicOrigin.setText("INTERNET");
                }break;
                case WAIT:{
                    tv_musicOrigin.setText("LIST");
                }break;
                case STORAGE:{
                    tv_musicOrigin.setText("STORAGE");
                }break;
            }
        });

	}

	private void goDownloadInfo(){//--跳转到下载界面
		Intent intent=new Intent(MusicPlayer_main_restruct.this,MusicDownLoad.class);
		startActivity(intent);
	}
	public void onDestroy(){
		super.onDestroy();
		unbindService(serviceConnection);
	}

	
}

