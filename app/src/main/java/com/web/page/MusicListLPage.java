package com.web.page;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.web.adpter.MusicExpandableAdapter;
import com.web.data.Music;
import com.web.data.MusicGroup;
import com.web.data.MusicList;
import com.web.service.MusicPlay;
import com.web.web.R;
import com.web.web.Text;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class MusicListLPage {
    private View view;
    private List<MusicList<Music>> data;
    private ExpandableListView listView;
    private MusicExpandableAdapter adapter;
    private Context context;
    private MusicPlay.Connect connect;
    public MusicListLPage(final Context context, ViewGroup parent, final MusicPlay.Connect connect){
        this.context=context;
        this.connect=connect;
        view= LayoutInflater.from(context).inflate(R.layout.music_list,parent,false);
        listView=view.findViewById(R.id.musicExpandableList);
        listView.setOnChildClickListener((p, v, groupPosition, childPosition, id)-> {
                if(MusicListLPage.this.connect!=null){
                    MusicListLPage.this.connect.play(groupPosition,childPosition);
                }
                return false;
            });

        //***长点击
        listView.setOnItemLongClickListener((parent1, view, position, id) -> {
            long p=listView.getExpandableListPosition(position);
            int groupIndex=ExpandableListView.getPackedPositionGroup(p);
            int childIndex=ExpandableListView.getPackedPositionChild(p);
            if(childIndex>=0){//**长点击的子项
                if(groupIndex==0)
                    defaultGroupChildLongClick(view,groupIndex,childIndex);
                else likeGroupChildLongClick(view,groupIndex,childIndex);
            }
            else if(groupIndex==0){//***默认音乐组长点击

            }

            return true;
        });


    }

    /**
     * 偏爱组子项长点击
     * @param v v
     * @param groupIndex gr
     * @param childIndex ch
     */
    private void likeGroupChildLongClick(View v,int groupIndex,int childIndex){
        PopupMenu popupMenu =new PopupMenu(context,v);
        popupMenu.inflate(R.menu.like_child_long_click);
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener((item -> {
            switch (item.getItemId()){
                case R.id.musicPlay:{
                    this.connect.play(groupIndex,childIndex);
                }break;
                case R.id.remove:{
                    Music music=data.get(groupIndex).get(childIndex);
                    music.setGroupId(0);
                    music.save();
                    data.get(groupIndex).remove(childIndex);
                    if(data.get(groupIndex).size()==0){
                        data.remove(groupIndex);
                    }
                    adapter.notifyDataSetChanged();
                }break;
                case R.id.detailInfo:{
                    showDtail(data.get(groupIndex).get(childIndex));
                }break;
            }

            return true;
        }));
    }

    /**
     * 默认组子项长点击
     * @param groupIndex gr
     * @param childIndex ch
     */
    private void defaultGroupChildLongClick(View view,int groupIndex,int childIndex){
        PopupMenu popupMenu =new PopupMenu(context,view);
        popupMenu.inflate(R.menu.default_child_long_click);
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.musicPlay:{//**播放
                    this.connect.play(groupIndex,childIndex);
                }break;
                case R.id.delete:{//**删除
                    data.get(groupIndex).get(childIndex).delete();
                    data.get(groupIndex).remove(childIndex);
                    adapter.notifyDataSetChanged();
                }break;
                case R.id.deleteOrigin:{//**完全删除
                    Music music= data.get(groupIndex).get(childIndex);
                    Music.deleteMusic(music);
                    data.get(groupIndex).remove(childIndex);
                    adapter.notifyDataSetChanged();
                }break;
                case R.id.setAsLiske:{
                    Music music=data.get(groupIndex).get(childIndex);
                    MusicGroup group=DataSupport.findFirst(MusicGroup.class);
                    if(group==null){
                        MusicGroup createGroup=new MusicGroup();
                        createGroup.setGroupName("分组");
                        createGroup.setId(1);
                        createGroup.save();
                        data.add(new MusicList<>(createGroup.getGroupName()));
                        data.get(1).setTitle(createGroup.getGroupName());
                    }else {
                        if(data.size()==1){
                            data.add(new MusicList<>(group.getGroupName()));
                        }
                        for(int i=0;i<data.get(1).size();i++){
                            if(data.get(0).get(i).getPath().equals(music.getPath())){
                                return true;
                            }
                        }
                    }
                    music.setGroupId(1);
                    music.save();
                    data.get(1).add(music.copy());
                    adapter.notifyDataSetChanged();
                }break;
                case R.id.detailInfo:{//**详细信息
                    showDtail(data.get(groupIndex).get(childIndex));

                }
            }
            return false;
        });
    }

    /**
     * 显示音乐信息
     * @param music music
     */
    private void showDtail(Music music){
        DecimalFormat format=new DecimalFormat("0.00");
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
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
    public void setConnect(MusicPlay.Connect connect) {
        this.connect = connect;
    }

    /**
     * 设置主音乐页面的数据
     * @param data data
     */
    public void setData(List<MusicList<Music>> data) {
        if(data==null) return;
        if(this.data==null||!this.data.equals(data)){
            adapter=new MusicExpandableAdapter(context,data);
            adapter.setIconClickListener((v,g,c)->{
                connect.addToWait(data.get(g).get(c));

            });
            listView.setAdapter(adapter);
            this.data = data;
            if(data.size()==1){
                expandGroup(0);
            }
        }else {
            adapter.notifyDataSetChanged();
        }

    }

    private void expandGroup(int group){
        if(group>=0&&group<data.size())
            listView.expandGroup(group);
    }

    public View getView() {
        return view;
    }

}
