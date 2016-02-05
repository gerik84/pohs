package com.redline.shop.Interface.Adapters;

import android.database.DataSetObserver;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ListAdapter;

import com.redline.shop.Utils.Tools;

import java.util.ArrayList;
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
        if (mSource == null) return 0;
        int cn = 0;
        for (Adapter adapter : mSource) cn += adapter.getCount();
        return cn;
    }

    @Override
    public Adapter getItem(int position) {
        return mSource == null ? null : mSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        Pair<Integer, Adapter> ad = _getAdapter(position);
        return ad == null ? 0 : ad.second.getItemId(ad.first);
    }

    private Pair<Integer, Adapter> _getAdapter(int position)
    {
        Pair<Integer, Integer> ad = _getAdapterPosition(position);
        return ad == null ? null : new Pair<>(ad.first, mSource.get(ad.second));
    }

    private Pair<Integer, Integer> _getAdapterPosition(int position)
    {
        if (mSource == null) return null;
        int start = 0;
        for (int i = 0; i < mSource.size(); i++)
        {
            Adapter a = mSource.get(i);
            int cn = a.getCount();
            if (position >= start && position < start + cn) return new Pair<>(position - start, i);
            start += cn;
        }
        Tools.log("adapter not found");
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Pair<Integer, Adapter> ad = _getAdapter(position);
        if (ad == null) throw new IllegalStateException("ad == null");
        return ad.second.getView(ad.first, convertView, parent);
    }

    public ComplexAdapter add(Adapter adapter) {
        if (adapter == null)
            throw new NullPointerException();
        if (mSource == null)
            mSource = new ArrayList<>();

        mSource.add(adapter);
        return this;
    }

}
