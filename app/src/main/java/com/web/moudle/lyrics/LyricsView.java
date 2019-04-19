package com.web.moudle.lyrics;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.web.common.util.ResUtil;
import com.web.common.util.ViewUtil;
import com.web.moudle.lyrics.bean.LyricsLine;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;


public class LyricsView extends RelativeLayout {

    private List<LyricsLine> lyrics = new ArrayList<>();
    //***要显示的歌词行数
    private int showLineAccount;
    private int maxLineAccount;
    private int lineHeight = ViewUtil.dpToPx(20);
    //**歌词行距
    private int lineGap = ViewUtil.dpToPx(8);
    //***当前播放的行数
    private int index;
    //***当前行歌词的宽高
    private Rect rect = new Rect();
    //***控件的宽高
    private int width, height;
    //**垂直滑动动画偏移量
    private int animationOffset;
    //**文字是否缩放
    private boolean enableFontScale = false;

    private Paint paint;
    private float textSize;
    //**歌词颜色
    private @ColorInt
    int textColor = Color.BLACK;
    //**当前歌词颜色
    private @ColorInt
    int textFocusColor = 0xffff00ee;

    private int clipPaddingTop,clipPaddingBottom;

    private int startIndex, endIndex;
    //**状态
    private boolean run = false;
    private boolean canScroll = true;
    private Thread thread;
    private SeekListener seekListener;
    private int time;

    private PorterDuffXfermode mode_cover = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);
    private PorterDuffXfermode mode_font = new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);

    public LyricsView(Context context) {
        super(context);
        initView();
    }

    public LyricsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LyricsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        maxLineAccount = 11;
        textSize = ViewUtil.dpToPx(18);
        paint = new Paint();

        paint.setAntiAlias(true);
        initData();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        setMaxLineAccount((height) / (lineHeight + lineGap));
        setTextSize(textSize);
    }

    private void initBitmap() {
        if (width <= 0 || height <= 0) return;
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        mCanvas = new Canvas(mBitmap);
        mCanvas.clipRect(0, height / 2f - halfShowHeight+clipPaddingTop, width, height / 2f + halfShowHeight-clipPaddingBottom);
    }

    private void initData() {
        index = 0;
        nextIndex = 0;
        animationOffset = 0;
        if (lyrics != null) {
            if (!run) {
                start();
            }
            updateRange();
            setTextSize(textSize);
            paint.setTextSize(textSize);
        } else {
            run = false;
            startIndex = 0;
            endIndex = 0;
        }

    }


    private int halfShowHeight = 0;
    @Nullable
    private Canvas mCanvas;
    @Nullable
    private Bitmap mBitmap;

    /**
     * 此处重写dispatchDraw（onDraw、draw方法要设置background才会执行）
     *
     * @param canvas canvas
     */
    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int centerH = height / 2;
        if (mCanvas != null)
            mCanvas.drawColor(textColor, PorterDuff.Mode.CLEAR);
        paint.setXfermode(mode_font);
        paint.setColor(textColor);
        if (!enableFontScale) {
            paint.setTextSize(textSize);
        }
        for (int i = startIndex; i < endIndex; i++) {
            LyricsLine line = lyrics.get(i);
            int dec = this.index - i;
            int bottom = (int) (centerH - lineHeight * (-0.5 + dec) - lineGap * dec) + animationOffset;
            float linear = 1 - Math.abs(bottom - lineHeight / 2f - centerH) / centerH;
            linear=Math.max(linear,0);
            if (enableFontScale)
                paint.setTextSize(textSize * linear);
            paint.getTextBounds(line.getLine(), 0, line.getLine().length(), rect);

            int left = (width - rect.width()) / 2;
            paint.setAlpha((int) (255 * linear));
            if (mCanvas != null)
                mCanvas.drawText(lyrics.get(i).getLine(), left, bottom, paint);
        }
        paint.setColor(textFocusColor);
        paint.setXfermode(mode_cover);
        if (mCanvas != null)
            mCanvas.drawRect(0, centerH - lineHeight / 2f + 10, width, centerH + lineHeight / 2 + 10, paint);
        paint.setXfermode(mode_font);
        if (mBitmap != null)
            canvas.drawBitmap(mBitmap, 0, 0, paint);
    }

    private int nextIndex = 0;

    public void start() {
        if (run) return;
        run = true;
        thread = new Thread(() -> {
            while (run && lyrics.size() > 1) {
                try {
                    if (nextIndex != index) {
                        animationOffset -= 1;
                        if (animationOffset <= -lineHeight - lineGap) {
                            index = nextIndex;
                            updateRange();
                            animationOffset = 0;
                        }
                        postInvalidate();
                    }
                    Thread.sleep(10);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            run = false;
        });
        thread.start();

    }

    /**
     * 立即显示到当前歌词，一般在滑动了进度、界面重新显示时调用
     *
     * @param time time
     */
    public void setCurrentTimeImmediately(int time) {
        setCurrentTime(time);
        index = nextIndex;
        animationOffset = 0;
        updateRange();
        postInvalidate();
    }

    /**
     * 设置当前进度
     *
     * @param currentTime mills
     */
    public void setCurrentTime(int currentTime) {
        this.time = currentTime;
        if (lyrics == null) return;
        if (lyrics.size() > 0 && time >= lyrics.get(lyrics.size() - 1).getTime()) {//**直接拖到底
            setNextIndex(lyrics.size() - 1);
            return;
        }
        for (int i = 0; i < lyrics.size(); i++) {
            int lt = lyrics.get(i).getTime();
            if (Math.abs(lt - time) < 100) {//**正常情况
                setNextIndex(i);
                break;
            } else if (lt > time) {//**拖动
                if (i == 0) setNextIndex(0);
                else setNextIndex(i - 1);
                break;
            }
        }


    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        run = false;
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        run = false;
        return super.onSaveInstanceState();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            start();
        }
    }


    public List<LyricsLine> getLyrics() {
        return lyrics;
    }

    public void setLyrics(List<LyricsLine> lyrics) {
        if (lyrics == null) return;
        this.lyrics.clear();
        this.lyrics.addAll(lyrics);
        requestLayout();
        refresh();
    }

    /**
     * 重置歌词，切换歌曲时调用
     */
    public void refresh() {
        initData();
        postInvalidate();
    }

    private float preY;
    private boolean startScroll;
    private float originY;
    public boolean ableToTouch=true;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (!canScroll || lyrics.size() <= 1) return false;
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                originY = preY = e.getRawY();
                startScroll = false;
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                if (!startScroll && Math.abs(originY - e.getRawY()) > Math.max(height / 32, lineHeight / 2)) {
                    startScroll = true;
                }
                if (startScroll) {
                    run = false;
                    //**到顶和到底
                    if (index == 0 && preY - e.getRawY() < 0 ||
                            index == lyrics.size() - 1 && preY - e.getRawY() > 0) {
                        preY = e.getRawY();
                        break;
                    }
                    animationOffset -= preY - e.getRawY();
                    if (Math.abs(animationOffset) >= lineHeight + lineGap) {
                        index -= Math.abs(animationOffset) / animationOffset;
                        updateRange();
                        animationOffset = 0;
                    }
                    invalidate();
                }
                preY = e.getRawY();

            }
            break;
            case MotionEvent.ACTION_UP: {
                if (!startScroll || thread == null || seekListener == null) {
                    start();
                    break;
                }
                startScroll = false;
                if (Math.abs(lyrics.get(index).getTime() - time) > 100) {
                    if (Math.abs(animationOffset) < (lineHeight + lineGap) / 2) {
                        recover(animationOffset, 0, 0);
                    } else {
                        if (animationOffset > 0) {
                            recover(animationOffset, lineHeight + lineGap, -1);
                        } else if (animationOffset < 0) {
                            recover(animationOffset, -lineHeight - lineGap, 1);
                        } else {
                            recover(animationOffset, 0, 0);
                        }
                    }
                } else {
                    recover(animationOffset, 0, 0);
                }
            }
            break;
        }
        return ableToTouch||super.onTouchEvent(e);
    }


    private void updateRange() {
        index = Math.min(index, lyrics.size() - 1);
        if (index < 0) index = 0;
        startIndex = Math.max(0, index - showLineAccount / 2 - 2);
        endIndex = Math.min(index + showLineAccount / 2 + 2, lyrics.size());
    }

    private void recover(int from, int to, int indexAdd) {
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(300);
        animator.addUpdateListener(animation -> {
            animationOffset = (int) animation.getAnimatedValue();
            if (animationOffset == to) {
                animationOffset = 0;
                start();
                index += indexAdd;
                updateRange();
                time = lyrics.get(index).getTime();
                setCurrentTime(time);

                seekListener.seekTo(time);

            }
            invalidate();
        });
        animator.start();

    }




    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    public void setShowLineAccount(int showLineAccount) {
        if(showLineAccount<maxLineAccount){
            this.showLineAccount=showLineAccount;
        }else {
            this.showLineAccount=maxLineAccount;
        }
        if(this.showLineAccount<=0){
            this.showLineAccount=1;
        }
    }


    public void setMaxLineAccount(int maxLineAccount) {
        this.maxLineAccount = maxLineAccount;
        setShowLineAccount(maxLineAccount);
        updateRange();
        invalidate();
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;

    }

    public int getLineHeight() {
        return lineHeight;
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

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        for (int i = 0; i < lyrics.size(); i++) {
            LyricsLine line = lyrics.get(i);
            paint.getTextBounds(line.getLine(), 0, line.getLine().length(), rect);
            line.setWidth(rect.width());
            line.setHeight(rect.height());
            lineHeight = Math.max(rect.height(), lineHeight);
            setLineGap((int) (lineHeight*0.7f));
        }
        halfShowHeight = (int) (showLineAccount / 2f * lineHeight + ((showLineAccount + 1) / 2 * lineGap)) - 10;
        initBitmap();
        invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public int getMaxLineAccount() {
        return maxLineAccount;
    }

    public void setTextFocusColor(int textFocusColor) {
        this.textFocusColor = textFocusColor;
        invalidate();
    }

    public void setEnableFontScale(boolean enableFontScale) {
        this.enableFontScale = enableFontScale;
    }

    public void setSeekListener(SeekListener seekListener) {
        this.seekListener = seekListener;
    }


    public void setClipPaddingTop(int clipPaddingTop) {
        this.clipPaddingTop = clipPaddingTop;
    }

    public void setClipPaddingBottom(int clipPaddingBottom) {
        this.clipPaddingBottom = clipPaddingBottom;
    }

    public interface SeekListener {
        boolean seekTo(int seekTo);
    }

}

















