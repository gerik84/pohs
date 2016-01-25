package com.redline.shop.Interface.Adapters;

import android.support.v4.app.FragmentActivity;
import android.widget.Adapter;
import android.widget.ListAdapter;

import com.asdevel.cache.jsondb.AbstractCachedJsonArrayAdapter;
import com.redline.shop.R;
import com.redline.shop.Utils.Tools;


/**
 * Created by Pavel on 25.01.2016.
 */
public class AdapterFactory {

    public enum MODE {
        CATALOG
    }

    public interface Listener {
        void onLoadComplete();
    }

    public static ListAdapter getAdapterInstance(FragmentActivity activity, MODE mode, final Listener listener) {

        ListAdapter adapter = null;
        switch (mode) {
            case CATALOG: {
                adapter = new CategoryAdapter(activity, mode.toString(), null);
                ((CategoryAdapter) adapter).setResourceId(R.layout.cell_menu_category);
//                ((CategoryAdapter) adapter).setRetrieverHandler(new AbstractCachedJsonArrayAdapter.IRetrieverHandler() {
//                    @Override
//                    public void onRetrieverStarted(boolean started, Adapter a) {
//
//                        if (started || listener == null) return;
//                        Tools.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                listener.onLoadComplete();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onRetrieverError(Exception e, Adapter a) {
//
//                    }
//                });
            }
        }

        return adapter;
    }
}
