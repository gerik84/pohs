package com.redline.shop.Interface.Adapters;

import android.content.Context;

import com.asdevel.cache.jsondb.CachedJsonArray;

public class CatalogListingAdapter extends ShopAuthorizationAbstractAdapter {

    public CatalogListingAdapter(Context ctx, String itemIDKey, IRetrieverHandler handler, boolean bDebugMode) {
        super(ctx, itemIDKey, handler, bDebugMode);
    }

    @Override
    public String setRequestUrl() {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean handlePages(CachedJsonArray.PageHandle urlInfo) {
        return false;
    }
}
