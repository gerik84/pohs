package com.redline.shop.Interface.Adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.asdevel.cache.jsondb.AbstractCachedJsonArrayAdapter;
import com.asdevel.cache.jsondb.JsonCacheDbHelper;
import com.redline.shop.BuildConfig;
import com.redline.shop.Utils.Tools;

import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.HashMap;

public abstract class ShopAuthorizationAbstractAdapter extends AbstractCachedJsonArrayAdapter {

    protected Integer mResourceId = null;
    protected Context mContext;

    public ShopAuthorizationAbstractAdapter(Context ctx, String itemIDKey, IRetrieverHandler handler) {
        super(ctx, itemIDKey, null, handler, BuildConfig.DEBUG);
        mContext = ctx;
        setUrl(setRequestUrl(), false);
        Tools.log(setRequestUrl());
    }

    public abstract String setRequestUrl();

    protected SQLiteDatabase getDB() {
       return JsonCacheDbHelper.getDataBaseForUrl(mContext, setRequestUrl());
    }

    protected void putItem(String id_field, HashMap<String, String> values) {
        String id = values.get(id_field);
        JsonCacheDbHelper.putItem(getDB(), id, values);
    }


    @Override
    public void updateRequestAuthentication(HttpURLConnection connection) {
        connection.setRequestProperty("Authorization", Tools.getApiKey());
        try {
            connection.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }

    }

    public <T extends ShopAuthorizationAbstractAdapter> T setResourceId(Integer resource) {
        mResourceId = resource;
        return (T) this;
    }

    @Override
    public String getQueryOrderBy() {
        return super.getQueryOrderBy();
    }

    @Override
    public String[] getOrderByField() {
        return new String[] {"category_id"};
    }

    @Override
    public String getQueryWhere() {
        return "seo_path = '' and status='A'";
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mResourceId == null)
            return null;

        if (convertView == null)
            convertView = LayoutInflater.from(mContext).inflate(mResourceId, parent, false);

        if (m_cursor != null)
            m_cursor.moveToPosition(position);

        return convertView;
    }
}
