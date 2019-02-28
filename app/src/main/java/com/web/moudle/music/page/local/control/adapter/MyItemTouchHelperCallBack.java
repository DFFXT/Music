package com.web.moudle.music.page.local.control.adapter;

import com.web.moudle.music.page.local.control.interf.RemoveItemListener;

import org.jetbrains.annotations.NotNull;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MyItemTouchHelperCallBack extends ItemTouchHelper.Callback{

    private RemoveItemListener listener;
    public MyItemTouchHelperCallBack(@NotNull RemoveItemListener listener){
        this.listener=listener;
    }
    @Override
    public int getMovementFlags(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder) {
        int flg=ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
        return makeMovementFlags(0,flg);
    }

    @Override
    public boolean onMove(@NotNull RecyclerView recyclerView, @NotNull RecyclerView.ViewHolder viewHolder, @NotNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NotNull RecyclerView.ViewHolder viewHolder, int direction) {
        listener.swipeItem(viewHolder,direction);
    }
}
