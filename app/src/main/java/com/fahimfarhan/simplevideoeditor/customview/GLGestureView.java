package com.fahimfarhan.simplevideoeditor.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.fahimfarhan.simplevideoeditor.mp4composer.guestures.AllGestureDetector;
import com.fahimfarhan.simplevideoeditor.mp4composer.mp4compose.glplayer.GPUPlayerView;


public class GLGestureView extends GPUPlayerView implements View.OnTouchListener {
    private AllGestureDetector allGestureDetector;
    boolean isTouchEnagled = true;
    private OnSizeChangeListener listener;

    public GLGestureView(Context context) {
//        super(context);
        this(context,null);
    }

    public GLGestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.allGestureDetector = new AllGestureDetector(this);
        allGestureDetector.setLimitScaleMin(0.1f);
        allGestureDetector.noRotate();
        allGestureDetector.setLimitScaleMax(2f);
        isTouchEnagled = true;
        this.setOnTouchListener(this);
    }

    public void setTouchEnagled(boolean isTouchEnagled) {   this.isTouchEnagled = isTouchEnagled;   }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(isTouchEnagled){
            this.allGestureDetector.onTouch(event);
            return true;
        }
        return false;
    }

    @Override
    public void onSurfaceSizeChanged(int width, int height) {
        super.onSurfaceSizeChanged(width,height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(this.listener !=null) listener.doOnSizeChange(w,h, oldw, oldh);  // if nothing happens, try with onSurfaceSIzeCHanged
    }

    public void setOnSizeChangeListener(OnSizeChangeListener listener1) { this.listener = listener1; }

    public interface OnSizeChangeListener{
        void doOnSizeChange(int width, int height, int oldwidth, int oldheight);
    }
}
