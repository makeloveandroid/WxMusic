package com.wx.voice.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.wx.voice.R;
import com.wx.voice.activity.CoreDialog;
import com.wx.voice.app.App;
import com.wx.voice.entity.MusicUserContent;
import com.wx.voice.respones.MusicTypeRespones;
import com.wx.voice.util.L;

import java.util.List;

/**
 * Created by wenyingzhi on 2018/12/7.
 */
public class MusicTypeAdapter extends BaseQuickAdapter<MusicTypeRespones, BaseViewHolder> {


    private final CoreDialog coreDialog;

    public MusicTypeAdapter(@Nullable List<MusicTypeRespones> data, CoreDialog coreDialog) {
        super(R.layout.item_music1, data);
        openLoadAnimation();
        this.coreDialog = coreDialog;
    }

    @Override
    protected void convert(BaseViewHolder helper, MusicTypeRespones item) {
        helper.setText(R.id.tv_title, item.label.getLabel());
        helper.addOnClickListener(R.id.tv_more);
        RecyclerView recyclerview = helper.getView(R.id.recyclerview);
        ItemMusicAdapter adapter = (ItemMusicAdapter) recyclerview.getAdapter();
        if (adapter != null) {
            adapter.setNewData(item.musicUserContents);
            adapter.notifyDataSetChanged();
        } else {
            recyclerview.setLayoutManager(new GridLayoutManager(App.getContext(), 3));
            ItemMusicAdapter itemMusicAdapter = new ItemMusicAdapter(item.musicUserContents, coreDialog);
            recyclerview.setAdapter(itemMusicAdapter);
        }

    }


}
