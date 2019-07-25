package com.wx.voice.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.baviux.voicechanger.services.FMODService;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wx.voice.R;
import com.wx.voice.activity.CoreDialog;
import com.wx.voice.adapter.ItemMusicInfoAdapter;
import com.wx.voice.app.App;
import com.wx.voice.base.view.base.v4.XBaseFragment;
import com.wx.voice.dao.DownFileDao;
import com.wx.voice.entity.DownFile;
import com.wx.voice.entity.MusicEntity;
import com.wx.voice.entity.MusicUserContent;
import com.wx.voice.fragment.contract.MusicInfoContract;
import com.wx.voice.fragment.presenter.MusicInfoPresenter;
import com.wx.voice.media.NetMediaPlayManager;
import com.wx.voice.request.BaseRequest;
import com.wx.voice.request.MusicListRequest;
import com.wx.voice.util.FileUtil;
import com.wx.voice.util.L;
import com.wx.voice.util.ToastUtil;
import com.wx.voice.view.emptylayout.FrameEmptyLayout;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MusicInfoListFragment extends XBaseFragment<MusicInfoPresenter> implements MusicInfoContract.View, NetMediaPlayManager.MediaListener {
    ItemMusicInfoAdapter adapter = null;
    @BindView(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.emptyView)
    FrameEmptyLayout emptyView;

    private BaseRequest<MusicListRequest> baseRequest;
    private MusicUserContent musicUserContent;
    private String sendWxId;


    public void setMusicUserContent(MusicUserContent musicUserContent) {
        this.musicUserContent = musicUserContent;
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
        mRecyclerview.setLayoutManager(new LinearLayoutManager(App.getContext()));
        Intent intent = getActivity().getIntent();
        String nickName = intent.getStringExtra(CoreDialog.NICK_NAME);
        String myWxid = intent.getStringExtra(CoreDialog.MY_WXID);
        sendWxId = intent.getStringExtra(CoreDialog.SEND_WXID);
        baseRequest = new BaseRequest(App.getContext(), nickName, "musicData", myWxid);
    }


    @Override
    public void onLazyLoad() {
        super.onLazyLoad();
        baseRequest.data = new MusicListRequest(1, musicUserContent.getId());
        CoreDialog activity = (CoreDialog) getActivity();
        activity.getToolbar().setSubtitle(musicUserContent.getTitle());
        refresh();
    }


    @Override
    public void showLoading() {
        emptyView.showLoading();
    }

    @Override
    public void setData(List<MusicEntity> musicTypeRespones) {
        if (musicTypeRespones == null || musicTypeRespones.size() <= 0) {
            showError(-1, "数据提取错误!");
        } else {
            emptyView.showContent();
            if (baseRequest.data.page == 1) {
                mRecyclerview.scrollToPosition(0);
            }
            if (mRecyclerview.getAdapter() != null) {
                adapter = (ItemMusicInfoAdapter) mRecyclerview.getAdapter();
                adapter.setNewData(musicTypeRespones);
            } else {
                adapter = new ItemMusicInfoAdapter(musicTypeRespones);
                adapter.openLoadAnimation();
                mRecyclerview.setAdapter(adapter);

                NetMediaPlayManager.getInstance().setCallBack(this);

                adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
                    @Override
                    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        final MusicEntity data = (MusicEntity) adapter.getData().get(position);
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("确定发送?")
                                .setContentText("发送语音[" + data.title + "]")
                                .setConfirmText("确定")
                                .setCancelText("取消")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.dismissWithAnimation();
                                        // 判断文件是否存在
                                        downLoadAndSend(data);
                                    }
                                })
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                })
                                .show();

                    }
                });
                adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {

                    @Override
                    public void onLoadMoreRequested() {
                        presenter.loadMore();
                    }
                }, mRecyclerview);
                adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                        Log.d("wyz", "点击:" + position);
                        MusicEntity item = (MusicEntity) adapter.getData().get(position);
                        NetMediaPlayManager.getInstance().play(item);
                        adapter.notifyDataSetChanged();
                    }
                });


            }
            // 页数增1
            baseRequest.data.page++;


        }
    }

    /**
     * 下载并
     *
     * @param data
     */
    private void downLoadAndSend(MusicEntity data) {
        // 判断文件是否存在
        DownFile downFile = DownFile.make(data.decoding_url);
        DownFile search = App.getDaoSession().getDownFileDao().queryBuilder().where(DownFileDao.Properties.Md5.eq(downFile.getMd5())).unique();
        if (search != null) {
            File searchFile = new File(search.file);
            boolean exists = searchFile.exists();
            if (search != null && exists && searchFile.length() == search.size) {
                //转码dialog
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                sweetAlertDialog.setTitle("");
                sweetAlertDialog.setCancelable(false);
                sweetAlertDialog.show();
                // 文件已经存在
                presenter.send(sweetAlertDialog, sendWxId, searchFile);
                return;
            }
        }

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.setContentText("马不停蹄的下载...");
        sweetAlertDialog.setTitle("");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();
        presenter.downloadFile(sweetAlertDialog, downFile, sendWxId);

    }

    @Override
    public void showToast(String msg) {
        ToastUtil.getInstance().show(App.getContext(), msg);
    }

    @Override
    public void setMoreData(List<MusicEntity> musicTypeRespones) {
        if (musicTypeRespones == null || musicTypeRespones.size() <= 0) {
            showToast("数据提取错误!");
        } else {
            emptyView.showContent();
            ItemMusicInfoAdapter adapter = (ItemMusicInfoAdapter) mRecyclerview.getAdapter();
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
        ItemMusicInfoAdapter adapter = (ItemMusicInfoAdapter) mRecyclerview.getAdapter();
        adapter.loadMoreComplete();
    }

    @Override
    public void loadMoreEnd() {
        ItemMusicInfoAdapter adapter = (ItemMusicInfoAdapter) mRecyclerview.getAdapter();
        adapter.loadMoreEnd();
    }

    @Override
    public void showSendSuccess() {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog
                .setTitleText("发送成功")
                .setContentText("继续发送?")
                .setConfirmText("继续")
                .setCancelText("结束")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                        getActivity().finish();
                    }
                })
                .show();
    }

    @Override
    public void showError(int type, String msg) {
        emptyView.showError(R.mipmap.net_error, msg, "点击重试");
    }


    @Override
    public BaseRequest getRequestData() {
        return baseRequest;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.d("onDestroy");
    }

    @Override
    public void onError(MusicEntity entity) {
        if (entity != null) {
            entity.isPlay = false;
            ToastUtil.getInstance().show(App.getContext(), "播放语音出错,请重新尝试!");
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onStart(MusicEntity entity) {

    }

    @Override
    public void onCompletion(MusicEntity entity) {
        if (entity != null) {
            entity.isPlay = false;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }


}