package com.omi.socketiochat.main_activity.custom_views;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by omar on 1/29/18.
 */

public class TextViewCustom extends android.support.v7.widget.AppCompatTextView{
    public TextViewCustom(Context context) {
        super(context);
        init(context);
    }

    public TextViewCustom(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextViewCustom(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Typeface type = Typeface.createFromAsset(context.getAssets(),"fonts/Aerolite Sky.otf");
        setTypeface(type);
    }

}
