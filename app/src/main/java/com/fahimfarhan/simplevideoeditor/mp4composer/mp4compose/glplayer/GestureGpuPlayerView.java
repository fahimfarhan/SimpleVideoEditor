package com.fahimfarhan.simplevideoeditor.mp4composer.mp4compose.glplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.fahimfarhan.simplevideoeditor.mp4composer.guestures.AllGestureDetector;

public class GestureGpuPlayerView extends  GPUPlayerView implements View.OnTouchListener {

    private AllGestureDetector allGestureDetector;
    private float baseWidthSize = 0;


    public GestureGpuPlayerView(Context context) {
        super(context);
        init();
    }

    public GestureGpuPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        allGestureDetector = new AllGestureDetector(this);
        allGestureDetector.setLimitScaleMin(0.1f);
        allGestureDetector.noRotate();
    }

    public void setBaseWidthSize(float baseSize) {
        this.baseWidthSize = baseSize;
        requestLayout();
    }

    // View.OnTouchListener
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(allGestureDetector!=null){
            allGestureDetector.onTouch(event);
            return true;
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (videoAspect == DEFAULT_ASPECT || baseWidthSize == 0) return;

        if (videoAspect == 1.0f) {
            setMeasuredDimension((int) baseWidthSize, (int) baseWidthSize);
            return;
        }
        setMeasuredDimension((int) baseWidthSize, (int) (baseWidthSize / videoAspect));

    }
}
