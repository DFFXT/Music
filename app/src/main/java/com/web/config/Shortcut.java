package com.web.config;

import android.content.Context;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.web.data.InternetMusic;
import com.web.data.InternetMusicDetail;
import com.web.moudle.music.player.MusicPlay;

import java.io.File;

public class Shortcut {
    public static MusicPlay.Connect connect;
    public static boolean fileExsist(String path){
        if(TextUtils.isEmpty(path))return false;
        File file=new File(path);
        return (file.exists()&&file.isFile());
    }

    public static void fileDelete(String path){
        File file=new File(path);
        file.delete();
    }
    public static Message makeMsg(int what, Object obj){
        Message msg=Message.obtain();
        msg.what=what;
        msg.obj=obj;
        return msg;
    }

    public static String createPath(InternetMusicDetail music){
        return GetFiles.cachePath+music.getArtistName()+" - "+music.getSongName()+"."+music.getFormat();
    }
    public static String getMusicPath(String musicName,String singerName,String suffix){
        return GetFiles.cachePath+musicName+" - "+singerName+"."+suffix;
    }
    public static String getLyricsPath(String musicName,String signerName){
        return GetFiles.cachePath+"lyrics"+ File.separator+signerName+" - "+musicName+".lrc";
    }
    public static String getIconPath(String singerName){
        return GetFiles.cachePath+"singer"+ File.separator+singerName+".png";
    }

    public static void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void closeKeyBord(Context context,View v){//--close键盘
        InputMethodManager imm=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
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
    /**
     * //---从文件名称获取歌手和歌名
     * @param nameAndSinger name
     * @return name , singer
     * @deprecated use getName(@Size(min = 2) String[] out,@NonNull String nameAndSinger)
     */
    @Deprecated
    public static String[] getName(String nameAndSinger){
        int index=nameAndSinger.indexOf(" - ");
        String out[]=new String[2];

        if(index>0) {
            out[1]=nameAndSinger.substring(0,index).trim();
            out[0]=nameAndSinger.substring(index+3).trim();
        }else {
            out[1]="未知";
            out[0]=nameAndSinger;
        }
        return out;
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
