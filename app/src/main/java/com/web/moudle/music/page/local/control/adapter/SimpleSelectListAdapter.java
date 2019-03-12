package com.web.moudle.music.page.local.control.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.web.common.base.BaseViewHolder;
import com.web.common.util.ResUtil;
import com.web.common.util.ViewUtil;
import com.web.moudle.music.page.local.control.interf.ListSelectListener;
import com.web.moudle.music.page.local.control.interf.RemoveItemListener;
import com.web.web.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleSelectListAdapter extends RecyclerView.Adapter<BaseViewHolder> implements RemoveItemListener {
    private Context context;
    private List<String> list;
    private int index=-1;
    private ListSelectListener listener;
    private int paddingStart= ViewUtil.dpToPx(16f);
    private int paddingOther= ViewUtil.dpToPx(8f);
    private int arrowSize=ViewUtil.dpToPx(16f);
    public SimpleSelectListAdapter(Context context, List<String> list){
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
        tv.setText(list.get(position));
        tv.setOnClickListener((v1)->{
            if(listener!=null){
                listener.select(v1,position);
                setIndex(position);
            }
        });
        if(index==position){
            tv.setPadding(paddingStart,paddingOther,paddingOther,paddingOther);
            Drawable drawable=context.getDrawable(R.drawable.icon_pause_black);
            if(drawable!=null){
                drawable.setBounds(0,0,arrowSize,arrowSize);
                tv.setCompoundDrawables(drawable,null,null,null);
            }
            tv.setTextColor(ResUtil.getColor(R.color.themeColor));

        }else {
            tv.setBackgroundColor(Color.TRANSPARENT);
            tv.setCompoundDrawables(null,null,null,null);
            tv.setPadding(arrowSize+paddingStart,paddingOther,paddingOther,paddingOther);
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
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void setListener(ListSelectListener listener) {
        this.listener = listener;
    }

    public void setIndex(int index) {
        if(index>=0&&index<list.size()) {
            if(this.index>=0)
                notifyItemChanged(this.index);
            this.index = index;
            notifyItemChanged(index);
        }
    }


}
