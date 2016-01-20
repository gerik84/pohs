package com.asdevel.cache.bitmap;

import android.graphics.Bitmap;
import com.asdevel.cache.AbstractCacheContainer;

/**
 * User: Andrew Rakhmatulin
 * Date: 07.11.13
 * Time: 9:34
 */
public class BitmapCacheContainer extends AbstractCacheContainer<Bitmap>
{
    final String key;

    public BitmapCacheContainer(Bitmap object, String key) {
        super(object);
        this.key = key;
    }

    public String getKey()
    {
        return key;
    }

    @Override
    protected long getCost() {
        Bitmap b = container;
        if(b == null) return 0;
        return b.getByteCount();
    }

    @Override
    protected void objectSpecificRecycle()
    {
        Bitmap b = get();
        if(b != null)
        {
            b.recycle();
        }
    }
}
