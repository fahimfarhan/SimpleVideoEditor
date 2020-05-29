package com.fahimfarhan.simplevideoeditor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fahimfarhan.simplevideoeditor.mp4composer.PathUtil;
import com.fahimfarhan.simplevideoeditor.videoeditor.VideoEditorFragment2;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_CODE = 7777;
    private static final int REQUEST_VIDEO_TRIMMER = 0x01;
    private FrameLayout baseFrameLayout;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGui();

        TextView start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(permissionsAreGranted()) {
                    pickFromGallery();
                }else{
                    requestPermissions();
                }
            }
        });

        if(permissionsAreGranted()) {
            pickFromGallery();
        }else{
            requestPermissions();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSIONS_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                pickFromGallery();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_VIDEO_TRIMMER) {
                final Uri selectedUri = data.getData();

                if (selectedUri != null) {
                    VideoEditorFragment2 videoEditorFragment2 = new VideoEditorFragment2();
                    videoEditorFragment2.setSelectedVideoUri(selectedUri);
                    String path = null;
                    try {
                        path = PathUtil.getPath(MainActivity.this, selectedUri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, "path = "+path);

//                    GestureFilterFragment gestureFilterFragment = new GestureFilterFragment();
//                    gestureFilterFragment.setSelectedVideoUri(selectedUri);
                    loadFragment(getSupportFragmentManager(), R.id.baseFrameLayout, videoEditorFragment2, VideoEditorFragment2.TAG);
                } else {
                    Toast.makeText(MainActivity.this, R.string.toast_cannot_retrieve_selected_video, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void initGui(){
        this.baseFrameLayout = findViewById(R.id.baseFrameLayout);
        this.fragmentManager = getSupportFragmentManager();


//        VideoEditorFragment videoEditorFragment = new VideoEditorFragment();
//        loadFragment(fragmentManager,R.id.baseFrameLayout,videoEditorFragment,  VideoEditorFragment.TAG);
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
    }

    public boolean permissionsAreGranted() {
        String readPermission = "android.permission.READ_EXTERNAL_STORAGE";
        int res1 = this.checkCallingOrSelfPermission(readPermission);

        String writePermission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res2 = this.checkCallingOrSelfPermission(writePermission);
        return ( (res1 == PackageManager.PERMISSION_GRANTED) && (res2 == PackageManager.PERMISSION_GRANTED) );
    }

    private void pickFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI); // todo: this thing might need some work :/, eg open from google drive, stuff like that
        intent.setTypeAndNormalize("video/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_video)), REQUEST_VIDEO_TRIMMER);
    }


    public void loadFragment(FragmentManager fragmentManager, int flToMountOnResId, Fragment fragment, String tag) {
        fragmentManager.beginTransaction()
                .setCustomAnimations(
                        android.R.animator.fade_in,
                        android.R.animator.fade_out,
                        android.R.animator.fade_in,
                        android.R.animator.fade_out
                )
                .add(flToMountOnResId, fragment , tag)
                .addToBackStack(tag)
                .commit();
    }

    @Override
    public void onBackPressed() {
        int entryCount = getSupportFragmentManager().getBackStackEntryCount();
        Log.e(TAG, "entryCount = "+entryCount);
        if(entryCount > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
//           super.onBackPressed();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onSuccess(String destPath) {
        // todo: use this dest path to pass it into your next fragment/activity, say videoPlayerFragment/Activity
    }
}
