package com.redline.shop.Interface;

import android.app.Application;

import com.asdevel.cache.jsondb.JsonCacheDbHelper;
import com.redline.shop.BuildConfig;
import com.redline.shop.Utils.Tools;

import static com.redline.shop.Utils.Tools.log;

public class ShopApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Tools.initialize(getApplicationContext());
        JsonCacheDbHelper.setDbVersion(BuildConfig.VERSION_CODE);
        JsonCacheDbHelper.setListener(new JsonCacheDbHelper.Listener() {
            @Override
            public void onException(Throwable e, String addon) {
                log(e);
//                Tools.gaTrackerPostException(e, addon);
            }
        });

    }
}
