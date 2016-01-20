package com.redline.shop.Interface.Adapters;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListAdapter;

import java.util.List;

/**
 * Created by Pavel on 19.01.2016.
 */
public class ComplexAdapter extends android.widget.BaseAdapter {

    private List<Adapter> mSource;

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return  mSource == null ? 0 : mSource.size();
    }

    @Override
    public int getCount() {
        return mSource == null ? 0 : mSource.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
