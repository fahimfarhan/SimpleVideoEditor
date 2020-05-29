package com.fahimfarhan.simplevideoeditor.videoeditor;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.fahimfarhan.simplevideoeditor.R;
import com.fahimfarhan.simplevideoeditor.customview.RoundCornerImageView;
import com.fahimfarhan.simplevideoeditor.gpuimage.GPUImage;
import com.fahimfarhan.simplevideoeditor.gpuimage.gpufilter.GPUImageFilter;
import com.fahimfarhan.simplevideoeditor.mp4composer.mp4compose.FilterType;

import org.mainstring.textee.ui.videoeditor.imageutil.GPUImageFilterTools;

import java.util.List;

public class FilterOptionsAdapter extends RecyclerView.Adapter<FilterOptionsAdapter.FilterOptionsViewHolder> {
    private List<Pair<FilterType, GPUImageFilterTools.ImageFilterType>> listFilters = FilterType.getFilterPairs();
    private FilterChangeListener filterChangeListener;
    private Bitmap[] thumbnail = new Bitmap[listFilters.size()];
    private Activity activity;
    private Bitmap bitmapThumbnail;
    private GPUImage gpuImage;
    private FilterOptionsViewHolder currentHolder;

    public FilterOptionsAdapter(Activity activity, FilterChangeListener filterChangeListener, Bitmap bitmapThumbnail) {
        this.activity = activity;
        this.filterChangeListener = filterChangeListener;
        this.bitmapThumbnail = bitmapThumbnail;
        this.thumbnail[0] = this.bitmapThumbnail;
        for(int i=1; i<this.thumbnail.length; i++) {     thumbnail[i] = null;    }
        this.gpuImage = new GPUImage(activity);
        this.currentHolder = null;
    }

    @NonNull
    @Override
    public FilterOptionsAdapter.FilterOptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_preview_video_item, parent, false);
        FilterOptionsViewHolder filterOptionsViewHolder = new FilterOptionsViewHolder(rootView);
        return filterOptionsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FilterOptionsAdapter.FilterOptionsViewHolder holder, int position) {
        holder.filterName.setText(listFilters.get(position).first.toString());
        if(thumbnail[position]==null) {
            GPUImageFilter gpuImageFilter = GPUImageFilterTools.INSTANCE.createFilterForType(activity,listFilters.get(position).second);
            gpuImage.setImage(bitmapThumbnail);
            gpuImage.setFilter(gpuImageFilter);
            thumbnail[position] = gpuImage.getBitmapWithFilterApplied();
        }
        holder.videoThumbnailPreview.setImageBitmap(thumbnail[position]);
        holder.itemView.setOnClickListener(v -> {
            if(filterChangeListener.isWaitingForConversion()) { return; }
            filterChangeListener.onFilterChange(listFilters.get(position).first);
            changeTextColor(holder);
        });
        if( (position == 0) && (currentHolder == null) ) { // onFirstTime(that is currentHolder = null), the DEFAULT filter(NO_FILTER) is selected, so it should have the green text
            changeTextColor(holder);
        }
    }

    @Override
    public int getItemCount() {
        return listFilters.size();
    }

    public static class FilterOptionsViewHolder extends RecyclerView.ViewHolder {
        private RoundCornerImageView videoThumbnailPreview;
        private TextView filterName;
        public FilterOptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.videoThumbnailPreview = itemView.findViewById(R.id.videoThumbnailPreview);
            this.filterName = itemView.findViewById(R.id.filterName);
        }
    }

    private void changeTextColor(FilterOptionsViewHolder holder) {
        if(currentHolder!=null) {
            currentHolder.filterName.setTextColor(activity.getResources().getColor(R.color.textColorPrimary));
        }
        currentHolder = holder;
        currentHolder.filterName.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
    }

    public interface FilterChangeListener{
        void onFilterChange(FilterType selectedFilterType);
        boolean isWaitingForConversion();
    }
}
