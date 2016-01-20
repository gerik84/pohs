package com.asdevel.cache.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.asdevel.cache.CacheUtils;

import java.lang.ref.WeakReference;

/**
 * User: Andrew Rakhmatulin
 * Date: 07.11.13
 * Time: 11:10
 */
public abstract class AbstractBitmapLoadWorker
{

    WeakReference<ImageLoadingHandler> mHandler = null;
    final Context context;

    public AbstractBitmapLoadWorker(Context context)
    {
        this.context = context;
    }

    protected abstract String getKey();

    protected abstract Bitmap loadBitmap(int maxSideSize);
    //fast
    protected abstract Bitmap preLoadBitmap(int maxSideSize);

    public synchronized void setonLoadingHandler(ImageLoadingHandler handler)
    {
        mHandler = new WeakReference<ImageLoadingHandler>(handler);
    }

    public void load(int maxSideSize)
    {
        new BitmapWorkerTask(maxSideSize).execute();
    }


    protected synchronized boolean isCanceled()
    {
        boolean ret = false;
        if(mHandler != null)
        {
            ImageLoadingHandler h = mHandler.get();
            if(h != null)
            {
                ret = h.isCanceled(this);
            }
        }
        return ret;
    }


    private class BitmapWorkerTask extends AsyncTask<Integer, BitmapCacheContainer, BitmapCacheContainer> {

        final int maxSideSize;

        public BitmapWorkerTask(int maxSideSize)
        {
            this.maxSideSize = maxSideSize;

        }
        @Override
        protected void onProgressUpdate(BitmapCacheContainer... values)
        {
            if(values != null && values.length > 0)
            {
                onPostExecute(values[0]);
            }
        }

        // Decode image in background.
        @Override
        protected BitmapCacheContainer doInBackground(Integer... params)
        {
            try {
                if(isCanceled()) return null;
                Bitmap bm = preLoadBitmap(maxSideSize);
                if(bm != null) publishProgress(new BitmapCacheContainer(bm, getKey()));
                if(isCanceled()) return null;
                bm = loadBitmap(maxSideSize);
                if(bm == null) return null;
                return new BitmapCacheContainer(bm, getKey());
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        // Once complete, notify handlers.
        @Override
        protected void onPostExecute(BitmapCacheContainer bitmap) {
            if(bitmap != null)
            {
                bitmap.setLastUpdateTime(CacheUtils.getModifiedSinceDate(context, getKey()));
                BitmapMemCache.getInstance().add(getKey(), bitmap);
                if(mHandler != null)
                {
                    ImageLoadingHandler h = mHandler.get();
                    if(h != null)
                    {
                        BitmapCacheContainer bmp = BitmapMemCache.getInstance().get(getKey());
                        h.onLoadComplete(AbstractBitmapLoadWorker.this, bmp);
                    }
                }
            }
        }
    }
}
