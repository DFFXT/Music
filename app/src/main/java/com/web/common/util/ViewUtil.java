package com.web.common.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.web.common.base.MyApplication;

import org.litepal.LitePalApplication;

public class ViewUtil {
    private static int width=-1,height=-1;
    public static int dpToPx(float dp){
        return (int)(MyApplication.getContext().
                getResources().getDisplayMetrics().density*dp+0.5);
    }
    public static int screenWidth(){
        if(width<0)
            width=LitePalApplication.getContext().getResources().getDisplayMetrics().widthPixels;
        return width;
    }
    public static int screenHeight(){
        if(height<0){
            height=LitePalApplication.getContext().getResources().getDisplayMetrics().heightPixels;
        }
        return height;
    }
    public static void stringSize(String str, float size, Rect rect){
        Paint paint=new Paint();
        paint.setTextSize(size);
        paint.getTextBounds(str,0,str.length(),rect);
    }
    public static Drawable getDrawable(@DrawableRes int id){
        return MyApplication.getContext().getResources().getDrawable(id,MyApplication.getContext().getTheme());
    }
    public static void transparentStatusBar(Window window){
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    public static void setWidth(View v,int width){
        ViewGroup.LayoutParams lp=v.getLayoutParams();
        if(lp==null){
            lp=new ViewGroup.LayoutParams(width,ViewGroup.LayoutParams.WRAP_CONTENT);
        }else{
            lp.width=width;
        }
        v.setLayoutParams(lp);
    }
    public static void setHeight(View v,int height){
        ViewGroup.LayoutParams lp=v.getLayoutParams();
        if(lp==null){
            lp=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,height);
        }else{
            lp.height=height;
        }
        v.setLayoutParams(lp);
    }

    public static Bitmap getViewBitmap(View view){
        /*Bitmap bitmap=Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas=new Canvas(bitmap);
        view.draw(canvas);*/
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap=view.getDrawingCache();
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }


}
