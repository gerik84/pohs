package com.asdevel.cache.jsondb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.JsonReader;

import com.asdevel.cache.CacheUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * User: Andrew Rakhmatulin
 * Date: 12.06.14
 * Time: 15:56
 */
public class CachedJsonArray {


    public class PageHandle {
        final public String baseUrl;
        public String currentUrl = null;
        public Object tag = null;
        public PageHandle(String baseUrl)
        {
            this.baseUrl = baseUrl;
        }
    }

    public interface IAdapter {
        String getPropertyNameValueKey();
        void installNewCursor(final Cursor new_cursor, final String curUrl);
        boolean handlePages(PageHandle urlInfo);
        String[] getOrderByField();
        String getQueryOrderBy();
        String getQueryWhere();
        void onRetrieverStarted(final boolean started); // will be called at worker threads
        void onError(Exception e);
        boolean parseJson(SQLiteDatabase db, InputStream in) throws IOException;
        void updateRequestAuthentication(HttpURLConnection connection);
        boolean needCachedVersion();

    }


    protected int maxPagesCount = 100;

    final protected String itemIdKey;
    protected Cursor cursor = null;
    final protected Context context;

    protected JsonRetriever retriever = null;

    final IAdapter adapter;

    protected String url;

    public CachedJsonArray(Context ctx, IAdapter adapter, String itemIDKey, String url)
    {
        this.adapter = adapter;
        this.context = ctx;
        this.itemIdKey = itemIDKey;
        setUrl(url, false);
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(final String new_url, final boolean bForce)
    {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(new_url == null) return;
                if(bForce || url == null || new_url.equals(url) == false)
                {
                    if(retriever != null)
                    {
                        retriever.cancel();
                    }
                    url = new_url;
                    retriever = new JsonRetriever(url);
                    new Thread(retriever).start();
                }
            }
        });

    }

    protected Handler mHandler = new Handler();

    private void installNewCursor(final Cursor new_cursor, final String curUrl)
    {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(adapter != null)
                {
                    adapter.installNewCursor(new_cursor, curUrl);
                }

                /*moved in adapter
                if(new_cursor == null)
                {
                    //error occurred

                } else if(url.equals(curUrl) && m_cursor != new_cursor){
                    if(m_cursor != null) m_cursor.close();
                    m_cursor = new_cursor;
                    notifyDataSetChanged();
                }
                */
            }
        });
    }





    public boolean parseJsonDefault(SQLiteDatabase db, InputStream in, JsonCacheDbHelper.ReadInterceptor interceptor) throws IOException {
        if(adapter == null) return false;
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        return JsonCacheDbHelper.saveJsonArray(db, itemIdKey, reader, adapter.getPropertyNameValueKey(), interceptor);
    }

    //add paging parameters to query if needed
    //protected abstract boolean handlePages(PageHandle urlInfo);

    /*
    protected String getQueryOrderBy()
    {
        return null;
    }
    protected boolean isQueryOrderByDesc()
    {
        return false;
    }

    protected String getQueryWhere()
    {
        return null;
    }


    public void onRetrieverStarted(final boolean started) {} // will be called at worker threads
    */

    private class JsonRetriever implements Runnable
    {
        final String url;

        public JsonRetriever(String url)
        {
            this.url = url;
        }

        boolean mIsCanceled = false;

        public synchronized void cancel()
        {
            mIsCanceled = true;
        }

        private synchronized boolean isCanceled() {return mIsCanceled;}


        @Override
        public void run()
        {
            if (isCanceled() || adapter == null) return;

            adapter.onRetrieverStarted(true);

            HttpURLConnection connect = null;
            SQLiteDatabase db = JsonCacheDbHelper.getDataBaseForUrl(context, this.url);
            //Log.e("CachedImageHolder", "GOT DB "+db.hashCode()+ " for "+this.url);
            Cursor cache_cursor = JsonCacheDbHelper.getItems(db, adapter.getQueryWhere(), adapter.getOrderByField(), adapter.getQueryOrderBy());
            boolean needIFModified = false;
            if (cache_cursor != null && cache_cursor.moveToFirst())
            {
                if(adapter.needCachedVersion()) installNewCursor(cache_cursor, this.url);
                needIFModified = true;
            }


            PageHandle info = new PageHandle(this.url);
            adapter.handlePages(info);
            int pageCount = 0;
            JsonCacheDbHelper.markForDelete(db, null);
            int nResponseCode = 0;
            do
            {
                try
                {
                    URL url = new URL(info.currentUrl == null ? info.baseUrl : info.currentUrl);
                    connect = (HttpURLConnection) url.openConnection();
                    connect.setConnectTimeout(10000);
                    connect.setReadTimeout(10000);
                    if (needIFModified)
                    {
                        CacheUtils.addIfModifiedSinceDate(context, connect);
                        needIFModified = false;
                    }
                    adapter.updateRequestAuthentication(connect);
                    //connect.setDoOutput(true);
                    connect.connect();
                    nResponseCode = connect.getResponseCode();

                    if (nResponseCode == HttpURLConnection.HTTP_OK)
                    {
                        if (adapter.parseJson(db, connect.getInputStream()))
                        {
                            cache_cursor = JsonCacheDbHelper.getItems(db, adapter.getQueryWhere(), adapter.getOrderByField(), adapter.getQueryOrderBy());
                            installNewCursor(cache_cursor, this.url);
                        }
                        CacheUtils.saveModifiedSinceDate(context, connect);
                    }
                    else if (nResponseCode == HttpURLConnection.HTTP_NOT_MODIFIED
                            || nResponseCode == HttpURLConnection.HTTP_UNAUTHORIZED)
                    {
                        break;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    adapter.onError(e);
                    if (e instanceof IOException) {break;}

                }
                pageCount++;
            } while (isCanceled() == false && pageCount < maxPagesCount && adapter.handlePages(info));

            if(isCanceled() == false && nResponseCode == HttpURLConnection.HTTP_OK) JsonCacheDbHelper.deleteMarked(db, null);

            adapter.onRetrieverStarted(false);
        }
    }



}
