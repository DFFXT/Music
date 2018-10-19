package com.web.music.model.lyrics.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

import com.web.music.model.lyrics.model.LyricsLine;
import com.web.common.util.ViewUtil;

import java.util.List;


public class LyricsView extends RelativeLayout{

    private List<LyricsLine> lyrics;
    //***要显示的歌词行数
    private int showLineAccount;
    private int maxLineAccount;
    private int lineHeight= ViewUtil.dpToPx(20);
    //**歌词行距
    private int lineGap=ViewUtil.dpToPx(8);
    //***当前播放的行数
    private int index;
    //***当前行歌词的宽高
    private Rect rect=new Rect();
    //***控件的宽高
    private int width,height;
    //**垂直滑动动画偏移量
    private int animationOffset;

    private Paint paint;
    private float textSize;
    //**歌词颜色
    private @ColorInt int textColor=Color.BLACK;
    //**当前歌词颜色
    private @ColorInt int textFocusColor=Color.BLUE;

    private int startIndex,endIndex;
    //**状态
    private boolean run=false;
    private boolean drag=true;
    private Thread thread;
    private SeekListener seekListener;
    private int time;
    private long t;

    private PorterDuffXfermode mode_cover=new PorterDuffXfermode(PorterDuff.Mode.SCREEN);
    private PorterDuffXfermode mode_font=new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
    public LyricsView(Context context) {
        super(context);
        initView();
    }

    public LyricsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        t=System.currentTimeMillis();
    }

    public LyricsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView(){
        maxLineAccount=11;
        textSize=ViewUtil.dpToPx(18);
        paint=new Paint();

        paint.setAntiAlias(true);
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                width=getWidth();
                height=getHeight();
                showLineAccount=height/(lineHeight+lineGap);
                getViewTreeObserver().removeOnPreDrawListener(this);
                return false;
            }
        });
        initData();
    }
    private void initData(){
        index=0;
        nextIndex=0;
        animationOffset=0;
        if(lyrics!=null){
            if(!run){
                start();
            }
            halfShowHeight=(showLineAccount-1)/2*(lineHeight+lineGap);
            updateRange();
            paint.setTextSize(textSize);
            for(int i=0;i<lyrics.size();i++){
                LyricsLine line=lyrics.get(i);
                paint.getTextBounds(line.getLine(),0,line.getLine().length(),rect);
                line.setWidth(rect.width());
                line.setHeight(rect.height());
                lineHeight=Math.max(rect.height(),lineHeight);
            }
        }
        else{
            run=false;
            startIndex=0;
            endIndex=0;
        }

    }


    private int halfShowHeight=0;

    /**
     * 此处重写dispatchDraw（onDraw、draw方法要设置background才会执行）
     * @param canvas canvas
     */
    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int centerH=height/2;
        canvas.clipRect(0,centerH-halfShowHeight,width,centerH+halfShowHeight);
        paint.setXfermode(mode_font);
        paint.setColor(textColor);
        for(int i=startIndex;i<endIndex;i++){
            LyricsLine line=lyrics.get(i);
            int dec=this.index-i;
            int bottom= (int) (centerH-lineHeight*(-0.5+dec)-lineGap*dec)+animationOffset;
            float linear=1-Math.abs(bottom-lineHeight/2f-centerH)/centerH;
            paint.setTextSize(textSize*linear);
            paint.getTextBounds(line.getLine(),0,line.getLine().length(),rect);

            int left=(width-rect.width())/2;
            paint.setAlpha((int) (255*linear));
            canvas.drawText(lyrics.get(i).getLine(),left,bottom,paint);
        }
        paint.setColor(textFocusColor);
        paint.setXfermode(mode_cover);
        canvas.drawRect(0,centerH-lineHeight/2,width,centerH+lineHeight/2+2,paint);
    }
    private int nextIndex=0;
    public void start(){
        if(run)return;
        run=true;
        thread=new Thread(() -> {
            while (run&&lyrics!=null&&lyrics.size()>1){
                try {
                    if(nextIndex!=index){
                        animationOffset-=1;
                        if(animationOffset<=-lineHeight-lineGap){
                            index=nextIndex;
                            updateRange();
                            animationOffset=0;
                        }
                        postInvalidate();
                    }
                    Thread.sleep(10);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            run=false;
        });
        thread.start();

    }


    public void setCurrentTime(int time){
        this.time=time;
        if(lyrics==null)return;
        if(lyrics.size()>0&&time>=lyrics.get(lyrics.size()-1).getTime()){//**直接拖到底
            setNextIndex(lyrics.size()-1);
            return;
        }
        for(int i=0;i<lyrics.size();i++){
            int lt=lyrics.get(i).getTime();
            if(Math.abs(lt-time)<10){//**正常情况
                setNextIndex(i);
                break;
            }
            else if(lt>time){//**拖动
                if(i==0)setNextIndex(0);
                else setNextIndex(i-1);
                break;
            }
        }


    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        run=false;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        run=false;
        return super.onSaveInstanceState();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if(hasWindowFocus){
            start();
        }
    }




    public List<LyricsLine> getLyrics() {
        return lyrics;
    }

    public void setLyrics(List<LyricsLine> lyrics) {
        if(lyrics!=null&&lyrics.equals(this.lyrics)){
            return;
        }
        this.lyrics = lyrics;
        refresh();
    }

    /**
     * 重置歌词，切换歌曲时调用
     */
    public void refresh(){
        initData();
        postInvalidate();
    }




    private float preY;
    private boolean startScroll;
    private float originY;
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if(!drag||lyrics.size()<=1)return false;
        switch (e.getAction()){
            case MotionEvent.ACTION_DOWN:{
                originY=preY=e.getRawY();
                startScroll=false;
            }break;
            case MotionEvent.ACTION_MOVE:{
                if(!startScroll&&Math.abs(originY-e.getRawY())>Math.max(height/32,lineHeight/2)){
                    startScroll=true;
                }
                if(startScroll){
                    run=false;
                    //**到顶和到底
                    if(index==0&&preY-e.getRawY()<0||
                            index==lyrics.size()-1&&preY-e.getRawY()>0){
                        preY=e.getRawY();
                        break;
                    }
                    animationOffset-=preY-e.getRawY();
                    if(Math.abs(animationOffset)>=lineHeight+lineGap){
                        index-=Math.abs(animationOffset)/animationOffset;
                        updateRange();
                        animationOffset=0;
                    }
                    invalidate();
                }
                preY=e.getRawY();

            }break;
            case MotionEvent.ACTION_UP:{
                if (!startScroll||thread==null||seekListener==null)break;
                startScroll=false;
                if(Math.abs(lyrics.get(index).getTime()-time)>10){
                    if(Math.abs(animationOffset)<(lineHeight+lineGap)/2){
                        recover(animationOffset,0,0);
                    }else{
                        if(animationOffset>0) {
                            recover(animationOffset, lineHeight + lineGap,-1);
                        }
                        else if(animationOffset<0) {
                            recover(animationOffset, -lineHeight - lineGap,1);
                        }
                        else{
                            recover(animationOffset,0,0);
                        }
                    }
                }else{
                    recover(animationOffset,0,0);
                }
            }break;
        }
        return true;
    }
    private void updateRange(){
        index=Math.min(index,lyrics.size()-1);
        if(index<0)index=0;
        startIndex=Math.max(0,index-(showLineAccount-1)/2);
        endIndex=Math.min(index+(showLineAccount-1)/2,lyrics.size());
    }
    private void recover(int from,int to,int indexAdd){
        ValueAnimator animator=ValueAnimator.ofInt(from,to);
        animator.setDuration(300);
        animator.addUpdateListener(animation -> {
            animationOffset=(int)animation.getAnimatedValue();
            if(animationOffset==to){
                animationOffset=0;
                start();
                index+=indexAdd;
                updateRange();
                time=lyrics.get(index).getTime();
                setCurrentTime(time);
                seekListener.seekTo(time);

            }
            invalidate();
        });
        animator.start();
    }

    public void setDragEnable(boolean enable){
        drag=enable;
    }

    public int getShowLineAccount() {
        return showLineAccount;
    }

    public void setShowLineAccount(int showLineAccount) {
        this.showLineAccount = showLineAccount;
    }

    public int getMaxLineAccount() {
        return maxLineAccount;
    }

    public void setMaxLineAccount(int maxLineAccount) {
        this.maxLineAccount = maxLineAccount;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    public int getLineGap() {
        return lineGap;
    }

    public void setLineGap(int lineGap) {
        this.lineGap = lineGap;
    }

    public int getIndex() {
        return index;
    }

    public void setNextIndex(int nextIndex) {
        this.nextIndex = nextIndex;
    }

    public int getAnimationOffset() {
        return animationOffset;
    }

    public void setAnimationOffset(int animationOffset) {
        this.animationOffset = animationOffset;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextFocusColor() {
        return textFocusColor;
    }

    public void setTextFocusColor(int textFocusColor) {
        this.textFocusColor = textFocusColor;
    }

    public void setSeekListener(SeekListener seekListener) {
        this.seekListener = seekListener;
    }


    public interface SeekListener{
        boolean seekTo(int seekTo);
    }

}

















