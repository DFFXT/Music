package com.web.moudle.music.page.local.control.interf;

import android.view.View;


public interface ListSelectListener {
    default void select(View v, int position){}
    default void remove(View v,int position){}
}
