package com.swallow.feise.regionandgifviewpager.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.swallow.feise.regionandgifviewpager.BannerPageAdapter;
import com.swallow.feise.regionandgifviewpager.BannerPointAdapter;
import com.swallow.feise.regionandgifviewpager.R;
import com.swallow.feise.regionandgifviewpager.bean.AdvertInfo;
import com.swallow.feise.regionandgifviewpager.imageloader.MyImageLoader;
import com.swallow.feise.regionandgifviewpager.listener.OnRegionClickListener;
import com.swallow.feise.regionandgifviewpager.listener.PageItemClickListenner;
import com.swallow.feise.regionandgifviewpager.utils.DeviceUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by yahui.zeng on 2018/1/16.
 *
 * 描述：自定义循环轮播ViewPager
 */

public class CirculatoryViewPager extends LinearLayout {

    private MyImageLoader imageLoader;
    // banner图片
    private ViewPager bannerViewPage;// 首页banner
    private GridView pointGridview;// banner上的点
    private final static int POINT_SPACE = 6;// 点间距（单位dp）
    private ScheduledExecutorService executorService;// 计时器
    private final static long DELAY_TIME = 4000l;// banner切换事件间隔
    private final static int CHANGE_PAGE = 0x01;
    private boolean isChange = false;
    private int currentItem = 0;// 广告栏当前显示的图片下标
    private BannerPointAdapter gridViewAdapter;// Banner上的点
    private BannerPageAdapter pageAdapter;// Banner图片适配器
    private TextView tv_number;// Banner下的数字显示
    // 是否暂停轮播
    private boolean stopPlay = false;

    private int type;//底部点的样式
    private static float density = 0f;// 屏幕像素密度

    private Context mContext;

    private PageItemClickListenner pageItemClickListener;//新 点击事件

    // 图片地址
    private ArrayList<String> imgUrls = new ArrayList<String>();
    private ArrayList<RegionGifImageView> views = new ArrayList<>();

    //多区域广告banner数据
    private List<AdvertInfo> adRegionList;


    //是否进行轮播
    private boolean autoPlay = true;

    private final Handler mHandler = new MyHandler(this);


    public CirculatoryViewPager(Context context) {
        this(context, null);
    }

    public CirculatoryViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(attrs);
    }


    /**
     * 设置数据, 除了图片地址, 还包含跳转相关数据
     * 支持单区域、多区域点击事件, 静态图, 动态图
     * 多区域点击需设置AdvertInfo中的adPageList
     */
    public void setRegionAdData(List<AdvertInfo> adRegionList) {
        this.adRegionList = adRegionList;
        if (adRegionList == null || adRegionList.size() == 0) {
            return;
        }
        ArrayList<String> imgpaths = new ArrayList<>();
        for (int i = 0; i < adRegionList.size(); i++) {
            imgpaths.add(adRegionList.get(i).getAdImgPath());
        }
        setData(imgpaths);
        startCirculation();
    }

    /**
     * 设置图片数据
     *
     * @param imgsStr-[图片地址] <br/>
     */
    public void setData(ArrayList<String> imgsStr) {
        if (imgsStr != null && imgsStr.size() > 0) {
            imgUrls.clear();
            imgUrls.addAll(imgsStr);
            initViews();
            pageAdapter.notifyDataSetChanged();
            if (imgsStr.size() > 1) {
                bannerViewPage.setCurrentItem(1);
            } else {
                bannerViewPage.setCurrentItem(0);
            }
            switch (type) {
                default:
                case 1:
                    initPointGridView();
                    break;
                case 2:
                    initNumberView();
                    break;
            }

        }
    }

    /**
     * 控件初始化
     */
    private void initView(AttributeSet attrs) {
        imageLoader = new MyImageLoader(mContext);
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CirculatoryVp);
        type = typedArray.getInt(R.styleable.CirculatoryVp_defaultIcon, 0);
        typedArray.recycle();
        density = DeviceUtil.getDeviceDisplayDensity(mContext.getApplicationContext());
        View view = LayoutInflater.from(mContext).inflate(R.layout.circulatory_viewpager_layout, null);
        bannerViewPage = view.findViewById(R.id.bannerViewPage);
        pointGridview = view.findViewById(R.id.pointGridview);
        tv_number = view.findViewById(R.id.tv_number);
        initBannerView();
        addView(view);
    }

    /**
     * 设置Banner <br/>
     */
    private void initBannerView() {
        // Banner适配器
        pageAdapter = new BannerPageAdapter(mContext, views);
        bannerViewPage.setAdapter(pageAdapter);
        bannerViewPage.setOnPageChangeListener(new MyPageChangeListener());
        // 设置ViewPage间距
        bannerViewPage.setPageMargin(15);
    }
    // 初始化banner上的点
    private void initPointGridView() {
        pointGridview.setVisibility(View.VISIBLE);
        tv_number.setVisibility(View.GONE);
        // 动态设置point_gridview的个数
        int count = imgUrls.size();

        pointGridview.setNumColumns(count);

        gridViewAdapter = new BannerPointAdapter(mContext, count);
        pointGridview.setAdapter(gridViewAdapter);

        ViewGroup.LayoutParams lp = pointGridview.getLayoutParams();
        lp.width = Math.round(density * 8 * count) + Math.round(density * POINT_SPACE * (count - 1))
                + Math.round(density * 8);
        pointGridview.setLayoutParams(lp);
        if (imgUrls.size() <= 1) {
            pointGridview.setVisibility(View.GONE);
        } else {
            pointGridview.setVisibility(View.VISIBLE);
        }
    }

    // 初始化banner上的数字
    private void initNumberView() {
        pointGridview.setVisibility(View.GONE);
        tv_number.setVisibility(View.VISIBLE);
        if (imgUrls.size() <= 0) {
            tv_number.setVisibility(View.GONE);
        } else {
            tv_number.setVisibility(View.VISIBLE);
            setNumberText(1, imgUrls.size());
        }
    }

    // 设置文字
    private void setNumberText(Integer i, Integer count) {
        SpannableString styledText = new SpannableString(i.toString() + "/" + count.toString());
        styledText.setSpan(new TextAppearanceSpan(mContext, R.style.text_size1), 0, i.toString().length() + 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new TextAppearanceSpan(mContext, R.style.text_size2), i.toString().length() + 1, i
                .toString().length() + 1 + count.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_number.setText(styledText, TextView.BufferType.SPANNABLE);
    }


    /**
     * 开始轮播
     */
    public void startCirculation() {
        stopCirculation();
        if (autoPlay && imgUrls.size() > 1) {
            // 每隔3秒钟切换一张图片
            executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleWithFixedDelay(new ViewPagerTask(), DELAY_TIME, DELAY_TIME, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 结束轮播
     */
    public void stopCirculation() {
        // 停止轮播
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    // 定时切换图片的Task
    private class ViewPagerTask implements Runnable {

        @Override
        public void run() {
            if (imgUrls.size() == 0 || stopPlay) {
                return;
            }
            // 更新界面
            mHandler.obtainMessage(CHANGE_PAGE).sendToTarget();
        }

    }

    /**
     * 切换当前页
     */
    public void changePage() {
        // 设置当前页面
        int count = pageAdapter.getCount();
        if (count > 1) { // 实际上，多于1个，就多于3个
            int index = bannerViewPage.getCurrentItem();
            index = index + 1; // 这里修改过
            bannerViewPage.setCurrentItem(index, true);
        }
    }

    // 控制banner轮播
    private static class MyHandler extends Handler {

        private final WeakReference<CirculatoryViewPager> mCvp;

        public MyHandler(CirculatoryViewPager cVp) {
            mCvp = new WeakReference<>(cVp);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mCvp.get() != null) {
                mCvp.get().changePage();
            }

        }

    }

    // banner滑动监听
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int pState) {
            if (ViewPager.SCROLL_STATE_IDLE == pState) {
                if (isChange) {
                    isChange = false;
                    bannerViewPage.setCurrentItem(currentItem, false);
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            if (imgUrls.size() > 1) { // 多于1，才会循环跳转
                if (position < 1) { // 首位之前，跳转到末尾（N）
                    position = imgUrls.size(); // 注意这里是mList，而不是mViews
                    currentItem = position;
                    isChange = true;
                    // bannerViewPage.setCurrentItem(position, false);
                } else if (position > imgUrls.size()) { // 末位之后，跳转到首位（1）
                    // bannerViewPage.setCurrentItem(1, false); //
                    // false:不显示跳转过程的动画
                    position = 1;
                    currentItem = 1;
                    isChange = true;
                } else {
                    isChange = false;
                }

                if (tv_number.getVisibility() == View.VISIBLE) {
                    if (imgUrls.size() <= 1) {
                        setNumberText(1, 1);
                    } else {
                        Integer count = imgUrls.size();
                        if (position == 0) {
                            setNumberText(1, count);
                        } else if (position == imgUrls.size() + 2) {
                            setNumberText(count, count);
                        }
                        Integer i = position;
                        setNumberText(i, count);
                    }
                }
                if (gridViewAdapter != null) {
                    gridViewAdapter.setCurrentIndex(position - 1);
                    gridViewAdapter.notifyDataSetInvalidated();
                }

            } else {
                currentItem = position;
                isChange = false;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (type != 2) {
                    stopPlay = true;
                    stopCirculation();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (type != 2) {
                    stopPlay = false;
                    startCirculation();
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    // 初始化ImageView
    private ArrayList<RegionGifImageView> initViews() {
        if (imgUrls == null || imgUrls.size() == 0) {
            return null;
        }

        ArrayList<RegionGifImageView> newViews = new ArrayList<RegionGifImageView>();

        // 设置views的数量，如果只有一张图片，则不进行轮播或者滑动
        int length = imgUrls.size();
        if (length > 1) {
            length = length + 2;// 需要在第一个位置添加最后一张图片；最后一个位置添加第一张图片
        }

        for (int i = 0; i < length; i++) {
            if (views.size() <= i) {
                RegionGifImageView imageView = new RegionGifImageView(mContext);
                imageView.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                newViews.add(imageView);
            } else {
                newViews.add(views.get(i));
            }

            final int position;
            String imgUrl = null;
            if (length > 1) {
                if (i == 0) {
                    // 设置第一张图片为最后一张
                    imgUrl = imgUrls.get(imgUrls.size() - 1);
                    position = imgUrls.size() - 1;
                } else if (i == length - 1) {
                    // 设置最后一张为第一张图片
                    imgUrl = imgUrls.get(0);
                    position = 0;
                } else {
                    imgUrl = imgUrls.get(i - 1);
                    position = i - 1;
                }
            } else {
                imgUrl = imgUrls.get(i);
                position = i;
            }
            if (adRegionList != null && adRegionList.size() > 0) {
                List<AdvertInfo.PageParam> linkUrls = adRegionList.get(position).getAdPageList();
                if (linkUrls != null) {
                    newViews.get(i).setNumber(linkUrls.size());
                }
            }
            if (imgUrl != null) {
               imageLoader.loadImageOrGif(imgUrl, newViews.get(i), MyImageLoader.initDisplayOptions(true, R.drawable.banner_default_icon));
            }
            newViews.get(i).setOnRegionClickListener(new OnRegionClickListener() {
                @Override
                public void onClick(int regionPosition) {

                    if (pageItemClickListener != null && adRegionList != null && adRegionList.size() > position) {
                        AdvertInfo advertInfo = adRegionList.get(position);
                        if (advertInfo.getAdPageList() != null && advertInfo.getAdPageList().size() >= regionPosition) {
                            advertInfo.setAdLinkUrl(advertInfo.getAdPageList().get(regionPosition - 1).getAdLinkUrl());
                        }

                        pageItemClickListener.click(position, advertInfo);
                    }

                }
            });
        }
        views.clear();
        views.addAll(newViews);
        return views;
    }

    /**
     * 取消handler对象的message和Runnable
     */
    public void destroy() {
        mHandler.removeCallbacksAndMessages(null);
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
    }

    public void setPageItemClickListener(PageItemClickListenner pageItemClickListener) {
        this.pageItemClickListener = pageItemClickListener;
    }

}
