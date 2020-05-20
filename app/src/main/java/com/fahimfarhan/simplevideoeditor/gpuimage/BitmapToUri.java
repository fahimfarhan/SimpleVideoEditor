package com.fahimfarhan.simplevideoeditor.gpuimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BitmapToUri {
    private static final String VIDEO_THUMBNAIL_TITLE = "VIDEO_THUMBNAIL_TITLE";
    public static Uri getImageUriFromBitmap(Context context, Bitmap bitmap){
        OutputStream bytesOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytesOutputStream);
        try {
            bytesOutputStream.close();
        } catch (IOException e) { e.printStackTrace(); }

        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, VIDEO_THUMBNAIL_TITLE, null);
        return Uri.parse(path.toString());

    }
}
