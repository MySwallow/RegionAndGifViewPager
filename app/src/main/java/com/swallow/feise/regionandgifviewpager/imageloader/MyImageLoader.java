package com.swallow.feise.regionandgifviewpager.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.swallow.feise.regionandgifviewpager.R;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by yahui.zeng on 2018/1/16.
 */

public class MyImageLoader {

    private final Context context;
    private final ImageLoader imageLoader;

    // SD卡文件根目录名称
    public final static String ROOT_DIRECTORY = "region_gif_viewpager";

    // 图片缓存目录
    public final static String IMAGECACHE = "imagecache";

    public MyImageLoader(Context context) {
        this.context = context;
        imageLoader = ImageLoader.getInstance();
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    // 图片缓存的SD卡路径
    public static String getImageCachePath() {
        return Environment.getExternalStorageDirectory() + File.separator + ROOT_DIRECTORY + File.separator
                + IMAGECACHE;
    }


    /**
     * 初始化ImageLoader <br/>
     */
    public static void initImageLoader(Context context) {
        // 获取本地缓存的目录，该目录在SDCard的根目录下
        File cacheDir = new File(getImageCachePath());

        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
                context);
        // 设置线程数量
        // 设定线程等级比普通低一点
        builder.threadPriority(Thread.NORM_PRIORITY - 1);
        builder.tasksProcessingOrder(QueueProcessingType.LIFO);
        builder.memoryCache(new LruMemoryCache(2 * 1024 * 1024));
        builder.threadPoolSize(3);
        builder.denyCacheImageMultipleSizesInMemory();
        // 设定缓存的SDcard目录，
        try {
            builder.diskCache(new LruDiskCache(cacheDir, new HashCodeFileNameGenerator(), 50 * 1024 * 1024));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //设定sd卡缓存大小
//		builder.diskCacheSize(50 * 1024);
        // 设定网络连接超时 timeout: 8s 读取网络连接超时read timeout: 15s
        builder.imageDownloader(new BaseImageDownloader(context, 8000, 30000));
        // 设置ImageLoader的配置参数
        builder.defaultDisplayImageOptions(initDisplayOptions(true,
                R.drawable.ic_launcher_background));

        // 初始化ImageLoader
        ImageLoader.getInstance().init(builder.build());
    }
    /**
     * 返回默认的参数配置
     *
     * @param isShowDefault true：显示默认的加载图片 false：不显示默认的加载图片
     * @return
     */
    public static DisplayImageOptions initDisplayOptions(boolean isShowDefault,
                                                         int resId) {
        DisplayImageOptions.Builder displayImageOptionsBuilder = new DisplayImageOptions.Builder();
        // 设置图片缩放方式
        // EXACTLY: 图像将完全按比例缩小的目标大小
        // EXACTLY_STRETCHED: 图片会缩放到目标大小
        // IN_SAMPLE_INT: 图像将被二次采样的整数倍
        // IN_SAMPLE_POWER_OF_2: 图片将降低2倍，直到下一减少步骤，使图像更小的目标大小
        // NONE: 图片不会调整
        displayImageOptionsBuilder.imageScaleType(ImageScaleType.EXACTLY);
        if (isShowDefault) {
            // 默认显示的图片
            displayImageOptionsBuilder.showImageOnLoading(resId);
            // 地址为空的默认显示图片
            displayImageOptionsBuilder.showImageForEmptyUri(resId);
            // 加载失败的显示图片
            displayImageOptionsBuilder.showImageOnFail(resId);
        }
        // 开启内存缓存
        displayImageOptionsBuilder.cacheInMemory(true);
        // 开启SDCard缓存
        displayImageOptionsBuilder.cacheOnDisk(true);
        displayImageOptionsBuilder.considerExifParams(true);
        displayImageOptionsBuilder.bitmapConfig(Bitmap.Config.RGB_565);
        return displayImageOptionsBuilder.build();
    }


    public void loadImageOrGif(String url, final ImageView imageView, DisplayImageOptions options) {
        imageLoader.displayImage(url, imageView, options, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingComplete(String url, View view, Bitmap loadedImage) {
                File file = DiskCacheUtils.findInCache(url, imageLoader.getDiskCache());
                if (file != null) {
                    try {
                        GifDrawable gifDrawable = new GifDrawable(file);
                        imageView.setImageDrawable(gifDrawable);
                    } catch (IOException e) {
                        imageView.setImageBitmap(loadedImage);
                    }
                }
            }
        });
    }


}
