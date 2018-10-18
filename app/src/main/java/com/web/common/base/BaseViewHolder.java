package com.web.common.base;

import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class BaseViewHolder extends RecyclerView.ViewHolder {
    public final View rootView;
    public BaseViewHolder(View itemView) {
        super(itemView);
        this.rootView=itemView;
    }
    public void bindText(@IdRes int id,String text){
        ((TextView)rootView.findViewById(id)).setText(text);
    }
}
