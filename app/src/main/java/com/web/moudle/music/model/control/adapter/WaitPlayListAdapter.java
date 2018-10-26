package com.web.moudle.music.model.control.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.web.common.base.BaseViewHolder;
import com.web.common.util.ResUtil;
import com.web.data.Music;
import com.web.moudle.music.model.control.interf.WaitMusicListener;
import com.web.moudle.music.model.control.interf.RemoveItemListener;
import com.web.web.R;

import java.util.List;

public class WaitPlayListAdapter extends RecyclerView.Adapter<BaseViewHolder> implements RemoveItemListener {
    private Context context;
    private List<Music> list;
    private int index=-1;
    private WaitMusicListener listener;
    public WaitPlayListAdapter(Context context, List<Music> list){
        this.context=context;
        this.list=list;
    }
    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.view_textview,parent,false);
        return new BaseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        TextView tv= (TextView) holder.rootView;
        tv.setText(list.get(position).getMusicName());
        tv.setOnClickListener((v1)->{
            if(listener!=null){
                listener.select(v1,position);
            }
        });
        if(index==position){
            tv.setPadding(20,20,20,20);
            Drawable drawable=context.getDrawable(R.drawable.pause);
            if(drawable!=null){
                drawable.setBounds(0,0,25,25);
                tv.setCompoundDrawables(drawable,null,null,null);
            }
            tv.setTextColor(ResUtil.getColor(R.color.themeColor));

        }else {
            tv.setBackgroundColor(Color.TRANSPARENT);
            tv.setCompoundDrawables(null,null,null,null);
            tv.setPadding(45,20,20,20);
            tv.setTextColor(ResUtil.getColor(R.color.textColorGray));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void swipeItem(RecyclerView.ViewHolder holder,int direction) {
        int position=holder.getAdapterPosition();
        if(listener!=null){
            listener.remove(holder.itemView,position);
        }
        notifyItemRemoved(position);
    }

    public void setListener(WaitMusicListener listener) {
        this.listener = listener;
    }

    public void setIndex(int index) {
        if(index>=0&&index<list.size()) {
            notifyItemChanged(this.index);
            this.index = index;
            notifyItemChanged(index);
        }
    }


}
