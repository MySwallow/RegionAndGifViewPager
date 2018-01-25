package com.swallow.feise.regionandgifviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.swallow.feise.regionandgifviewpager.widget.RegionGifImageView;

import java.util.ArrayList;

/**
 * Created by yahui.zeng on 2018/1/25.
 */

public class BannerPageAdapter extends PagerAdapter {
    private ArrayList<RegionGifImageView> views;
    // 图片地址
    private Context context;

    public BannerPageAdapter(Context context, ArrayList<RegionGifImageView> views) {
        this.context = context;
        this.views = views;
    }

    @Override
    public int getCount() {
        if (views == null) {
            return 0;
        }
        return views.size();

    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
