package com.web.common.base;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import com.web.common.util.UncaughtException;

public abstract class BaseActivity extends AppCompatActivity {
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        Thread.setDefaultUncaughtExceptionHandler(UncaughtException.INSTANCE);

        //**设置字体大小
        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = .85f;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        setContentView(getLayoutId());
        loadData(b);
        initView();
    }
    abstract public @LayoutRes int getLayoutId();
    abstract public void initView();
    protected void loadData(Bundle bundle){ }
}
