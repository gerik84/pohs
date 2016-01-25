package com.redline.shop.Interface.Adapters;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.widget.Adapter;

import com.asdevel.cache.jsondb.AbstractCachedJsonArrayAdapter;
import com.redline.shop.ClassProvider;
import com.redline.shop.Interface.Common.PathItemHolder;
import com.redline.shop.Interface.Fragment.BaseListFragment;
import com.redline.shop.Utils.Preconditions;
import com.redline.shop.Utils.Tools;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class AdapterFactory {

    public enum TYPE {
        INFO_PHOTO, BUTTONS, INFO_TEXT, INFO_FULL, CATALOG, COMMENTS, COMPLEX
    }

    public interface Listener {

        void onDataLoaded(TYPE t, Adapter a);
        void onModeRequested(BaseListFragment.MODE catalog);
        void onAdapterCreated(String strType, Adapter a);
    }

    protected WeakReference<Listener> listener = new WeakReference<Listener>(null);
    final HashMap<String, Adapter> cache = new HashMap<>();
    protected PathItemHolder pathItemHolder;



    public Adapter getAdapterInstance(final TYPE t) {

        Adapter a = getCachedAdapter(t.toString());

        if (a != null) {
            return a;
        }

        switch (t) {
            case CATALOG: {
                a = _createCatalog(null);
            }
            break;
            case COMMENTS: {
            }
            break;
            case INFO_FULL: {
            }
            break;
            case INFO_PHOTO: {
            }
            break;
            case INFO_TEXT: {
            }
            break;
            case BUTTONS: {
            }
            break;
            case COMPLEX: {
            }
            break;
            default:
                throw new IllegalStateException("unknown type " + t);
        }



        putCacheAdapter(t.toString(), a);
        return a;
    }

    public static AdapterFactory createAdapterFactory(FragmentActivity activity, Listener listener, PathItemHolder pathItemHolder) {

        AdapterFactory adapterFactory = null;
        try {
            adapterFactory = ClassProvider.getInstance().CLASS_CATALOG_ADAPTER_FACTORY.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        Preconditions.checkNotNull(adapterFactory);

        adapterFactory.initialize(activity, listener, pathItemHolder);
        return adapterFactory;
    }

    protected void initialize(FragmentActivity activity, Listener listener, PathItemHolder pathItemHolder) {

        this.activity = activity;
        this.pathItemHolder = pathItemHolder;
        this.listener = new WeakReference<>(listener);

//        Preconditions.checkNotNull(this.activity);
//        Preconditions.checkNotNull(this.pathItemHolder);
//        Preconditions.checkNotNull(this.listener.get());
    }

    protected FragmentActivity activity;

    @NonNull
    protected CatalogListingAdapter _createCatalog(String tag) {

        AbstractCachedJsonArrayAdapter.IRetrieverHandler retrieverHandler = getRetrieverHandler(TYPE.CATALOG);

        return new CatalogListingAdapter(activity, tag, retrieverHandler, false);
    }

    @Nullable
    public Adapter getCachedAdapter(String str) {

        return cache.get(str);
    }

    protected void putCacheAdapter(String str, Adapter a) {

        if (!cache.containsKey(str) && listener != null && listener.get() != null) {
            this.listener.get().onAdapterCreated(str, a);
        }

        cache.put(str, a);
    }


    @NonNull
    protected AbstractCachedJsonArrayAdapter.IRetrieverHandler getRetrieverHandler(final TYPE type) {

        return new AbstractCachedJsonArrayAdapter.IRetrieverHandler() {
            @Override
            public void onRetrieverStarted(boolean started, final Adapter a) {

                if (started) return;
                Tools.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Listener l = AdapterFactory.this.listener.get();
                        if (l != null) {
                            l.onDataLoaded(type, a);
                        }
                    }
                });
            }

            @Override
            public void onRetrieverError(Exception e, Adapter a) {

            }
        };
    }
}
