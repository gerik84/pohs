package com.asdevel.examples;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.asdevel.cache.bitmap.CachedImageHolder;
import com.asdevel.cache.R;

import java.util.ArrayList;

/**
 * User: Andrew Rakhmatulin
 * Date: 07.11.13
 * Time: 16:01
 */
public class SimpleHttpImagesAdapter extends BaseAdapter
{
    ArrayList<String> items;
    Context context;
    Drawable defaultDrawable = null;
    public SimpleHttpImagesAdapter(Context ctx, ArrayList<String> items, int defaultDrawableResourceID)
    {
        this.items = items;
        context = ctx;
        defaultDrawable = context.getResources().getDrawable(defaultDrawableResourceID);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CachedImageHolder v;
        if(convertView == null || !(convertView instanceof CachedImageHolder))
        {
            v = (CachedImageHolder) View.inflate(context, R.layout.cachedimage, null);
            v.setDefaultDrawable(defaultDrawable);
        } else {
            v = (CachedImageHolder) convertView;
        }
        v.loadUrl(items.get(position));
        return v;
    }
}
