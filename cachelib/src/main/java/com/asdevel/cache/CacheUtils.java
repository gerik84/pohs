package com.asdevel.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * User: Andrew Rakhmatulin
 * Date: 07.11.13
 * Time: 13:05
 */
public class CacheUtils {

    private static final String PREF_MODIFIED_SINCE = "PREF_MODIFIED_SINCE";

    public static Bitmap decodeFileToBitmap(String path, int maxSize) throws Exception
    {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, o);
        int scale = 1;

        if (o.outHeight > maxSize || o.outWidth > maxSize)
        {
            int picSide = Math.max(o.outHeight, o.outWidth);
            scale = (int) Math.pow(2, (int) Math.round(Math.log(maxSize
                    / (double) picSide)
                    / Math.log(0.5)));
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inPreferredConfig = Bitmap.Config.ARGB_8888;
        o2.inSampleSize = scale;
        return BitmapFactory.decodeFile(path, o2);

    }

    static public String makeKeyForResource(String packageName, int resourceID)
    {
        return "pkg://"+packageName+"/"+resourceID;

    }
    static public String makeKeyForMediaStore(long thumbID, boolean isVideo)
    {
        String key;
        if(isVideo)
        {
            key = "MediaStore.Video.Thumbnails.";
        } else {
            key = "MediaStore.Images.Thumbnails.";
        }
        key +=thumbID;
        return key;

    }

    static public String makePathByURL(String url)
    {
        if(url == null || url.length() <= 0) return null;
        String fileName = url.replace("/","_").replace(":", "_");
        return fileName;
    }


    public static File getCacheFile(Context ctx, String strUrl)
    {
        File dir = ctx.getCacheDir();//Environment.getDownloadCacheDirectory();
        File outputFile = new File(dir,  makePathByURL(strUrl));
        return outputFile;
    }

    public static int downloadFile(Context ctx, String strUrl)
    {
        return downloadFile(ctx, strUrl, makePathByURL(strUrl));
    }

    static public long getModifiedSinceDate(Context ctx, String url)
    {
        SharedPreferences pref = ctx.getSharedPreferences(PREF_MODIFIED_SINCE, 0);
        return pref.getLong(url, 0);
    }

    static public void addIfModifiedSinceDate(Context ctx, HttpURLConnection connection)
    {
        SharedPreferences pref = ctx.getSharedPreferences(PREF_MODIFIED_SINCE, 0);
        long date = getModifiedSinceDate(ctx, connection.getURL().toExternalForm());
        if(date > 0)
        {
            connection.setIfModifiedSince(date);
        }
    }
    static public void saveModifiedSinceDate(Context ctx, HttpURLConnection connection)
    {
        SharedPreferences pref = ctx.getSharedPreferences(PREF_MODIFIED_SINCE, 0);
        long date = connection.getHeaderFieldDate("Last-Modified", 0);
        if(date > 0)
        {
            SharedPreferences.Editor e =  pref.edit();
            e.putLong(connection.getURL().toExternalForm(), date);
            e.commit();
        }
    }


    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

    public static int downloadFile(Context ctx, String strUrl, String fileName)
    {
        //todo make file cache controller for keeping disk usage in reasonable values
        int bResult = -1;
        if (strUrl == null || fileName == null) { return bResult; };
        HttpURLConnection connect = null;
        File outputFile = getCacheFile(ctx, strUrl);
        try
        {

            URL url = new URL(Uri.encode(strUrl, ALLOWED_URI_CHARS));
            connect = (HttpURLConnection) url.openConnection();
            if(outputFile.exists() && outputFile.length() > 0)
            {
                addIfModifiedSinceDate(ctx, connect);
            }


            //connect.setDoOutput(true);
            connect.connect();
            int nResponseCode = connect.getResponseCode();

            if (nResponseCode == HttpURLConnection.HTTP_OK)
            {
                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream is = connect.getInputStream();

                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1)
                {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                is.close();

                saveModifiedSinceDate(ctx, connect);
            }
            bResult = nResponseCode;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        } finally {
            return bResult;
        }

    }
}
