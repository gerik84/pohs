package com.redline.shop.Interface.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.redline.shop.Interface.View.AutoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends TestAdapter {

    AutoScrollViewPager pager;
    List<String> list;

    public BannerAdapter(Context context) {
        super(context);
        pager = new AutoScrollViewPager(context);

        list = new ArrayList<>();
        list.add("http://static.adzerk.net/Advertisers/af217662e49a4cbda030feae88418cdd.png");
        list.add("http://static.adzerk.net/Advertisers/af217662e49a4cbda030feae88418cdd.png");
        list.add("http://static.adzerk.net/Advertisers/af217662e49a4cbda030feae88418cdd.png");
        list.add("http://static.adzerk.net/Advertisers/af217662e49a4cbda030feae88418cdd.png");
        pager.setImageUrl(list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        




        return pager;
    }
}
