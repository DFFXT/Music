package com.web.common.constant;

import android.os.Environment;

import com.web.config.Shortcut;
import com.web.moudle.setting.cache.CacheActivity;

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
        public final static String customerCachePath="customerCachePath";
        public final static String customerDownloadPath="customerDownloadPath";

        public final static String lyricsColor="lyricsColor";
        public final static String lyricsFocusColor="lyricsFocusColor";
        public final static String lyricsSize="lyricsSize";
        public final static String lyricsOverlapOpen="lyricsOverlapOpen";


        public final static String floatWindowX="floatWindowX";
        public final static String floatWindowY="floatWindowY";


    }
    public static class LocalConfig{

        //**全局字体缩放比例
        public static float fontScale=0.85f;

        public static String rootPath= Environment.getExternalStorageDirectory().toString()+"/0/";
        public static String cachePath=rootPath+"cache/";
        public static String musicDownloadPath=cachePath+"download/";
        public static String musicCachePath=cachePath+"cache/";
        public static String singerIconPath =cachePath+"singer/";
        public static String krcPath=cachePath+"lyrics/";
        private static String[] invalidChar=new String[]{
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
