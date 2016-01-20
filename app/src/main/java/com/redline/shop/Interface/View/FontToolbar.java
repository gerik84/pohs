package com.redline.shop.Interface.View;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.redline.shop.R;

/**
 * Created by Pavel on 20.01.2016.
 */
public class FontToolbar extends android.support.v7.widget.Toolbar {

    private FontTextView mTitle;

    public FontToolbar(Context context) {
        super(context);
    }

    public FontToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void setTitle(CharSequence title) {
        super.setTitle("");

        mTitle = (FontTextView) findViewById(R.id.toolbar_title);
        if (mTitle != null) {
            if (!TextUtils.isEmpty(title)) {
                mTitle.setText(title);
            }
        }
    }
}
