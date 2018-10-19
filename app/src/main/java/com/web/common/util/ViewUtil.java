package com.web.common.util;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;

import com.web.common.base.MyApplication;

import org.litepal.LitePalApplication;

public class ViewUtil {
    public static int dpToPx(float dp){
        return (int)(LitePalApplication.getContext().
                getResources().getDisplayMetrics().density*dp+0.5);
    }
    public static int screenWidth(){
        return LitePalApplication.getContext().getResources().getDisplayMetrics().widthPixels;
    }
    public static void stringSize(String str, float size, Rect rect){
        Paint paint=new Paint();
        paint.setTextSize(size);
        paint.getTextBounds(str,0,str.length(),rect);
    }
    public static Drawable getDrawable(@DrawableRes int id){
        return MyApplication.getContext().getResources().getDrawable(id,MyApplication.getContext().getTheme());
    }
}
