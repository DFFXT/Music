package com.web.common.base;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.web.common.imageLoader.glide.ImageLoad;

public class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        this(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }
    public BaseViewHolder(View itemView) {
        super(itemView);
    }
    public TextView bindText(@IdRes int id,CharSequence text){
        TextView tv=itemView.findViewById(id);
        tv.setText(text);
        return tv;
    }
    public ImageView bindImage(@IdRes int id, @DrawableRes int drawableId){
        ImageView iv=itemView.findViewById(id);
        iv.setImageResource(drawableId);
        return iv;
    }
    public ImageView bindImage(@IdRes int id, Drawable drawable){
        ImageView iv=itemView.findViewById(id);
        iv.setImageDrawable(drawable);
        return iv;
    }
    public ImageView bindImage(@IdRes int id,String url){
        ImageView iv=itemView.findViewById(id);
        ImageLoad.load(url).into(iv);
        return iv;
    }
    public ImageView bindImage(@IdRes int id,@DrawableRes int placeHolder,String url){
        ImageView iv=itemView.findViewById(id);
        ImageLoad.load(url).placeholder(placeHolder).into(iv);
        return iv;
    }
    public <T extends View> T findViewById(int id){
        return itemView.findViewById(id);
    }
}
