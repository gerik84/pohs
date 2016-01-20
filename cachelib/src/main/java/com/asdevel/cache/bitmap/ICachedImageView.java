package com.asdevel.cache.bitmap;

import android.graphics.drawable.Drawable;

/**
 * Created by asdevel.com
 * Date: 19.06.2014
 * Time: 13:20
 */
public interface ICachedImageView
{
	void setDefaultDrawable(Drawable d);

	void setImageBitmap(BitmapCacheContainer container);

	void restoreDefaultDrawable();

	int getMaxImageSize();
}
