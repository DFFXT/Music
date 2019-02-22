package com.web.common.base;


import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    public static String INTENT_DATA="_mData";
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
    abstract public @LayoutRes int getLayoutId();
    abstract public void initView();

}
