package com.web.moudle.music.page.local.control.interf;

import androidx.recyclerview.widget.RecyclerView;

public interface RemoveItemListener {
    void swipeItem(RecyclerView.ViewHolder holder,int direction);
}
