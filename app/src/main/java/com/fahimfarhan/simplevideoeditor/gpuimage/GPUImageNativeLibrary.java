package com.fahimfarhan.simplevideoeditor.gpuimage;

import android.graphics.Bitmap;

public class GPUImageNativeLibrary {
    static {
        System.loadLibrary("yuv-decoder");
    }

    public static native void YUVtoRBGA(byte[] yuv, int width, int height, int[] out);

    public static native void YUVtoARBG(byte[] yuv, int width, int height, int[] out);

    public static native void adjustBitmap(Bitmap srcBitmap);
}
