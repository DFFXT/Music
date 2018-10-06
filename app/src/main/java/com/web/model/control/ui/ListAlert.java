package com.web.model.control.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;

import com.web.data.Music;
import com.web.model.control.interf.WaitMusicListener;
import com.web.model.control.model.MyItemTouchHelper;
import com.web.model.control.model.WaitPlayListAdapter;
import com.web.util.StrUtil;
import com.web.util.ViewUtil;
import com.web.web.R;

import java.util.List;

public class ListAlert {
    private Context context;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private List<Music> list;
    private RecyclerView view;
    private WaitMusicListener waitMusicListener;
    private int index=-1;
    private WaitPlayListAdapter adapter;
    public ListAlert(Context context){
        this.context=context;
        builder=new AlertDialog.Builder(context,R.style.Alert);
        builder.setTitle(StrUtil.getString(R.string.playList));


    }
    public void setMusicList(List<Music> list){
        this.list=list;
    }

    public void setIndex(int index) {
        this.index = index;
        if(adapter!=null){
            adapter.setIndex(index);
        }
    }

    public WaitPlayListAdapter getAdapter() {
        return adapter;
    }

    public void build(){
        view= (RecyclerView) LayoutInflater.from(context).inflate(R.layout.view_recycler,null);
        view.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));

        adapter=new WaitPlayListAdapter(context,list);
        adapter.setListener(waitMusicListener);
        adapter.setIndex(index);
        view.setAdapter(adapter);
        ItemTouchHelper.Callback callback=new MyItemTouchHelper(adapter);
        ItemTouchHelper helper=new ItemTouchHelper(callback);
        helper.attachToRecyclerView(view);
        dialog=builder.create();
        dialog.setView(view,5,5,5,5);


    }

    public void setWaitMusicListener(WaitMusicListener waitMusicListener) {
        this.waitMusicListener = waitMusicListener;
    }

    public void show(){
        dialog.show();
        Window window=dialog.getWindow();
        if(window!=null){
            WindowManager.LayoutParams params= window.getAttributes();
            params.height= WindowManager.LayoutParams.WRAP_CONTENT;
            params.width= (int) (ViewUtil.screenWidth()*0.75f);
            window.setAttributes(params);
        }
    }
    public void cancel(){
        dialog.cancel();
    }
}
