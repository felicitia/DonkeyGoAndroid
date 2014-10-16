package com.donkey.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.view.animation.TranslateAnimation;

public class AppUtil {
	// 标记当前用户是否已经登录
	public static boolean haveLoggedin = false;
	public static int currentUserId = 1;
	public static String currentUserName = "liushuaikobe";

	/**
	 * 移动方法
	 * 
	 * @param v需要移动的View
	 * @param startX
	 *            起始x坐标
	 * @param toX终止x坐标
	 * @param startY
	 *            起始y坐标
	 * @param toY
	 *            终止y坐标
	 * 
	 * @author zouliping
	 * 
	 */
	public static void moveFrontBg(View v, int startX, int toX, int startY,
			int toY) {
		TranslateAnimation anim = new TranslateAnimation(startX, toX, startY,
				toY);
		anim.setDuration(250);
		anim.setFillAfter(true);
		v.startAnimation(anim);
	}

	/**
	 * 比较日期，判断是否在今天以后
	 * 
	 * @param date1
	 * @return
	 * @author zouliping
	 */
	public static boolean isDateAfter(String date1, String currentTime) {

		java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date d1 = null;
		Date d2 = null;
		try {
			d1 = df.parse(date1);
			d2 = df.parse(currentTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return d1.before(d2); // true date1在date2后
	}

	/**
	 * get current Date and Time String,return "yyyy-MM-dd HH:mm:ss"
	 * 
	 * @return
	 */
	public static String getCurrentTimeString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String dateTime = dateFormat.format(new Date());
		return dateTime;
	}

	/**
	 * create a folder
	 * 
	 * @param dirName
	 */
	public static void MakeDir(String dirName) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File destDir = new File(dirName);
			if (!destDir.exists()) {
				destDir.mkdirs();
			}
		}
	}

	/**
	 * encode the given bitmap to Base64
	 * 
	 * @param bitmap
	 * @return
	 */
	public static String encodeBase64(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 100, baos);
		byte[] bytes = baos.toByteArray();
		return Base64.encodeToString(bytes, Base64.URL_SAFE | Base64.NO_WRAP);
	}

	/**
	 * decode from a base64 to a bitmap
	 * 
	 * @param base64
	 * @return
	 */
	public static Bitmap decodeFromBase64(String base64) {
		byte[] bitmapArray = Base64.decode(base64, Base64.NO_WRAP
				| Base64.URL_SAFE);
		return BitmapFactory
				.decodeByteArray(bitmapArray, 0, bitmapArray.length);
	}

	/**
	 * finish dialog 
	 * 
	 * @param context
	 */
	public static void getBackDialog(final Context context) {
		new AlertDialog.Builder(context).setTitle("退出").setMessage("确定退出吗？")
				.setPositiveButton("退出", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						AppUtil.haveLoggedin = false;
						((Activity) context).finish();
					}

				}).setNegativeButton("取消", null).create().show();
	}
}