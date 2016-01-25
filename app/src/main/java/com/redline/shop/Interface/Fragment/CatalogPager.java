package com.redline.shop.Interface.Fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import com.redline.shop.R;

/**
 * Created by Pavel on 25.01.2016.
 */
public class CatalogPager extends FragmentStatePagerAdapter {

    public CatalogPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }
}
