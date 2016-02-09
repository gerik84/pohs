package com.redline.shop.Interface.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.redline.shop.Interface.View.AutoScrollViewPager;
import com.redline.shop.R;
import com.redline.shop.Utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class BannerAdapter extends TestAdapter {

    AutoScrollViewPager pager;
    List<String> list = new ArrayList<>();

    public BannerAdapter(Context context) {
        super(context);

//        getContext().getAssets().open()

//        list.add("http://www.bugaga.ru/uploads/posts/2015-10/1445539642_kartinki-8.jpg");
//        list.add("http://www.bugaga.ru/uploads/posts/2015-10/1445539642_kartinki-8.jpg");
//        list.add("http://www.bugaga.ru/uploads/posts/2015-10/1445539642_kartinki-8.jpg");
//        list.add("http://www.bugaga.ru/uploads/posts/2015-10/1445539642_kartinki-8.jpg");

        list.add("images/banner/1.jpg");
        list.add("images/banner/2.png");
        list.add("images/banner/3.png");
        list.add("images/banner/4.png");
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.inc_banner, null);


        AutoScrollViewPager carousel = (AutoScrollViewPager) convertView.findViewById(R.id.asp_carousel);
        if (carousel.getImages() == null || carousel.getImages().size() == 0) {
            carousel.setMode(AutoScrollViewPager.MODE.SYNC);
            carousel.setImageUrl(list);
        }

        return convertView;
    }
}
