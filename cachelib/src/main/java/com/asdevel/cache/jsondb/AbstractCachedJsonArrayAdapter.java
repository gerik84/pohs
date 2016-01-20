package com.asdevel.cache.jsondb;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.util.JsonReader;
import android.util.Log;
import android.widget.Adapter;
import android.widget.BaseAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;


/**
 * User: Andrew Rakhmatulin
 * Date: 08.11.13
 * Time: 17:13
 */
public abstract class AbstractCachedJsonArrayAdapter  extends BaseAdapter implements CachedJsonArray.IAdapter, Refreshable, JsonCacheDbHelper.ReadInterceptor {
	private DataSetObserver m_observer;
	protected String m_lastUrl;

	//protected int maxPagesCount = 100;

	public interface IRetrieverHandler
	{
		void onRetrieverStarted(final boolean started, Adapter a);
		void onRetrieverError(Exception e, Adapter a);
	}

	@Override
	public void refresh()
	{
		setUrl(m_lastUrl, true);
	}

	final protected String itemIdKey;
	private boolean mDebugMode;
	protected Cursor m_cursor = null;
	final protected Context context;

	private IRetrieverHandler retriverHandler = null;

	// protected JsonRetriever retriever = null;
	protected CachedJsonArray retriever = null;

	//protected String url;

	public AbstractCachedJsonArrayAdapter(Context ctx, String itemIDKey, String url, IRetrieverHandler handler, boolean bDebugMode)
	{
		this.context = ctx;
		this.itemIdKey = itemIDKey;
		mDebugMode = bDebugMode;
		retriever = new CachedJsonArray(ctx, this, itemIDKey, url);

		// todo It is bad approach to start anything at Constructor. Should be implemented function like start() instead.

		setUrl(url, false);
		this.retriverHandler = handler;
	}

	public void setUrl(String new_url, boolean bForce)
	{
		if (mDebugMode) Log.e("cachelib", getClass().getName() + " new_url[ " + new_url + "&skipAccess=1 ] ");

		retriever.setUrl(new_url, bForce);
		m_lastUrl = new_url;
        /*
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
        */
    }

    @Override
    public int getCount() {
        if(m_cursor == null) return 0;
        return m_cursor.getCount();
    }

    @Override
    public Object getItem(int position) {

        if(m_cursor != null && m_cursor.moveToPosition(position))
        {
			int columnIndex = m_cursor.getColumnIndex(itemIdKey);
			if (columnIndex >= 0) {
				return m_cursor.getString(columnIndex);
			}
        }

        return null;
    }


    public synchronized void setRetrieverHandler(IRetrieverHandler handler)
    {
        retriverHandler = handler;
    }
    public int getPositionForID(String itemID)
    {
        if(itemID != null && m_cursor != null && m_cursor.moveToFirst())
        {
            int position = 0;
            int idx = m_cursor.getColumnIndex(JsonCacheDbHelper.ITEM_ID);
            do {
                String id = m_cursor.getString(idx);
                if(itemID.equals(id))
                {
                    return position;
                }
                position++;
            } while (m_cursor.moveToNext());
        }
        return -1;
    }

    public String getIdForPosition(int position)
    {
        if(m_cursor != null && m_cursor.moveToPosition(position))
        {
            int idx = m_cursor.getColumnIndex(JsonCacheDbHelper.ITEM_ID);
            return m_cursor.getString(idx);
        }

        return null;
    }

    public void installNewCursor(final Cursor new_cursor, final String curUrl)
    {
        if(retriever == null) return;
        String url = retriever.getUrl();
        if(new_cursor == null || retriever == null)
        {
            //error occurred



        } else if((url != null && url.equals(curUrl)) && m_cursor != new_cursor){
            if(m_cursor != null) m_cursor.close();
            //Log.e("CachedImageHolder", "installNewCursor for " +new_cursor.getCount()+" "+ url);
            m_cursor = new_cursor;
            notifyDataSetChanged();
        }
    }



    public String getPropertyNameValueKey()
    {
        return null;
    }


    public boolean parseJson(SQLiteDatabase db, InputStream in) throws IOException {
        if(retriever != null)
        {
            return retriever.parseJsonDefault(db, in, this);
        }
        return false;
        /*
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        return JsonCacheDbHelper.saveJsonArray(db, itemIdKey, reader, getPropertyNameValueKey());
        */
    }

	@Override
	public boolean onReadJsonField(String field, JsonReader reader) {

		return false;
	}

	@Override
    public synchronized void onError(Exception e) {
        if(retriverHandler != null)
        {
            retriverHandler.onRetrieverError(e, this);
        }
    }

    //add paging parameters to query if needed
    //protected abstract boolean handlePages(CachedJsonArray.PageHandle urlInfo);

    public String getQueryOrderBy()
    {
        return null;
    }

    @Override
    public String[] getOrderByField() {
        return null;
    }

    public String getQueryWhere()
    {
        return null;
    }

    private volatile boolean needCachedVersion = true;
    public void enableCachedVersion(boolean enable)
    {
        needCachedVersion = enable;
    }
    @Override
    public boolean needCachedVersion() {
        return needCachedVersion;
    }

    public synchronized void onRetrieverStarted(final boolean started)
    {
        if(retriverHandler != null)
        {
            retriverHandler.onRetrieverStarted(started, this);
        }
    } // will be called at worker threads

	HashSet<DataSetObserver> m_setObservers;

	@Override
	public void registerDataSetObserver(DataSetObserver observer)
	{
		if (m_setObservers == null)
		{
			m_setObservers = new HashSet<DataSetObserver>();
			if (this.m_observer == null) this.m_observer = new DataSetObserver()
			{
				@Override
				public void onChanged()
				{
					if (m_setObservers != null) for (DataSetObserver o : m_setObservers)
					{
						o.onChanged();
					}
				}

				@Override
				public void onInvalidated()
				{
					if (m_setObservers != null) for (DataSetObserver o : m_setObservers)
					{
						o.onInvalidated();
					}
				}
			};

			super.registerDataSetObserver(this.m_observer);
		}

		m_setObservers.add(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer)
	{
		if (m_setObservers == null) return;
		m_setObservers.remove(observer);
		if (!m_setObservers.isEmpty()) return;
		super.unregisterDataSetObserver(this.m_observer);
		m_setObservers = null;
	}

	/*
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
			if (isCanceled()) return;

			onRetrieverStarted(true);

			HttpURLConnection connect = null;
			SQLiteDatabase db = JsonCacheDbHelper.getDataBaseForUrl(context, this.url);
			Cursor cache_cursor = JsonCacheDbHelper.getItems(db, getQueryWhere(), getQueryOrderBy(), isQueryOrderByDesc());
			boolean needIFModified = false;
			if (cache_cursor != null && cache_cursor.moveToFirst())
			{
				installNewCursor(cache_cursor, this.url);
				needIFModified = true;
			}

			PageHandle info = new PageHandle(this.url);
			handlePages(info);
			int pageCount = 0;
			JsonCacheDbHelper.markForDelete(db, null);
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
					//connect.setDoOutput(true);
					connect.connect();
					int nResponseCode = connect.getResponseCode();

					if (nResponseCode == HttpURLConnection.HTTP_OK)
					{
						if (parseJson(db, connect.getInputStream()))
						{
							cache_cursor = JsonCacheDbHelper.getItems(db, getQueryWhere(), getQueryOrderBy(), isQueryOrderByDesc());
							installNewCursor(cache_cursor, this.url);
						}
						CacheUtils.saveModifiedSinceDate(context, connect);
					}
					else if (nResponseCode == HttpURLConnection.HTTP_NOT_MODIFIED)
					{
						break;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				pageCount++;
			} while (isCanceled() == false && pageCount < maxPagesCount && handlePages(info));

			JsonCacheDbHelper.deleteMarked(db, null);

			onRetrieverStarted(false);
		}
	}
    */
}
