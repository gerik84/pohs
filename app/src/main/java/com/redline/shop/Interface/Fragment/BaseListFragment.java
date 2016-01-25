package com.redline.shop.Interface.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.redline.shop.Interface.Adapters.AdapterFactory;
import com.redline.shop.Interface.Common.PathItemHolder;
import com.redline.shop.R;

import java.util.HashSet;

/**
 * Created by Pavel on 19.01.2016.
 */
public abstract class BaseListFragment extends BaseFragment implements AdapterFactory.Listener {

    public enum MODE {
        CATALOG, COMMENTS, INFO
    }

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        Holder h = _h();
        h.adapterFactory = AdapterFactory.createAdapterFactory(getActivity(), this, h.pathItemHolder);
        return root;
    }

    protected Holder _h() {

        if (m_holder == null) m_holder = new Holder();
        return m_holder;
    }

    private Holder m_holder = null;

    public interface ListenerFactory {

        Listener getListener(FragmentCatalogPageBase p);
    }

    public interface Listener {

        void onFavoritesClicked();
        void onMapLinkClicked(PathItem pathItem, HashSet<Integer> areasLinked);
        void onItemSelected(PathItem pathItem);
    }

    protected static class Holder {

        protected PathItemHolder pathItemHolder = new PathItemHolder();
        protected AdapterFactory adapterFactory;
        protected ListenerFactory factory;

        protected boolean bSkipLoading;
        protected View root;

        protected int position = -1;

        protected RefreshMenuHandler refreshMenuHandler;
        protected Menu menu;

        FavoritesProcessor favoritesProcessor;
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
