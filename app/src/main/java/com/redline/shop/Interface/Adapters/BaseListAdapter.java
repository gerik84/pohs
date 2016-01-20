package com.redline.shop.Interface.Adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.JsonReader;
import android.view.View;
import android.view.ViewGroup;

import com.asdevel.cache.jsondb.AbstractCachedJsonArrayAdapter;
import com.asdevel.cache.jsondb.CachedJsonArray;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Created by Pavel on 19.01.2016.
 */
public class BaseListAdapter extends AbstractCachedJsonArrayAdapter {


    public BaseListAdapter(Context ctx, String itemIDKey, String url, IRetrieverHandler handler, boolean bDebugMode) {
        super(ctx, itemIDKey, url, handler, bDebugMode);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean handlePages(CachedJsonArray.PageHandle urlInfo) {
        return false;
    }

    @Override
    public void updateRequestAuthentication(HttpURLConnection connection) {

    }

    @Override
    public boolean onReadJsonField(String field, JsonReader reader) {
        return super.onReadJsonField(field, reader);
    }

    @Override
    public boolean parseJson(SQLiteDatabase db, InputStream in) throws IOException {
        return super.parseJson(db, in);
    }


}
