package com.web.moudle.music.page;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.web.common.tool.MToast;
import com.web.common.util.ResUtil;
import com.web.common.util.ViewUtil;
import com.web.config.GetFiles;
import com.web.config.Shortcut;
import com.web.data.InternetMusic;
import com.web.data.InternetMusicInfo;
import com.web.moudle.music.model.InternetDataSource;
import com.web.moudle.music.model.InternetViewModel;
import com.web.moudle.music.page.control.adapter.InternetMusicAdapter;
import com.web.moudle.music.player.MusicPlay;
import com.web.subWeb.GetInfo;
import com.web.web.R;

import java.util.Objects;

public class InternetMusicPage extends BaseMusicPage {
    public final static String pageName="InternetMusic";
    public Context context;
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    private InternetViewModel vm;
    @Nullable
    private MusicPlay.Connect connect;
    private TextView tv_searchICon;//***居中的搜索控件
    private SearchView searchView;//***搜索控件
    private String keyWords;
    private InternetMusicAdapter adapter;


    @Override
    public int getLayoutId() {
        return R.layout.music_internet;
    }

    @Override
    public void initView(@NonNull View view) {
        context=getContext();
        recyclerView=view.findViewById(R.id.internetMusic);
        smartRefreshLayout=view.findViewById(R.id.smartRefreshLayout);
        tv_searchICon=view.findViewById(R.id.searchIcon);
        LinearLayoutManager manager=new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        DividerItemDecoration decoration=new DividerItemDecoration(context,DividerItemDecoration.VERTICAL);
        decoration.setDrawable(ViewUtil.getDrawable(R.drawable.recycler_divider));
        recyclerView.addItemDecoration(decoration);
        adapter=new InternetMusicAdapter(context);
        vm= ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(InternetViewModel.class);

        recyclerView.setAdapter(adapter);
        adapter.setListener(this::downloadConsider);



        smartRefreshLayout.setEnableRefresh(false);
        smartRefreshLayout.setEnableOverScrollDrag(true);
        smartRefreshLayout.setEnableLoadMore(true);
        smartRefreshLayout.setRefreshFooter(new ClassicsFooter(context));

        //**点击搜索
        tv_searchICon.setOnClickListener((v)->{
            if(searchView!=null){
                searchView.onActionViewExpanded();
            }
        });
        init();
    }
    private void init(){
        vm.getStatus().observe((LifecycleOwner) context, wrapper -> {
            if(wrapper==null)return;
            switch (wrapper.getCode()){
                case InternetDataSource.CODE_OK:{
                    smartRefreshLayout.finishLoadMore();
                }break;
                case InternetDataSource.CODE_NO_DATA:{
                    smartRefreshLayout.setNoMoreData(true);
                    //MToast.showToast(context, ResUtil.getString(R.string.noMoreData));
                }break;
                case InternetDataSource.CODE_JSON_ERROR:{
                    MToast.showToast(context, ResUtil.getString(R.string.dataAnalyzeError));
                }break;
                case InternetDataSource.CODE_URL_ERROR:{
                    MToast.showToast(context, ResUtil.getString(R.string.urlAnalyzeError));
                }break;
                case InternetDataSource.CODE_NET_ERROR:{
                    MToast.showToast(context, ResUtil.getString(R.string.noInternet));
                }break;
            }
        });
    }
    private void closeKeyBord(){//--close键盘
        InputMethodManager imm=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
        }
    }

    @Override
    public void setConnect(@NonNull MusicPlay.Connect connect) {
        this.connect = connect;
    }

    @NonNull
    @Override
    public String getPageName() {
        return pageName;
    }

    /**
     * 外部调用搜索
     * @param keyword 关键词
     */
    public void search(String keyword){
        smartRefreshLayout.setNoMoreData(false);
        closeKeyBord();
        tv_searchICon.setVisibility(View.GONE);
        vm.setKeyWords(keyword);
        vm.getMusicList().observe((LifecycleOwner) context, pl-> adapter.submitList(pl));
        this.keyWords=keyword;
    }

    /**
     * 网络音乐点击
     * @param music p
     */
    @SuppressLint("SetTextI18n")
    private void downloadConsider(InternetMusic music){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.create();
        builder.setPositiveButton("取消", (arg0, arg1) -> {

        });
        builder.setNeutralButton("在线试听", (dialog, which) -> new Thread(() -> {
            if (connect==null)return;
            GetInfo getInfo=new GetInfo();
            InternetMusicInfo info=getInfo.getMusicInfo(music.getHash());
            info.setMusicName(Shortcut.validatePath(music.getMusicName()));
            info.setSinger(Shortcut.validatePath(music.getSingerName()));
            if(!Shortcut.fileExsist(info.getLyricsPath())){
                new GetFiles().write(info.getLyricsPath(),getInfo.getKrc(music.getHash()),false);
            }
            if(!Shortcut.fileExsist(info.getIconPath())){
                if(info.getImgAddress()!=null){
                    new GetFiles().NetDataToLocal(info.getImgAddress().replace("{size}", "80"), GetFiles.singerPath+ music.getSingerName()+".png");//---下载图片
                }
            }
            connect.playInternet(info);
        }).start());
        builder.setNegativeButton("下载("+ ResUtil.getFileSize(music.getFullSize())+")", (arg0, arg1) -> {
            if(connect==null)return;
            //**网络获取的时间以秒为单位、后面需要毫秒(媒体库里面的单位为毫秒)
            music.setDuration(music.getDuration()*1000);
            connect.download(music);
        });

        builder.setTitle(music.getMusicName());
        TextView tv_songName=new TextView(context);
        tv_songName.setTextColor(context.getResources().getColor(R.color.white,context.getTheme()));
        builder.setMessage("歌手："+music.getSingerName()+
                "\n时长："+ ResUtil.timeFormat("mm:ss",music.getDuration()*1000)+
                "\n大小："+ ResUtil.getFileSize(music.getFullSize()));
        builder.show();
    }

    public void setSearchView(SearchView searchView) {
        this.searchView = searchView;
    }

    public void refresh(){
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            searchView.onActionViewExpanded();
            searchView.setQuery(keyWords,false);
            searchView.clearFocus();
        }
    }
}
