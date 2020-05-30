package com.fahimfarhan.simplevideoeditor.videoeditor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fahimfarhan.simplevideoeditor.R;

public class WaitingFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = WaitingFragment.class.getSimpleName();

    private View fragmentRootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentRootView = inflater.inflate(R.layout.fragment_video_waiting, container, false);
        fragmentRootView.setOnClickListener(this);
        initGui();
        return fragmentRootView;
    }

    private void initGui() {    }

    @Override
    public void onClick(View v) {
        Log.e(TAG, "click detected!");
    }
}
