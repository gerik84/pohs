package com.redline.shop.Interface.Adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

/**
 * Created by Redline on 05.02.2016.
 */
public abstract class BaseStaticAdapter implements ListAdapter {
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    protected Context m_context;

    public BaseStaticAdapter(Context ctx) {
        m_context = ctx;
    }

    protected abstract int setResourceId();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (m_context == null)
            throw new NullPointerException();
        if (convertView == null)
            convertView= LayoutInflater.from(m_context).inflate(setResourceId(), null);

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
