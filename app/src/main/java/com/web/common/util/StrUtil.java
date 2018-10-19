package com.web.common.util;

import android.support.annotation.StringRes;

import org.litepal.LitePalApplication;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StrUtil {
    public static String getString(@StringRes int id){
        return LitePalApplication.getContext().getResources().getString(id);
    }
    public static String timeFormat(String pattern,long time){
        DateFormat format=new SimpleDateFormat(pattern, Locale.CHINA);
        return format.format(new Date(time));
    }
}
