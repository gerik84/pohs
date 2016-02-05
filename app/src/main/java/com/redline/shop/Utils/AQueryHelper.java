package com.redline.shop.Utils;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.redline.shop.R;

/**
 * Created by asdevel.com
 * Date: 26/12/14
 * Time: 21:00
 */
public class AQueryHelper {

	private final ImageView m_iv;
	private AQuery m_aq;
	private String m_url;
	private int m_width = Tools.minScreenSize() / 2;
	private ImageView.ScaleType m_fail_scaleType = ImageView.ScaleType.FIT_XY;
	private ImageView.ScaleType m_succ_ScaleType = ImageView.ScaleType.CENTER_CROP;
	private int m_fail_placeholder = R.drawable.placeholder_small;

	private int m_progress = 0;
	private Listener m_listener = null;

	private int m_fail_view = 0;
	private boolean m_fail_view_replace = false;
	private boolean m_cacheFile = true;
	private boolean m_cacheMemory = true;
	private boolean m_hideBefore = false;
	private int position = -1;
	private ViewGroup parent;
	private View convertView;

	public static class Listener {

		// return true if NO further processing necessary
		public boolean onFail(ImageView iv) {

			return false;
		}

		public boolean onSucc(ImageView iv, Bitmap bm) {

			return false;
		}

		public boolean onPreload(ImageView iv) {

			return false;
		}
	}

	public AQueryHelper(AQuery aq, ImageView iv, String url) {

		m_aq = aq;
		m_iv = iv;
		m_url = url;
	}

	public AQueryHelper failScale(ImageView.ScaleType s) {m_fail_scaleType = s; return this;}

	public AQueryHelper succScale(ImageView.ScaleType s) {m_succ_ScaleType = s; return this;}

	public AQueryHelper failDrawable(int res) {m_fail_placeholder = res; return this;}

	public AQueryHelper failView(int res, boolean bReplace) {m_fail_view = res; m_fail_view_replace = bReplace; return this;}

	public AQueryHelper progress(int res) {m_progress = res; return this;}

	public AQueryHelper listener(Listener l) {m_listener = l; return this;}

	public AQueryHelper useCache(boolean bFile, boolean bMemory) {m_cacheFile = bFile; m_cacheMemory = bMemory; return this;}

	public AQueryHelper hideImageViewBefore(boolean hide) {m_hideBefore = hide; return this;}

	public AQueryHelper shouldConsiderScrolling(int position, View convertView, ViewGroup parent) {

		this.position = position;
		this.convertView = convertView;
		this.parent = parent;

		return this;
	}

	public AQueryHelper width(int w) {

		Preconditions.checkState(w > 0);
		m_width = w;
		return this;
	}

	public void loadImage() {

		if (m_iv == null) return;

		onPreload(m_iv);
		if (m_url == null || m_url.isEmpty() || m_url.toLowerCase().endsWith(".gif")) {
			onFail(m_iv);
		}
		else {
            try {
                Bitmap cachedImage = m_aq.getCachedImage(m_url, m_width);
                if (cachedImage != null) {
                    onSucc(m_iv, cachedImage, true);
                }
                else if (position >= 0 && m_aq.shouldDelay(position, convertView, parent, m_url)) {
                    onFail(m_iv);
                }
                else {

                    onAsyncStarted(m_iv);

                    m_iv.setTag(AQuery.TAG_URL, m_url);

                    BitmapAjaxCallback cb = new BitmapAjaxCallback() {
                        @Override
                        protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {

                            if (bm == null && onFail(iv)) return;
                            if (bm != null && onSucc(iv, bm, false)) return;
                            super.callback(url, iv, bm, status);
                        }
                    };
                    cb.url(m_url);
                    cb.fileCache(m_cacheFile);
                    cb.memCache(m_cacheMemory);
                    cb.targetWidth(m_width);
                    cb.animation(AQuery.FADE_IN_NETWORK);

                    //if (m_fail_placeholder != 0) cb.fallback(m_fail_placeholder);
                    if (m_progress != 0) {m_aq.progress(m_progress);}
                    m_aq.id(m_iv).image(cb);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

		}
	}

	private void onAsyncStarted(ImageView iv) {

		if (m_fail_placeholder != 0) {
			if (m_fail_scaleType != null) iv.setScaleType(m_fail_scaleType);
			iv.setImageResource(m_fail_placeholder);
		}
	}

	private void onPreload(ImageView iv) {

		if (m_listener != null && m_listener.onPreload(iv)) {return;}
		if (m_hideBefore) Tools.hidev(iv);
		else Tools.showv(iv);
		if (m_fail_view != 0) {m_aq.id(m_fail_view).gone();}
		if (m_progress != 0) {m_aq.id(m_progress).visible();}
	}

	private boolean onSucc(ImageView iv, Bitmap bm, boolean loadImage) {

		if (m_listener != null && m_listener.onSucc(iv, bm)) {return true;}
		if (m_progress != 0) {m_aq.id(m_progress).gone();}
		if (m_hideBefore) Tools.showv(iv);
		iv.setScaleType(m_succ_ScaleType);

		if (loadImage) {
			m_aq.id(iv).image(bm);
		}

		return false;
	}

	private boolean onFail(ImageView iv) {

		if (m_listener != null && m_listener.onFail(iv)) {return true;}
		if (m_progress != 0) {m_aq.id(m_progress).gone();}
		iv.setScaleType(m_fail_scaleType);
		if (m_fail_view != 0) {
			m_aq.id(m_fail_view).visible();
			if (m_fail_view_replace) Tools.gonev(iv);
		}
		else if (m_fail_placeholder != 0) {
			iv.setImageResource(m_fail_placeholder);
		}

		return true;
	}
}
