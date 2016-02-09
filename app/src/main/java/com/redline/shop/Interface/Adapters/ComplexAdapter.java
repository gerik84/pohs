package com.redline.shop.Interface.Adapters;

import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.asdevel.cache.jsondb.Refreshable;
import com.redline.shop.Utils.Preconditions;
import com.redline.shop.Utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ComplexAdapter extends BaseAdapter implements Refreshable {
    private final Integer m_maxViewTypes;
    private DataSetObserver m_observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            notifyDataSetChanged();
            super.onChanged();
        }

        @Override
        public void onInvalidated() {
            notifyDataSetInvalidated();
            super.onInvalidated();
        }
    };

    private List<Adapter> m_source;
    private HashMap<Integer, Integer> m_knownTypes = new HashMap<>();

    public ComplexAdapter(Integer maxViewTypes) {
        m_maxViewTypes = maxViewTypes == null ? AdapterFactory.TYPE.values().length : maxViewTypes;
        Preconditions.checkState(m_maxViewTypes > 0);
    }

    public void reset(List<Adapter> src) {
        if (m_source != null) for (Adapter a : m_source) a.unregisterDataSetObserver(m_observer);

        m_source = src == null ? null : new ArrayList<Adapter>();
        if (m_source != null) for (Adapter a : src) {

            _addAdapter(a);
        }

        notifyDataSetChanged();
    }

    private void _addAdapter(Adapter a) {

        if (a instanceof ComplexAdapter) {
            List<Adapter> sources = ((ComplexAdapter) a).m_source;
            if (sources != null) for (Adapter aNested : sources) {
                _addAdapter(aNested);
            }
            return;
        }

        m_source.add(a);
        a.registerDataSetObserver(m_observer);
        int hash = a.hashCode();
        if (m_knownTypes.containsKey(hash)) return;
        m_knownTypes.put(hash, m_knownTypes.size());
        Preconditions.checkState(m_maxViewTypes >= m_knownTypes.size());
    }

    public int getAdaptersCount() {
        return m_source == null ? 0 : m_source.size();
    }

    private Pair<Integer, Adapter> _getAdapter(int position) {
        Pair<Integer, Integer> ad = _getAdapterPosition(position);
        return ad == null ? null : new Pair<>(ad.first, m_source.get(ad.second));
    }

    private Pair<Integer, Integer> _getAdapterPosition(int position) {
        if (m_source == null) return null;
        int start = 0;
        for (int i = 0; i < m_source.size(); i++) {
            Adapter a = m_source.get(i);
            int cn = a.getCount();
            if (position >= start && position < start + cn) return new Pair<>(position - start, i);
            start += cn;
        }
        Tools.log("adapter not found");
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return m_maxViewTypes;
    }

    @Override
    public int getItemViewType(int position) {
        Pair<Integer, Adapter> ad = _getAdapter(position);
        Preconditions.checkState(ad == null || m_knownTypes.containsKey(ad.second.hashCode()));
        return ad == null ? 0 : m_knownTypes.get(ad.second.hashCode());
    }

    @Override
    public int getCount() {
        if (m_source == null) return 0;
        int cn = 0;
        for (Adapter adapter : m_source) cn += adapter.getCount();
        return cn;
    }

    @Override
    public Object getItem(int position) {
        Pair<Integer, Adapter> ad = _getAdapter(position);
        return ad == null ? null : ad.second.getItem(ad.first);
    }

    @Override
    public long getItemId(int position) {
        Pair<Integer, Adapter> ad = _getAdapter(position);
        return ad == null ? 0 : ad.second.getItemId(ad.first);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Pair<Integer, Adapter> ad = _getAdapter(position);
        if (ad == null) throw new IllegalStateException("ad == null");
        return ad.second.getView(ad.first, convertView, parent);
    }

    @Override
    public void refresh() {
        if (m_source != null) for (Adapter a : m_source) {
            if (a instanceof Refreshable) ((Refreshable) a).refresh();
        }
    }
}
