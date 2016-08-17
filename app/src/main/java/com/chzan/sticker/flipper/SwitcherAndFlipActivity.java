package com.chzan.sticker.flipper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.chzan.sticker.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by chenzan on 2016/8/17.
 */
public class SwitcherAndFlipActivity extends AppCompatActivity {
    private ImageSwitcher imageSwitcher;
    private TextSwitcher textSwitcher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switcher_flip);
        imageSwitcher = (ImageSwitcher) findViewById(R.id.image_switcher);
        textSwitcher = (TextSwitcher) findViewById(R.id.text_switcher);
        VerticalMarqueeView marqueeView = (VerticalMarqueeView) findViewById(R.id.marquee_view);
        List<String> lists = new ArrayList<>();
        lists.add("aaaa");
        lists.add("bbbb");
        lists.add("cccc");
        lists.add("dddd");
        lists.add("eeee");
        marqueeView.setViews(lists);

        Animation verticalMarqueeIn = AnimationUtils.loadAnimation(this, R.anim.vertical_marquee_in);
        Animation verticalMarqueeOut = AnimationUtils.loadAnimation(this, R.anim.vertical_marquee_out);
        imageSwitcher.setInAnimation(verticalMarqueeIn);
        imageSwitcher.setOutAnimation(verticalMarqueeOut);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView iv = new ImageView(imageSwitcher.getContext());
                iv.setLayoutParams(new ImageSwitcher.LayoutParams(300, 300));
                return iv;
            }
        });
        imageSwitcher.setImageDrawable(getResources().getDrawable(R.drawable.ic_assignment_late_black_24dp));

        textSwitcher.setInAnimation(verticalMarqueeIn);
        textSwitcher.setInAnimation(verticalMarqueeOut);
        textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(SwitcherAndFlipActivity.this);
                return textView;
            }
        });
        textSwitcher.setText("saaa");
    }

    public void next(View view) {
        imageSwitcher.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
    }

    public void pre(View view) {
        imageSwitcher.setImageDrawable(getResources().getDrawable(R.drawable.ic_assignment_late_black_24dp));
    }

    public void set(View view) {
        textSwitcher.setText("sss" + new Random().nextInt());
    }
}
