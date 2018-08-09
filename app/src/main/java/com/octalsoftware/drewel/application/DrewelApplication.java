package com.octalsoftware.drewel.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.octalsoftware.drewel.AppDelegate;
import io.fabric.sdk.android.Fabric;

/**
 * Created by sharukhb on 4/17/2018.
 */

public class DrewelApplication extends Application {
    DrewelApplication drewelApplication;
    public static DisplayImageOptions options = options = new DisplayImageOptions.Builder()
//            .showImageOnLoading(new BitmapDrawable(getResources(), default_bitmap1))
//            .showImageForEmptyUri(new BitmapDrawable(getResources(), default_bitmap1))
//            .showImageOnFail(new BitmapDrawable(getResources(), default_bitmap1))
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .build();


    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        drewelApplication = this;
        MultiDex.install(this);
        initImageLoader(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        try {
//            Fabric.with(this, new Crashlytics());
            MultiDex.install(this);
        } catch (Exception e) {
            AppDelegate.Companion.LogE(e);
        }
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(100 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app
        config.imageDownloader(new BaseImageDownloader(context, 20 * 1000, 40 * 1000)); // connectTimeout (20 s), readTimeout (40 s)
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }

    public DrewelApplication getInstance() {
        return drewelApplication;
    }

}
