package com.web.moudle.music.page.control.interf;

import android.support.v7.widget.RecyclerView;

public interface RemoveItemListener {
    void swipeItem(RecyclerView.ViewHolder holder,int direction);
}
