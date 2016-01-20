package com.asdevel.cache.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import com.asdevel.cache.CacheUtils;

/**
 * User: Andrew Rakhmatulin
 * Date: 07.11.13
 * Time: 14:33
 */
public class MediaThumbLoadWorker extends AbstractBitmapLoadWorker {

    final long thumbID;
    final boolean isVideo;
    final String key;
    public MediaThumbLoadWorker(Context context, long thumbID, boolean isVideo ) {
        super(context);
        this.thumbID = thumbID;
        this.isVideo = isVideo;
        key = CacheUtils.makeKeyForMediaStore(thumbID, isVideo);
    }



    @Override
    protected String getKey() {
        return key;
    }



    @Override
    protected Bitmap loadBitmap(int maxSideSize)
    {
        Bitmap b = null;

        /*    * MINI_KIND: 512 x 384 thumbnail
            * MICRO_KIND: 96 x 96 thumbnail*/
        //todo choose kind of thumbnail similar to maxSideSize
        if(isVideo)
        {
            b = MediaStore.Video.Thumbnails.getThumbnail
                    (context.getContentResolver(), thumbID,
                            MediaStore.Video.Thumbnails.MICRO_KIND,null);
        } else {
            //photo
            b = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), thumbID,
                    MediaStore.Images.Thumbnails.MICRO_KIND ,null);
        }
        return b;
    }

    @Override
    protected Bitmap preLoadBitmap(int maxSideSize) {
        //do nothing
        return null;
    }
}
