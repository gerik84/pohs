package com.redline.shop.Interface.Fragment;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.redline.shop.Interface.Adapters.AdapterFactory;
import com.redline.shop.Interface.Adapters.CategoryAdapter;
import com.redline.shop.R;

/**
 * Created by Pavel on 19.01.2016.
 */
public abstract class BaseListFragment extends BaseFragment implements AdapterFactory.Listener {

    @Override
    public void onLoadComplete() {
//        View root = getView();
//        if (root == null)
//            return;
//
//        View progress = root.findViewById(R.id.fr_loading);
//        if (progress != null)
//            progress.setVisibility(View.GONE);
    }

    @Override
    protected int setResourceId() {
        return R.layout.frg_list;
    }

    private   ViewPager                        m_pager;

    @Override
    protected void initView(View root) {

        if (root == null)
            return;

        ListAdapter a = AdapterFactory.getAdapterInstance(getActivity(), AdapterFactory.MODE.CATALOG, this);

      
        ListView listView = (ListView) root.findViewById(R.id.lv_list);
        if (listView != null && a != null) {
            View progress = root.findViewById(R.id.fr_loading);
            if (progress != null) {
                //progress.setVisibility(View.VISIBLE);
                listView.setEmptyView(progress);
            }
            listView.setAdapter(a);
            listView.setOnItemClickListener(setListener());
        }

    }

    protected abstract ListAdapter setAdapters();

    protected abstract AdapterView.OnItemClickListener setListener();

}
