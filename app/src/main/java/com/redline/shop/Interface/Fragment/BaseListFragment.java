package com.redline.shop.Interface.Fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.redline.shop.R;

/**
 * Created by Pavel on 19.01.2016.
 */
public abstract class BaseListFragment extends BaseFragment {

    @Override
    protected int setResourceId() {
        return R.layout.frg_list;
    }

    @Override
    protected void initView(View root) {

        if (root == null)
            return;

        ListView listView = (ListView) root.findViewById(R.id.lv_list);
        if (listView != null) {
            listView.setAdapter(setAdapters());
            listView.setOnItemClickListener(setListener());
        }

    }

    protected ListView getListView() {
        View root = getView();
        if (root == null)
            return null;

        return (ListView) root.findViewById(R.id.lv_list);

    }

    protected abstract ListAdapter setAdapters();

    protected abstract AdapterView.OnItemClickListener setListener();
}
