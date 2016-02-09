package com.redline.shop.Interface.View;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AutoScrollViewPager extends RelativeLayout {

    public enum MODE {
        ASYNC,
        SYNC
    }
    private List<String> mListImage;
    private Handler handler = new Handler();
    Runnable runnable;
    private final int DELAY = 5000;
    private MODE m_mode = MODE.ASYNC;


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
            mAdapter = new PlaceSlidesFragmentAdapter(mListImage, MODE.SYNC);
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

    public void setMode(MODE mode) {
        m_mode = mode;
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

    public List<String> getImages() {
        return m_urls;
    }

    public void setImageUrl(List<String> listImage) {

//         if (m_mode == MODE.ASYNC) {
//            async(listImage);
//        } else {
//            syncWithAssets(listImage);
//        }

        mListImage = listImage;
        if (mAdapter != null && listImage != null) {
//            if (listImage.size() < 2) {
//                mIndicator.setVisibility(GONE);
//                handler.removeCallbacks(runnable);
//            } else {
//                mIndicator.setVisibility(VISIBLE);
//                handler.postDelayed(runnable, DELAY);
//            }
            mAdapter.setData(listImage);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void syncWithAssets(List<String> listImage) {

        for (int i = 0; i < listImage.size(); i++) {
            ImageView image = new ImageView(Tools.getApplicationContext());
            InputStream ims = null;
            try {
                ims = getResources().getAssets().open(listImage.get(i));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            image.setImageDrawable(d);
        }



    }

    private void async(List<String> listImage) {
        m_urls = listImage;
        for (int i = 0; i < listImage.size(); i++) {
            ImageView image = new ImageView(Tools.getApplicationContext());
            final AQuery aq = new AQuery(image);
            AQueryHelper qh = new AQueryHelper(aq, image, listImage.get(i))
                    .failDrawable(R.drawable.placeholder_big);
            qh.loadImage();
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
        private MODE m_mode = MODE.ASYNC;

        public PlaceSlidesFragmentAdapter(List<String> listImage, MODE mode) {
            imagesArray = listImage;
            m_mode = mode;
        }

        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView image = new ImageView(getContext());
            image.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            image.setAdjustViewBounds(false);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            final AQuery aq = new AQuery(image);
            if (m_mode == MODE.ASYNC) {
                AQueryHelper qh = new AQueryHelper(aq, image, imagesArray.get(position)).failDrawable(R.drawable.placeholder_big);
                qh.loadImage();
            } else {
                InputStream ims = null;
                try {
                    ims = getResources().getAssets().open(imagesArray.get(position));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // load image as Drawable
                Drawable d = Drawable.createFromStream(ims, null);
                // set image to ImageView
                image.setImageDrawable(d);
            }
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
