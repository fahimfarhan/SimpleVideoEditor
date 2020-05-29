package com.fahimfarhan.simplevideoeditor.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fahimfarhan.simplevideoeditor.R;


/**
 * 1. create R.layout.CropVUdeoView
 * 2. link resources, add videoPlayerView(or whatever it i called)
 * 3. VVI: DETECT crop area size!!!
 * */
public class AwesomeVideoView extends FrameLayout {
    private final int CROP_STATE = 0;
    private final int NON_CROP_STATE = 1;
    int STATE = NON_CROP_STATE;

    private View rootView;
    private GLGestureView glGestureView;
    private SimpleCropView simpleCropView;
    private OnSizeChangeListener onSizeChangeListener;
//    private SimpleCropView simpleCropView;
    private int awesomeVideoViewHeight = 0;
    private int awesomeVideoViewWidth = 0;

    public AwesomeVideoView(@NonNull Context context) {
        this(context,null);
    }

    public AwesomeVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context,attrs);
        // todo: write code here!

        LayoutInflater inflater = LayoutInflater.from(context);
        rootView = inflater.inflate(R.layout.awesome_video_view, this, true);
        glGestureView = rootView.findViewById(R.id.glGestureView);
        glGestureView.setTouchEnagled(false);

        simpleCropView = rootView.findViewById(R.id.cropOverlayView);
    }

    public GLGestureView getGlGestureView() { return glGestureView; } // I think we'll need these getters
    public SimpleCropView getSimpleCropView() {   return simpleCropView; }


    public void onCropState() {
        if(STATE != CROP_STATE) {
            STATE = CROP_STATE;
            changeClickableState(simpleCropView, true);

//            imageEditorView.setVisibility(GONE);
//            videoEditorHud.setVisibility(GONE);
//            changeClickableState(imageEditorView, false); // todo: try to fix it
//            changeClickableState(videoEditorHud, false);
        }
    }

    public void onNonCropState() {
        if(STATE != NON_CROP_STATE) {
            STATE = NON_CROP_STATE;
            changeClickableState(simpleCropView,false);

//            imageEditorView.setVisibility(VISIBLE);
//            videoEditorHud.setVisibility(VISIBLE);
//            changeClickableState(imageEditorView, true);
//            changeClickableState(videoEditorHud, true);
        }
    }

    private void changeClickableState(View inputView, boolean isClickable){
        inputView.setClickable(isClickable);
        inputView.setFocusable(isClickable);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.awesomeVideoViewHeight = h;
        this.awesomeVideoViewWidth = w;
        if(this.onSizeChangeListener != null) {
            this.onSizeChangeListener.doOnSizeChange(w,h,oldw, oldh);
        }

    }

    public void setOnSizeChangeListener(OnSizeChangeListener listener){
        this.onSizeChangeListener = listener;
    }

    public int getAwesomeVideoViewHeight() {    return awesomeVideoViewHeight;   }

    public int getAwesomeVideoViewWidth() {     return awesomeVideoViewWidth;    }

    public interface OnSizeChangeListener{
        void doOnSizeChange(int width, int height, int oldwidth, int oldheight);
    }
}
