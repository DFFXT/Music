package com.web.moudle.music.page.local.control.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.web.common.base.BaseAdapter;
import com.web.common.base.BaseViewHolder;
import com.web.common.util.KeyboardManager;
import com.web.common.util.ResUtil;
import com.web.common.util.ViewUtil;
import com.web.moudle.music.page.local.control.interf.LocalSheetListener;
import com.web.moudle.music.page.local.control.interf.RemoveItemListener;
import com.web.web.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LocalSheetAdapter extends BaseAdapter<String> implements RemoveItemListener {
    private List<String> list;
    private int index=-1;
    private LocalSheetListener listener;
    private int paddingStart= ViewUtil.dpToPx(16f);
    private int paddingOther= ViewUtil.dpToPx(8f);
    private int arrowSize=ViewUtil.dpToPx(16f);
    public LocalSheetAdapter(List<String> list){
        super(list);
        this.list=list;
    }
    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit,parent,false);
        return new BaseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position,String item) {
        EditText tv = holder.itemView.findViewById(R.id.et_name);
        tv.setFocusableInTouchMode(false);

        tv.setText(list.get(position));
        tv.setOnClickListener((v1)->{
            if(listener!=null){
                listener.select(v1,position);
                setIndex(position);
            }
        });
        if(index==position){
            tv.setPadding(paddingStart,paddingOther,paddingOther,paddingOther);
            Drawable drawable=tv.getContext().getDrawable(R.drawable.icon_pause_black);
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
        if(position==0){
            holder.itemView.findViewById(R.id.iv_changeStatus).setVisibility(View.INVISIBLE);
        }else {
            holder.itemView.findViewById(R.id.iv_changeStatus).setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.iv_changeStatus).setOnClickListener(v->{
                v.setSelected(!v.isSelected());
                tv.setFocusableInTouchMode(v.isSelected());
                if(v.isSelected()){
                    tv.setSelection(item.length());
                    KeyboardManager.requestKeyboard(tv);
                    tv.setOnClickListener(null);
                }else{
                    KeyboardManager.hideKeyboard(tv.getContext(),tv.getWindowToken());
                    tv.setOnClickListener((v1)->{
                        if(listener!=null){
                            listener.select(v1,position);
                            setIndex(position);
                        }
                    });
                    if(listener!=null){
                        listener.saveEdit(tv.getText().toString(),position);
                    }
                }

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
