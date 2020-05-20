package com.fahimfarhan.simplevideoeditor.mp4composer.mp4compose;


import com.fahimfarhan.simplevideoeditor.mp4composer.mp4compose.filter.GlFilter;

public interface FilterAdjuster {
    public void adjust(GlFilter filter, int percentage);
}
