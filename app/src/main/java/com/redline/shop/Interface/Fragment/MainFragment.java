package com.redline.shop.Interface.Fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import com.redline.shop.Interface.Adapters.BannerAdapter;
import com.redline.shop.Interface.Adapters.CategoryAdapter;
import com.redline.shop.Interface.Adapters.ComplexAdapter;
import com.redline.shop.R;

public class MainFragment extends BaseListFragment {

    @Override
    protected ListAdapter setAdapters() {
        return new ComplexAdapter()
                .add(new BannerAdapter(getActivity()))
                .add(new CategoryAdapter(getActivity(), null, null).setResourceId(R.layout.cell_menu_category));
    }

    @Override
    protected AdapterView.OnItemClickListener setListener() {
        return null;
    }
}
