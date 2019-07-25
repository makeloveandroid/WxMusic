package com.wx.voice.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.wx.voice.R;

/**
 * Created by wenyingzhi on 2018/12/12.
 */
@SuppressLint("AppCompatCustomView")
public class PlayImageView extends ImageView {
    private boolean isPlay = false;

    public PlayImageView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (isPlay) {
            setBackgroundResource(R.drawable.list_loading);
            AnimationDrawable drawable = (AnimationDrawable) getBackground();
            drawable.start();
        } else {
            setBackgroundResource(R.mipmap.list_icn_play);
        }
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
        init();
    }

    public PlayImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
}
