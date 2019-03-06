package com.web.moudle.setting.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.web.common.base.BaseActivity;
import com.web.common.bean.Version;
import com.web.common.constant.Apk;
import com.web.common.constant.Constant;
import com.web.common.tool.MToast;
import com.web.common.util.IOUtil;
import com.web.common.util.ResUtil;
import com.web.common.util.ViewUtil;
import com.web.misc.ConfirmDialog;
import com.web.misc.LoadingWindow;
import com.web.misc.TextWithDrawable;
import com.web.moudle.music.player.MusicPlay;
import com.web.moudle.service.UpdateService;
import com.web.moudle.setting.about.AboutActivity;
import com.web.moudle.setting.lockscreen.LockScreenSettingActivity;
import com.web.moudle.setting.model.SettingViewModel;
import com.web.moudle.setting.suffix.SuffixSelectActivity;
import com.web.web.R;

@SuppressLint("InlinedApi")
public class SettingActivity extends BaseActivity {
    private View rootView;

    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        rootView = findViewById(R.id.rootView);
        ViewUtil.transparentStatusBar(getWindow());
        TextWithDrawable twd_lockScreen = findViewById(R.id.twd_lockScreen);
        TextWithDrawable twd_musicScan = findViewById(R.id.twd_musicScan);
        TextWithDrawable twd_checkUpdate = findViewById(R.id.twd_checkUpdate);
        twd_lockScreen.setOnClickListener(v -> LockScreenSettingActivity.Companion.actionStart(this));
        twd_musicScan.setOnClickListener(v -> SuffixSelectActivity.Companion.actionStart(SettingActivity.this));

        twd_checkUpdate.setText(ResUtil.getString(R.string.setting_checkUpdate, Apk.getVersionName()));
        twd_checkUpdate.setOnClickListener(v ->Apk.checkUpdate(this,v));


        findViewById(R.id.twd_clearAllMusic).setOnClickListener(v -> {
            new ConfirmDialog(this)
                    .setLeftText(ResUtil.getString(R.string.no))
                    .setRightText(ResUtil.getString(R.string.yes))
                    .setMsg(ResUtil.getString(R.string.setting_clearAllMusicAlert))
                    .setRightListener((dialog) -> {
                        Intent intent = new Intent(this, MusicPlay.class);
                        intent.setAction(MusicPlay.ACTION_ClEAR_ALL_MUSIC);
                        startService(intent);
                        dialog.dismiss();
                        return null;
                    })
                    .setLeftListener((dialog) -> {
                        dialog.dismiss();
                        return null;
                    })
                    .showCenter(rootView);
        });

        findViewById(R.id.twd_about).setOnClickListener(v-> AboutActivity.actionStart(this));

    }



    public static void actionStart(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }
}
