package com.web.util;

import android.graphics.Paint;
import android.graphics.Rect;

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
}
