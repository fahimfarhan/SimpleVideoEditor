package com.fahimfarhan.simplevideoeditor.videoeditor;

import android.graphics.Bitmap;
import android.util.Size;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.fahimfarhan.simplevideoeditor.customview.AwesomeVideoView;
import com.fahimfarhan.simplevideoeditor.mp4composer.mp4compose.FilterType;

import idv.luchafang.videotrimmer.VideoTrimmerView;


public interface VideoEditorContract {
    interface View {
        VideoTrimmerView getVideoTrimmerView();
        VideoTrimmerView.OnSelectedRangeChangedListener getOnSelectedRangeChangedListener();
        ConstraintLayout getFilterSeekBarLayout();
        AwesomeVideoView getAwesomeVideoView();
        VideoEditorFragment2.CallBack getOnCallBack();
        void startWaitingFragment();
        void stopWaitingFragment();
    }

    interface Presenter {
        void startConversion();
        void initializePlayer();
        void hideSystemUi();
        void releasePlayer();
        void displayTrimmerView();
        Bitmap getThumbnail();
        void changeFilter(FilterType type);
        SeekBar.OnSeekBarChangeListener getOnFilterSeekBarChangedListener();
        Size getResolution();
        void setAspectRatio(VideoEditModel.AspectRatioType type);
    }
}
