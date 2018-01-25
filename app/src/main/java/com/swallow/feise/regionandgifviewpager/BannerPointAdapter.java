package com.swallow.feise.regionandgifviewpager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.swallow.feise.regionandgifviewpager.utils.DeviceUtil;

/**
 * Created by yahui.zeng on 2018/1/25.
 */

public class BannerPointAdapter extends BaseAdapter {

    private Integer[] store_thumbIds={R.mipmap.point_default, R.mipmap.point_hov};

    private Integer[] thumbIds ;
    private Context context;
    private int size;
    private int currentIndex = 0;
    private int height;//图片高度

    public BannerPointAdapter(Context context, int size) {
        this.context = context;
        this.size = size;
        height = DeviceUtil.dip2px(context, 8);
    }

    //设置当前显示的position
    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        int index = 0;
        if (position == currentIndex) {
            index = 0;
        } else {
            index = 1;
        }
        return store_thumbIds[index];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView img = new ImageView(context);
        img.setLayoutParams(new GridView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                height));
        // 红球表示选中状态
        if (position == currentIndex) {
            img.setImageResource(store_thumbIds[1]);
        } else {
            img.setImageResource(store_thumbIds[0]);
        }
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        img.setAdjustViewBounds(true);
        return img;
    }

}
