package com.web.common.constant;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.view.View;

import com.web.common.base.MyApplication;
import com.web.common.bean.Version;
import com.web.common.tool.MToast;
import com.web.common.util.ResUtil;
import com.web.misc.ConfirmDialog;
import com.web.misc.LoadingWindow;
import com.web.moudle.service.UpdateService;
import com.web.moudle.setting.model.SettingViewModel;
import com.web.web.R;

public final class Constant {
    public final static String spName="appConfig";
    public class SpKey{
        public final static String clearAll="clearAll";
        public final static String noNeedScan="noNeedScan";
        public final static String lockScreenBgColor="lockScreenBgColor";
        public final static String lockScreenBgImagePath="lockScreenBgImagePath";
        public final static String lockScreenBgMode="lockScreenBgMode";
        public final static String noLockScreen="noLockScreen";

        public final static String currentVersion="currentVersion";
        public final static String latestVersion="latestVersion";
    }
    public static class LocalConfig{
        public static String rootPath= Environment.getExternalStorageDirectory().toString()+"/0/";
        public static String appPath=rootPath+"0/app/";
        public static String cachePath=rootPath+"cache/";
        public static String singerPath=cachePath+"singer/";
        public static String krcPath=cachePath+"lyrics/";
    }


}
