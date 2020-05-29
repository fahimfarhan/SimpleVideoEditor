package com.fahimfarhan.simplevideoeditor.videoeditor;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fahimfarhan.simplevideoeditor.MainActivity;
import com.fahimfarhan.simplevideoeditor.R;
import com.fahimfarhan.simplevideoeditor.customview.AwesomeVideoView;
import com.fahimfarhan.simplevideoeditor.customview.GLGestureView;
import com.google.android.exoplayer2.source.ClippingMediaSource;
import com.google.android.exoplayer2.util.Util;

import java.util.Objects;

import idv.luchafang.videotrimmer.VideoTrimmerView;

public class VideoEditorFragment2 extends Fragment implements VideoEditorContract.View {
    public static final String TAG = VideoEditorFragment2.class.getSimpleName();
    private View fragmentRootView;
    private VideoEditorPresenter presenter;
    private GLGestureView glGestureView;
    private TextView kontinue;
    private Uri selectedVideoUri1;
    private RecyclerView editOptionsRecyclerView;
    private RecyclerView filterOptionsRecyclerView;
    private VideoTrimmerView videoTrimmerView;
    private TextView durationView;
    private ConstraintLayout trimConstraintLayout;
    private ConstraintLayout effectConstraintLayout;
    private ConstraintLayout formatConstraintLayout;
    private ConstraintLayout filterSeekBarLayout;
    private ConstraintLayout waitingConstraintLayout;

    private SeekBar filterSeekBar;

    private AwesomeVideoView awesomeVideoView;
    private GLGestureView.OnSizeChangeListener glGestureViewSizeChangeListener;
    private AwesomeVideoView.OnSizeChangeListener avvOnSizeChangeListener;
    private ImageView ivSquare;
    private ImageView ivPortrait;
    private ImageView ivLandscape;
    private View squareBackground;
    private View portraintBackGround;
    private View landscapeBackground;

    private Drawable videoCropSelected;
    private Drawable videoCropNotSelected;


    private final VideoTrimmerView.OnSelectedRangeChangedListener onSelectedRangeChangedListener = new VideoTrimmerView.OnSelectedRangeChangedListener() {
        @Override
        public void onSelectRangeStart() {
            presenter.exoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSelectRange(long startMillis, long endMillis) {
            showDuration(startMillis, endMillis);
        }

        @Override
        public void onSelectRangeEnd(long startMillis, long endMillis) {
            presenter.videoEditModel.mStartTimeMs = startMillis;
            presenter.videoEditModel.mEndTimeMs = endMillis;
            Log.e(TAG, "start = "+startMillis + " end = "+endMillis);
            showDuration(startMillis, endMillis);
            playVideo(startMillis, endMillis);
        }
    };

    private EditOptionsAdapter.WaitingForConversionListener waitingForConversionListener = this::isWaitingForVideoConversion;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.fragmentRootView = inflater.inflate(R.layout.fragment_videoeditor2, container, false);
        this.presenter = new VideoEditorPresenter(this, Objects.requireNonNull(getActivity()));
        this.presenter.videoEditModel.selectedVideoUri = selectedVideoUri1;
        initGui();
        return this.fragmentRootView;
    }

    private void initGui() {
        setupToolbar();
        this.awesomeVideoView = (AwesomeVideoView) findView(R.id.awesomeVideoView);
        this.glGestureView = awesomeVideoView.getGlGestureView();
        this.kontinue = (TextView) findView(R.id.kontinue); this.kontinue.setOnClickListener(onClickListener);
        this.trimConstraintLayout = (ConstraintLayout) findView(R.id.trimConstraintLayout);
        this.effectConstraintLayout = (ConstraintLayout) findView(R.id.filterConstraintLayout);
        this.formatConstraintLayout = (ConstraintLayout) findView(R.id.formatConstraintLayout);

        this.ivSquare = (ImageView) findView(R.id.ivSquare);        this.ivSquare.setOnClickListener(this.onClickListener);
        this.ivLandscape = (ImageView) findView(R.id.ivLangscape);  this.ivLandscape.setOnClickListener(this.onClickListener);
        this.ivPortrait = (ImageView) findView(R.id.ivPortrait);    this.ivPortrait.setOnClickListener(this.onClickListener);

        this.squareBackground = findView(R.id.squareBackground);
        this.portraintBackGround = findView(R.id.portraitBackground);
        this.landscapeBackground = findView(R.id.landscapeBackground);

        videoCropSelected = ContextCompat.getDrawable(getActivity(), R.drawable.video_crop_selected);
        videoCropNotSelected = ContextCompat.getDrawable(getActivity(), R.drawable.video_crop_notselected);

        this.presenter.videoEditModel.imageMaxHeight = 720; //dp2px(300, getActivity()); // todo: remove hardcoded text
        this.presenter.videoEditModel.imageMaxWidth  = 720; //dp2px(300, getActivity());

        this.editOptionsRecyclerView = (RecyclerView) findView(R.id.editOptionsRecyclerView);
        this.editOptionsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager editOptionsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        this.editOptionsRecyclerView.setLayoutManager(editOptionsLayoutManager);
        EditOptionsAdapter editOptionsAdapter = new EditOptionsAdapter(getActivity(), trimConstraintLayout, effectConstraintLayout, formatConstraintLayout, awesomeVideoView, waitingForConversionListener, presenter);
        this.editOptionsRecyclerView.setAdapter(editOptionsAdapter);

        this.videoTrimmerView = (VideoTrimmerView) findView(R.id.videoTrimmerView);  // <--
        this.durationView = (TextView) findView(R.id.durationView);                  // <-- Fix/Replace it later

        this.filterOptionsRecyclerView = (RecyclerView) findView(R.id.filterOptionsRecyclerView);
        this.filterOptionsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager filterOptionsLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        this.filterOptionsRecyclerView.setLayoutManager(filterOptionsLayoutManager);
        FilterOptionsAdapter filterOptionsAdapter = new FilterOptionsAdapter(getActivity(), presenter.getOnFilterChangeListener(), presenter.getThumbnail());
        this.filterOptionsRecyclerView.setAdapter(filterOptionsAdapter);

        this.filterSeekBarLayout = (ConstraintLayout) findView(R.id.filterSeekBarLayout);
        this.filterSeekBar = (SeekBar) findView(R.id.filterSeekBar);
        this.filterSeekBar.setOnSeekBarChangeListener(presenter.getOnFilterSeekBarChangedListener());

        this.glGestureViewSizeChangeListener = (width,height,oldw, oldh) -> {
            FrameLayout.LayoutParams lpForAvv = new FrameLayout.LayoutParams(width,height);
            lpForAvv.gravity = Gravity.CENTER;
            awesomeVideoView.getSimpleCropView().setLayoutParams(lpForAvv);
        };

        this.glGestureView.setOnSizeChangeListener(glGestureViewSizeChangeListener);

        this.avvOnSizeChangeListener = (width,height,oldw, oldh) -> {
            // if(video.height > video.width)
            //      glGestureView.setHeight(height);  sth like that using LayoutParameter
            Size resolution = presenter.getResolution();
            int videoHeight = resolution.getHeight();
            int videoWidth = resolution.getWidth();
            if(videoHeight > videoWidth) {
                double tempAspectRatio = 1.0*videoWidth / videoHeight;

                FrameLayout.LayoutParams lpForGgv = new FrameLayout.LayoutParams((int) (tempAspectRatio*height), height);
                lpForGgv.gravity = Gravity.CENTER;
                glGestureView.setLayoutParams(lpForGgv);
            }
        };
        this.awesomeVideoView.setOnSizeChangeListener(avvOnSizeChangeListener);
        this.waitingConstraintLayout = (ConstraintLayout) findView(R.id.waitingConstraintLayout);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated");
        this.presenter.displayTrimmerView();
    }

    public void setSelectedVideoUri(Uri uri) {
        selectedVideoUri1 = uri; //
    }

    private View findView(int viewId) { return fragmentRootView.findViewById(viewId);   }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) fragmentRootView.findViewById(R.id.toolbar);
        // add back arrow to toolbar
//        if(getActivity() instanceof MainActivity) {
//            MainActivity activity1 = (MainActivity) getActivity();
//            activity1.setSupportActionBar(toolbar);
//            if (activity1.getSupportActionBar() != null){
//                activity1.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                activity1.getSupportActionBar().setDisplayShowHomeEnabled(true);
//            }
//        }
    }

    @Override
    public boolean isWaitingForVideoConversion() {
        return (waitingConstraintLayout.getVisibility()==View.VISIBLE); // todo: for now I am keeping this. If necessary I'll change the logic
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isWaitingForVideoConversion()){  return; }
            int vid = v.getId();

            if(vid== kontinue.getId()) {
                presenter.startConversion();
            }else if(vid == ivSquare.getId()) {
                Log.e(TAG, "ivSquare clicked!");    // todo: change background and change aspect ratio
                // 1. change selected color
                squareBackground.setBackground(videoCropSelected);
                landscapeBackground.setBackground(videoCropNotSelected);
                portraintBackGround.setBackground(videoCropNotSelected);
                // 2. change aspect ratio
                presenter.setAspectRatio(VideoEditModel.AspectRatioType.SQUARE);

            }else if(vid == ivLandscape.getId()) {
                Log.e(TAG, "ivLandScape clicked!");
                // 1. change selected color
                landscapeBackground.setBackground(videoCropSelected);
                squareBackground.setBackground(videoCropNotSelected);
                portraintBackGround.setBackground(videoCropNotSelected);
                // 2. change aspect ratio
                presenter.setAspectRatio(VideoEditModel.AspectRatioType.LANDSCAPE);
            }else if(vid == ivPortrait.getId()) {
                Log.e(TAG, "ivPortrait clicked!");
                // 1. change selected color
                portraintBackGround.setBackground(videoCropSelected);
                squareBackground.setBackground(videoCropNotSelected);
                landscapeBackground.setBackground(videoCropNotSelected);
                // 2. change aspect ratio
                presenter.setAspectRatio(VideoEditModel.AspectRatioType.PORTRAIT);
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            this.awesomeVideoView = (AwesomeVideoView) findView(R.id.awesomeVideoView);
            this.glGestureView = awesomeVideoView.getGlGestureView();
            presenter.initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.hideSystemUi();
        if ((Util.SDK_INT < 24 || presenter.exoPlayer == null)) {
            this.awesomeVideoView = (AwesomeVideoView) findView(R.id.awesomeVideoView);
            this.glGestureView = awesomeVideoView.getGlGestureView();
            presenter.initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            presenter.releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            presenter.releasePlayer();
        }
    }

    @Override
    public VideoTrimmerView getVideoTrimmerView() {     return this.videoTrimmerView;   }

    @Override
    public VideoTrimmerView.OnSelectedRangeChangedListener getOnSelectedRangeChangedListener() {
        return this.onSelectedRangeChangedListener;
    }

    @Override
    public ConstraintLayout getFilterSeekBarLayout() {
        return filterSeekBarLayout;
    }

    @Override
    public AwesomeVideoView getAwesomeVideoView() { return this.awesomeVideoView; }

    private void showDuration(long startMillis, long endMillis) {
        long duration = (endMillis - startMillis) / 1000L;
        String s = ""+duration+" Sec";
        durationView.setText(s);
    }

    private void playVideo(long startMillis, long endMillis){
        long startPositionUs = startMillis*1000;
        long endPositionUs = endMillis*1000;
        ClippingMediaSource clippingMediaSource = new ClippingMediaSource(presenter.getMediaSource(), startPositionUs, endPositionUs );
//        exoPlayer.stop();
        presenter.exoPlayer.setPlayWhenReady(true);
        presenter.exoPlayer.prepare(clippingMediaSource);
    }

    @Override
    public ConstraintLayout getWaitingConstraintLayout() { return waitingConstraintLayout; }
}
