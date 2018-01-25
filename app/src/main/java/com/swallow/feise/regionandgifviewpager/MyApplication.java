package com.swallow.feise.regionandgifviewpager;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.swallow.feise.regionandgifviewpager.imageloader.MyImageLoader;

/**
 * Created by yahui.zeng on 2018/1/25.
 */

public class MyApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化ImageLoader
        MyImageLoader.initImageLoader(this);
    }
}
