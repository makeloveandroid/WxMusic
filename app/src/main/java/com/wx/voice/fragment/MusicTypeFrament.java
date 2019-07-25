package com.wx.voice.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wx.voice.R;
import com.wx.voice.activity.CoreDialog;
import com.wx.voice.adapter.MusicTypeAdapter;
import com.wx.voice.app.App;
import com.wx.voice.base.view.base.v4.XBaseFragment;
import com.wx.voice.fragment.contract.MusicTypeContract;
import com.wx.voice.fragment.presenter.MusicTypePresenter;
import com.wx.voice.request.BaseRequest;
import com.wx.voice.respones.MusicTypeRespones;
import com.wx.voice.view.emptylayout.FrameEmptyLayout;


import java.util.List;

import butterknife.BindView;

/**
 * Created by wenyingzhi on 2018/12/7.
 */
public class MusicTypeFrament extends XBaseFragment<MusicTypePresenter> implements MusicTypeContract.View {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.emptyView)
    FrameEmptyLayout emptyView;


    private BaseRequest baseRequest;

    @Override
    public void refresh() {
        presenter.load();
    }

    @Override
    public int layoutId() {
        return R.layout.layout_music_type;
    }


    @Override
    public void onInitCircle() {
        super.onInitCircle();
        emptyView.setRetryListener(new FrameEmptyLayout.OnRetryClickListener() {
            @Override
            public void onClick() {
                refresh();
            }
        });
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        Intent intent = getActivity().getIntent();
        String nickName = intent.getStringExtra(CoreDialog.NICK_NAME);
        String myWxid = intent.getStringExtra(CoreDialog.MY_WXID);
        String sendWxId = intent.getStringExtra(CoreDialog.SEND_WXID);

        baseRequest = new BaseRequest(App.getContext(),nickName, "AllType", myWxid);

        refresh();
    }

    @Override
    public void showLoading() {
        emptyView.showLoading();
    }

    @Override
    public void setData(List<MusicTypeRespones> musicTypeRespones) {
        if (musicTypeRespones == null || musicTypeRespones.size() <= 0) {
            showError(-1, "数据提取错误!");
        } else {
            emptyView.showContent();
            MusicTypeAdapter adapter = null;
            if (mRecyclerview.getAdapter() != null) {
                adapter = (MusicTypeAdapter) mRecyclerview.getAdapter();
                adapter.setNewData(musicTypeRespones);
            } else {
                adapter = new MusicTypeAdapter(musicTypeRespones, (CoreDialog) getActivity());
                mRecyclerview.setAdapter(adapter);
            }

            adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                @Override
                public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                    List<MusicTypeRespones> data = adapter.getData();
                    MusicTypeRespones musicTypeRespones = data.get(position);
                    startFragment(musicTypeRespones);
                }
            });

        }
    }


    public void startFragment(MusicTypeRespones musicTypeRespones) {
        // 判断当前有fragment吗?
        CoreDialog activity = (CoreDialog) getActivity();
        MusicListFragment musicListFragment = (MusicListFragment) activity.getFragment(CoreDialog.MUSICLISTFRAGMENT);
        musicListFragment.setSmallLabelBean(musicTypeRespones.label);
        activity.setCurrentItem(CoreDialog.MUSICLISTFRAGMENT);
    }


    @Override
    public void showError(int type, String msg) {
        emptyView.showError(R.mipmap.net_error, msg, "点击重试");
    }


    @Override
    public BaseRequest getRequestData() {
        return baseRequest;
    }

}
