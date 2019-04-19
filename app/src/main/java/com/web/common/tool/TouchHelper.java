package com.web.common.tool;

import android.view.MotionEvent;
import android.view.View;

import com.web.common.util.ViewUtil;


/**
 * 判断Touch的方向
 * Created by home on 2018/2/15.
 */

public abstract class TouchHelper implements View.OnTouchListener{
    private int id=0;//***点击的id
    private float preX,preY;//**点击位置
    private float movePreX,movePreY;
    private boolean up;
    private boolean longClicked=false;//**这次点下的长点击是否有效
    private boolean moved=false;
    private boolean startMove=false;//****开始滑动，提高距离限制，后面滑动就不进行限制
    private boolean startLongClickMove=false;
    private boolean longClickConsume=false;

    private int startMoveMinGap= (int) (ViewUtil.screenWidth()*0.08f);

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        onTouch(event);
        if(event.getAction()== MotionEvent.ACTION_DOWN){
            down(event);
            startMove=false;
            up=false;
            id++;
            longClicked=false;
            moved=false;
            movePreX=preX=event.getRawX();
            movePreY=preY=event.getRawY();
            int tmpId=id;
            v.postDelayed(()->{
                if(tmpId==id){
                    if(!up&&!longClicked&&!moved){
                        longClicked=true;
                        longClickConsume=longOnClick(preX,preY);
                    }
                }
            },520);


            return false;
        }else if(event.getAction()== MotionEvent.ACTION_MOVE){
            //**开始滑动跳高距离限制
            if(!startMove&&!longClickConsume&& Math.abs(preX-event.getRawX())> Math.abs(preY-event.getRawY())*0.5){
                if(Math.abs(preX-event.getRawX())>=startMoveMinGap) {
                    touchDirection(event.getRawX() - movePreX);
                    moved = true;
                    startMove=true;
                }
            }
            else if(startMove&&!longClickConsume){//进行滑动即时响应
                touchDirection(event.getRawX()-movePreX);
                moved=true;
            }
            float abs= Math.abs(preX-event.getRawX())+ Math.abs(preY-event.getRawY());
            if(abs>20){
                moved=true;
            }
            if(!startLongClickMove&&longClicked&&abs>=startMoveMinGap){//**长点击有效，可以拖动图标
                moved=true;
                startLongClickMove=true;
                longClickMove(event.getRawX()-movePreX,event.getRawY()-movePreY);
            }else if(longClicked&&startLongClickMove) {
                longClickMove(event.getRawX()-movePreX,event.getRawY()-movePreY);
            }
            movePreX=event.getRawX();
            movePreY=event.getRawY();
            return true;
        }
        else if(event.getAction()== MotionEvent.ACTION_UP){
            startLongClickMove=false;
            startMove=false;
            up=true;
            if(Math.abs(preX-event.getRawX())+ Math.abs(preY-event.getRawY())<startMoveMinGap){
                if(!longClicked&&!moved) {
                    onClick(preX, preY);
                }
            }
            if(longClicked){//**长点击有效,点击接收时【显示选项】
                if(!moved)
                    longClickUpNoMove(event.getRawX(),event.getRawY());
                else
                    longClickUpMoved(event.getRawX(),event.getRawY());
            }
            longClickConsume=false;
        }
        return false;

    }

    /**
     * 水平滑动
     * @param distance 滑动距离
     */
    public abstract void touchDirection(float distance);

    /**
     * 点击事件
     * @param x x
     * @param y y
     */
    public void onClick(float x,float y){};

    /**
     * 长点击
     * @param x x
     * @param y y
     */
    public abstract boolean longOnClick(float x,float y);
    /**
     * 长点击移动
     * @param dx dx
     * @param dy dy
     */
    public abstract void longClickMove(float dx,float dy);
    /**
     * 长点击结束
     * @param x x
     * @param y y
     */
    public abstract void longClickUpNoMove(float x,float y);
    public abstract void longClickUpMoved(float x,float y);
    public abstract void down(MotionEvent event);
    public void onTouch(MotionEvent event){}

}
