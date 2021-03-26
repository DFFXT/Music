package com.web.config;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.web.common.constant.Constant;
import com.web.data.InternetMusicDetail;
import com.web.moudle.setting.cache.CacheActivity;

import java.io.File;

public class Shortcut {
    public static boolean fileExsist(String path){
        if(TextUtils.isEmpty(path))return false;
        File file=new File(path);
        return (file.exists()&&file.isFile());
    }
    public static boolean dirExist(String dir){
        File file=new File(dir);
        return file.isDirectory();
    }

    public static void fileDelete(String path){
        File file=new File(path);
        file.delete();
    }


    public static String createPath(InternetMusicDetail music){
        return createPath(music.getSongName(),music.getArtistName(),music.getFormat());
    }
    public static String createPath(String musicName,String singerName,String suffix){
        return CacheActivity.getCustomerDownloadPath() +singerName+" - "+musicName+"."+suffix;
    }
    public static String getLyricsPath(String musicName,String signerName){
        return Constant.LocalConfig.krcPath+signerName+" - "+musicName+".lrc";
    }
    public static String getIconPath(String singerName){
        return Constant.LocalConfig.singerIconPath+singerName+".png";
    }

    public static void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void getName(@Size(min = 2) String[] out,@NonNull String nameAndSinger){
        int index=nameAndSinger.indexOf(" - ");
        if(index>0) {
            out[1]=nameAndSinger.substring(0,index).trim();
            out[0]=nameAndSinger.substring(index+3).trim();
        }else {
            out[1]="未知";
            out[0]=nameAndSinger;
        }
    }

    public static String validatePath(String name){
        return name.replace('/',',')
                .replace('&',' ')
                .replace(':',' ')
                .replace('<',' ')
                .replace('>',' ')
                .replace('*',' ')
                .replace('"',' ')
                .replace('\\',' ')
                .replace('|',' ')
                .replace('?',' ');
    }

    /**
     * 判断字符串是不是严格空，连续空格为空
     * @param string str
     * @return isEmpty
     */
    public static boolean isStrictEmpty(String string){
        if(string==null)return true;
        for(int i=0;i<string.length();i++){
            if(string.charAt(i)!=' ')return false;
        }
        return true;
    }

}
