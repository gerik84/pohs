package com.redline.shop.Interface.Fragment;

import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.redline.shop.Interface.Adapters.AdapterFactory;

public class MainFragment extends BaseListFragment {

    @Override
    protected ListAdapter setAdapters() {

        return AdapterFactory.getAdapterInstance(getActivity(), AdapterFactory.TYPE.MAIN, null);

    }

    @Override
    protected AdapterView.OnItemClickListener setListener() {
        return null;
    }
}
