package com.web.common.constant;

import android.os.Environment;

import com.web.config.Shortcut;
import com.web.moudle.setting.cache.CacheActivity;

import java.io.File;

public final class Constant {
    public final static String spName="appConfig";
    @Deprecated
    public static class SpKey{
        public final static String currentVersion="currentVersion";
        public final static String latestVersion="latestVersion";
    }
    public static class LocalConfig{

        //**全局字体缩放比例
        public static float fontScale=0.85f;
        // 默认机身储存
        public static String rootPath= "/storage/emulated/0/0/";
        public static String cachePath=rootPath+"cache/";
        public static String musicDownloadPath=cachePath+"download/";
        public static String musicCachePath=cachePath+"cache/";
        public static String singerIconPath =cachePath+"singer/";
        public static String krcPath=cachePath+"lyrics/";
        private static final String[] invalidChar=new String[]{
                "/","\\","?","|","<",">",":","*","\""
        };

        public static void initPath() {
            LocalConfig.createPathIfNotExist(cachePath);
            if(Shortcut.dirExist(CacheActivity.getCustomerCachePath())){
                musicCachePath=CacheActivity.getCustomerCachePath();
            }
            if(Shortcut.dirExist(CacheActivity.getCustomerDownloadPath())){
                musicDownloadPath=CacheActivity.getCustomerDownloadPath();
            }
            LocalConfig.createPathIfNotExist(musicDownloadPath);
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
        public static boolean isValidFileName(String name){
            for (String anInvalidChar : invalidChar) {
                if (name.contains(anInvalidChar)) return true;
            }
            return false;
        }
    }


}
