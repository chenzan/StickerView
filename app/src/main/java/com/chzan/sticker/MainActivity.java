package com.chzan.sticker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.chzan.sticker.flipper.SwitcherAndFlipActivity;

public class MainActivity extends AppCompatActivity {

    private StickerView stickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stickerView = (StickerView) findViewById(R.id.sticker_view);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        stickerView.addSticker(bitmap);
        stickerView.addSticker(bitmap1);
        stickerView.addSticker(bitmap2);
    }

    public void next(View view) {
        startActivity(new Intent(this, SwitcherAndFlipActivity.class));
    }
}
