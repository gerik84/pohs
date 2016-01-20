package com.asdevel.cache.bitmap;

import android.graphics.Bitmap;
import com.asdevel.cache.AbstractCache;

/**
 * User: Andrew Rakhmatulin
 * Date: 07.11.13
 * Time: 12:43
 */
public class BitmapMemCache
{
	AbstractCache<String, BitmapCacheContainer> mCache = new AbstractCache<String, BitmapCacheContainer>(calcMaxMemoryForCache())
	{
		@Override
		protected void releaseObject(BitmapCacheContainer container)
		{
			container.recycle();
		}
	};
	final static float PartOfRuntime = 0.5f;

	static long calcMaxMemoryForCache()
	{
		Runtime rt = Runtime.getRuntime();
		return (long) (rt.maxMemory() * PartOfRuntime);
	}

	private BitmapMemCache()
	{

	}

	private static class Singleton
	{
		private final static BitmapMemCache instance = new BitmapMemCache();
    }

    public static BitmapMemCache getInstance()
    {
        return Singleton.instance;
    }

    public void add(String key, BitmapCacheContainer container)
    {
        if(container  == null || container.isRecycled() || container.get() == null) return;
        BitmapCacheContainer old = mCache.object(key);
        if(old != null)
        {
            if(old.getLastUpdateTime() < container.getLastUpdateTime())
            {
                mCache.remove(key);
            } else {
                Bitmap oldBmp = old.get();
                Bitmap newBmp = container.get();
                if(oldBmp != null &&  Math.max(oldBmp.getWidth(), oldBmp.getHeight()) > Math.max(newBmp.getWidth(), newBmp.getHeight()))
                {
                    return;
                }
            }
        }
        mCache.insert(key, container, container.getCost());
    }

    public BitmapCacheContainer get(String key)
    {
        BitmapCacheContainer b = mCache.object(key);
        if(b != null && b.isRecycled())
        {
            mCache.remove(key);
            return null;
        }
        return b;
    }
}
