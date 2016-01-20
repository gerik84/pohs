package com.asdevel.cache.bitmap;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.asdevel.cache.CacheUtils;

/**
 * User: Andrew Rakhmatulin
 * Date: 07.11.13
 * Time: 15:31
 */
public class ResourceBitmapLoadWorker extends AbstractBitmapLoadWorker
{

    final String key;
    final String packageName;
    final int resourceID;

    public ResourceBitmapLoadWorker(Context context, String packageName, int resourceID) {
        super(context);
        this.resourceID = resourceID;
        this.packageName = packageName;
        this.key = CacheUtils.makeKeyForResource(packageName, resourceID);
    }

    @Override
    protected String getKey() {
        return key;
    }

    @Override
    protected Bitmap loadBitmap(int maxSideSize) {
        Bitmap b = null;
        try {
            Resources res = context.getPackageManager().getResourcesForApplication(packageName);
            b = BitmapFactory.decodeResource(res, resourceID);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return b;
    }

    @Override
    protected Bitmap preLoadBitmap(int maxSideSize) {
        //do nothing
        return null;
    }
}
