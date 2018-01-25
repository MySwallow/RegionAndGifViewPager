package com.swallow.feise.regionandgifviewpager.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.swallow.feise.regionandgifviewpager.listener.OnRegionClickListener;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by yahui.zeng on 2018/1/16.
 * 描述：支持动态图,自定义点击区域的ImageView
 */

public class RegionGifImageView extends GifImageView {

    private int number = 1;//几个点击区域
    private int width;
    private float startX;
    private int height;
    private float startY;
    private boolean button = false;//是否能点击
    private int startCount;//初始点击的区域

    private OnRegionClickListener listener;//点击事件监听

    public void setOnRegionClickListener(OnRegionClickListener listener) {
        this.listener = listener;
    }

    public RegionGifImageView(Context context) {
        this(context, null);
    }

    public RegionGifImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RegionGifImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setNumber(int number) {
        this.number = number;
        if (number == 0) {
            this.number = 1;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int change = width / number;
        int min = 0;
        int max = change;
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();

                if (startX > 0) {
                    if (startX >= 0 && number > 0) {
                        button = true;
                        for (int i = 1; i <= number; i++) { //记录点下时的区域
                            if (startX > min && startX < max) {
                                startCount = i;
                                break;
                            }
                            min += change;
                            max += change;
                        }
                    }
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                if (moveX - startX > 20 || moveY - startY > 20) { //滑动距离过长, 取消点击事件
                    button = false;
                    return super.onTouchEvent(event);
                }

                if (!(moveX >= 0 && moveY >= 0 && moveX < width && moveY < height)) {//滑动超过图片位置, 取消点击事件
                    button = false;
                    return super.onTouchEvent(event);
                }
                return true;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                if (button) {
                    if (upX >= 0 && number > 0) {
                        for (int i = 1; i <= number; i++) {
                            if (upX > min && upX < max) {
                                if (startCount == i) {//判断点下时区域是否和抬起时区域一致
                                    if (listener != null) {
                                        listener.onClick(i);
                                    }
                                }
                                break;
                            }
                            min += change;
                            max += change;
                        }
                    }
                }
                button = false;
                startCount = 0;
                break;
        }
        return super.onTouchEvent(event);
    }
}
