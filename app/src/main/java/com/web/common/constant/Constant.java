package com.web.common.constant;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.web.common.base.MyApplication;

public final class Constant {
    public final static String spName="appConfig";
    public class SpKey{
        public final static String clearAll="clearAll";
        public final static String noNeedScan="noNeedScan";
        public final static String lockScreenBgColor="lockScreenBgColor";
        public final static String lockScreenBgImagePath="lockScreenBgImagePath";
        public final static String lockScreenBgMode="lockScreenBgMode";
        public final static String noLockScreen="noLockScreen";
    }
    public static class LocalConfig{
        public static String rootPath= Environment.getExternalStorageDirectory().toString()+"/0/";
        public static String appPath=rootPath+"0/app/";
        public static String cachePath=rootPath+"cache/";
        public static String singerPath=cachePath+"singer/";
        public static String krcPath=cachePath+"lyrics/";
    }

    public static String getVersionName(){
        String versionName="";
        try {
            versionName=MyApplication.context.getPackageManager().getPackageInfo(MyApplication.context.getPackageName(), PackageManager.GET_GIDS).versionName;
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
}
