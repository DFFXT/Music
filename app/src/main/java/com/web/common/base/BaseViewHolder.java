package com.web.common.base;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class BaseViewHolder extends RecyclerView.ViewHolder {
    public final View rootView;
    public BaseViewHolder(View itemView) {
        super(itemView);
        this.rootView=itemView;
    }
    public TextView bindText(@IdRes int id,String text){
        TextView tv=rootView.findViewById(id);
        tv.setText(text);
        return tv;
    }
    public TextView bindSpannable(@IdRes int id, Spannable spannable){
        TextView tv=rootView.findViewById(id);
        tv.setText(spannable);
        return tv;
    }
    public ImageView bindImage(@IdRes int id, @DrawableRes int drawableId){
        ImageView iv=rootView.findViewById(id);
        iv.setImageResource(drawableId);
        return iv;
    }
    public <T extends View> T findViewById(int id){
        return itemView.findViewById(id);
    }
}
