package com.web.moudle.music.page.local;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.web.common.util.PinYin;
import com.web.common.util.ResUtil;
import com.web.data.Music;
import com.web.data.MusicList;
import com.web.misc.DrawableItemDecoration;
import com.web.misc.IndexBar;
import com.web.moudle.music.page.BaseMusicPage;
import com.web.moudle.music.page.local.control.adapter.LocalMusicAdapter;
import com.web.moudle.music.page.local.control.ui.SingleTextListAlert;
import com.web.moudle.music.player.MusicPlay;
import com.web.moudle.music.player.SongSheetManager;
import com.web.moudle.music.player.bean.SongSheet;
import com.web.web.R;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MusicListLPage extends BaseMusicPage {
    public final static String pageName="MusicList";
    private MusicList<Music> data;
    private RecyclerView rv_musicList;
    private IndexBar indexBar;
    private LocalMusicAdapter adapter;
    private MusicPlay.Connect connect;
    private int groupIndex=0;




    /**
     * 默认组子项长点击
     * @param position p
     */
    private void defaultGroupChildLongClick(View view,int position){
        PopupMenu popupMenu =new PopupMenu(Objects.requireNonNull(getContext()),view);
        popupMenu.inflate(R.menu.default_child_long_click);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.musicPlay:{//**播放
                    this.connect.musicSelect(groupIndex,position);
                }break;
                case R.id.delete:{//**删除
                    connect.delete(false,groupIndex,position);
                }break;
                case R.id.deleteOrigin:{//**完全删除
                    if(groupIndex==0){//**group 0 才可以删除源文件
                        new android.app.AlertDialog.Builder(getContext())
                                .setTitle(ResUtil.getString(R.string.deleteOrigin))
                                .setMessage(data.get(position).getPath())
                                .setNegativeButton(ResUtil.getString(R.string.no),null)
                                .setPositiveButton(ResUtil.getString(R.string.yes),(dialog,witch)->{
                                    connect.delete(true,groupIndex,position);
                                }).create().show();
                    }
                }break;
                case R.id.setAsLiske:{
                    List<SongSheet> list=SongSheetManager.INSTANCE.getSongSheetList().getSongList();
                    List<String> sheetNameList=new ArrayList<>();
                    for(SongSheet sheet:list){
                        sheetNameList.add(sheet.getName());
                    }
                    sheetNameList.add("新建");
                    SingleTextListAlert alert=new SingleTextListAlert(getContext(),"歌单");
                    alert.setList(sheetNameList);
                    alert.setItemClickListener(index->{
                        if(index!=sheetNameList.size()-1){
                            list.get(index).add(data.get(position).getId());
                            connect.groupChange();
                            SongSheetManager.INSTANCE.getSongSheetList().save();
                            alert.cancel();
                        }else {
                            String name="sheet-"+SongSheetManager.INSTANCE.getSongSheetList().getSongList().size();
                            SongSheetManager.INSTANCE.createNewSongSheet(name);
                            sheetNameList.add(index,name);
                            alert.getAdapter().notifyItemRangeInserted(index,1);
                            connect.groupChange();
                        }
                        return null;
                    });
                    alert.build();
                    alert.show();
                }break;
                case R.id.detailInfo:{//**详细信息
                    showDetail(data.get(position));
                }break;
                case R.id.multiSelect:{//**多选
                    showMultiSelect(view);
                    adapter.select(position);
                }break;
            }
            return false;
        });
    }

    /**
     * 显示音乐信息
     * @param music music
     */
    private void showDetail(Music music){
        DecimalFormat format=new DecimalFormat("0.00");
        AlertDialog.Builder builder=new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setTitle("曲名："+music.getMusicName());
        File file=new File(music.getPath());
        builder.setMessage("路径："+music.getPath()
                +"\r\n大小："+format.format(file.length()/1024.0/1024)+"MB");
        builder.create().show();
    }

    /**
     * 显示多选
     * @param v view
     */
    private void showMultiSelect(View v){
        adapter.setSelect(true);
        v.startActionMode(new ActionMode.Callback2() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.like_child_long_click,menu);
                return true;
            }
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.remove:{
                        connect.delete(false,groupIndex, getSelectedList());
                        adapter.getSelectedSet().clear();
                    }break;
                    case R.id.deleteOrigin:{
                        connect.delete(true,groupIndex, getSelectedList());
                        adapter.getSelectedSet().clear();
                    }break;
                    case R.id.selectAll:{
                        adapter.setSelectAll(!adapter.isSelectAll());
                    }break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapter.setSelect(false);
            }
        },ActionMode.TYPE_PRIMARY);
    }


    /**
     * 获取被选中的position
     * @return
     */
    private List<Integer> getSelectedList(){
        SparseBooleanArray hashSet=adapter.getSelectedSet();
        ArrayList<Integer> list=new ArrayList<>();
        for(int i=0;i<adapter.getItemCount();i++){
            if(hashSet.get(i,false)){
                list.add(i);
            }
        }
        return list;
    }
    /**
     * 设置连接接口
     * @param connect connect
     */
    @Override
    public void setConnect(@NonNull MusicPlay.Connect connect) {
        if(this.connect==null){
            this.connect = connect;
            connect.getList(groupIndex);
        }

    }

    @NotNull
    @Override
    public String getPageName() {
        return pageName;
    }

    /**
     * 设置主音乐页面的数据
     * @param data data
     */
    public void setData(int groupIndex,MusicList<Music> data) {
        this.groupIndex=groupIndex;
        this.data=data;
        if(adapter!=null){
            if(musicGroupIndex!=groupIndex){
                adapter.setIndex(-1);
            }else {
                adapter.setIndex(childPosition);
            }
            adapter.notifyItemChanged(childPosition);
            adapter.setData(data.getMusicList());
            adapter.notifyDataSetChanged();
        }

    }

    private int musicGroupIndex=0;
    private int childPosition=0;
    protected void loadMusic(int musicGroupIndex,int position){
        this.musicGroupIndex=musicGroupIndex;
        this.childPosition=position;
        if(adapter!=null&&musicGroupIndex==groupIndex) {
            adapter.setIndex(position);
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.music_list;
    }


    @Override
    public void viewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void initView(@NotNull View rootView) {
        rv_musicList =rootView.findViewById(R.id.musicExpandableList);
        indexBar=rootView.findViewById(R.id.indexBar_musicList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL,false);
        rv_musicList.setLayoutManager(layoutManager);
        rv_musicList.addItemDecoration(new DrawableItemDecoration(0,0,0,4,LinearLayout.VERTICAL,ResUtil.getDrawable(R.drawable.recycler_divider)));
        if(data!=null){
            adapter=new LocalMusicAdapter(rootView.getContext(),data.getMusicList());
        }else {
            adapter=new LocalMusicAdapter(rootView.getContext(),null);
        }
        adapter.setSelect(false);

        adapter.setItemClickListener((v,position)->{
            connect.musicSelect(groupIndex,position);
            return null;
        });
        adapter.setAddListener(position->{
            connect.addToWait(data.get(position));
            return null;
        });

        adapter.setItemLongClickListener((v,position)->{
            defaultGroupChildLongClick(v,position);
            return true;
        });
        rv_musicList.setAdapter(adapter);
        if(connect!=null){
            connect.getList(groupIndex);
        }
        String str="A B C D E F G H I J K L M N O P Q R S T U V W X Y Z";
        List<String> indexList=Arrays.asList(str.split(" "));
        indexBar.setVerticalGap(10);
        indexBar.setIndexList(indexList);
        rv_musicList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int p=layoutManager.findFirstVisibleItemPosition();
                if(p<0)return;
                try {
                    char code=PinYin.getFirstChar(data.get(p).getMusicName().charAt(0)+"");
                    for(int i=0;i<indexList.size();i++){
                        if(code==indexList.get(i).charAt(0)){
                            indexBar.setSelectedIndex(i);
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        rv_musicList.setFocusable(true);


    }

}
