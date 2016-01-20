package com.asdevel.cache.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import com.asdevel.cache.CacheUtils;

import java.net.HttpURLConnection;

/**
 * User: Andrew Rakhmatulin
 * Date: 07.11.13
 * Time: 12:54
 */
public class HttpBitmapLoadWorker extends AbstractBitmapLoadWorker
{
    private final String mUrl;
    boolean preloadSuccessed = false;
    public HttpBitmapLoadWorker(Context context, String url) {
        super(context);
        mUrl = url;
        if(mUrl == null)
        {
            throw new IllegalArgumentException("url can't be null");
        }
    }

    @Override
    protected String getKey() {
        return mUrl;
    }

    @Override
    protected Bitmap loadBitmap(int maxSideSize)
    {
        int result = CacheUtils.downloadFile(context, mUrl);
        if(result == HttpURLConnection.HTTP_OK ||
                (result == HttpURLConnection.HTTP_NOT_MODIFIED && preloadSuccessed == false))
        {
            return preLoadBitmap(maxSideSize);
        }
        //we got error or already load last version of bitmap from file cache
        return null;
    }


    //try decode cache file before downloading last version of file
    @Override
    protected Bitmap preLoadBitmap(int maxSideSize)
    {
        try {
            Bitmap b = CacheUtils.decodeFileToBitmap(CacheUtils.getCacheFile(context, mUrl).getAbsolutePath(), maxSideSize);
            preloadSuccessed = (b != null);

            return b;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
