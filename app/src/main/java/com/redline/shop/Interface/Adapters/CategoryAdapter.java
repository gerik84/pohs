package com.redline.shop.Interface.Adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.JsonReader;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.asdevel.cache.jsondb.CachedJsonArray;
import com.asdevel.cache.jsondb.JsonCacheDbHelper;
import com.redline.shop.R;
import com.redline.shop.Utils.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class CategoryAdapter extends ShopAuthorizationAbstractAdapter {


    public CategoryAdapter(Context ctx, String itemIDKey, IRetrieverHandler handler, boolean bDebugMode) {
        super(ctx, itemIDKey, handler, bDebugMode);
    }

    @Override
    public String setRequestUrl() {
        return Tools.getApiUrl() + "categories?get_images=true";
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static String slurp(final InputStream is, final int bufferSize) {
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        try {
            Reader in = new InputStreamReader(is, "UTF-8");
            for (; ; ) {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
        } catch (IOException ex) {
        /* ... */
        }
        return out.toString();
    }

    @Override
    public boolean parseJson(SQLiteDatabase db, InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
//String s= slurp(in, 1024);
        reader.beginObject();
        while (reader.hasNext()) {

            String name = reader.nextName();

            if (name.equals("categories")) {

                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginObject();
                    HashMap<String, String> values = new HashMap<>();
                    while (reader.hasNext()) {
                        String n = reader.nextName();
                        if (n.equals("main_pair")) {
                            reader.skipValue();
                            continue;
                        }
                        String v = reader.nextString();
                        values.put(n, v);
                    }
                    reader.endObject();
                    putItem("category_id", values);
                }
                reader.endArray();

            } else
                reader.skipValue();

        }
        reader.endObject();

        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View root = super.getView(position, convertView, parent);

        ImageView image = (ImageView) root.findViewById(R.id.iv_image);
        TextView label = (TextView) root.findViewById(R.id.tv_label);

        if (position == 0)
            image.setImageDrawable(Tools.getResources().getDrawable(R.drawable.laminat));
        else
            image.setImageDrawable(Tools.getResources().getDrawable(R.drawable.parket));

        label.setText(m_cursor.getString(m_cursor.getColumnIndex("category")));

        return root;
    }

    @Override
    public boolean handlePages(CachedJsonArray.PageHandle urlInfo) {
        return false;
    }
}
