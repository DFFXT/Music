package com.web.moudle.setting.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.web.common.base.BaseActivity;
import com.web.common.constant.Apk;
import com.web.common.util.ResUtil;
import com.web.common.util.ViewUtil;
import com.web.misc.ConfirmDialog;
import com.web.misc.TextWithDrawable;
import com.web.moudle.setting.about.AboutActivity;
import com.web.moudle.setting.cache.CacheActivity;
import com.web.moudle.setting.lockscreen.LockScreenSettingActivity;
import com.web.moudle.setting.lyrics.LyricsSettingActivity;
import com.web.moudle.setting.suffix.SuffixSelectActivity;
import com.web.moudle.user.UserManager;
import com.music.m.R;

@SuppressLint("InlinedApi")
public class SettingActivity extends BaseActivity {

    private View logout;
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        ViewUtil.transparentStatusBar(getWindow());
        TextWithDrawable twd_lockScreen = findViewById(R.id.twd_lockScreen);
        TextWithDrawable twd_musicScan = findViewById(R.id.twd_musicScan);
        TextWithDrawable twd_lyrics = findViewById(R.id.twd_lyrics);
        TextWithDrawable twd_checkUpdate = findViewById(R.id.twd_checkUpdate);
        logout = findViewById(R.id.twd_logout);
        twd_lockScreen.setOnClickListener(v -> LockScreenSettingActivity.Companion.actionStart(this));
        twd_musicScan.setOnClickListener(v -> SuffixSelectActivity.Companion.actionStart(SettingActivity.this));
        twd_lyrics.setOnClickListener(v -> LyricsSettingActivity.actionStart(this));
        twd_checkUpdate.setText(ResUtil.getString(R.string.setting_checkUpdate, Apk.getVersionName()));
        twd_checkUpdate.setOnClickListener(v -> Apk.checkUpdate(this, v));

        findViewById(R.id.twd_cacheSetting).setOnClickListener(v -> CacheActivity.actionStart(this));


        findViewById(R.id.twd_about).setOnClickListener(v -> AboutActivity.actionStart(this));

        logout.setOnClickListener(v->{
            logout();
        });

        if(UserManager.isLogin()){
            logout.setVisibility(View.VISIBLE);
        }else{
            logout.setVisibility(View.GONE);
        }

    }

    private void logout(){
        new ConfirmDialog(this)
                .setMsg(ResUtil.getString(R.string.logout))
                .setLeftText(ResUtil.getString(R.string.no))
                .setRightText(ResUtil.getString(R.string.yes))
                .setLeftListener(dialog->{
                    dialog.dismiss();
                    return null;
                })
                .setRightListener(dialog->{
                    UserManager.setLogin(false);
                    logout.setVisibility(View.GONE);
                    dialog.dismiss();
                    return null;
                })
                .show(logout);

    }

    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }
}
