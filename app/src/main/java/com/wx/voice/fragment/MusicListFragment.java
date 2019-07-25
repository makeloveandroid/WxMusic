package com.wx.voice.fragment;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wx.voice.R;
import com.wx.voice.activity.CoreDialog;
import com.wx.voice.adapter.ItemMusicAdapter;
import com.wx.voice.adapter.MusicTypeAdapter;
import com.wx.voice.app.App;
import com.wx.voice.base.view.base.v4.XBaseFragment;
import com.wx.voice.entity.MusicUserContent;
import com.wx.voice.entity.SmallLabelBean;
import com.wx.voice.fragment.contract.MusicListContract;
import com.wx.voice.fragment.presenter.MusicListPresenter;
import com.wx.voice.request.BaseRequest;
import com.wx.voice.request.MusicListRequest;
import com.wx.voice.respones.MusicTypeRespones;
import com.wx.voice.util.L;
import com.wx.voice.util.ToastUtil;
import com.wx.voice.view.emptylayout.FrameEmptyLayout;

import java.util.List;

import butterknife.BindView;

public class MusicListFragment extends XBaseFragment<MusicListPresenter> implements MusicListContract.View {

    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.emptyView)
    FrameEmptyLayout emptyView;

    private BaseRequest<MusicListRequest> baseRequest;
    private SmallLabelBean smallLabelBean;


    public void setSmallLabelBean(SmallLabelBean smallLabelBean) {
        this.smallLabelBean = smallLabelBean;
    }

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
        mRecyclerview.setLayoutManager(new GridLayoutManager(App.getContext(), 3));
        Intent intent = getActivity().getIntent();
        String nickName = intent.getStringExtra(CoreDialog.NICK_NAME);
        String myWxid = intent.getStringExtra(CoreDialog.MY_WXID);
        String sendWxId = intent.getStringExtra(CoreDialog.SEND_WXID);
        baseRequest = new BaseRequest(App.getContext(),nickName, "musicByType", myWxid);
    }


    @Override
    public void onLazyLoad() {
        super.onLazyLoad();
        baseRequest.data = new MusicListRequest(1, smallLabelBean.getLabelid());
        CoreDialog activity = (CoreDialog) getActivity();
        activity.getToolbar().setSubtitle(smallLabelBean.getLabel());
        refresh();
    }



    @Override
    public void showLoading() {
        emptyView.showLoading();
    }

    @Override
    public void setData(List<MusicUserContent> musicTypeRespones) {
        if (musicTypeRespones == null || musicTypeRespones.size() <= 0) {
            showError(-1, "数据提取错误!");
        } else {
            emptyView.showContent();
            ItemMusicAdapter adapter = null;
            if (mRecyclerview.getAdapter() != null) {
                adapter = (ItemMusicAdapter) mRecyclerview.getAdapter();
                adapter.setNewData(musicTypeRespones);
            } else {
                adapter = new ItemMusicAdapter(musicTypeRespones, (CoreDialog) getActivity());
                mRecyclerview.setAdapter(adapter);
            }
            // 页数增1
            baseRequest.data.page++;

            adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {

                @Override
                public void onLoadMoreRequested() {
                    presenter.loadMore();
                }
            }, mRecyclerview);
        }
    }

    @Override
    public void showToast(String msg) {
        ToastUtil.getInstance().show(App.getContext(),msg);
    }

    @Override
    public void setMoreData(List<MusicUserContent> musicTypeRespones) {
        if (musicTypeRespones == null || musicTypeRespones.size() <= 0) {
            showToast("数据提取错误!");
        } else {
            emptyView.showContent();
            ItemMusicAdapter adapter = (ItemMusicAdapter) mRecyclerview.getAdapter();
            adapter.addData(musicTypeRespones);
            adapter.notifyDataSetChanged();
            // 页数增1
            baseRequest.data.page++;
            if (musicTypeRespones.size() < 21) {
                // 已经小于分页了
                loadMoreEnd();
            }
        }
    }

    @Override
    public void loadMoreOk() {
        ItemMusicAdapter adapter = (ItemMusicAdapter) mRecyclerview.getAdapter();
        adapter.loadMoreComplete();
    }

    @Override
    public void loadMoreEnd() {
        ItemMusicAdapter adapter = (ItemMusicAdapter) mRecyclerview.getAdapter();
        adapter.loadMoreEnd();
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
