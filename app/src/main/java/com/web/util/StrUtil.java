package com.web.util;

import android.support.annotation.StringRes;

import org.litepal.LitePalApplication;

public class StrUtil {
    public static String getString(@StringRes int id){
        return LitePalApplication.getContext().getResources().getString(id);
    }
}
