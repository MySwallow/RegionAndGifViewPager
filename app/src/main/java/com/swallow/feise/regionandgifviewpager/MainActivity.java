package com.swallow.feise.regionandgifviewpager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.swallow.feise.regionandgifviewpager.bean.AdvertInfo;
import com.swallow.feise.regionandgifviewpager.listener.PageItemClickListenner;
import com.swallow.feise.regionandgifviewpager.utils.DeviceUtil;
import com.swallow.feise.regionandgifviewpager.widget.CirculatoryViewPager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private CirculatoryViewPager bannerCvp;
    private ArrayList<AdvertInfo> bannerDatas = new ArrayList<>();// banner数据
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bannerCvp = findViewById(R.id.cvp);
        setData();
        setAttribute();
    }

    private void setData() {
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        bannerDatas.add(getBannerData(0));
        bannerDatas.add(getBannerData(1));
        bannerDatas.add(getBannerData(2));
        bannerDatas.add(getBannerData(3));
        bannerDatas.add(getBannerData(4));
    }

    private AdvertInfo getBannerData(int number) {
        AdvertInfo advertInfo = new AdvertInfo();
        advertInfo.setAdImgPath("http://download.mypharma.com/2018/01/17/mph_665d1cdfdec646cb98027a778b36178d.jpg");
        ArrayList<AdvertInfo.PageParam> pageParams = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            AdvertInfo.PageParam pageParam = new AdvertInfo.PageParam();
            pageParam.setAdLinkUrl("show" + i);
            pageParams.add(pageParam);
        }
        advertInfo.setAdPageList(pageParams);
        return advertInfo;
    }

    private void setAttribute() {
        int height = (int) (DeviceUtil.getDeviceWidth(this) * 100 / 267f);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        bannerCvp.setLayoutParams(params);
        bannerCvp.setPageItemClickListener(bannerListener);
        showViewPager();

    }

    private void showViewPager() {
        if (bannerDatas.size() == 0) {
            bannerCvp.setVisibility(View.GONE);
            return;
        }

        if (bannerCvp.getVisibility() == View.GONE) {
            bannerCvp.setVisibility(View.VISIBLE);
        }

        bannerCvp.setRegionAdData(bannerDatas);

        bannerCvp.startCirculation();

    }


    private PageItemClickListenner bannerListener = new PageItemClickListenner<AdvertInfo>() {
        @Override
        public void click(int position, AdvertInfo advertInfo) {
            showToast(advertInfo.getAdLinkUrl());
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // 开始轮播
        if (bannerCvp != null) {
            bannerCvp.startCirculation();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 结束轮播
        if (bannerCvp != null) {
            bannerCvp.stopCirculation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bannerCvp != null) {
            bannerCvp.destroy();
        }
    }

    private void showToast(String message) {
        toast.setText(message);
        toast.show();
    }

}
