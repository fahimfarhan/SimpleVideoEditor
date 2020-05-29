package com.fahimfarhan.simplevideoeditor.videoeditor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.fahimfarhan.simplevideoeditor.MainActivity;
import com.fahimfarhan.simplevideoeditor.customview.AwesomeVideoView;
import com.fahimfarhan.simplevideoeditor.customview.GLGestureView;
import com.fahimfarhan.simplevideoeditor.mp4composer.PathUtil;
import com.fahimfarhan.simplevideoeditor.mp4composer.mp4compose.FillMode;
import com.fahimfarhan.simplevideoeditor.mp4composer.mp4compose.FillModeCustomItem;
import com.fahimfarhan.simplevideoeditor.mp4composer.mp4compose.FilterAdjuster;
import com.fahimfarhan.simplevideoeditor.mp4composer.mp4compose.FilterType;
import com.fahimfarhan.simplevideoeditor.mp4composer.mp4compose.composer.Mp4Composer;
import com.fahimfarhan.simplevideoeditor.mp4composer.mp4compose.filter.GlFilter;
import com.fahimfarhan.simplevideoeditor.mp4composer.mp4compose.filter.GlFilterGroup;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;


import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class VideoEditorPresenter implements VideoEditorContract.Presenter {

    public static final String TAG = VideoEditorPresenter.class.getSimpleName();
    public VideoEditModel videoEditModel;
    public SimpleExoPlayer exoPlayer;
    public MediaSource mediaSource;
    private VideoEditorContract.View videoEditContractView;
    private Activity activity;
    private FilterAdjuster adjuster;
    private final MediaMetadataRetriever retriever = new MediaMetadataRetriever();
    private FilterOptionsAdapter.FilterChangeListener onFilterChangeListener;
    private SeekBar.OnSeekBarChangeListener OnFilterSeekBarChangedListener;
    private Mp4Composer mp4Composer;
    private boolean isFirstTimeInitTrimmer = false;

    public VideoEditorPresenter(VideoEditorContract.View videoEditContractView, Activity activity) {
        this.videoEditContractView = videoEditContractView;
        this.activity = activity;
        this.videoEditModel = new VideoEditModel();
        this.exoPlayer = new SimpleExoPlayer.Builder(activity).build();
        this.isFirstTimeInitTrimmer = true;
        this.onFilterChangeListener = new FilterOptionsAdapter.FilterChangeListener() {
            @Override
            public void onFilterChange(FilterType selectedFilterType) {
                changeFilter(selectedFilterType);
            }

            @Override
            public boolean isWaitingForConversion() {
                return videoEditContractView.isWaitingForVideoConversion();
            }
        };
    }

    public FilterOptionsAdapter.FilterChangeListener getOnFilterChangeListener() { return onFilterChangeListener; }

    @Override
    public void startConversion() {
        // -1. make waitingLayout visible
        videoEditContractView.getWaitingConstraintLayout().setVisibility(VISIBLE);
        // 0. mp4converter code
        if (videoEditModel.sourcePath == null) {
            try {
                videoEditModel.sourcePath = PathUtil.getPath(activity, videoEditModel.selectedVideoUri);
            } catch (URISyntaxException e) {    e.printStackTrace();    }
        }
        // todo: fix this logic later cz with videoSizevariable and cropSize const, the math slightly changes
        // 1. get p0..3
        //   p0 --- p1
        //   |      |
        //   |      |    <-- height
        //   p2 --- p3
        //       width
        AwesomeVideoView myAwesomeVideoView = videoEditContractView.getAwesomeVideoView();
        GLGestureView glGestureView = myAwesomeVideoView.getGlGestureView();
        Point[] points = myAwesomeVideoView.getSimpleCropView().getPoints();

        //1.
        float baseWidthPx = points[1].x - points[0].x;    // should be +// todo: match every views one side , then set that side. need an if-else
        float  baseHeightPx = (baseWidthPx / VideoEditModel.aspectRatio);
        // 2. get the center of crop quad
        float  cropQuadCenterX, cropQuadCenterY;
        cropQuadCenterX = points[0].x + baseWidthPx/2;
        cropQuadCenterY = points[0].y + baseHeightPx/2;

        // 3. get video frame quad center
        float  videoQuadCenterX, videoQuadCenterY;
        videoQuadCenterX = glGestureView.getWidth()/2;
        videoQuadCenterY = glGestureView.getHeight()/2;

        float dx = videoQuadCenterX - cropQuadCenterX;
        float dy = videoQuadCenterY - cropQuadCenterY;

        dx = 2*dx/baseWidthPx;  // idk why multiply with 2 :/ divide with baseSizePx to get ratio, that makes sense but why 2???
        dy = 2*dy/baseHeightPx;

        if(videoEditModel.resolution == null)
            videoEditModel.resolution = getVideoResolution(videoEditModel.sourcePath);

        float translateX = dx, translateY = dy;
        float scale = myAwesomeVideoView.getSimpleCropView().getMyScalingX(); // todo: scale = pointsPrevDx / pointsNewDx
        float rotation = glGestureView.getRotation();    // should be 0

        Log.e(TAG, "translateX = "+translateX + " translateY = "+translateX );
        Log.e(TAG, "resolution.width = "+videoEditModel.resolution.getWidth()+" resolution.getHeight = "+videoEditModel.resolution.getHeight());

        FillModeCustomItem fillModeCustomItem = new FillModeCustomItem(
                scale,
                rotation,
                translateX,
                translateY,
                videoEditModel.resolution.getWidth() ,
                videoEditModel.resolution.getHeight()
        );

//        new Mp4Composer( (String srcPath) srcMp4Path, (String destPath) destMp4Path) // todo: there are alternatives though
        GlFilterGroup glFilterGroup;
        if(videoEditModel.composerGlFilter == null) {   videoEditModel.composerGlFilter = new GlFilter();   }

        glFilterGroup = new GlFilterGroup(new GlFilter()); // the default filter
        Log.e(TAG, "currentGlFilter is null");

        String destPath = getVideoFilePath();

        int outputVideoWidthPx = 720;    // 720 for now change later
        int outputVideoHeightPx = (int) (outputVideoWidthPx/VideoEditModel.aspectRatio);

        mp4Composer = null;
        mp4Composer = new Mp4Composer(videoEditModel.selectedVideoUri, destPath, activity, videoEditModel.mylogger);
        mp4Composer
                .size(outputVideoWidthPx, outputVideoHeightPx) // fake it till you make it
                .fillMode(FillMode.CUSTOM)
                .customFillMode(fillModeCustomItem);
        if(videoEditModel.composerGlFilter != null){
                mp4Composer.filter(glFilterGroup);
        }
        mp4Composer.trim(videoEditModel.mStartTimeMs, videoEditModel.mEndTimeMs)
                .listener(new Mp4Composer.Listener() {
                    @Override
                    public void onProgress(double progress) {
                        Log.d(TAG, "onProgress = " + progress);
                    }

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted()");
                        activity.runOnUiThread(() -> {
                            videoEditContractView.getWaitingConstraintLayout().setVisibility(GONE);
                            Toast.makeText(activity, "codec complete path = " + destPath, Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "codec complete path = " + destPath);
                        });

                        try{
                            // I need the try catch cz the Activity may not be an instance of VideoEditorActivity
                            // todo: what should be the appropriate way to write this code segment? o.O
                            MainActivity mainActivity = (MainActivity) activity;
                            mainActivity.onSuccess(destPath);
                        }catch (Exception x){
                            x.printStackTrace();
                        }
                    }

                    @Override
                    public void onCanceled() {
                        Log.d(TAG, "onCanceled");
                        Toast.makeText(activity, "videoProcessing onCanceled", Toast.LENGTH_SHORT).show();
                        videoEditContractView.getWaitingConstraintLayout().setVisibility(GONE);
                    }

                    @Override
                    public void onFailed(Exception exception) {
                        Log.e(TAG, "onFailed()", exception);
                        Toast.makeText(activity, "videoProcessing onFailure", Toast.LENGTH_SHORT).show();
                        videoEditContractView.getWaitingConstraintLayout().setVisibility(GONE);
                    }
                });

        mp4Composer.start();
    }

    @Override
    public Size getResolution() {
        if(videoEditModel.resolution == null)
            videoEditModel.resolution = getVideoResolution(videoEditModel.sourcePath);
        return videoEditModel.resolution;
    }

    @Override
    public void initializePlayer() {
        if(this.videoEditModel.selectedVideoUri == null) {  return; }
        if(this.exoPlayer == null) {
            this.exoPlayer = new SimpleExoPlayer.Builder(activity).build();
        }
        videoEditContractView.getAwesomeVideoView().getGlGestureView().setSimpleExoPlayer(this.exoPlayer);
        this.mediaSource = buildMediaSource(this.videoEditModel.selectedVideoUri);

        this.exoPlayer.setPlayWhenReady(videoEditModel.playWhenReady);
        this.exoPlayer.seekTo(videoEditModel.currentWindow, videoEditModel.playbackPosition);

        this.exoPlayer.prepare(this.mediaSource, false, false);  // original line
        this.exoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);
        this.videoEditModel.mDurationMs = this.exoPlayer.getDuration();
//        String videoPath = getRealPathFromMediaData(selectedVideoUri); //todo: uncomment later
//        displayTrimmerView(); //todo: uncomment later still error. /storage/emulated/0/Movies/20200420-110158.mp4  should be in this formay

//        baseWidthSize = (getWindowHeight(getActivity()) - dp2px(192, getActivity())) / 16f * 9; //todo: uncomment later
        if(this.videoEditModel.previewGlFilter!=null)
            videoEditContractView.getAwesomeVideoView().getGlGestureView().setGlFilter(this.videoEditModel.previewGlFilter);
    }

    @Override
    public void hideSystemUi() {
        videoEditContractView.getAwesomeVideoView().getGlGestureView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void releasePlayer() {
        if(exoPlayer != null ){
            videoEditModel.playWhenReady = exoPlayer.getPlayWhenReady();
            videoEditModel.playbackPosition = exoPlayer.getCurrentPosition();
            videoEditModel.currentWindow = exoPlayer.getCurrentWindowIndex();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    public Size getVideoResolution(String path) {  // todo: replace Size with Pair<int, int> to support api 19
        Log.e(TAG, "path = " + path);
        retriever.setDataSource(path);
        int width = Integer.parseInt(
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
        );
        int height = Integer.parseInt(
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
        );
        retriever.release();
//        int rotation = getVideoRotation(path);
//        if (rotation == 90 || rotation == 270) {
//            return new Size(height, width);
//        }
        return new Size(width, height);
    }

    public File getAndroidMoviesFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
    }

    @SuppressLint("SimpleDateFormat")
    public String getVideoFilePath() {
        return getAndroidMoviesFolder().getAbsolutePath() + "/" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".mp4";
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(activity, TAG); // "exoplayer2-fragment"
        return new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
    }

    public MediaSource getMediaSource() {
        if(mediaSource == null) {   mediaSource = buildMediaSource(videoEditModel.selectedVideoUri);    }
        return mediaSource;
    }

    /* VideoTrimmer */

    @Override
    public void displayTrimmerView() {
        if(!isFirstTimeInitTrimmer) {   return;  }
        Log.e(TAG, "VideoEditorPresenter#displayTrimmerView()");
        videoEditModel.sourcePath = getRealPathFromMediaData(videoEditModel.selectedVideoUri) ; // videoEditModel.sourcePath;
        String path = videoEditModel.sourcePath;
        // todo: check the difference between these 2 methods. If both give the same output, remove one to reduce redundant codes
//        try {
//            videoEditModel.sourcePath = PathUtil.getPath(activity,videoEditModel.selectedVideoUri); // getRealPathFromMediaData(videoEditModel.selectedVideoUri) ; // videoEditModel.sourcePath;
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
        Log.e(TAG, "getRealPathFromMediaData() path = " + path);
        File file = new File(path);
        Log.e(TAG, "filePath = " + file.getAbsolutePath());
        videoEditContractView.getVideoTrimmerView()
                .setVideo(file)
                .setMaxDuration(30_000)
                .setMinDuration(3_000)
                .setFrameCountInWindow(8)
                .setExtraDragSpace(10)
                .setOnSelectedRangeChangedListener(videoEditContractView.getOnSelectedRangeChangedListener())
                .show();

        isFirstTimeInitTrimmer = false;
    }

    private float dpToPx(float dp) {
        float density = activity.getResources().getDisplayMetrics().density;
        return dp*density;
    }

    private String getRealPathFromMediaData(Uri data) {
        if(data == null) {  return null;  }
        Cursor cursor = null;

        String videoPath = null;
        final ContentResolver contentResolver = Objects.requireNonNull(activity).getContentResolver();
        try{
            cursor = contentResolver.query(data,new String[]{MediaStore.Video.Media.DATA}, null, null, null);
            int col = Objects.requireNonNull(cursor).getColumnIndex(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            videoPath = cursor.getString(col);
        }catch (Exception x){
            x.printStackTrace();
        }finally{
            if(cursor!=null) {  cursor.close();  }
        }
        return videoPath;
    }

    @Override
    public Bitmap getThumbnail() {
        if(videoEditModel.thumbnail == null) {
            try {
                videoEditModel.sourcePath = PathUtil.getPath(activity, videoEditModel.selectedVideoUri);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            videoEditModel.thumbnail = ThumbnailUtils.createVideoThumbnail(videoEditModel.sourcePath, MediaStore.Video.Thumbnails.MINI_KIND);
        }
        return videoEditModel.thumbnail;
    }

    @Override
    public void changeFilter(FilterType type) {
        videoEditModel.currentFilterType = type;
        videoEditModel.composerGlFilter = null;
        videoEditModel.composerGlFilter = FilterType.createGlFilter(videoEditModel.currentFilterType, activity); // initial value
        videoEditModel.previewGlFilter = null;
        videoEditModel.previewGlFilter = FilterType.createGlFilter(videoEditModel.currentFilterType, activity);
        adjuster = FilterType.createFilterAdjuster(videoEditModel.currentFilterType);
        videoEditContractView.getFilterSeekBarLayout().setVisibility( adjuster==null?GONE:VISIBLE);
        if(videoEditModel.previewGlFilter!=null)
            videoEditContractView.getAwesomeVideoView().getGlGestureView().setGlFilter(videoEditModel.previewGlFilter);
    }

    @Override
    public SeekBar.OnSeekBarChangeListener getOnFilterSeekBarChangedListener() {
        OnFilterSeekBarChangedListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(adjuster!=null) {  adjuster.adjust(videoEditModel.previewGlFilter,progress); }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        };
        return OnFilterSeekBarChangedListener;
    }

    @Override
    public void setAspectRatio(VideoEditModel.AspectRatioType type) {
        // 1. change the ratio in model
        // 2. change ratio in the cropView
        switch (type){
            case SQUARE:{
                // 1
                VideoEditModel.ratioX = 1;
                VideoEditModel.ratioY = 1;
                VideoEditModel.aspectRatio = VideoEditModel.ratioX/VideoEditModel.ratioY;
                break;
            }case PORTRAIT:{
                // 1
                VideoEditModel.ratioX = 3;
                VideoEditModel.ratioY = 4;
                VideoEditModel.aspectRatio = VideoEditModel.ratioX/VideoEditModel.ratioY;
                break;
            }case LANDSCAPE:{
                // 1
                VideoEditModel.ratioX = 4;
                VideoEditModel.ratioY = 3;
                VideoEditModel.aspectRatio = VideoEditModel.ratioX/VideoEditModel.ratioY;
                break;
            }
        }
        // 2
        videoEditContractView.getAwesomeVideoView()
                .getSimpleCropView()
                .resetAspectRatio(VideoEditModel.ratioX, VideoEditModel.ratioY);
    }
}
