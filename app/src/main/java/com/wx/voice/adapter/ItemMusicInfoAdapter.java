package com.wx.voice.adapter;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wx.voice.R;
import com.wx.voice.app.App;
import com.wx.voice.entity.MusicEntity;
import com.wx.voice.entity.MusicUserContent;
import com.wx.voice.view.PlayImageView;

import java.util.List;

public class ItemMusicInfoAdapter extends BaseQuickAdapter<MusicEntity, BaseViewHolder> {

    public ItemMusicInfoAdapter(List<MusicEntity> data) {
        super(R.layout.item_pay_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MusicEntity item) {
        helper.setText(R.id.fragment_main_playlist_item_title, item.getTitle());
        helper.setText(R.id.fragment_main_playlist_item_count, item.getType());
        PlayImageView view = helper.getView(R.id.play_img);
        view.setPlay(item.isPlay);
        helper.addOnClickListener(R.id.fragment_main_playlist_item_menu);
    }
}