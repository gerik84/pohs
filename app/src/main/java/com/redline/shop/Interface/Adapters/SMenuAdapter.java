package com.redline.shop.Interface.Adapters;

import android.content.Context;

import com.redline.shop.R;

/**
 * Created by Redline on 05.02.2016.
 */
public class SMenuAdapter extends BaseStaticAdapter {


    public SMenuAdapter(Context ctx) {
        super(ctx);
    }

    @Override
    protected int setResourceId() {
        return R.layout.cell_main_menu;
    }
}
