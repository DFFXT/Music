package com.web.config;

import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.web.data.InternetMusic;
import com.web.data.Music;
import com.web.service.MusicPlay;

import java.io.File;

public class Shortcut {
    public static MusicPlay.Connect connect;
    public static boolean fileExsist(String path){
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

    public static String createPath(InternetMusic music){
        return GetFiles.cachePath+music.getSingerName()+" - "+music.getMusicName()+".mp3";
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

    /**
     * //---从文件名称获取歌手和歌名
     * @param nameAndSinger name
     * @return name , singer
     */
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

}
