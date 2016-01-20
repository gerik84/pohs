package com.asdevel.cache.jsondb;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.TextUtils;
import android.util.JsonReader;
import com.asdevel.cache.CacheUtils;

import java.io.IOException;
import java.util.HashMap;

/**
 * User: Andrew Rakhmatulin
 * Date: 08.11.13
 * Time: 15:04
 */
public class JsonCacheDbHelper
{
    public static int DB_VERSION = 0;
    public static String sTableName = "json_cache";
    public static String sIdxName = "idxUniqueNameCatcher";
    public static final String ITEM_ID = "item_id";
    public static final String PROP_NAME = "name";
    public static final String PROP_VALUE = "value";
    public static final String MARK_FOR_DELETE = "delete_flag";
    public static final String[] sColumnList = new String[] {
            ITEM_ID,
            PROP_NAME,
            PROP_VALUE,
            MARK_FOR_DELETE
    };
    public static final String[] sColumnListTypes = new String[] {
            "TEXT ",//ITEM_ID,
            "TEXT",//PROP_NAME,
            "TEXT COLLATE UNICODE",//PROP_VALUE,
            "INTEGER"
    };

	private static Listener mListener;

	public interface Listener
	{
		void onException(Throwable e, String addon);
	}

	static public void setListener(Listener l)
	{
		mListener = l;
	}

	static public void  postException(Throwable e, String addon)
	{
		if (mListener == null) return;
		mListener.onException(e, addon);
	}


    private static String sInsertSql = null;
    public synchronized static String getInsertSql()
    {
        if(sInsertSql == null)
        {
            sInsertSql = "INSERT OR REPLACE INTO "+ sTableName + "("+ TextUtils.join(",", sColumnList) +")";
            String values = " VALUES (";
            for (int i = 0; i < sColumnList.length - 1 ; i++)
            {
                values+="?,";
            }
            values+="?)";
            sInsertSql += values;
        }
        return sInsertSql;
    }


    public static String getTableNameForUrl(String url)
    {
        return CacheUtils.makePathByURL(url);
    }

    private static HashMap<String, SQLiteDatabase> mDBPointerCache = new HashMap<String, SQLiteDatabase>();


    public static SQLiteDatabase getDataBaseForUrl(Context ctx, String url)
    {
		if (DB_VERSION == 0) throw new IllegalStateException("DB_VERSION should be installed previously with setDbVersion(...)");

        SQLiteDatabase db = null;
        String dbName =getTableNameForUrl(url);

        synchronized (mDBPointerCache)
        {
            if(mDBPointerCache.containsKey(dbName))
            {
                db = mDBPointerCache.get(dbName);
                //Log.e("CachedImageHolder", "db in cache "+db.hashCode()+ " for "+url);
                if(db != null && db.isOpen()) {
                    //Log.e("CachedImageHolder", "db in cache normal "+db.hashCode()+ " for "+url);
                    return db;
                } else {
                    //Log.e("CachedImageHolder", "db in cache expired "+db.hashCode()+ " for "+url);
                    mDBPointerCache.remove(dbName);
                }
            }
        }
        if(ctx == null) return null;

		try
		{
			if(Build.VERSION.SDK_INT >= 16)
			{
				db = ctx.openOrCreateDatabase(dbName, Context.MODE_ENABLE_WRITE_AHEAD_LOGGING, null);
			}
			else
			{
				db = ctx.openOrCreateDatabase(dbName, 0, null);
				db.enableWriteAheadLogging();
			}

		}
		catch (SQLException e)
		{
			e.printStackTrace();
			postException(e, dbName);
			return null;
		}


        int ver = db.getVersion();
        if(ver > 0 && DB_VERSION != ver)
        {
            //Log.e("CachedImageHolder", "db drop tables "+db.hashCode()+ " for "+url);
			try
			{
				db.execSQL("DROP TABLE "+sTableName);
				db.execSQL("DROP INDEX "+sIdxName);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				postException(e, null);
			}
		}
		db.setVersion(DB_VERSION);


        if(JsonCacheDbHelper.sColumnList.length != JsonCacheDbHelper.sColumnListTypes.length || JsonCacheDbHelper.sColumnList.length <= 0)
        {
            throw new IllegalArgumentException("Lengths of arrays with column list and types mismatch");
        }
        String sql = "CREATE TABLE IF NOT EXISTS "+ JsonCacheDbHelper.sTableName +" ( ";
        for (int i = 0; i < JsonCacheDbHelper.sColumnList.length - 1; i++)
        {
            sql += JsonCacheDbHelper.sColumnList[i] + " " + JsonCacheDbHelper.sColumnListTypes[i] + ", ";
        }
        sql += JsonCacheDbHelper.sColumnList[JsonCacheDbHelper.sColumnList.length-1]
                + " " + JsonCacheDbHelper.sColumnListTypes[JsonCacheDbHelper.sColumnListTypes.length-1]
                + ")";
        db.execSQL(sql);
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS "+sIdxName+" on "
                + JsonCacheDbHelper.sTableName + " ( "+ ITEM_ID+","+PROP_NAME+");");
        db.setLockingEnabled(false);
        mDBPointerCache.put(dbName, db);
        return db;
    }

    public static void markForDelete(SQLiteDatabase db, String where)
    {
        if(db != null)
        {
            db.execSQL("UPDATE " + sTableName + " SET " + MARK_FOR_DELETE + "=1" + (where == null ? "" : " WHERE " + where));
        }
    }

    public static void deleteMarked(SQLiteDatabase db, String where)
    {
        if(db != null)
        {
            db.delete(sTableName, MARK_FOR_DELETE+"=1" +(where == null?"":" AND "+where), null);
        }
    }

    public static Cursor getItems(SQLiteDatabase db, String where, String[] orderByProp, String orderByString)
    {
        if(db == null) return null;
        Cursor cursor = db.rawQuery("SELECT DISTINCT("+PROP_NAME+") FROM "+ sTableName, null);
        if(cursor == null) return null;
        String sql = null;
        String order = null; //" ORDER BY CAST( "+ITEM_ID+" AS INTEGER)";
        int orderByFieldsFound = 0;
        StringBuilder builder = null;
        if(cursor.moveToFirst())
        {
            builder = new StringBuilder();
            builder.append("SELECT ").append(ITEM_ID);
            //sql = "SELECT "+ITEM_ID;
            do {
                String prop = cursor.getString(0);
                if(prop == null || prop.length() < 1) continue;
                //sql += " , MAX(CASE WHEN NAME = '"+prop+"' THEN VALUE END ) AS  '"+prop+"' ";
                builder.append(" , MAX(CASE WHEN NAME = '")
                        .append(prop)
                        .append("' THEN VALUE END ) AS  '")
                        .append(prop)
                        .append("' ");
                //
                if(orderByProp != null)
                {
                    for (int i = 0; i < orderByProp.length; i++)
                    {
                        if(orderByProp[i].equals(prop))
                        {
                            orderByFieldsFound++;
                        }
                    }

                }
            } while (cursor.moveToNext());
            //sql += " FROM "+sTableName+ (where == null ?"":" "+where+" ") +" GROUP BY "+ITEM_ID+" " +order;
            builder.append(" FROM ").append(sTableName);

            builder.append(" GROUP BY ").append(ITEM_ID);

            if(where != null)
            {
                builder.append(" HAVING ").append(where).append(" ");
            }
            if(orderByProp != null && orderByFieldsFound == orderByProp.length && orderByString != null)
            {
                order = " ORDER BY "+orderByString;
            }

            if(order == null)
            {
                order = " ORDER BY CAST( "+ITEM_ID+" AS INTEGER)";
            }
            builder.append(" ").append(order);

            sql = builder.toString();
        }
        cursor.close();
        if(sql == null) return null;
        //Log.e("CachedImageHolder", "db sql "+db.hashCode()+ " "+sql );
        return db.rawQuery(sql, null);
    }

    public static Cursor getItems(SQLiteDatabase db)
    {
        return getItems(db, null, null, null);
    }

    /*
    public static boolean putItem(SQLiteDatabase db, String idName, JSONObject object)
    {
        if(db == null || idName == null || object == null) return false;
        String id = object.optString(idName);
        if(object.length() < 1 || id == null || id.length() < 1) return false;
        Iterator i = object.keys();
        String sql = getInsertSql();

        String values[] =  new String[sColumnList.length];
        values[0] = id;//ITEM_ID
        values[3] = "0";//MARK_FOR_DELETE
        while (i.hasNext())
        {
            String name = (String) i.next();
            if(name == null || name.length() < 1) continue;
            String val = object.optString(name);
            if(val == null || val.length() < 1) continue;
            values[1] = name;//PROP_NAME,
            values[2] = val;//PROP_VALUE,
            db.execSQL(sql, values);
        }
        return true;
    }
    */

    public static boolean putItem(SQLiteDatabase db, String idName, JsonReader reader, ReadInterceptor i)
    {
        return putItem(db, idName, reader, null, i);
    }

    public interface ReadInterceptor {
        boolean onReadJsonField(String field, JsonReader reader);
    }

    public interface PropertyParser {
        Boolean onPropertyParse(SQLiteDatabase db, JsonReader reader, HashMap<String, String> pairs);
    }

    public static boolean putItem(SQLiteDatabase db, String idName, JsonReader reader, String propItemName, ReadInterceptor readInterceptor, PropertyParser parserProperty) {
        if(db == null || idName == null) return false;
        HashMap<String, String> pairs = new HashMap<String, String>();
        String id = null;
        String prop = null;
        try {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();

                if (readInterceptor != null && readInterceptor.onReadJsonField(name, reader)) {
                    continue;
                }


                if(propItemName != null && name.equalsIgnoreCase(propItemName))
                {
                    if(parserProperty == null || !parserProperty.onPropertyParse(db, reader, pairs))
                        parseNVItem(db, reader, pairs);
                } else {
                    String val = null;
                    try {
                        val = reader.nextString();
                    } catch (IllegalStateException e ) {
                        reader.skipValue();
                        val = "";
                    }
                    if(name == null || val == null || name.length() < 1) continue;
                    if(name.equalsIgnoreCase(idName))
                    {
                        id = val;
                    } else {
                        pairs.put(name, val);
                    }
                }
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return insertIntoDB(db, id, pairs);
    }

    public static boolean putItem(SQLiteDatabase db, String idName, HashMap<String, String> pairs) {
        return insertIntoDB(db, idName, pairs);
    }


    public static boolean putItem(SQLiteDatabase db, String idName, JsonReader reader, String propItemName, ReadInterceptor readInterceptor)
    {
        return putItem(db, idName, reader, propItemName, readInterceptor, null);
    }

    public static void parseNVItem(SQLiteDatabase db, JsonReader reader, HashMap<String, String> pairs)
    {
        if(db == null) return;
        try {
            reader.beginArray();
            while (reader.hasNext())
            {
                String n = null;
                String v = null;
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    String val = reader.nextString();
                    if(name == null || val == null || name.length() < 1 || val.length() < 1) continue;
                    if(name.equalsIgnoreCase("key") || name.equalsIgnoreCase("name"))
                    {
                        n = val;
                    } else if(name.equalsIgnoreCase("value")){
                        v = val;
                    }
                }
                reader.endObject();
                if(n != null && v != null)
                {
                    pairs.put(n, v);
                }
            }
            reader.endArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean insertIntoDB(SQLiteDatabase db, String id, HashMap<String, String> pairs)
    {
        if(id == null) return false;
        String sql = getInsertSql();

        String values[] =  new String[sColumnList.length];
        values[0] = id;//ITEM_ID
        values[3] = "0";//MARK_FOR_DELETE
        for (String name : pairs.keySet())
        {
            String val = pairs.get(name);
            values[1] = name;//PROP_NAME
            values[2] = val;//PROP_VALUE
            db.execSQL(sql, values);
        }
        return true;

    }


    public static boolean saveJsonArray(SQLiteDatabase db, String idName, JsonReader reader, String propItemName, ReadInterceptor readInterceptor)
    {
        int cnt = 0;
        try {
            reader.beginArray();
            while (reader.hasNext()) {
                if(putItem(db, idName, reader, propItemName, readInterceptor))
                {
                    cnt++;
                }
            }
            reader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cnt > 0;
    }

	public static void setDbVersion(int dbVersion)
	{
		JsonCacheDbHelper.DB_VERSION = dbVersion;
	}

    /*
    public static boolean saveJsonArray(SQLiteDatabase db, String idName, JSONArray array)
    {
        int cnt = 0;
        for (int i = 0; i < array.length(); i++)
        {
            if(putItem(db, idName, array.optJSONObject(i)))
            {
                cnt++;
            }
        }
        return cnt > 0;
    }
    */
}
