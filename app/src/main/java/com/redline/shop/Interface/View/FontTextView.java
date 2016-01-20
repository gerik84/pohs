package com.redline.shop.Interface.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.redline.shop.R;
import com.redline.shop.Utils.Tools;


/**
 * Created by Circular on 17.06.2015.
 */
public class FontTextView extends TextView {

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.FontTextView, 0, 0);
        String font = a.getString(R.styleable.FontTextView_font);

        if (font == null || font.length() < 1)
            font = Tools.getFontRegular();

        Typeface fontFace = Typeface.createFromAsset(context.getAssets(), "fonts/" + font);
        this.setTypeface(fontFace);
        a.recycle();
    }


}
