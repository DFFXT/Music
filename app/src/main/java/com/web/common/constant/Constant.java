package com.web.common.constant;

import android.os.Environment;

import java.io.File;

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
        public static String cachePath=rootPath+"cache/";
        public static String singerPath=cachePath+"singer/";
        public static String krcPath=cachePath+"lyrics/";

        public static void initPath() {
            LocalConfig.createPathIfNotExist(cachePath);
            LocalConfig.createPathIfNotExist(singerPath);
            LocalConfig.createPathIfNotExist(krcPath);

        }
        private static void createPathIfNotExist(String path){
            File dir=new File(path);
            if(!dir.isDirectory()||!dir.exists()){
                dir.mkdirs();
            }
        }
    }


}
