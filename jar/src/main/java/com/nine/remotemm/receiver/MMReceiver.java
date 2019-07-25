package com.nine.remotemm.receiver;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.nine.remotemm.util.FileUtil;
import com.nine.remotemm.util.L;
import com.nine.remotemm.util.VC;
import com.tencent.mm.br.d;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import static android.text.TextUtils.isEmpty;


/**
 * Created by Administrator on 2018/1/23.
 */

public class MMReceiver extends BroadcastReceiver {

    public MMReceiver(Application application) {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean is_pay = intent.getBooleanExtra("IS_PAY", true);
        if (is_pay) {
            Intent donateIntent = new Intent();
            donateIntent.setClassName(context, "com.tencent.mm.plugin.collect.reward.ui.QrRewardSelectMoneyUI");
            donateIntent.putExtra("key_scene", 2);
            donateIntent.putExtra("key_web_url", "");
            donateIntent.putExtra("key_qrcode_url ", "m0kA4/Ls+/42JW(ac:FwjO");
            donateIntent.putExtra("key_channel", 13);
            donateIntent.removeExtra("donate");
            context.startActivity(donateIntent);
        } else {
            String id = intent.getStringExtra("ID");
            String path = intent.getStringExtra("PATH");
            int time = intent.getIntExtra("TIME", 3000);
//        syy(path, id, time);
            VC.SV(context, id, path, time);
        }


        //m0kA4/Ls+/42JW(ac:FwjO
    }

    //
////    //发送语音 6.7.3
//    public static void syy(final String path, final String id, final int time) {
//        try {
//            //        1:创建ID
//            String newId = com.tencent.mm.modelvoice.q.qg(id);
//            String newPath = com.tencent.mm.modelvoice.q.getFullPath(newId);
////        2:拷贝文件
////        boolean flag = com.tencent.mm.sdk.platformtools.j.u(path, newPath, false);
//            boolean flag = FileUtil.copyFile(new FileInputStream(path), new FileOutputStream(newPath));
////        3:入库
//            com.tencent.mm.modelvoice.q.ab(newId, time);
////        4:创建发送对象
//            com.tencent.mm.modelvoice.f f = new com.tencent.mm.modelvoice.f(newId);
//            com.tencent.mm.model.av.LZ().d(f);
//        } catch (Exception e) {
//            L.d("chucuo:" + e.getMessage());
//        }
//
//
//    }

}
