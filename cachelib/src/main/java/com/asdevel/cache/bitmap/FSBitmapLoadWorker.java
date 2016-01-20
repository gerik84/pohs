package com.asdevel.cache.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import com.asdevel.cache.CacheUtils;

/**
 * User: Andrew Rakhmatulin
 * Date: 07.11.13
 * Time: 15:39
 */
public class FSBitmapLoadWorker extends AbstractBitmapLoadWorker {
    final String path;

    public FSBitmapLoadWorker(Context context, String path) {
        super(context);
        this.path = path;
    }

    @Override
    protected String getKey() {
        return path;
    }

    @Override
    protected Bitmap loadBitmap(int maxSideSize)
    {
        try {
            Bitmap b = CacheUtils.decodeFileToBitmap(path, maxSideSize);
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected Bitmap preLoadBitmap(int maxSideSize) {
        //do nothing
        return null;
    }
}
