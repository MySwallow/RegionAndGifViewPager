package com.swallow.feise.regionandgifviewpager.utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by yahui.zeng on 2018/1/16.
 * 描述：设备分辨率工具类
 */

public class DeviceUtil {

    /**
     * 获取屏幕的像素密度 <br/>
     *
     * @param cx -[上下文对象] <br/>
     * @return 屏幕像素密度
     */
    public static float getDeviceDisplayDensity(Context cx) {
        DisplayMetrics dm = cx.getApplicationContext().getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * 获取屏幕的宽度 <br/>
     *
     * @param cx-[上下文对象] <br/>
     * @return 屏幕宽度（单位px）
     */
    public static float getDeviceWidth(Context cx) {
        DisplayMetrics  dm = cx.getApplicationContext().getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 将dp值转换为px值，保证文字大小不变
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
