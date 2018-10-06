package com.web.page;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.web.adpter.MusicInternetAdapter;
import com.web.config.GetFiles;
import com.web.config.Shortcut;
import com.web.data.InternetMusic;
import com.web.data.InternetMusicInfo;
import com.web.data.MusicList;
import com.web.model.internet.music.bean.InternetMusicWrapper;
import com.web.model.internet.music.viewmodel.InternetViewModel;
import com.web.service.MusicPlay;
import com.web.subWeb.GetInfo;
import com.web.web.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class InternetMusicPage {
    public Context context;
    private View view;
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView recyclerView;
    private List<InternetMusic> data=new ArrayList<>();
    private InternetViewModel vm;
    private MusicInternetAdapter adapter;
    private MusicPlay.Connect connect;
    private TextView tv_searchICon;//***居中的搜索控件
    private SearchView searchView;//***搜索控件

    private String keyword;
    private int page=0;
    private boolean addToEnd;
    private boolean isSearch=false;//**是否正在搜索
    public InternetMusicPage(@NonNull ViewGroup parent, AppCompatActivity activity, MusicPlay.Connect connect){
        this.context=parent.getContext();
        this.connect=connect;
        view= LayoutInflater.from(context).inflate(R.layout.music_internet,parent,false);
        recyclerView=view.findViewById(R.id.internetMusic);
        smartRefreshLayout=view.findViewById(R.id.smartRefreshLayout);
        tv_searchICon=view.findViewById(R.id.searchIcon);
        LinearLayoutManager manager=new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
        vm= ViewModelProviders.of(activity).get(InternetViewModel.class);
        vm.getData().observe(activity, wrapper -> {
            if(wrapper==null){
                return;
            }
            if(wrapper.getCode()==InternetMusicWrapper.OK){
                smartRefreshLayout.finishRefresh();
                smartRefreshLayout.finishLoadMore();
                if(addToEnd){
                    data.addAll(wrapper.getList());
                }else{
                    data.addAll(0,wrapper.getList());
                }
                if(wrapper.getList().size()==0){
                    smartRefreshLayout.setNoMoreData(true);
                }
                notify(data);
            }
        });
        setNewData(data);
        //**添加上拉加载
        smartRefreshLayout.setOnLoadMoreListener(refreshLayout -> {
            vm.search(keyword,++page);
            addToEnd=true;
        });
        smartRefreshLayout.setRefreshFooter(new ClassicsFooter(context));
        smartRefreshLayout.setRefreshHeader(new ClassicsHeader(context));
        //**添加下拉顶部加载
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            if(page!=0) {
                vm.search(keyword, ++page);
                addToEnd = false;
            }
        });

        //**点击搜索
        tv_searchICon.setOnClickListener((v)->{
            if(searchView!=null){
                searchView.onActionViewExpanded();
            }
        });
    }
    private void closeKeyBord(){//--close键盘
        InputMethodManager imm=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
        }
    }

    public void setConnect(MusicPlay.Connect connect) {
        this.connect = connect;
    }

    /**
     * 外部调用搜索
     * @param keyword 关键词
     * @param page page
     */
    public void search(String keyword,final int page){
        data.clear();
        this.keyword=keyword;
        this.page=page;
        smartRefreshLayout.setNoMoreData(false);
        closeKeyBord();
        vm.search(keyword,page);
    }

    /**
     * 网络音乐点击
     * @param position p
     */
    @SuppressLint("SetTextI18n")
    private void downloadConsider(final int position){
        InternetMusic music=data.get(position);
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.create();
        builder.setPositiveButton("取消", (arg0, arg1) -> {

        });
        builder.setNeutralButton("在线试听", (dialog, which) -> new Thread(() -> {
            InternetMusic music1 =data.get(position);
            GetInfo getInfo=new GetInfo();
            InternetMusicInfo info=getInfo.getMusicInfo(data.get(position).getHash());
            info.setMusicName(music1.getMusicName());
            info.setSinger(music1.getSingerName());
            if(!Shortcut.fileExsist(info.getLyricsPath())){
                new GetFiles().write(info.getLyricsPath(),getInfo.getKrc(music1.getHash()),false);
            }
            if(!Shortcut.fileExsist(info.getIconPath())){
                if(info.getImgAddress()!=null){
                    new GetFiles().NetDataToLocal(info.getImgAddress().replace("{size}", "80"), GetFiles.singerPath+ music1.getSingerName()+".png");//---下载图片
                }
            }
            connect.playInternet(info);
        }).start());
        builder.setNegativeButton("下载", (arg0, arg1) -> {
            if(connect==null)return;
            connect.download(data.get(position));
        });

        builder.setTitle(music.getMusicName());
        TextView textView_songname=new TextView(context);
        textView_songname.setTextColor(context.getResources().getColor(R.color.white,context.getTheme()));
        DecimalFormat format=new DecimalFormat("0.00");
        String ss=format.format(music.getFullSize()/1024.0/1024);
        builder.setMessage("歌手："+music.getSingerName()+"\n"+"大小："+ss+"MB");
        builder.show();
    }

    public void setSearchView(SearchView searchView) {
        this.searchView = searchView;
    }

    /**
     * 设置显示数据
     * @param data data
     */
    private void setNewData(List<InternetMusic> data){
        this.data=data;
        adapter=new MusicInternetAdapter(data);
        recyclerView.setAdapter(adapter);
        adapter.setListener(this::downloadConsider);

    }

    /**
     * 添加数据
     * @param data data
     */
    private void notify(List<InternetMusic> data){
        if(data==null||data.size()==0){
            tv_searchICon.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }else {
            tv_searchICon.setVisibility(View.GONE);
            tv_searchICon.setEditableFactory(Editable.Factory.getInstance());
        }
        if(data!=null){
            adapter.notifyDataSetChanged();
        }
    }
    public void refresh(){
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    public List<InternetMusic> getData() {
        return data;
    }

    public View getView() {
        return view;
    }

    public String getKeyword() {
        return keyword;
    }
}
