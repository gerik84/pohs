package com.redline.shop.Interface;

import java.lang.reflect.Type;

public class HandlerParketUrl extends HandlerBaseURL {
    public static String BASE_URL = "";

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[0];
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    @Override
    public Type getRawType() {
        return null;
    }
}
