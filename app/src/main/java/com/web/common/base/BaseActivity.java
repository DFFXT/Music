package com.web.common.base;


import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.web.misc.SwipeFrameLayout;
import com.web.web.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    public static String INTENT_DATA="_mData";
    private List<KeyListener> keyListenerList=new ArrayList<>();
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        //Thread.setDefaultUncaughtExceptionHandler(UncaughtException.INSTANCE);

        //**设置字体大小
        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = .85f;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        setContentView(getLayoutId());
        initView();

        if(enableSwipeToBack()){//**向右滑可以关闭activity，需要设置activity透明
            ViewGroup group= (ViewGroup) getWindow().getDecorView().findViewById(R.id.action_bar_root).getParent();
            SwipeFrameLayout layout=new SwipeFrameLayout(this);
            View child=group.getChildAt(0);
            child.setBackgroundColor(Color.WHITE);
            group.removeViewAt(0);
            group.addView(layout,0);
            layout.addView(child);
        }


    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        for(int i=0;i<keyListenerList.size();i++){
            if(keyListenerList.get(i).key(event)){
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public final void addKeyEventListener(KeyListener keyListener){
        if(!keyListenerList.contains(keyListener)){
            keyListenerList.add(keyListener);
        }
    }
    public void removeKeyListener(KeyListener keyListener){
        keyListenerList.remove(keyListener);
    }

    /**
     * 是否允许滑动返回
     * @return bool
     */
    public Boolean enableSwipeToBack(){
        return false;
    }

    abstract public @LayoutRes int getLayoutId();
    abstract public void initView();


    public interface KeyListener{
        boolean key(KeyEvent event);
    }

}
