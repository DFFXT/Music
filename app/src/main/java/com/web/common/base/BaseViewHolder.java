package com.web.common.base;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.web.common.imageLoader.glide.ImageLoad;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.recyclerview.widget.RecyclerView;

public class BaseViewHolder extends RecyclerView.ViewHolder {
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
