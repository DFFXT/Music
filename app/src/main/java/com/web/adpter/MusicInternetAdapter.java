package com.web.adpter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.web.data.InternetMusic;
import com.web.data.MusicList;
import com.web.web.R;

import java.text.DecimalFormat;
import java.util.List;

public class MusicInternetAdapter extends RecyclerView.Adapter<MusicInternetAdapter.ViewHolder> {

    private Context context;
    private List<InternetMusic> data;
    private OnItemClickListener listener;

    private DecimalFormat format=new DecimalFormat("0.00");
    public MusicInternetAdapter(List<InternetMusic> data){
        this.data=data;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context==null){
            context=parent.getContext();
        }
        View v= LayoutInflater.from(context).inflate(R.layout.music_internet_item,parent,false);
        return new ViewHolder(v);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InternetMusic music=data.get(position);
        holder.musicName.setText(music.getMusicName());
        holder.singerName.setText(music.getSingerName());
        holder.size.setText(format.format(music.getFullSize()/1024.0/1024)+"MB");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView musicName,singerName,size;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            musicName=v.findViewById(R.id.musicName);
            singerName=v.findViewById(R.id.singerName);
            size=v.findViewById(R.id.size);
        }
        @Override
        public void onClick(View v) {
            if (listener!=null){
                listener.onClick(getLayoutPosition());
            }
        }
    }
    public interface OnItemClickListener{
        void onClick(int position);
    }
}
