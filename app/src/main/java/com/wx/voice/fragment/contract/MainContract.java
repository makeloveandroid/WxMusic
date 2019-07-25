package com.wx.voice.fragment.contract;

import com.wx.voice.base.contracts.XContract;

/**
 * Created by wenyingzhi on 2018/12/20.
 */
public interface MainContract {
    interface View extends XContract.View {
        void method();
    }

    interface Presenter extends XContract.Presenter {
        void method();
    }

    interface Model extends XContract.Model {
        void method();
    }
}
