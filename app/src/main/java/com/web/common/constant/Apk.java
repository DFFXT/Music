package com.web.common.constant;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.view.View;

import com.web.app.MyApplication;
import com.web.common.bean.Version;
import com.web.common.tool.MToast;
import com.web.common.util.ResUtil;
import com.web.misc.ConfirmDialog;
import com.web.misc.LoadingWindow;
import com.web.moudle.service.UpdateService;
import com.web.moudle.setting.model.SettingViewModel;
import com.music.m.R;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * 一些和apk有关的信息和操作
 */
public class Apk {

    public final static String publishTime="2019-3-7";

    /**
     * 如果没有Version就去网络请求
     * 如果有就将存储的信息和apk进行比较，apk版本高则进行替换
     * 如果last的信息和apk信息相同则存储last信息
     *
     *
     * 进入时进行检测，如果是新安装，就会检测当前版本，如果不是最新版，会提示更新
     * 如果是不新安装，但是是老版本，不会提示更新
     * @param activity activity
     * @param v view
     */
    public static void init(Activity activity,View v){
        new Thread(()->{
            Version currentV=Version.readCurrentVersion();
            if(currentV==null){
                SettingViewModel.INSTANCE.requestVersion((res)->{
                    if (res == null) {
                        return null;
                    }
                    if(res.getVersionCode()>Apk.getVersionCode()){
                        AndroidSchedulers.mainThread().scheduleDirect(()-> considerUpdate(activity,v,res));
                    }
                    res.saveAsCurrent();
                    return null;
                },null);
            }else{
                Version realV=new Version(Apk.getVersionCode(),Apk.getVersionName(),publishTime,"","");
                if(currentV.getVersionCode()<realV.getVersionCode()){
                    realV.saveAsCurrent();
                }
                Version lastV=Version.readLatestVersion();
                if(lastV!=null){
                    if(lastV.getVersionCode()==Apk.getVersionCode()){
                        lastV.saveAsCurrent();
                    }
                }
            }
        }).start();
    }

    public static String getVersionName(){
        String versionName="";
        try {
            versionName= MyApplication.context.getPackageManager().getPackageInfo(MyApplication.context.getPackageName(), PackageManager.GET_GIDS).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }
    public static int getVersionCode(){
        int versionCode=0;
        try {
            versionCode=MyApplication.context.getPackageManager().getPackageInfo(MyApplication.context.getPackageName(), PackageManager.GET_GIDS).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static void checkUpdate(Activity activity, View v){
        {
            LoadingWindow loadingWindow=new LoadingWindow(activity);
            loadingWindow.showCenter(v);
            loadingWindow.enableTouchDismiss(false);
            loadingWindow.setDismissCallback(()->{
                SettingViewModel.INSTANCE.cancelRequest();
                return null;
            });
            SettingViewModel.INSTANCE.requestVersion((res) -> {
                loadingWindow.dismiss();
                considerUpdate(activity,v,res);
                return null;
            }, null);
        }
    }
    private static void considerUpdate(Activity activity, View v, Version version) {
        version.saveAsLast();
        if (version.getVersionCode() > Apk.getVersionCode()) {
            ConfirmDialog dialog = new ConfirmDialog(activity);
            dialog.setMsg(version.getDesc());
            dialog.setLeftText(ResUtil.getString(R.string.no));
            dialog.setRightText(ResUtil.getString(R.string.yes));
            dialog.setRightListener((d)->{
                UpdateService.downloadUpdateApk(activity,version);
                d.dismiss();
                return null;
            });
            dialog.setLeftListener((d)->{
                d.dismiss();
                return null;
            });
            dialog.showCenter(v);
        } else {
            MToast.showToast(activity, R.string.setting_lastVersion);
        }
    }
}
