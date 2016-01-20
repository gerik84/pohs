package com.asdevel.cache.bitmap;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

/**
 * User: Andrew Rakhmatulin
 * Date: 07.11.13
 * Time: 14:13
 */
public class PackageIconLoadWorker extends AbstractBitmapLoadWorker
{
    final String packageName;
    public PackageIconLoadWorker(Context context, String packageName) {
        super(context);
        this.packageName = packageName;
    }

    @Override
    protected String getKey() {
        return packageName;
    }

    @Override
    protected Bitmap loadBitmap(int maxSideSize)
    {
        Bitmap b = null;
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            // Get the application's resources
            Resources res = context.getPackageManager().getResourcesForApplication(packageName);

            //works only in > Android 4.0
            Drawable d = res.getDrawableForDensity(appInfo.icon, DisplayMetrics.DENSITY_XHIGH);
            /*
            // Get a copy of the configuration, and set it to the desired resolution
            Configuration config = res.getConfiguration();
            Configuration originalConfig = new Configuration(config);

            config.screenLayout = Configuration.SCREENLAYOUT_SIZE_XLARGE;////DisplayMetrics.DENSITY_XHIGH;

            // Update the configuration with the desired resolution
            DisplayMetrics dm = res.getDisplayMetrics();
            res.updateConfiguration(config, dm);

            // Grab the app icon
            Drawable d = res.getDrawable(appInfo.icon);

            // Set our configuration back to what it was
            res.updateConfiguration(originalConfig, dm);
            */
            if(d instanceof BitmapDrawable)
            {
                b = ((BitmapDrawable) d).getBitmap();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            //ignored
        }
        return b;
    }

    @Override
    protected Bitmap preLoadBitmap(int maxSideSize) {
        //do nothing
        return null;
    }
}
