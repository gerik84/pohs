package com.redline.shop.Interface.Adapters;

import android.support.v4.app.FragmentActivity;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.asdevel.cache.jsondb.AbstractCachedJsonArrayAdapter;
import com.redline.shop.R;
import com.redline.shop.Utils.Tools;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Pavel on 25.01.2016.
 */
public class AdapterFactory {

    public enum TYPE {
        MAIN, BUTTONS, INFO_TEXT, INFO_FULL, CATALOG, COMMENTS, COMPLEX
    }

    public interface Listener {
        void onLoadComplete();
    }

    public static BaseAdapter getAdapterInstance(FragmentActivity activity, TYPE mode, final Listener listener) {

        BaseAdapter adapter = null;
        switch (mode) {
            case MAIN: {
                List<Adapter> adapters = new ArrayList<>();
                adapters.add(new BannerAdapter(activity));
                adapters.add(new SMenuAdapter(activity));
                adapters.add(new NewGoodsAdapter(activity));
                adapters.add(new CategoryAdapter(activity, null, null).setResourceId(R.layout.cell_menu_category));

                adapter = new ComplexAdapter(null);
                ((ComplexAdapter)adapter).reset(adapters);
            }
        }

        return adapter;
    }
}
