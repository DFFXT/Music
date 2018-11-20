package com.web.moudle.music.page;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.web.common.util.ResUtil;
import com.web.data.Music;
import com.web.data.MusicList;
import com.web.misc.DrawableItemDecoration;
import com.web.misc.IndexBar;
import com.web.moudle.music.model.control.adapter.LocalMusicAdapter;
import com.web.moudle.music.model.control.ui.SingleTextListAlert;
import com.web.moudle.music.player.MusicPlay;
import com.web.moudle.music.player.SongSheetManager;
import com.web.moudle.music.player.bean.SongSheet;
import com.web.web.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
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
                    connect.delete(groupIndex,position,false);
                }break;
                case R.id.deleteOrigin:{//**完全删除
                    new android.app.AlertDialog.Builder(getContext())
                            .setTitle(ResUtil.getString(R.string.deleteOrigin))
                            .setMessage(data.get(position).getPath())
                            .setNegativeButton(ResUtil.getString(R.string.no),null)
                            .setPositiveButton(ResUtil.getString(R.string.yes),(dialog,witch)->{
                                connect.delete(groupIndex,position,true);
                            }).create().show();
                }break;
                case R.id.setAsLiske:{
                    List<SongSheet> list=SongSheetManager.INSTANCE.getSongSheetList().getSongList();
                    List<String> sheetNameList=new ArrayList<>();
                    for(SongSheet sheet:list){
                        sheetNameList.add(sheet.getName());
                    }
                    sheetNameList.add("新建");
                    SingleTextListAlert alert=new SingleTextListAlert(getContext(),"");
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
                }
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
    public void initView(@NotNull View rootView) {
        rv_musicList =rootView.findViewById(R.id.musicExpandableList);
        indexBar=rootView.findViewById(R.id.indexBar_musicList);
        rv_musicList.setLayoutManager(new LinearLayoutManager(rootView.getContext(),LinearLayoutManager.VERTICAL,false));
        rv_musicList.addItemDecoration(new DrawableItemDecoration(LinearLayout.VERTICAL,4,ResUtil.getDrawable(R.drawable.recycler_divider)));
        if(data!=null){
            adapter=new LocalMusicAdapter(rootView.getContext(),data.getMusicList());
        }else {
            adapter=new LocalMusicAdapter(rootView.getContext(),null);
        }

        adapter.setItemClickListener((v,position)->{
            connect.musicSelect(groupIndex,position);
            return null;
        });
        adapter.setAddListenner(position->{
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


        String str="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        List<String> indexList=Arrays.asList(str.split(""));
        indexBar.setVerticalGap(10);
        indexBar.setIndexList(indexList);

    }
}
