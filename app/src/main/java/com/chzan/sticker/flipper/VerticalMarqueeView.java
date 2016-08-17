package com.chzan.sticker.flipper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.chzan.sticker.R;

import java.util.List;

/**
 * Created by chenzan on 2016/8/16.
 */
public class VerticalMarqueeView extends ViewFlipper {
    private Context context;

    private int interval = 2000;
    private LayoutInflater layoutInflater;

    public VerticalMarqueeView(Context context) {
        this(context, null);
    }

    public VerticalMarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        Animation verticalMarqueeIn = AnimationUtils.loadAnimation(context, R.anim.vertical_marquee_in);

        Animation verticalMarqueeOut = AnimationUtils.loadAnimation(context, R.anim.vertical_marquee_out);

        setInAnimation(verticalMarqueeIn);
        setOutAnimation(verticalMarqueeOut);

        setFlipInterval(interval);
    }

    public void setViews(List<String> lists) {
        if (lists == null || lists.size() == 0) {
            return;
        }
        removeAllViews();
        for (int i = 0; i < lists.size(); i++) {
            View view = layoutInflater.inflate(R.layout.item_flip, this, false);
            TextView textView = (TextView) view.findViewById(R.id.tv);
            textView.setText(lists.get(i));
            addView(view);
        }
        startFlipping();
    }
}
