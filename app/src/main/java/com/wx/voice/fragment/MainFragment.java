package com.wx.voice.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.wx.voice.R;
import com.wx.voice.activity.CoreDialog;
import com.wx.voice.base.view.base.v4.XBaseFragment;
import com.wx.voice.fragment.contract.MainContract;
import com.wx.voice.fragment.presenter.MainPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by wenyingzhi on 2018/12/20.
 */
public class MainFragment extends XBaseFragment<MainPresenter> implements MainContract.View {
    @BindView(R.id.baibian)
    LinearLayout mBaibian;
    @BindView(R.id.henpi)
    LinearLayout mHenpi;

    @Override
    public void method() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void showError(int type, String msg) {

    }

    @Override
    public void refresh() {

    }

    @Override
    public int layoutId() {
        return R.layout.main_fragment_layout;
    }


    @OnClick({R.id.baibian, R.id.henpi})
    public void onClick(View v) {
        CoreDialog activity = (CoreDialog) getActivity();
        switch (v.getId()) {
            default:
                break;
            case R.id.baibian:
                activity.setCurrentItem(CoreDialog.RECOREDERFRAGMENT);
                break;
            case R.id.henpi:
                activity.setCurrentItem(CoreDialog.MUSICTYPEFRAMENT);
                break;
        }
    }
}
