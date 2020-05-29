package com.fahimfarhan.simplevideoeditor.videoeditor;


import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.util.Size;

import androidx.annotation.Nullable;

import com.fahimfarhan.simplevideoeditor.mp4composer.mp4compose.FilterType;
import com.fahimfarhan.simplevideoeditor.mp4composer.mp4compose.filter.GlFilter;
import com.fahimfarhan.simplevideoeditor.mp4composer.mp4compose.logger.Logger;


/**
 * @brief: this model will hold the values of different edit parameters
 *         eg, trimStart, trimEnd, currentFilter, etc...
 * */
public class VideoEditModel {
    // todo: add variables accordingly. Nothing stays in the VideoEditFragment2
    public static float ratioX = 1;
    public static float ratioY = 1;
    public static float aspectRatio = ratioX/ratioY;  // todo: maybe add getter, setter for aspect ratio to be used with onClick changeAspectRatio...
                                                      //       making them static for convenience/ testing. change if necessary
    public Size resolution = null;
    boolean playWhenReady = true;
    int currentWindow = 0;
    long playbackPosition = 0;

    Uri selectedVideoUri=null;

    long mStartTimeMs = 0;
    long mEndTimeMs = 0;
    long mDurationMs = 0;
    private float baseWidthSize = 0;

    private String destPath = null; // "// some path";

    FilterType currentFilterType = null; //  = filters[0];
    GlFilter composerGlFilter = null;  // <-- use with the mp4composer
    GlFilter previewGlFilter = null;  // <-- use with glGestureView
    String sourcePath = null;

    int imageMaxHeight = 0;
    int imageMaxWidth = 0;
    public Bitmap thumbnail;
    final Logger mylogger = new Logger() {
        @Override
        public void debug(String tag, String message) {
            Log.d(tag, message);
        }

        @Override
        public void error(String tag, String message, Throwable error) {
            Log.e(tag, "Message: "+message + ". Error: "+error.getLocalizedMessage());
        }

        @Override
        public void warning(String tag, String message) {
            Log.w(tag, message);
        }
    };

    public enum AspectRatioType{
        SQUARE, PORTRAIT,LANDSCAPE
    };
}
