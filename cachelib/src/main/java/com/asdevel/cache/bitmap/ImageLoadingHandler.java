package com.asdevel.cache.bitmap;

/**
 * User: Andrew Rakhmatulin
 * Date: 07.11.13
 * Time: 11:02
 */
public interface ImageLoadingHandler {

    boolean isCanceled(AbstractBitmapLoadWorker worker);
    void onLoadComplete(AbstractBitmapLoadWorker worker, BitmapCacheContainer container);
}
