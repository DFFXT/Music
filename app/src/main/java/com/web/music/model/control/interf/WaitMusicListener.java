package com.web.music.model.control.interf;

import android.view.View;

public interface WaitMusicListener {
    void select(View v,int position);
    void remove(View v,int position);
}
