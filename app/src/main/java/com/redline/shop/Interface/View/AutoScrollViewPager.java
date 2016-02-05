package com.redline.shop.Interface.View;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.androidquery.AQuery;
import com.redline.shop.R;
import com.redline.shop.Utils.AQueryHelper;
import com.redline.shop.Utils.Tools;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

public class AutoScrollViewPager extends RelativeLayout {

    private List<String> mListImage;
    private Handler handler = new Handler();
    Runnable runnable;
    private final int DELAY = 5000;

    public AutoScrollViewPager(Context context) {
        super(context);
        init();
    }

    public AutoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    PlaceSlidesFragmentAdapter mAdapter;
    ViewPager pager;
    CirclePageIndicator mIndicator;

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.auto_scroll_view_pager, this);
        try {
            pager = getPager();
            mIndicator = getPagerIndicator();
            mAdapter = new PlaceSlidesFragmentAdapter(mListImage);
            if (pager != null) {
                runnable = new Runnable() {
                    public void run() {
                        int page = pager.getCurrentItem() + 1 < pager.getAdapter().getCount() ? pager.getCurrentItem() + 1 : 0;
                        pager.setCurrentItem(page);
                        handler.postDelayed(this, DELAY);
                    }
                };
                pager.setAdapter(mAdapter);
                pager.setCurrentItem(0);
            }

            if (mIndicator != null && pager != null) {
                mIndicator.setViewPager(getPager());
                mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        handler.removeCallbacks(runnable);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                        handler.postDelayed(runnable, DELAY * 2);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == GONE || visibility == INVISIBLE)
            handler.removeCallbacks(runnable);
        else
            handler.postDelayed(runnable, DELAY);
    }

    private ViewPager getPager() {
        return (ViewPager) findViewById(R.id.viewPager);
    }

    private CirclePageIndicator getPagerIndicator() {
        return (CirclePageIndicator) findViewById(R.id.indicator);
    }

    private List<String> m_urls = new ArrayList<>();

    public void addImage(String url) {
        m_urls.add(url);
        setImageUrl(m_urls);
    }

    public void setImageUrl(List<String> listImage) {
        for (int i = 0; i < listImage.size(); i++) {
            ImageView image = new ImageView(Tools.getApplicationContext());
            final AQuery aq = new AQuery(image);
            AQueryHelper qh = new AQueryHelper(aq, image, listImage.get(i)).failDrawable(R.drawable.placeholder_big);
            qh.loadImage();
        }
        mListImage = listImage;
        if (mAdapter != null && listImage != null) {
            if (listImage.size() < 2) {
                mIndicator.setVisibility(GONE);
                handler.removeCallbacks(runnable);
            } else {
                mIndicator.setVisibility(VISIBLE);
                handler.postDelayed(runnable, DELAY);
            }
            mAdapter.setData(listImage);
            mAdapter.notifyDataSetChanged();
        }
    }

//    private HashMap<View, String> fillData(List<String> data) {
//        HashMap<View, String> dataView = new HashMap<>();
//        if(data.size() > 0) {
//            for(String url : data) {
//                ImageView image = new ImageView(getContext());
//                image.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//                image.setAdjustViewBounds(true);
//                image.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                dataView.put(image, url);
//            }
//
//        }
//        return dataView;
//    }

    public class PlaceSlidesFragmentAdapter extends PagerAdapter {

        List<String> imagesArray;

        public PlaceSlidesFragmentAdapter(List<String> listImage) {
            imagesArray = listImage;
        }

        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView image = new ImageView(getContext());
            image.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            image.setAdjustViewBounds(true);
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            final AQuery aq = new AQuery(image);
            AQueryHelper qh = new AQueryHelper(aq, image, imagesArray.get(position)).failDrawable(R.drawable.placeholder_big);
            qh.loadImage();
            container.addView(image, 0);
            return image;
        }

        public void setData(List<String> data) {
            imagesArray = data;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return imagesArray == null ? 0 : imagesArray.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }
    }

}
