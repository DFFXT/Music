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

        public final static String currentSoundEffect="currentSoundEffect";

        public final static String currentVersion="currentVersion";
        public final static String latestVersion="latestVersion";

        public final static String cacheEnable="cacheEnable";
    }
    public static class LocalConfig{
        public static String rootPath= Environment.getExternalStorageDirectory().toString()+"/0/";
        public static String cachePath=rootPath+"cache/";
        public static String musicCachePath=cachePath+"cache/";
        public static String singerIconPath =cachePath+"singer/";
        public static String krcPath=cachePath+"lyrics/";
        private static String[] invalidChar=new String[]{
                "/","\\","?","|","<",">",":","*","\""
        };

        public static void initPath() {
            LocalConfig.createPathIfNotExist(cachePath);
            LocalConfig.createPathIfNotExist(musicCachePath);
            LocalConfig.createPathIfNotExist(singerIconPath);
            LocalConfig.createPathIfNotExist(krcPath);

        }
        private static void createPathIfNotExist(String path){
            File dir=new File(path);
            if(!dir.isDirectory()||!dir.exists()){
                dir.mkdirs();
            }
        }
        public static boolean validFileName(String name){
            for (String anInvalidChar : invalidChar) {
                if (name.contains(anInvalidChar)) return false;
            }
            return true;
        }
    }


}
