package com.fahimfarhan.simplevideoeditor.mp4composer.guestures;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PortraitView extends FrameLayout {
    public PortraitView(@NonNull Context context) {
        super(context);
    }

    public PortraitView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PortraitView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight();
        setMeasuredDimension((int) (height / 16f * 9), height);
    }

}
