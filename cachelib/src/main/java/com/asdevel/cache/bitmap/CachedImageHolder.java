package com.asdevel.cache.bitmap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * User: Andrew Rakhmatulin
 * Date: 07.11.13
 * Time: 10:48
 */
public class CachedImageHolder extends RelativeLayout implements ImageLoadingHandler
{
    public static int TAG_KEY = 1;
    public static String TAG = "CachedImageHolder";
	ICachedImageView imageView = null;
    ProgressBar progress = null;

    public CachedImageHolder(Context context) {
        super(context);
    }

    public CachedImageHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CachedImageHolder(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    protected void checkNewView(View view)
    {
        if(imageView == null && view instanceof ICachedImageView)
        {
            imageView = (ICachedImageView) view;
        }
        if(progress == null && view instanceof ProgressBar)
        {
            progress = (ProgressBar) view;
        }
    }

    public void setDefaultDrawable(Drawable d)
    {
        if(imageView != null)
        {
            imageView.setDefaultDrawable(d);
        }

    }

	public void restoreDefaultDrawable()
	{
		currentKey = null;
		imageView.restoreDefaultDrawable();
		if(progress != null) progress.setVisibility(GONE);
	}

    //download image to FS cache if necessary and decode them
    public void loadUrl(String url)
    {
        if (url == null) restoreDefaultDrawable();
		else load(new HttpBitmapLoadWorker(getContext(), url));
    }
    //decode image from fs
    public void loadFile(String path)
    {
        if(path == null) restoreDefaultDrawable();
        else load(new FSBitmapLoadWorker(getContext(), path));
    }

    //decode image from self resources
    public void loadResource(int resourceID)
    {
        load(new ResourceBitmapLoadWorker(getContext(), getContext().getPackageName(), resourceID));
    }

    //decode image from other package resources
    public void loadResource(String packageName, int resourceID)
    {
        if(packageName == null) return;
        load(new ResourceBitmapLoadWorker(getContext(), packageName, resourceID));
    }

    //decode thumbnail from local MediaStore
    public void loadMediaStoreThumbnail(long thumbID, boolean isVideoThumbnail)
    {
        load(new MediaThumbLoadWorker(getContext(), thumbID, isVideoThumbnail));
    }

    private String currentKey = null;
    protected void load(AbstractBitmapLoadWorker worker)
    {
        if(imageView == null) return;
        if(currentKey != null && currentKey.equals(worker.getKey())) return;

       // Log.d(TAG, "load "+imageView.hashCode()+" "+worker.getKey());

        worker.setonLoadingHandler(this);


        currentKey = worker.getKey();
        BitmapCacheContainer container = BitmapMemCache.getInstance().get(worker.getKey());
        if(container != null && container.isRecycled() == false)
        {
            imageView.setImageBitmap(container);
           // Log.d(TAG, "setImageBitmap from cache "+imageView.hashCode()+" "+worker.getKey());
            if(progress != null) progress.setVisibility(GONE);
        } else {
            if(progress != null) progress.setVisibility(VISIBLE);
           // Log.d(TAG, "restoreDefaultDrawable"+imageView.hashCode()+" "+worker.getKey());
            imageView.restoreDefaultDrawable();
            worker.load(imageView.getMaxImageSize());
        }
        //setTag(TAG_KEY, worker.getKey());

    }

    @Override
    public boolean isCanceled(AbstractBitmapLoadWorker worker) {
        return (currentKey == null || currentKey.equals(worker.getKey()) == false);
    }

    @Override
    public void onLoadComplete(AbstractBitmapLoadWorker worker, BitmapCacheContainer container)
    {
        if(container == null || currentKey == null || currentKey.equals(worker.getKey()) == false)
        {
            //don't load wrong bitmap
            return;
        }
        if(imageView != null)
        {
           // Log.d(TAG, "setImageBitmap onLoadComplete "+imageView.hashCode()+" "+worker.getKey());
            imageView.setImageBitmap(container);
            requestLayout();
        }
        if(progress != null) progress.setVisibility(GONE);
    }

    @Override
    public void addView(View child) {
        checkNewView(child);
        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        checkNewView(child);
        super.addView(child, index);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        checkNewView(child);
        super.addView(child, index, params);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        checkNewView(child);
        super.addView(child, params);
    }

    @Override
    public void addView(View child, int width, int height) {
        checkNewView(child);
        super.addView(child, width, height);
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params) {
        checkNewView(child);
        return super.addViewInLayout(child, index, params);
    }

    @Override
    protected boolean addViewInLayout(View child, int index, ViewGroup.LayoutParams params, boolean preventRequestLayout) {
        checkNewView(child);
        return super.addViewInLayout(child, index, params, preventRequestLayout);
    }
}
