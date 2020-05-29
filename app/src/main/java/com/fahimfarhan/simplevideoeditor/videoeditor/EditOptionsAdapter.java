package com.fahimfarhan.simplevideoeditor.videoeditor;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fahimfarhan.simplevideoeditor.R;
import com.fahimfarhan.simplevideoeditor.customview.AwesomeVideoView;


public class EditOptionsAdapter extends RecyclerView.Adapter<EditOptionsAdapter.EditOptionsViewHolder> {
    private final int TRIM = 0;
    private final int FORMAT = 1;
    private final int EFFECT = 2;

    public static final String TAG = EditOptionsAdapter.class.getSimpleName();
    private int[] optionImages = new int[] { R.drawable.ic_video_trim, R.drawable.ic_video_format,  R.drawable.ic_video_effects };
//    R.drawable.ic_video_sticker, R.drawable.ic_video_text, R.drawable.ic_video_draw,
    private String[] optionNames = new String[]{"Trim", "Format", "Effect"};    // "Sticker", "Text", "Draw",
    private final int SIZE = optionImages.length; // 3
    private Activity activity;
    private EditOptionsViewHolder currentViewHolder = null;

    private Drawable selectedBackground;
    private Drawable notSelectedBackground;
    private ConstraintLayout trimConstraintLayout;
    private ConstraintLayout effectConstraintLayout;
    private ConstraintLayout formatConstraintLayout;
    private AwesomeVideoView awesomeVideoView;
    private WaitingForConversionListener listener;
    private VideoEditorPresenter presenter;

    public EditOptionsAdapter(Activity activity, ConstraintLayout trimConstraintLayout, ConstraintLayout effectConstraintLayout,
                              ConstraintLayout formatConstraintLayout, AwesomeVideoView awesomeVideoView, WaitingForConversionListener listener, VideoEditorPresenter presenter) {
        this.activity = activity;
        this.selectedBackground = ContextCompat.getDrawable(activity, R.drawable.round_edit_option_selected);
        this.notSelectedBackground = ContextCompat.getDrawable(activity, R.drawable.round_edit_option_notselected);
        this.trimConstraintLayout = trimConstraintLayout;
        this.effectConstraintLayout = effectConstraintLayout;
        this.formatConstraintLayout = formatConstraintLayout;
        this.awesomeVideoView = awesomeVideoView;
        this.listener = listener;
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public EditOptionsAdapter.EditOptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_edit_options_item, parent, false);
        return new EditOptionsViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull EditOptionsAdapter.EditOptionsViewHolder holder, int position) {
        Drawable drawable = ContextCompat.getDrawable(activity, optionImages[position]);
        holder.optionLogo.setImageDrawable(drawable);
        holder.optionName.setText(optionNames[position]);
        holder.optionLogo.setOnClickListener( v -> {
            if(listener.isWaitingForConversion()){  return; }
            toogleColor(holder, position);
            myOnClick(position);
        });
     }

    // todo: I'll keep this method here for now. might move into presenter for convenience/if necessary
     private void myOnClick(int position) {
         trimConstraintLayout.setVisibility(View.GONE);  // todo: might blink so add an if block if necessary
         effectConstraintLayout.setVisibility(View.GONE);
         formatConstraintLayout.setVisibility(View.GONE);
        switch (position) {
            case TRIM: {
                awesomeVideoView.onNonCropState();
                presenter.displayTrimmerView();
                trimConstraintLayout.setVisibility(View.VISIBLE);
                break;
            } case FORMAT: {
                awesomeVideoView.onCropState();
                formatConstraintLayout.setVisibility(View.VISIBLE);
                break;
            } case EFFECT: {
                awesomeVideoView.onNonCropState();
                effectConstraintLayout.setVisibility(View.VISIBLE);
                break;
            } default: {
                awesomeVideoView.onNonCropState();
                Log.e(TAG, "EditOptionsAdapter#myOnClick#default");
                break;
            }

        }
     }

     private void toogleColor(EditOptionsViewHolder holder, int position) {
         if(currentViewHolder != null) {
             currentViewHolder.optionLogo.setBackground(notSelectedBackground);
             currentViewHolder.optionName.setTextColor( activity.getResources().getColor(R.color.textColorPrimary));
         }
         currentViewHolder = holder;
         currentViewHolder.optionLogo.setBackground(selectedBackground);
         currentViewHolder.optionName.setTextColor( activity.getResources().getColor(R.color.colorPrimary));
         Log.e(TAG, "click detected! "+position);
     }

    @Override
    public int getItemCount() {
        return SIZE;
    }

    public class EditOptionsViewHolder extends RecyclerView.ViewHolder {
        ImageButton optionLogo;
        TextView optionName;

        public EditOptionsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.optionLogo = itemView.findViewById(R.id.optionLogo);
            this.optionName = itemView.findViewById(R.id.optionName);
        }
    }

    public interface WaitingForConversionListener{
        boolean isWaitingForConversion();
    }
}
