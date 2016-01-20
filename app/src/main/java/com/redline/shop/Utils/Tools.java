package com.redline.shop.Utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.redline.shop.BuildConfig;
import com.redline.shop.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by asdevel.com
 * Date: 26.11.13
 * Time: 15:59
 */
public class Tools {

    public static final int REQUEST_CODE_PICKUP_IMAGE = 1150;
    //	public final static String IK_PATHMANAGER_PATH_CLICKED    = "IK_PATHMANAGER_PATH_CLICKED";
    public final static String IK_REFRESH_ACTION_IN_PROGRESS = "IK_REFRESH_ACTION_IN_PROGRESS";

    public final static String BR = System.getProperty("line.separator");

    //	public static final String IK_LIST_NAVIGATION             = "IK_LIST_NAVIGATION";
//	public static final String IK_LIST_NAVIGATION_KEY_FORWARD = "IK_LIST_NAVIGATION_KEY_FORWARD";
//	public static final String IK_LIST_NAVIGATION_GOOD_ID     = "IK_LIST_NAVIGATION_GOOD_ID";
//	public static final String IK_MAIN_ACTIVITY_BACKBUTTON_PRESSED = "IK_MAIN_ACTIVITY_BACKBUTTON_PRESSED";
    private final static Handler m_shandler = new Handler();
    private static Context m_sctx;
    private static int m_sBackButtonCounter = 0;
    private static SharedPreferences s_sharedPreference;
    private static Uri m_cameraUri = null;
//	private static Tracker m_tracker;
//
//	public static Tracker gaTracker() {
//
//		return m_tracker;
//	}

    public static void initialize(Context applicationContext) {

        m_sctx = applicationContext;
        //m_tracker = GoogleAnalytics.getInstance(applicationContext).newTracker(R.xml.ga_tracker);

        //UtilConstants.setDebugMode(BuildConfig.DEBUG);
//		String mac = Tools.myMac(); // do not remove this line, mac address should be initialized ASAP

//		log("Tools initialized. mac: " + mac);
    }

    public static Handler getMainHandler() {

        return m_shandler;
    }

    public static Context getApplicationContext() {

        return m_sctx;
    }

    public static Resources getResources() {

        return m_sctx.getResources();
    }

    public static String getString(int id) {

        return m_sctx.getResources().getString(id);
    }

    public static int getColor(int resource) {
        return getResources().getColor(resource);
    }

    public static boolean getBoolean(int id) {

        return m_sctx.getResources().getBoolean(id);
    }

    public static final String TITLE = "TITLE";
    public static final String IMAGE = "IMAGE";
    public static final String ITEMID = "ITEMID";

    public static int crtAdapterItem(Context ctx, ArrayList l, int title, int image) // helper for SimpleAdapter's data preporation
    {

        HashMap<String, Object> m = new HashMap();
        m.put(IMAGE, image);
        m.put(ITEMID, image);
        m.put(TITLE, ctx.getString(title));
        l.add(m);
        return l.size() - 1;
    }

    public static String getDeviceName() {

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    public static String getGoogleUser() {

        AccountManager mgr = AccountManager.get(getApplicationContext());
        Account[] acts = mgr.getAccountsByType("com.google");
        if (acts != null && acts.length > 0) {
            return acts[0].name;
        }
        return "unknown";
    }

    public static void ASSERT(boolean expr, String message) {

        if (BuildConfig.DEBUG && !(expr)) {
            AssertionError assertionError = new AssertionError(message == null ? "<no message>" : message);
            log("ASSERT");
            log(assertionError);
            throw assertionError;
        }
    }

    public static void log(Throwable e) {

        log(e, true);
    }

    public static void log(Throwable e, boolean verbose) {

        if (e == null) {
            log("e == null");
            return;
        }

        if (!BuildConfig.DEBUG) {
            e.printStackTrace();
            return;
        }
        StackTraceElement[] stackTraceElements = e.getStackTrace();

        log("----------- Exception! -----------");
        log("message: " + e.getMessage());
        log("class: " + e.getClass().getCanonicalName());
        log("file: " + stackTraceElements == null || stackTraceElements.length < 1 ? "<null>" : stackTraceElements[0].toString());

		/*if (!(
                e instanceof SocketException ||
				e instanceof SocketTimeoutException ||
				e instanceof org.apache.http.conn.ConnectTimeoutException
		))*/
        if (verbose) {
            log("---------- Stack trace: ----------");
            StringBuilder sb = new StringBuilder().append('\n');
            for (StackTraceElement s : stackTraceElements) {
                sb.append(s.toString()).append('\n');
            }

            log(sb.toString());
        }
        log("----------------------------------");
    }

    public static void log(String s) {

        log(null, s == null ? "<null>" : s);
    }

    public static void logth(String s) {

        log(null, s == null ? "<null>" : s + " th:" + Thread.currentThread());
    }

    public static <T extends Object> void log(T s) {

        log(null, s == null ? "<null>" : s.getClass().getSimpleName() + " " + s.toString());
    }

    public static void log(String tag, String message) {

        if (BuildConfig.DEBUG != true) return;

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement se = null;

        for (int i = 4; i < stackTraceElements.length; i++) {
            StackTraceElement s = stackTraceElements[i];
            String methodName = s.getMethodName();
            if (methodName.startsWith("access$") || methodName.equals("log") || methodName.equals("logth")) {
                continue;
            }
            se = s;
            break;
        }

        if (se == null) {
            se = stackTraceElements[4];
        }

        if (tag == null) {
            //tag = se.getClassName();
            //tag = tag.substring(tag.lastIndexOf('.') + 1);
            tag = se.getFileName() + ":" + se.getLineNumber();
        }

        Log.e(tag,
                //se.getFileName() + ":" + se.getLineNumber() + " " +
                se.getMethodName() + "() --> " + message + " <-- ");
    }

    public static LocalBroadcastManager getLocalBroadcastManager() {

        return LocalBroadcastManager.getInstance(getApplicationContext());
    }

    public static void registerLocalBroadcastReceiver(BroadcastReceiver r, IntentFilter f) {

        //getLocalBroadcastManager().registerReceiver(new BroadcastReceiverWrapper(r), f);
    }

    public static boolean sendLocalBroadcast(Intent i) {

        return getLocalBroadcastManager().sendBroadcast(i);
    }

    private static float s_dpiRatio = -1;

    static public int dpToPx(int dp) {

        if (s_dpiRatio == -1) {
            DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
            s_dpiRatio = displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT;
        }
        return Math.round(dp * s_dpiRatio);
    }

    static Point screenSize = null;

    static public Point screenSize() {

        if (screenSize == null) {
            DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
            screenSize = new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
        }
        return screenSize;
    }

    static public int minScreenSize() {

        Point s = screenSize();
        return Math.min(s.x, s.y);
    }

    public static int maxScreenSize() {

        Point s = screenSize();
        return Math.max(s.x, s.y);
    }

    static public int pxToDp(int px) {

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static void backbuttonProcessedSetupFlag(boolean b) {

        m_sBackButtonCounter += b ? 1 : -1;
        if (m_sBackButtonCounter < 0) {
            m_sBackButtonCounter = 0;
        }
    }

    public static boolean popBackbuttonProcessed() {

        boolean processed = m_sBackButtonCounter > 0;
        m_sBackButtonCounter = 0;
        return processed;
    }

    public static boolean isUiThread() {

        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public static void checkUiThread() {

        if (!Tools.isUiThread()) {
            throw new IllegalStateException("only UI thread allowed here");
        }
    }

    public static void checkNonUiThread() {

        if (Tools.isUiThread()) {
            throw new IllegalStateException("only UI thread allowed here");
        }
    }

    public static void runOnUiThread(Runnable runnable) // clone of Activity.runOnUiThread()
    {

        if (isUiThread()) {
            runnable.run();
        } else {
            getMainHandler().post(runnable);
        }
    }

    public static boolean compareDoubles(double d1, double d2) {

        return Math.abs(d1 - d2) <= 0.000001;
    }

    private static HashMap<String, Typeface> s_fontsCache = new HashMap<String, Typeface>();

    public static Typeface loadFont(String fontName) {

        Typeface font = s_fontsCache.get(fontName);

        if (font == null) {
            font = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/" + fontName);
            s_fontsCache.put(fontName, font);
        }

        return font;
    }

    public static SharedPreferences getSharedPreferences() {

        if (s_sharedPreference == null) {
            s_sharedPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        }
        return s_sharedPreference;
    }

    public static void sendEmail(String email, String subject, String body, String chooserHeader) throws ActivityNotFoundException {

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", email, null));
        if (subject != null) {
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }

        if (body != null) {
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        }
        Intent chooser = Intent.createChooser(emailIntent, chooserHeader);
        chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(chooser);
    }

    public static void makeCall(String number) throws ActivityNotFoundException {

        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null));
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(i);
    }

    public static void sendSms(String number, String body, String chooserHeader) throws ActivityNotFoundException {

        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", number, null));
        if (body != null) {
            i.putExtra("sms_body", body);
        }
        Intent chooser = Intent.createChooser(i, chooserHeader);
        chooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(chooser);
    }

    public static Intent pickUpPhoneNumbers() {

        return pickUpContacts(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
    }

    public static Intent pickUpEmails() {

        return pickUpContacts(ContactsContract.CommonDataKinds.Email.CONTENT_TYPE);
    }

    public static String getMimeType(String url) {

        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String inputStream2String(InputStream in) {

        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }

            return total.toString();
        } catch (IOException e) {
            e.printStackTrace();
            log("inputStream2String error: " + e.getMessage());
        }

        return null;
    }

    public static Intent pickUpImage() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT, null);
        galleryIntent.setType("image/*");
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        path.mkdirs();
        File file = new File(path, "ir_" + System.currentTimeMillis() + ".jpg");

        m_cameraUri = Uri.fromFile(file);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, m_cameraUri);

        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
        chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);

        Intent[] intentArray = {cameraIntent};
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

        return chooser;
    }

    public static Intent pickUpContacts(String type) {

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        if (type != null) {
            intent.setType(type);
        }
        return intent;
    }

    public static String getContactColumnFromPickerIntent(Intent pickerIntent, String column) {

        if (pickerIntent == null) {
            return null;
        }
        String value = null;
        Uri uri = pickerIntent.getData();
        if (uri != null) {
            Cursor cursor = null;
            String[] prj = {column};

            try {
                cursor = getApplicationContext().getContentResolver().query(uri, prj
                        , null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    value = cursor.getString(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
        }
        return value;
    }

    public static String getPhoneNumberFromPickerIntent(Intent pickerIntent) {

        return getContactColumnFromPickerIntent(pickerIntent, ContactsContract.CommonDataKinds.Phone.NUMBER);
    }

    public static String getEmailFromPickerIndent(Intent pickerIntent) {

        return getContactColumnFromPickerIntent(pickerIntent, ContactsContract.CommonDataKinds.Email.ADDRESS);
    }

    public static String getPhoneNumber(String name) {

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.PhoneLookup._ID, ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone
                .NUMBER};
        String selection = null;//ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME + " = ?";
        String[] selectionArguments = null;//{ name };
        String phone = null;
        try {
            Cursor cursor = contentResolver.query(uri, projection, selection, selectionArguments, null);
            if (cursor != null) {
                if (cursor.moveToNext()) {
                    DatabaseUtils.dumpCursor(cursor);
                    //phone = m_cursor.getString(m_cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone));
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return phone;
    }

    public static String getSettingString(int idPref, String defValue) {

        SharedPreferences prefs = getSharedPreferences();
        return prefs.getString(getString(idPref), defValue);
    }

    public static String getSettingString(int idPref, int reaDefValue) {

        return getSettingString(idPref, getString(reaDefValue));
    }

    public static int getSettingStringAsInteger(int idPref, int reaDefValue) {

        int timeOut;
        try {
            timeOut = Integer.valueOf(getSettingString(idPref, reaDefValue));
        } catch (NumberFormatException e) {
            log(e);
            timeOut = Integer.valueOf(Tools.getString(reaDefValue));
        }

        return timeOut;
    }

    public static boolean getSettingBoolean(int idPref, boolean defValue) {

        SharedPreferences prefs = getSharedPreferences();
        return prefs.getBoolean(getString(idPref), defValue);
    }

    public static int getSettingInt(int idPref, int defValueRes) {

        SharedPreferences prefs = getSharedPreferences();
        return prefs.getInt(getString(idPref), getResources().getInteger(defValueRes));
    }

    public static void setSetting(int idPref, String obj) {

        SharedPreferences prefs = getSharedPreferences();
        prefs.edit().putString(getString(idPref), obj).apply();
    }

    public static Bundle makeStringArgument(String argEventId, String eventId) {

        Bundle arg = new Bundle();
        arg.putString(argEventId, eventId);
        return arg;
    }

    public static void setVisibility(View view, int vis) {

        if (view == null || view.getVisibility() == vis) {
            return;
        }
        view.setVisibility(vis);
    }

    public static void gonev(View view) {

        if (view != null) {
            setVisibility(view, View.GONE);
        }
    }

    public static void showv(View view) {

        if (view != null) {
            setVisibility(view, View.VISIBLE);
        }
    }

    public static void showv(View view, boolean show) {

        if (view != null) {
            setVisibility(view, show ? View.VISIBLE : View.GONE);
        }
    }

    public static void hidev(View view) {

        if (view != null) {
            setVisibility(view, View.INVISIBLE);
        }
    }

    public static void toast(final int res) {

        toast(getString(res));
    }

    public static void toast(final String s) {

        if (isEmpty(s)) return;

        getMainHandler().post(new Runnable() {
            @Override
            public void run() {

                Toast toast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
                int margin = Tools.getResources().getDimensionPixelOffset(R.dimen.padding16);
                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, margin);
                View view = toast.getView();
                View viewById = view == null ? null : view.findViewById(android.R.id.message);
                if (viewById instanceof TextView) {
                    ((TextView) viewById).setGravity(Gravity.CENTER);
                }

                toast.show();
            }
        });
    }

    public static boolean startActivity(Context ctx, Intent i) {

        try {
            ctx.startActivity(i);
            return true;
        } catch (ActivityNotFoundException e) {
            Tools.toast(R.string.there_is_no_application_for_activity);
            e.printStackTrace();
            log(e.getMessage());
        }

        return false;
    }

    /*public static void updateText(TextView tv, String colName, Cursor cursor)
	{
        if (tv == null)
        {
            return;
        }
        int columnIndex = cursor == null ? -1 : cursor.getColumnIndex(colName);
        String text = columnIndex < 0 ? null : cursor.getString(columnIndex);
        if (text == null || text.isEmpty())
        {
            gonev(tv);
        }
        else
        {
            showv(tv);
            tv.setText(text);
        }
    }*/

    private static double eps = 0.000001;

    public static boolean isEqual(double a, double b) {

        return Math.abs(a - b) < eps;
    }

    public static Bitmap cropCenterBitmap(Bitmap bitmap, int size, int roundCornersPixels) {

        if (bitmap == null || size == 0) {
            return null;
        }

        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final int szSrc = Math.min(rect.width(), rect.height());
        if (szSrc <= 0) {
            return null;
        }

        Rect rcSrc = new Rect(0, 0, szSrc, szSrc);
        rcSrc.offset(rect.centerX() - rcSrc.centerX(), rect.centerY() - rcSrc.centerY());

        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();

        final RectF rcDst = new RectF(0, 0, size, size);
        final float roundPx = roundCornersPixels;

        if (roundPx > 0) {
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 255, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rcDst, roundPx, roundPx, paint);

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        }

        canvas.drawBitmap(bitmap, rcSrc, rcDst, paint);

        return output;
    }

    static volatile String selfMac = null;
    static final String SELF_MAC_KEY = "SELF_MAC_KEY";

//	public static String myMac() {
//		//if (BuildConfig.DEBUG) return  "74:2F:68:26:8E:94";
//		//if (BuildConfig.DEBUG) return  "B0:9F:BA:77:C8:6B"; 	5c
//		//if (BuildConfig.DEBUG) return  "4C:B1:99:A9:CF:77"; 	//stas
//		//if (BuildConfig.DEBUG) return  "84:8E:0C:6D:17:E1"; 	//semenov helper
//		//if (BuildConfig.DEBUG) return "50:55:27:fe:f4:47"; 	//kulagin
//
//		//if (BuildConfig.DEBUG) return "d8:3c:69:93:71:34"; 		//kusakin
//
//		try
//		{
//
//			if (selfMac != null) return selfMac;
//			if (Tools.getSharedPreferences().contains(SELF_MAC_KEY)) {
//				selfMac = Tools.getSharedPreferences().getString(SELF_MAC_KEY, null);
//			}
//			if (selfMac != null) return selfMac;
//
//			WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//			boolean disabled = !wifi.isWifiEnabled();
//			if (disabled) {
//				//Toast.makeText(context, R.string.enable_wifi_by_app, Toast.LENGTH_LONG).showv();
//				wifi.setWifiEnabled(true);
//			}
//			selfMac = wifi.getConnectionInfo().getMacAddress().toLowerCase();
//			if (disabled) {
//				wifi.setWifiEnabled(false);
//			}
//			Tools.getSharedPreferences().edit().putString(SELF_MAC_KEY, selfMac).commit();
//		} catch (Throwable e) {
//			log(e);
//			gaTrackerPostEvent("mac", "Gathering mac error.\n" + Tools.dumpDeviceInfo());
//		}
//
//		return Tools.isEmpty(selfMac) ? "macunknown" : selfMac;
//	}

    public static long freeSpaceExternal() {

        return bytesAvailable(getApplicationContext().getExternalCacheDir());
    }

    public static long freeSpaceInternal() {

        return bytesAvailable(getApplicationContext().getFilesDir());
    }

    public static long bytesAvailable(File file) {

        StatFs stat = new StatFs(file.getPath());
        long bytesAvailable;
        if (Build.VERSION.SDK_INT >= 18) {
            bytesAvailable = getAvailableBytes(stat);
        } else {
            //noinspection deprecation
            bytesAvailable = stat.getBlockSize() * stat.getAvailableBlocks();
        }

        return bytesAvailable;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static long getAvailableBytes(StatFs stat) {

        return stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
    }

    public static void post(Runnable runnable) {

        getMainHandler().post(runnable);
    }

    public static boolean equals(String hasBarcode, String str) {

        return hasBarcode != null && hasBarcode.equals(str);
    }

    public static boolean iss(String s) {

        return s != null && !s.trim().isEmpty();
    }

    public static boolean isEmpty(String s) {

        return s == null || s.trim().isEmpty();
    }

    public static int getDimensionPixelSize(int dimen) {

        return getResources().getDimensionPixelSize(dimen);
    }

    public static void post(Runnable runnable, int delay) {

        getMainHandler().postDelayed(runnable, delay);
    }

    static MenuItem s_stubMenuItem;

    public static MenuItem stubMenuItem() {

        if (s_stubMenuItem == null) s_stubMenuItem = new MenuItem() {
            @Override
            public int getItemId() {

                return 0;
            }

            @Override
            public int getGroupId() {

                return 0;
            }

            @Override
            public int getOrder() {

                return 0;
            }

            @Override
            public MenuItem setTitle(CharSequence title) {

                return null;
            }

            @Override
            public MenuItem setTitle(int title) {

                return null;
            }

            @Override
            public CharSequence getTitle() {

                return null;
            }

            @Override
            public MenuItem setTitleCondensed(CharSequence title) {

                return null;
            }

            @Override
            public CharSequence getTitleCondensed() {

                return null;
            }

            @Override
            public MenuItem setIcon(Drawable icon) {

                return null;
            }

            @Override
            public MenuItem setIcon(int iconRes) {

                return null;
            }

            @Override
            public Drawable getIcon() {

                return null;
            }

            @Override
            public MenuItem setIntent(Intent intent) {

                return null;
            }

            @Override
            public Intent getIntent() {

                return null;
            }

            @Override
            public MenuItem setShortcut(char numericChar, char alphaChar) {

                return null;
            }

            @Override
            public MenuItem setNumericShortcut(char numericChar) {

                return null;
            }

            @Override
            public char getNumericShortcut() {

                return 0;
            }

            @Override
            public MenuItem setAlphabeticShortcut(char alphaChar) {

                return null;
            }

            @Override
            public char getAlphabeticShortcut() {

                return 0;
            }

            @Override
            public MenuItem setCheckable(boolean checkable) {

                return null;
            }

            @Override
            public boolean isCheckable() {

                return false;
            }

            @Override
            public MenuItem setChecked(boolean checked) {

                return null;
            }

            @Override
            public boolean isChecked() {

                return false;
            }

            @Override
            public MenuItem setVisible(boolean visible) {

                return null;
            }

            @Override
            public boolean isVisible() {

                return false;
            }

            @Override
            public MenuItem setEnabled(boolean enabled) {

                return null;
            }

            @Override
            public boolean isEnabled() {

                return false;
            }

            @Override
            public boolean hasSubMenu() {

                return false;
            }

            @Override
            public SubMenu getSubMenu() {

                return null;
            }

            @Override
            public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {

                return null;
            }

            @Override
            public ContextMenu.ContextMenuInfo getMenuInfo() {

                return null;
            }

            @Override
            public void setShowAsAction(int actionEnum) {

            }

            @Override
            public MenuItem setShowAsActionFlags(int actionEnum) {

                return null;
            }

            @Override
            public MenuItem setActionView(View view) {

                return null;
            }

            @Override
            public MenuItem setActionView(int resId) {

                return null;
            }

            @Override
            public View getActionView() {

                return null;
            }

            @Override
            public MenuItem setActionProvider(ActionProvider actionProvider) {

                return null;
            }

            @Override
            public ActionProvider getActionProvider() {

                return null;
            }

            @Override
            public boolean expandActionView() {

                return false;
            }

            @Override
            public boolean collapseActionView() {

                return false;
            }

            @Override
            public boolean isActionViewExpanded() {

                return false;
            }

            @Override
            public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {

                return null;
            }
        };
        return s_stubMenuItem;
    }

    public static String implode(ArrayList<String> fields, String glue) {

        StringBuilder sb = new StringBuilder();
        for (String f : fields) {
            boolean b = !isEmpty(f);
            if (sb.length() > 0 && b) {
                sb.append(glue);
            }
            sb.append(f);
        }

        return sb.toString();
    }

    private static SimpleDateFormat s_simpleDateFormat = null;

    @NonNull
    public static SimpleDateFormat getSimpleDateFormat() {

        if (s_simpleDateFormat == null) {
            s_simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
        return s_simpleDateFormat;
    }

//	public static String trimTime(String strDate) {
//
//		Preconditions.checkArgument(!TextUtils.isEmpty(strDate));
//		int spaceIndex = strDate.indexOf(" "); // check data format at getSimpleDateFormat()
//		Preconditions.checkState(spaceIndex > 0);
//		return strDate.substring(0, spaceIndex);
//	}

//	public static boolean compareFormattedDates(String str1, String str2) {
//
//		return trimTime(str1).equals(trimTime(str2));
//	}

    public static String formatTimeStamp(long timestampInSecs) {

        return getSimpleDateFormat().format(new Date(timestampInSecs * 1000));
    }

    public static String formatTimeStamp(String timestampInSecs) {

        try {
            String formattedTime = Tools.formatTimeStamp(Long.valueOf(timestampInSecs));
            return Tools.isEmpty(formattedTime) ? "" : formattedTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

//	public static Drawable filterBitmap(Bitmap bitmap, int colorRes) {
//
//		ColorFilterDrawable bitmapDrawableFiltered = new ColorFilterDrawable(Tools.getResources(), bitmap, colorRes);
//		int w = bitmap.getWidth();
//		int h = bitmap.getHeight();
//		bitmapDrawableFiltered.setBounds(0, 0, w, h);
//		return bitmapDrawableFiltered;
//	}
//
//	public static String getCurrentApplicationLoadingUrl() {
//		//return "http://" + ClientConfig.getServerIP() + "/api_v1/app_redirect?id=" + Uri.encode(ClientConfig.getCurrentObjectID()) + "&p=android";
//		//http://kp.indoorroute.com/api_v1/app_install?id=RedSquare
//		return "http://" + ClientConfig.getServerIP() + "/api_v1/app_install?id=" + Uri.encode(ClientConfig.getCurrentObjectID()) + "&p=android";
//	}

    public static void intentEmail(String text, String subject, String emailTo) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        //if (!isEmpty(emailTo)) {shareIntent.setData(Uri.fromParts("mailto", emailTo, null));}
        shareIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailTo});
        Tools.startActivity(getApplicationContext(), shareIntent);
    }

    public static void intentBrowser(String url) {

        Intent shareIntent = new Intent(Intent.ACTION_VIEW);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setData(Uri.parse(url));
        Tools.startActivity(getApplicationContext(), shareIntent);
    }

//	public static String shareUrlCatalogItem(String goodsId) {
//
//		return ClientConfig.getWebCabinetPartUrl() +
//			   Tools.getString(R.string.get_request_lang) + "/" +
//			   ClientConfig.getCurrentObjectID() +
//			   "/gm/" + goodsId;
//	}
//
//	public static boolean isNavigationEnabled() {
//
//		return BuildConfig.DEBUG || Config.getConfig().navigationEnabled || getBoolean(R.bool.bo_auto_positioning_enabled);
//	}

    public static String getJsonStringSafe(JSONObject jsonObject, String tag) {

        try {
            return jsonObject == null ? "" : jsonObject.getString(tag);
        } catch (JSONException e) {
            return "";
        }
    }

    public static int getJsonIntSafe(JSONObject jsonObject, String tag, int defValue) {

        try {
            return jsonObject == null ? defValue : jsonObject.getInt(tag);
        } catch (JSONException e) {
            return defValue;
        }
    }

    public static void debugPause() {

        if (!BuildConfig.DEBUG) return;

        for (int i = 0; i < 5; i++) {
            log("wait ... " + i);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static File sinkStream(InputStream inputStream, String fileDirectory, String fileName) throws IOException {

        File path = new File(fileDirectory);
        if (!path.exists()) path.mkdirs();

        File file = new File(path, fileName);
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));

        int read = 0;
        byte[] bytes = new byte[1024];
        while ((read = inputStream.read(bytes)) != -1) os.write(bytes, 0, read);
        os.close();

        return file;
    }

    public static void waitForDebugger() {

        if (!BuildConfig.DEBUG) return;

        for (int i = 0; i < 5; i++) {
            try {
                log("Waiting fro debugger ... ");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static StringBuilder dumpDeviceInfo() {

        StringBuilder sb = new StringBuilder();
        Point ss = screenSize();
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();

        sb.append("DEVICE_INFO");
        sb.append(BR).append("OS Version: ").append(System.getProperty("os.version")).append(" (").append(Build.VERSION.INCREMENTAL).append(")");
        sb.append(BR).append("OS API Level: ").append(Build.VERSION.SDK_INT);
        sb.append(BR).append("Device: ").append(Build.DEVICE);
        sb.append(BR).append("Model (and Product): ").append(Build.MODEL).append(" (").append(Build.PRODUCT).append(")");
        sb.append(BR).append("Display: ").append(ss.x).append("x").append(ss.y);
        sb.append(BR).append(displayMetrics.toString());
        sb.append(BR).append("User: ").append(Tools.getGoogleUser());
        sb.append(BR).append("Free space internal: ").append(Tools.freeSpaceInternal()).append(" bytes");
        sb.append(BR).append("external: ").append(Tools.freeSpaceExternal());

        return sb;
    }

//	public static void gaTrackerPostEvent(String category, String message) {
//
//		Tracker gaTracker = gaTracker(); if (gaTracker == null) return;
//		gaTracker.send(new HitBuilders.EventBuilder()
//				.setCategory(category)
//				.setAction(message).build());
//	}
//
//	public static void gaTrackerPostScreen(Class<? extends FragmentBase> aClass) {
//
//		Tracker tracker = Tools.gaTracker();
//		if (tracker != null) {
//			String name = aClass.getName();
//			//log("GA logged screenName: " + name);
//			tracker.setScreenName(name);
//			tracker.send(new HitBuilders.ScreenViewBuilder().build());
//		}
//	}
//
//	public static void gaTrackerPostException(Throwable e, String addon) {
//
//		Tracker gaTracker = gaTracker(); if (gaTracker == null) return;
//		gaTracker.send(new HitBuilders.ExceptionBuilder()
//				.setDescription(parseException(e).toString() + (addon == null ? "" : addon))
//				.setFatal(false)
//				.build());
//
//		//gaTrackerPostEvent("exception", parseException(e).toString());
//	}

    @NonNull
    static public StringBuilder parseException(Throwable e) {

        StringBuilder sb = new StringBuilder();
        //e.getMessage() + "\n"; // e.getMessage() is forbidden by GA rules
        sb.append(e.getClass().getSimpleName()).append(Tools.BR);
        sb.append(Log.getStackTraceString(e)).append(Tools.BR);
        sb.append(Tools.dumpDeviceInfo());
        return sb;
    }

    public interface OnPickupImageListener {

        void onPickupImage(String filePath);
    }

//	public static void onPickupImageResult(int requestCode, int resultCode, Intent data, OnPickupImageListener listener) {
//
//		String filePath = null;
//		if (resultCode == Activity.RESULT_OK) {
//			if (requestCode == REQUEST_CODE_PICKUP_IMAGE) {
//				if (data != null) {
//					boolean bErrorSave = false;
//					Uri uri = data.getData();
//					if (uri != null) {
//						if ("content".equals(uri.getScheme())) {
//
//							ContentResolver resolver = null;
//							Cursor cursor = null;
//							try {
//								resolver = getApplicationContext().getContentResolver();
//								cursor = resolver.query(uri, new String[]{MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.MIME_TYPE},
//														null, null, null);
//							} catch (Exception e) {
//								e.printStackTrace();
//								log(e);
//							}
//
//							if (cursor != null) {
//								if (cursor.moveToFirst()) {
//									String mimeType = cursor.getString(1);
//									if (mimeType != null && mimeType.startsWith("image/")) {
//										if (cursor.getString(0) != null) {
//											filePath = cursor.getString(0);
//										}
//										//cursor.getString(0);
//									}
//								}
//								cursor.close();
//							}
//
//							if (filePath == null) {
//								File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//
//								path.mkdirs();
//								File file = new File(path, "ir_" + System.currentTimeMillis() + ".jpg");
//
//								try {
//									inputStream2File(resolver.openInputStream(uri), file.getPath());
//									filePath = file.getPath();
//								} catch (IOException e) {
//									log(e);
//									bErrorSave = true;
//								}
//							}
//						}
//						else if ("file".equals(uri.getScheme())) {
//							String path = uri.getPath();
//							String mimeType = Tools.getMimeType(path);
//
//							if (mimeType != null && mimeType.startsWith("image/")) {
//								filePath = uri.getPath(); //path;
//							}
//						}
//					}
//
//					if (filePath == null) {
//						toast(bErrorSave ? com.asdevel.indoorroute.app.R.string.photo_save_error : com.asdevel.indoorroute.app.R.string.photo_error_format);
//					}
//				}
//				else { // data is null, so we hope that image stored in m_cameraUri place
//					if (m_cameraUri != null && (new File(m_cameraUri.getPath())).exists()) {
//						filePath = m_cameraUri.getPath();
//					}
//				}
//			}
//		}
//		else {
//			//Toast.makeText(getApplicationContext(), R.string.pickup_photo_cancel, Toast.LENGTH_SHORT).showv();
//		}
//
//		if (listener != null) {
//			listener.onPickupImage(filePath);
//		}
//	}
//
//	public static <T> void inputStream2File(final InputStream is, final T file) throws IOException {
//
//		new AutoCloser() {
//			@Override
//			protected void doWork() throws Throwable {
//
//				OutputStream os = null;
//				if (file instanceof File) os = new FileOutputStream((File) file);
//				else if (file instanceof String) os = new FileOutputStream((String) file);
//				else throw new IllegalStateException("file should be a File or a String instance");
//				autoClose(os);
//				inputStream2outputStream(is, os);
//			}
//		};
//	}

    private static void inputStream2outputStream(InputStream is, OutputStream os) throws IOException {

        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
    }

    public static void playSound(int typeNotification) {

        try {
            final Ringtone r = RingtoneManager.getRingtone(Tools.getApplicationContext(), RingtoneManager.getDefaultUri(typeNotification));
            r.play();
            Tools.post(new Runnable() {
                @Override
                public void run() {

                    if (!r.isPlaying()) r.stop();
                    else post(this, 500);
                }
            }, 500);
        } catch (Exception e) {
            log(e);
        }
    }

    public static boolean deleteDirectory(File path) {

        boolean retCode = true;
        LinkedList<File> list = new LinkedList<>();
        HashSet<File> processed = new HashSet<>();
        list.addLast(path);

        while (!list.isEmpty()) {
            File file = list.pollLast();
            if (file.isDirectory() && !processed.contains(file)) {
                File[] children = file.listFiles();
                if (children != null && children.length != 0) {
                    processed.add(file);
                    list.add(file);
                    list.addAll(Arrays.asList(children));
                    continue;
                }
            }

            log("rm " + file.getAbsolutePath());
            if (!file.delete()) retCode = false;
        }

        return retCode;
    }

    public static void setBackground(View view, Drawable bg) {
        if (view == null || bg == null) return;
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(bg);
        } else {
            view.setBackground(bg);
        }
    }

    public static String getFontRegular() {
        return BuildConfig.FONT_REGULAR;
    }
}
