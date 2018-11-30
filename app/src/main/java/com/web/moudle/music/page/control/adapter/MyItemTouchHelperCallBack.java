package com.web.moudle.music.page.control.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.web.moudle.music.page.control.interf.RemoveItemListener;

public class MyItemTouchHelperCallBack extends ItemTouchHelper.Callback{

    private RemoveItemListener listener;
    public MyItemTouchHelperCallBack(RemoveItemListener listener){
        this.listener=listener;
    }
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int flg=ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
        return makeMovementFlags(0,flg);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.swipeItem(viewHolder,direction);
    }
}
