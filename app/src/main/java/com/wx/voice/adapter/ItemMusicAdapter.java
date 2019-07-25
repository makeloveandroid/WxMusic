package com.wx.voice.adapter;

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
import com.wx.voice.fragment.MusicInfoListFragment;

import java.util.List;

public class ItemMusicAdapter extends BaseQuickAdapter<MusicUserContent, BaseViewHolder> {

    public ItemMusicAdapter(final List<MusicUserContent> data, final CoreDialog dialog) {
        super(R.layout.item_music_type, data);
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MusicUserContent musicUserContent = (MusicUserContent) adapter.getData().get(position);
                // 判断当前有fragment吗?
                MusicInfoListFragment musicListFragment = (MusicInfoListFragment) dialog.getFragment(CoreDialog.MUSICINFOLISTFRAGMENT);
                musicListFragment.setMusicUserContent(musicUserContent);
                dialog.setCurrentItem(CoreDialog.MUSICINFOLISTFRAGMENT);
            }
        });

    }

    @Override
    protected void convert(BaseViewHolder helper, MusicUserContent item) {
        helper.setText(R.id.tv_title, item.getTitle());
        helper.addOnClickListener(R.id.tv_more);
        //设置图片圆角角度
        RoundedCorners roundedCorners = new RoundedCorners(15);
        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(300, 300);

        Glide.with(App.getContext()).load(item.getNewimg()).apply(options).into((ImageView) helper.getView(R.id.iv_icon));


    }

}