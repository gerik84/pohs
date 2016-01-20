package com.asdevel.cache.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Andrew Rakhmatulin
 * Date: 19.11.13
 * Time: 14:26
 */
public class SharedPreferencesUtils {

    public static void setStringArrayPref(Context context, String key, ArrayList<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();
    }

    public static ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    /*

    private void serializeKey(String key, Bundle bundle, SharedPreferences.Editor editor)
            throws JSONException {
        Object value = bundle.get(key);
        if (value == null) {
            // Cannot serialize null values.
            return;
        }

        String supportedType = null;
        JSONArray jsonArray = null;
        JSONObject json = new JSONObject();

        if (value instanceof Byte) {
            supportedType = TYPE_BYTE;
            json.put(JSON_VALUE, ((Byte)value).intValue());
        } else if (value instanceof Short) {
            supportedType = TYPE_SHORT;
            json.put(JSON_VALUE, ((Short)value).intValue());
        } else if (value instanceof Integer) {
            supportedType = TYPE_INTEGER;
            json.put(JSON_VALUE, ((Integer)value).intValue());
        } else if (value instanceof Long) {
            supportedType = TYPE_LONG;
            json.put(JSON_VALUE, ((Long)value).longValue());
        } else if (value instanceof Float) {
            supportedType = TYPE_FLOAT;
            json.put(JSON_VALUE, ((Float)value).doubleValue());
        } else if (value instanceof Double) {
            supportedType = TYPE_DOUBLE;
            json.put(JSON_VALUE, ((Double)value).doubleValue());
        } else if (value instanceof Boolean) {
            supportedType = TYPE_BOOLEAN;
            json.put(JSON_VALUE, ((Boolean)value).booleanValue());
        } else if (value instanceof Character) {
            supportedType = TYPE_CHAR;
            json.put(JSON_VALUE, value.toString());
        } else if (value instanceof String) {
            supportedType = TYPE_STRING;
            json.put(JSON_VALUE, (String)value);
        } else {
            // Optimistically create a JSONArray. If not an array type, we can null
            // it out later
            jsonArray = new JSONArray();
            if (value instanceof byte[]) {
                supportedType = TYPE_BYTE_ARRAY;
                for (byte v : (byte[])value) {
                    jsonArray.put((int)v);
                }
            } else if (value instanceof short[]) {
                supportedType = TYPE_SHORT_ARRAY;
                for (short v : (short[])value) {
                    jsonArray.put((int)v);
                }
            } else if (value instanceof int[]) {
                supportedType = TYPE_INTEGER_ARRAY;
                for (int v : (int[])value) {
                    jsonArray.put(v);
                }
            } else if (value instanceof long[]) {
                supportedType = TYPE_LONG_ARRAY;
                for (long v : (long[])value) {
                    jsonArray.put(v);
                }
            } else if (value instanceof float[]) {
                supportedType = TYPE_FLOAT_ARRAY;
                for (float v : (float[])value) {
                    jsonArray.put((double)v);
                }
            } else if (value instanceof double[]) {
                supportedType = TYPE_DOUBLE_ARRAY;
                for (double v : (double[])value) {
                    jsonArray.put(v);
                }
            } else if (value instanceof boolean[]) {
                supportedType = TYPE_BOOLEAN_ARRAY;
                for (boolean v : (boolean[])value) {
                    jsonArray.put(v);
                }
            } else if (value instanceof char[]) {
                supportedType = TYPE_CHAR_ARRAY;
                for (char v : (char[])value) {
                    jsonArray.put(String.valueOf(v));
                }
            } else if (value instanceof List<?>) {
                supportedType = TYPE_STRING_LIST;
                @SuppressWarnings("unchecked")
                List<String> stringList = (List<String>)value;
                for (String v : stringList) {
                    jsonArray.put((v == null) ? JSONObject.NULL : v);
                }
            } else {
                // Unsupported type. Clear out the array as a precaution even though
                // it is redundant with the null supportedType.
                jsonArray = null;
            }
        }

        if (supportedType != null) {
            json.put(JSON_VALUE_TYPE, supportedType);
            if (jsonArray != null) {
                // If we have an array, it has already been converted to JSON. So use
                // that instead.
                json.putOpt(JSON_VALUE, jsonArray);
            }

            String jsonString = json.toString();
            editor.putString(key, jsonString);
        }
    }

    private void deserializeKey(String key, Bundle bundle)
            throws JSONException {
        String jsonString = cache.getString(key, "{}");
        JSONObject json = new JSONObject(jsonString);

        String valueType = json.getString(JSON_VALUE_TYPE);

        if (valueType.equals(TYPE_BOOLEAN)) {
            bundle.putBoolean(key, json.getBoolean(JSON_VALUE));
        } else if (valueType.equals(TYPE_BOOLEAN_ARRAY)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            boolean[] array = new boolean[jsonArray.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = jsonArray.getBoolean(i);
            }
            bundle.putBooleanArray(key, array);
        } else if (valueType.equals(TYPE_BYTE)) {
            bundle.putByte(key, (byte)json.getInt(JSON_VALUE));
        } else if (valueType.equals(TYPE_BYTE_ARRAY)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            byte[] array = new byte[jsonArray.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = (byte)jsonArray.getInt(i);
            }
            bundle.putByteArray(key, array);
        } else if (valueType.equals(TYPE_SHORT)) {
            bundle.putShort(key, (short)json.getInt(JSON_VALUE));
        } else if (valueType.equals(TYPE_SHORT_ARRAY)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            short[] array = new short[jsonArray.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = (short)jsonArray.getInt(i);
            }
            bundle.putShortArray(key, array);
        } else if (valueType.equals(TYPE_INTEGER)) {
            bundle.putInt(key, json.getInt(JSON_VALUE));
        } else if (valueType.equals(TYPE_INTEGER_ARRAY)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            int[] array = new int[jsonArray.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = jsonArray.getInt(i);
            }
            bundle.putIntArray(key, array);
        } else if (valueType.equals(TYPE_LONG)) {
            bundle.putLong(key, json.getLong(JSON_VALUE));
        } else if (valueType.equals(TYPE_LONG_ARRAY)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            long[] array = new long[jsonArray.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = jsonArray.getLong(i);
            }
            bundle.putLongArray(key, array);
        } else if (valueType.equals(TYPE_FLOAT)) {
            bundle.putFloat(key, (float)json.getDouble(JSON_VALUE));
        } else if (valueType.equals(TYPE_FLOAT_ARRAY)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            float[] array = new float[jsonArray.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = (float)jsonArray.getDouble(i);
            }
            bundle.putFloatArray(key, array);
        } else if (valueType.equals(TYPE_DOUBLE)) {
            bundle.putDouble(key, json.getDouble(JSON_VALUE));
        } else if (valueType.equals(TYPE_DOUBLE_ARRAY)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            double[] array = new double[jsonArray.length()];
            for (int i = 0; i < array.length; i++) {
                array[i] = jsonArray.getDouble(i);
            }
            bundle.putDoubleArray(key, array);
        } else if (valueType.equals(TYPE_CHAR)) {
            String charString = json.getString(JSON_VALUE);
            if (charString != null && charString.length() == 1) {
                bundle.putChar(key, charString.charAt(0));
            }
        } else if (valueType.equals(TYPE_CHAR_ARRAY)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            char[] array = new char[jsonArray.length()];
            for (int i = 0; i < array.length; i++) {
                String charString = jsonArray.getString(i);
                if (charString != null && charString.length() == 1) {
                    array[i] = charString.charAt(0);
                }
            }
            bundle.putCharArray(key, array);
        } else if (valueType.equals(TYPE_STRING)) {
            bundle.putString(key, json.getString(JSON_VALUE));
        } else if (valueType.equals(TYPE_STRING_LIST)) {
            JSONArray jsonArray = json.getJSONArray(JSON_VALUE);
            int numStrings = jsonArray.length();
            ArrayList<String> stringList = new ArrayList<String>(numStrings);
            for (int i = 0; i < numStrings; i++) {
                Object jsonStringValue = jsonArray.get(i);
                stringList.add(i, jsonStringValue == JSONObject.NULL ? null : (String)jsonStringValue);
            }
            bundle.putStringArrayList(key, stringList);
        }
    }
    */
}
