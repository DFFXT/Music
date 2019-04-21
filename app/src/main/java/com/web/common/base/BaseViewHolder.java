package com.web.common.base;

import android.text.Spannable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.web.common.imageLoader.glide.GlideApp;
import com.web.common.imageLoader.glide.ImageLoad;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

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
    public ImageView bindImage(@IdRes int id,String url){
        ImageView iv=rootView.findViewById(id);
        ImageLoad.load(url).into(iv);
        return iv;
    }
    public <T extends View> T findViewById(int id){
        return itemView.findViewById(id);
    }
}
