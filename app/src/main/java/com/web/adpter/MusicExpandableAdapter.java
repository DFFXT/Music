package com.web.adpter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.web.data.Music;
import com.web.data.MusicList;
import com.web.web.R;

import java.util.List;

public class MusicExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<MusicList<Music>> musicList;
    private IconClickListener listener;
    public MusicExpandableAdapter(Context context, List<MusicList<Music>> musicList){
        this.musicList=musicList;
        this.context=context;
    }
    public void setIconClickListener(IconClickListener listener){
        this.listener=listener;
    }
    @Override
    public int getGroupCount() {
        if(musicList==null)return 0;
        return musicList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(musicList.get(groupPosition)==null)return 0;
        return musicList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return musicList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return musicList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition*1000+childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder holder;
        if(convertView==null){
            holder=new GroupHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.music_group_item,parent,false);
            holder.groupName=convertView.findViewById(R.id.groupName);
            holder.musicNum=convertView.findViewById(R.id.musicNum);
            convertView.setTag(holder);
        }else {
            holder=(GroupHolder)convertView.getTag();
        }
        holder.groupName.setText(musicList.get(groupPosition).getTitle());
        holder.musicNum.setText(musicList.get(groupPosition).size()+"");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition,int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder;
        if(convertView==null){
            holder=new ChildHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.music_item_list,parent,false);
            holder.musicName=convertView.findViewById(R.id.musicName);
            holder.singer=convertView.findViewById(R.id.singerName);
            holder.add=convertView.findViewById(R.id.add);
            convertView.setTag(holder);
        }else {
            holder=(ChildHolder) convertView.getTag();
        }
        Music music=musicList.get(groupPosition).get(childPosition);
        holder.musicName.setText(music.getMusicName());
        holder.singer.setText(music.getSinger());
        if(music.getGroupId()==0){
            holder.musicName.setTextColor(Color.BLACK);
            holder.singer.setTextColor(Color.BLACK);
        }
        else if (music.getGroupId()==1){
            holder.musicName.setTextColor(context.getResources().getColor(R.color.colorAccent,context.getTheme()));
            holder.singer.setTextColor(context.getResources().getColor(R.color.colorAccent,context.getTheme()));
        }
        //**添加到准备列表
        if(listener!=null){
            holder.add.setOnClickListener(v -> {
                listener.onClick(v,groupPosition, childPosition);
            });
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }








    private class GroupHolder{
        TextView groupName,musicNum;
    }
    private class ChildHolder{
        TextView musicName,singer;
        ImageView add;
    }
    public interface IconClickListener{
        void onClick(View v,int group,int child);
    }



}
