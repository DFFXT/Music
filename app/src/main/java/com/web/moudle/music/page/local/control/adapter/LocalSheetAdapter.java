package com.web.moudle.music.page.local.control.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.web.common.base.BaseAdapter;
import com.web.common.base.BaseViewHolder;
import com.web.common.util.ResUtil;
import com.web.common.util.ViewUtil;
import com.web.misc.InputItem;
import com.web.moudle.music.page.local.control.interf.LocalSheetListener;
import com.web.moudle.music.page.local.control.interf.RemoveItemListener;
import com.music.m.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 歌单选择adapter
 */
public class LocalSheetAdapter extends BaseAdapter<String> implements RemoveItemListener {
    private List<String> list;
    private int index=-1;
    private LocalSheetListener listener;
    private int paddingStart= ViewUtil.dpToPx(16f);
    private int paddingOther= ViewUtil.dpToPx(8f);
    private int arrowSize=ViewUtil.dpToPx(16f);
    private int editIndex=-1;
    public LocalSheetAdapter(List<String> list){
        super(list);
        this.list=list;
    }
    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolder(new InputItem(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position,String item) {
        InputItem inputItem = (InputItem) holder.itemView;
        inputItem.setText(list.get(position));
        EditText et=inputItem.getInputBox();
        ImageView button=inputItem.getClickButton();


        if(index==position){
            et.setPadding(paddingStart,paddingOther,paddingOther,paddingOther);
            Drawable drawable=inputItem.getContext().getDrawable(R.drawable.icon_pause_fill);
            if(drawable!=null){
                drawable.setTint(ResUtil.getColor(R.color.themeColor));
                drawable.setBounds(0,0,arrowSize,arrowSize);
                et.setCompoundDrawables(drawable,null,null,null);
            }
            et.setTextColor(ResUtil.getColor(R.color.themeColor));

        }else {
            et.setBackgroundColor(Color.TRANSPARENT);
            et.setCompoundDrawables(null,null,null,null);
            et.setPadding(arrowSize+paddingStart,paddingOther,paddingOther,paddingOther);
            et.setTextColor(ResUtil.getColor(R.color.textColorGray));
        }

        if(position!=editIndex){
            et.setOnClickListener((v1)->{
                if(listener!=null){
                    listener.select(v1,position);
                    setIndex(position);
                }
            });
            button.setSelected(false);
        }else{
            button.setSelected(true);
            et.setOnClickListener(null);
        }

        if(position==0||position==1){
            button.setVisibility(View.INVISIBLE);
        }else {
            button.setVisibility(View.VISIBLE);
            if(listener!=null){
                inputItem.setListenerSave(text->{
                    listener.saveEdit(text,position);
                    return text;
                });
            }
            inputItem.setListenSelect(isSelected->{
                editIndex=position;
                return null;
            });
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

    public void setListener(LocalSheetListener listener) {
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
