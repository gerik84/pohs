package com.redline.shop.Interface.Fragment;

import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.redline.shop.R;

/**
 * Created by Redline on 09.02.2016.
 */
public class BaseListCardFragment extends BaseListFragment {

    @Override
    protected int setResourceId() {
        return R.layout.frg_list_card;
    }

    @Override
    protected ListAdapter setAdapters() {
        return null;
    }

    @Override
    protected AdapterView.OnItemClickListener setListener() {
        return null;
    }
}
