package com.web.common.base;


import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;

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

    public void addKeyEventListener(KeyListener keyListener){
        if(!keyListenerList.contains(keyListener)){
            keyListenerList.add(keyListener);
        }
    }
    public void removeKeyListener(KeyListener keyListener){
        keyListenerList.remove(keyListener);
    }

    abstract public @LayoutRes int getLayoutId();
    abstract public void initView();


    public interface KeyListener{
        boolean key(KeyEvent event);
    }

}
