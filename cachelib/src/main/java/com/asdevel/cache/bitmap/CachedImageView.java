package com.asdevel.cache.bitmap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.asdevel.cache.R;

/**
 * User: Andrew Rakhmatulin
 * Date: 07.11.13
 * Time: 9:38
 */
public class CachedImageView extends ImageView implements ICachedImageView {

    static public int minSize = 500;

    BitmapCacheContainer mContainer = null;
    Drawable defaultDrawable;

    public CachedImageView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CachedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CachedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle)
    {
		TypedArray arr =  context.obtainStyledAttributes(attrs, R.styleable.CachedImageView);
		Drawable dr = arr.getDrawable(R.styleable.CachedImageView_defaultImage);
		arr.recycle();
		if (dr != null) {defaultDrawable = dr;}
    }


    @Override
    public void setImageBitmap(Bitmap bm)
    {
        //to set image use setImageBitmap(BitmapCacheContainer container) instead
        super.setImageBitmap(bm);
    }


    public int getMaxImageSize()
    {
        return Math.max(minSize, Math.max(getWidth(), getHeight()));
    }

    @Override
    public void setImageDrawable(Drawable drawable)
    {
        //to set real image, use setImageBitmap(BitmapCacheContainer container) instead
        //defaultDrawable = new SoftReference<Drawable>(drawable);
        super.setImageDrawable(drawable);
    }

    public void setDefaultDrawable(Drawable drawable)
    {
        defaultDrawable = drawable;
    }


    public void restoreDefaultDrawable()
    {
        //if(defaultDrawable == null) return;
        super.setImageDrawable(defaultDrawable);
//        Drawable d = defaultDrawable.get();
//        if(d != null)
//        {
//            super.setImageDrawable(d);
//        }

    }

    public void setImageBitmap(BitmapCacheContainer container)
    {
        if(container.isRecycled())
        {
            restoreDefaultDrawable();
            return;
        }
        Bitmap bm = container.get();
        if(bm == null)
        {
            restoreDefaultDrawable();
            return;
        }
        if(mContainer != null)
        {
            mContainer.removeReference();
        }
        try {
            container.addReference();
            mContainer = container;
            super.setImageDrawable(new BitmapDrawable(getContext().getResources(), bm));
            //super.setImageBitmap(bm);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
    }
}
