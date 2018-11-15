package com.web.moudle.setting.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.WorkerThread;
import android.support.v4.content.LocalBroadcastManager;

import com.web.common.base.BaseActivity;
import com.web.common.util.ResUtil;
import com.web.common.util.ViewUtil;
import com.web.misc.ColorPicker;
import com.web.moudle.music.player.MusicPlay;
import com.web.moudle.setting.lockscreen.LockScreenSettingActivity;
import com.web.moudle.setting.suffix.SuffixSelectActivity;
import com.web.web.R;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

@SuppressLint("InlinedApi")
public class SettingActivity extends BaseActivity {
	public int getLayoutId(){
		return (R.layout.activity_setting);

	}
	@Override
	public void initView() {
		ViewUtil.transparentStatusBar(getWindow());
		findViewById(R.id.twd_lockScreen).setOnClickListener(v->LockScreenSettingActivity.Companion.actionStart(this));
		findViewById(R.id.twd_musicScan).setOnClickListener(v -> SuffixSelectActivity.Companion.actionStart(SettingActivity.this));
		findViewById(R.id.twd_clearAllMusic).setOnClickListener(v->{
			new AlertDialog.Builder(this)
					.setMessage(ResUtil.getString(R.string.setting_clearAllMusicAlert))
					.setNegativeButton(ResUtil.getString(R.string.no), null)
					.setPositiveButton(ResUtil.getString(R.string.yes), (dialog, which) -> {
						Intent intent=new Intent(this,MusicPlay.class);
						intent.setAction(MusicPlay.ACTION_ClEAR_ALL_MUSIC);
						startService(intent);
						dialog.cancel();
					}).create().show();

		});
	}

	public static void actionStart(Context context){
		context.startActivity(new Intent(context,SettingActivity.class));
	}


	
}
