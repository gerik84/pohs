package com.redline.shop.Interface.Adapters;

import android.content.Context;

import com.redline.shop.R;

/**
 * Created by Redline on 09.02.2016.
 */
public class NewGoodsAdapter extends BaseStaticAdapter {

    public NewGoodsAdapter(Context ctx) {
        super(ctx);
    }

    @Override
    protected int setResourceId() {
        return R.layout.cell_new_goods;
    }
}
