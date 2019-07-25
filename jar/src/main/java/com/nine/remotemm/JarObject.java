package com.nine.remotemm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nine.remotemm.base.Constant;
import com.nine.remotemm.receiver.MMReceiver;
import com.nine.remotemm.util.FileUtil;
import com.nine.remotemm.util.L;
import com.tencent.mm.model.q;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by Administrator on 2016/5/22.
 */
public class JarObject {
    private static Activity context;
    private MMReceiver mmReceiver;
    private boolean isFirst = true;

    public void init(Activity activity) {
        context = activity;
        IntentFilter intent = new IntentFilter(Constant.CONFIG.MM_RECEIVER);
        mmReceiver = new MMReceiver(activity.getApplication());
        context.registerReceiver(mmReceiver, intent);
        addSo(activity);
    }

    public void deInit(Activity activity) {
        if (mmReceiver != null) {
            activity.unregisterReceiver(mmReceiver);
        }
    }

    public void addView(final LinearLayout layout, final String wxId) {
        Object tag = layout.getTag();
        if (tag == null) {
            View childAt = layout.getChildAt(0);
            if (childAt != null) {
                LinearLayout childAt1 = (LinearLayout) childAt;
                View childAt2 = childAt1.getChildAt(0);
                if (childAt2 != null) {
                    LinearLayout childAt3 = (LinearLayout) childAt2;
                    if (childAt3 != null) {
                        View meView = childAt3.getChildAt(1);
                        if (meView != null) {
                            LinearLayout messangerLayout = (LinearLayout) meView;
                            final Context context = messangerLayout.getContext();
                            ImageView imageView = new ImageView(layout.getContext());
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            layoutParams.gravity = Gravity.CENTER;
                            int resId = context.getResources().getIdentifier("fts_setmode_voice_normal", "raw", context.getPackageName());
                            L.d("当前wxid:" + wxId);
                            imageView.setImageResource(resId);
                            imageView.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
                            imageView.setLayoutParams(layoutParams);
                            layout.setTag(imageView);
                            messangerLayout.addView(imageView);
                        }
                    }

                }
            }
        }

        final View imageView = (View) layout.getTag();
        if (imageView != null) {
            if (!TextUtils.equals(imageView.getTag() == null ? "" : imageView.getTag().toString(), wxId)) {
                imageView.setTag(wxId);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        L.d("启动当前:" + v + " " + wxId);
                        if (wxId != null) {
                            if (isFirst) {
                                isFirst = false;
                                Toast.makeText(v.getContext(), "第一次启动稍慢请耐心等待下!", Toast.LENGTH_SHORT).show();
                            }
                            ComponentName componentName = new ComponentName("com.wx.voice", "com.wx.voice.activity.CoreDialog");
                            Intent intent = new Intent("activity.intent.action.recorder");
                            intent.putExtra("SEND_WXID", wxId);
                            // 获取自己的wxid
                            intent.putExtra("MY_WXID", q.Wt());
                            intent.putExtra("NICK_NAME", q.Wv());
                            intent.setComponent(componentName);
                            v.getContext().startActivity(intent);
                        }

                    }
                });
            }
        } else {
            Toast.makeText(layout.getContext(), "按钮植入出错,请尝试重启微信修复问题", Toast.LENGTH_SHORT).show();
        }

    }

    public static Context getContext() {
        return context;
    }


    public static String getSdWxPath(Context context) {
        String s = Environment.getExternalStorageDirectory() + "/docker/Android/data/com.tencent.mm/cache/";
        File file = new File(s);
        if (!file.exists()) {
            file.mkdirs();
        }
        return s;
    }

    private void addSo(Activity activity) {
        try {
            File soFile = new File(activity.getCacheDir(), "libvoice.so");
            if (soFile.exists()) {
                System.load(soFile.getAbsolutePath());
            } else {
                Toast.makeText(activity, "核心文件不存在,尝试重启微信!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(activity, "核心文件加载出错,尝试重启微信!", Toast.LENGTH_SHORT).show();
        }
    }
}
