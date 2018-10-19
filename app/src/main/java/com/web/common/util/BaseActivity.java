package com.web.common.util;


import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        //**设置字体大小
        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = .75f;
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        setContentView(getLayoutId());
        initView();
    }
    abstract public @LayoutRes int getLayoutId();
    abstract public void initView();
}
