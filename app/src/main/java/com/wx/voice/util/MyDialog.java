package com.wx.voice.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class MyDialog {
	
	public static void showConfirm(String title, String msg, boolean cancelable, Context context, DialogInterface.OnClickListener positiveListener){
		if(((Activity)context).isFinishing()){
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setCancelable(cancelable);
		builder.setPositiveButton("是", positiveListener);
		builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}
	
	public static void showAlert(String title, String msg, boolean cancelable, Context context){
		if(((Activity)context).isFinishing()){
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setCancelable(cancelable);
		builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.show();
	}

	public static void showAlert(String title, String msg, String positiveBtnText, final DialogInterface.OnClickListener onClickListener, boolean cancelable, Context context){
		if(((Activity)context).isFinishing()){
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setCancelable(cancelable);
		builder.setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				onClickListener.onClick(dialog, which);
			}
		});
		builder.show();
	}


	public static void showAlert(String title, String msg, String positiveBtnText, String negativeBtnText, final DialogInterface.OnClickListener positive0nClickListener, final DialogInterface.OnClickListener negative0nClickListener, boolean cancelable, Context context){
		if(((Activity)context).isFinishing()){
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setCancelable(cancelable);
		builder.setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				positive0nClickListener.onClick(dialog, which);
			}
		});
		builder.setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				negative0nClickListener.onClick(dialog, which);
			}
		});
		builder.show();
	}

	public static void showCustomDialog(String title, String msg, boolean cancelable, Context context, String positiveBtnTxt,
                                        String negativeBtnTxt, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener){
		if(((Activity)context).isFinishing()){
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(msg);
		builder.setCancelable(cancelable);
		builder.setPositiveButton(positiveBtnTxt, positiveListener);
		builder.setNegativeButton(negativeBtnTxt, negativeListener);
		builder.show();
	}

	public static void showCustomLayoutAlert(String title, Context context, View view){
		showCustomLayoutAlert(title, context, view, 0, 0);
	}

	public static void showCustomLayoutAlert(String title, Context context, View view, double heightScale, double widthScale){
		if(((Activity)context).isFinishing()){
			return;
		}
		AlertDialog dialog = new AlertDialog.Builder(context)
				.setView(view)
				.setTitle(title)
				.setCancelable(true)
				.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.create();

		dialog.show();

		// 修改弹窗宽高
		if(heightScale != 0 || widthScale != 0){
			Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
			Point p = new Point();
			display.getSize(p);

			WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
			if(heightScale != 0){
				params.height = (int)(p.y * heightScale);
			}
			if(widthScale != 0){
				params.width = (int)(p.x * widthScale);
			}

			dialog.getWindow().setAttributes(params);
		}
	}
	
	public static void showCustomLayoutDialog(String title, Context context, DialogInterface.OnClickListener positiveListener, View view){
		if(((Activity)context).isFinishing()){
			return;
		}
		createCustomLayoutDialog(title, context, positiveListener, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}, view).show();
	}

	public static AlertDialog createCustomLayoutDialog(String title, Context context,
                                                       DialogInterface.OnClickListener positiveListener,
                                                       DialogInterface.OnClickListener negativeListener,
                                                       View view){
		AlertDialog dialog = new AlertDialog.Builder(context)
				.setView(view)
				.setTitle(title)
				.setCancelable(false)
				.setPositiveButton("确定", positiveListener)
				.setNegativeButton("取消", negativeListener)
				.create();

		// 键盘默认不弹出
		dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		return dialog;
	}
}
