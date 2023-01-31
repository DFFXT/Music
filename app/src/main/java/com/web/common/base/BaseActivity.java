package com.web.common.base;


import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.web.common.constant.Constant;
import com.web.misc.SwipeFrameLayout;
import com.music.m.R;

import java.util.ArrayList;
import java.util.List;

import androidx.activity.ComponentActivity;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.http.Headers;

public abstract class BaseActivity extends AppCompatActivity {
    public static String INTENT_DATA = "_mData";
    //**返回键监听
    private final List<KeyListener> keyListenerList = new ArrayList<>();
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        //**设置字体大小
        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = Constant.LocalConfig.fontScale;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        View rootView = viewBindingInit();
        if (rootView != null) {
            setContentView(rootView);
        } else {
            if(getLayoutId() !=0 ){
                setContentView(getLayoutId());
            }
        }

        initView();

        if (enableSwipeToBack()) {//**向右滑可以关闭activity，需要设置activity透明
            ViewGroup group = (ViewGroup) getWindow().getDecorView().findViewById(R.id.action_bar_root).getParent();
            SwipeFrameLayout layout = new SwipeFrameLayout(this);
            View child = group.getChildAt(0);
            child.setBackgroundColor(Color.WHITE);
            group.removeViewAt(0);
            group.addView(layout, 0);
            layout.addView(child);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        for (int i = 0; i < keyListenerList.size(); i++) {
            if (keyListenerList.get(i).key(event)) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public final void addKeyEventListener(KeyListener keyListener) {
        if (!keyListenerList.contains(keyListener)) {
            keyListenerList.add(keyListener);
        }
    }

    public void removeKeyListener(KeyListener keyListener) {
        keyListenerList.remove(keyListener);
    }

    /**
     * 是否允许滑动返回
     *
     * @return bool
     */
    public Boolean enableSwipeToBack() {
        return false;
    }

    abstract public @LayoutRes
    int getLayoutId();
    // viewBinding初始化
    protected View viewBindingInit() {
        return null;
    }

    abstract public void initView();


    public interface KeyListener {
        boolean key(KeyEvent event);
    }

}
