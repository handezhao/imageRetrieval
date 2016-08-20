package com.personal.image.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;
import android.view.WindowManager;

import com.personal.image.R;

public class RetrievalHelper {

	private static RetrievalHelper instance = new RetrievalHelper();

	private static final int HASH_SIZE = 8;

	private RetrievalHelper() {
	}

	public static RetrievalHelper getInstance() {
		return instance;
	}
	
	/**
	 * 对资源文件适当压缩
	 */
	public Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.inJustDecodeBounds = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, opt);
		if (bitmap == null) {
		}
		float realWidth = opt.outWidth;
		float realHeight = opt.outHeight;

//		int width = Utils.getScreenWidth(context);
//		int height = Utils.getScreenHeight(context);
		int width = Utils.dip2px(context, 80);
		int height = Utils.dip2px(context, 80);
		int heightRatio = (int) Math.ceil(opt.outHeight / (float) height);
		int widthRatio = (int) Math.ceil(opt.outWidth / (float) width);
		// 如果两个比率都大于1，那么图像的一条边将大于屏幕
		if (heightRatio > 1 && widthRatio > 1) {
			if (heightRatio > widthRatio) {
				// 若高度比率更大，则根据它缩放
				opt.inSampleSize = heightRatio;
			} else {
				opt.inSampleSize = widthRatio;
			}
		} else {
			opt.inSampleSize = 1;
		}
		Log.d("inSampleSize", "inSampleSize is " + opt.inSampleSize);
		opt.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeStream(is, null, opt);
		return bitmap;
	}

	// 所有椅子
	public List<Bitmap> localBitmap(Context context) {
		int[] pics = { R.drawable.ic_pc_1, R.drawable.ic_pc_2,
				R.drawable.ic_pc_3, R.drawable.ic_pc_4, R.drawable.ic_pc_5,
				R.drawable.ic_pc_6, R.drawable.ic_pc_7, R.drawable.ic_pc_8,
				R.drawable.ic_pc_9, R.drawable.ic_pc_10, R.drawable.ic_pc_11,
				R.drawable.ic_ts_1, R.drawable.ic_ts_2, R.drawable.ic_ts_3,
				R.drawable.ic_ts_4, R.drawable.ic_ts_5, R.drawable.ic_ts_6,
				R.drawable.ic_ts_7, R.drawable.ic_ts_8, R.drawable.ic_ts_9,
				R.drawable.ic_ts_10, R.drawable.ic_ts_11 };
		List<Bitmap> bmplist = new ArrayList<Bitmap>();
		if (pics.length > 0) {
			for (int i = 0; i < pics.length; i++) {
				Bitmap bmp = readBitMap(context, pics[i]);
				bmplist.add(bmp);
			}
		}
		return bmplist;
	}

	// 电脑椅
	public List<Bitmap> pcChair(Context context) {
		int[] pics = { R.drawable.ic_pc_1, R.drawable.ic_pc_2,
				R.drawable.ic_pc_3, R.drawable.ic_pc_4, R.drawable.ic_pc_5,
				R.drawable.ic_pc_6, R.drawable.ic_pc_7, R.drawable.ic_pc_8,
				R.drawable.ic_pc_9, R.drawable.ic_pc_10, R.drawable.ic_pc_11 };
		List<Bitmap> bmplist = new ArrayList<Bitmap>();
		if (pics.length > 0) {
			for (int i = 0; i < pics.length; i++) {
				Bitmap bmp = readBitMap(context, pics[i]);
				bmplist.add(bmp);
			}
		}
		return bmplist;
	}

	// 太师椅
	public List<Bitmap> tsChair(Context context) {
		int[] pics = { R.drawable.ic_ts_1, R.drawable.ic_ts_2,
				R.drawable.ic_ts_3, R.drawable.ic_ts_4, R.drawable.ic_ts_5,
				R.drawable.ic_ts_6, R.drawable.ic_ts_7, R.drawable.ic_ts_8,
				R.drawable.ic_ts_9, R.drawable.ic_ts_10, R.drawable.ic_ts_11 };
		List<Bitmap> bmplist = new ArrayList<Bitmap>();
		if (pics.length > 0) {
			for (int i = 0; i < pics.length; i++) {
				Bitmap bmp = readBitMap(context, pics[i]);
				bmplist.add(bmp);
			}
		}
		return bmplist;
	}

	// 将图片与对应的信息封装
	public List<ImageInfo> initDataList(Context context) {
		int[] pics = { R.drawable.ic_pc_1, R.drawable.ic_pc_2,
				R.drawable.ic_pc_3, R.drawable.ic_pc_4, R.drawable.ic_pc_5,
				R.drawable.ic_pc_6, R.drawable.ic_pc_7, R.drawable.ic_pc_8,
				R.drawable.ic_pc_9, R.drawable.ic_pc_10, R.drawable.ic_pc_11,
				R.drawable.ic_ts_1, R.drawable.ic_ts_2, R.drawable.ic_ts_3,
				R.drawable.ic_ts_4, R.drawable.ic_ts_5, R.drawable.ic_ts_6,
				R.drawable.ic_ts_7, R.drawable.ic_ts_8, R.drawable.ic_ts_9,
				R.drawable.ic_ts_10, R.drawable.ic_ts_11 };
		// 公开号
		String[] publicNum = context.getResources().getStringArray(
				R.array.publicNum);
		// 申请人
		String[] applyPerson = context.getResources().getStringArray(
				R.array.applyPerson);
		// 产品名
		String[] name = context.getResources().getStringArray(R.array.name);
		// 申请号
		String[] patentNum = context.getResources().getStringArray(
				R.array.patentNum);

		List<Bitmap> bmplist = new ArrayList<Bitmap>();
		if (pics.length > 0) {
			for (int i = 0; i < pics.length; i++) {
				Bitmap bmp = readBitMap(context, pics[i]);
				bmplist.add(bmp);
			}
		}

		List<ImageInfo> list = new ArrayList<ImageInfo>();
		if (bmplist.isEmpty()) {
		} else {
			for (int i = 0; i < bmplist.size(); i++) {
				ImageInfo info = new ImageInfo();
				info.setApplyPerson(applyPerson[i]);
				info.setBitmap(bmplist.get(i));
				info.setPatentNum(patentNum[i]);
				info.setName(name[i]);
				info.setPublicNum(publicNum[i]);
				list.add(info);
			}
		}
		return list;
	}

	/**
	 * 
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		final int height = bmpOriginal.getHeight();
		final int width = bmpOriginal.getWidth();

		final Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		final Canvas c = new Canvas(bmpGrayscale);
		final Paint paint = new Paint();
		final ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		final ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	/**
	 * 一张图片的Dh值
	 */
	public static String getDhash(Bitmap img) {
		// Resize & Grayscale
		img = toGrayscale(img);
		img = Bitmap.createScaledBitmap(img, HASH_SIZE + 1, HASH_SIZE, true);

		List<Boolean> difference = new ArrayList<Boolean>();
		for (int row = 0; row < HASH_SIZE; row++) {
			for (int col = 0; col < HASH_SIZE; col++) {
				int pixel_left = img.getPixel(col, row);
				int pixel_right = img.getPixel(col + 1, row);

				difference.add(pixel_left > pixel_right);
			}
		}

		img.recycle();
		// Bitmap.recycle(img);
		img = null;

		int decimalValue = 0;
		String result = "";
		for (int i = 0, max = difference.size(); i < max; i++) {
			boolean value = difference.get(i);
			if (value)
				decimalValue += 2 ^ (i % 8);

			if (i % 8 == 7) {
				String hex = String.format("%02x", decimalValue);
				result = String.format("%s%s", result, hex);
				decimalValue = 0;
			}
		}
		return result;
	}

	/**
	 * 两个Dh值得差异大小
	 */
	public static int getDistance(String s1, String s2) {
		if (s1 == null || s2 == null || s1.length() != s2.length()) {
			throw new IllegalArgumentException();
		}

		int distance = 0;
		for (int i = 0; i < s1.length(); i++) {
			if (s1.charAt(i) != s2.charAt(i)) {
				distance++;
			}
		}
		return distance;
	}

	/**
	 * 本地库中的DH值
	 */

	public List<ImageInfo> getLocalDh(Context context) {
		// 公开号
		String[] publicNum = context.getResources().getStringArray(
				R.array.publicNum);
		// 申请人
		String[] applyPerson = context.getResources().getStringArray(
				R.array.applyPerson);
		// 产品名
		String[] name = context.getResources().getStringArray(R.array.name);
		// 申请号
		String[] patentNum = context.getResources().getStringArray(
				R.array.patentNum);

		List<Bitmap> localList = localBitmap(context);

		List<ImageInfo> bitmapDhList = new ArrayList<ImageInfo>();
		if (localList.isEmpty()) {
		} else {
			for (int i = 0; i < localList.size(); i++) {
				ImageInfo bitmapInfo = new ImageInfo();
				bitmapInfo.setDh(getDhash(localList.get(i)));
				bitmapInfo.setBitmap(localList.get(i));
				bitmapInfo.setApplyPerson(applyPerson[i]);
				bitmapInfo.setName(name[i]);
				bitmapInfo.setPatentNum(patentNum[i]);
				bitmapInfo.setPublicNum(publicNum[i]);
				bitmapDhList.add(bitmapInfo);
			}
		}
		return bitmapDhList;
	}
	
	
	public List<ImageInfo> getLocalDh(Context context, boolean isPcChair, boolean isFromLocal) {
		// 公开号
		String[] publicNum = context.getResources().getStringArray(
				R.array.publicNum);
		// 申请人
		String[] applyPerson = context.getResources().getStringArray(
				R.array.applyPerson);
		// 产品名
		String[] name = context.getResources().getStringArray(R.array.name);
		// 申请号
		String[] patentNum = context.getResources().getStringArray(
				R.array.patentNum);

		List<Bitmap> localList = localBitmap(context);

		List<ImageInfo> bitmapDhList = new ArrayList<ImageInfo>();
		List<ImageInfo> resultDhList = new ArrayList<ImageInfo>();
		if (localList.isEmpty()) {
		} else {
			for (int i = 0; i < localList.size(); i++) {
				ImageInfo bitmapInfo = new ImageInfo();
				bitmapInfo.setDh(getDhash(localList.get(i)));
				bitmapInfo.setBitmap(localList.get(i));
				bitmapInfo.setApplyPerson(applyPerson[i]);
				bitmapInfo.setName(name[i]);
				bitmapInfo.setPatentNum(patentNum[i]);
				bitmapInfo.setPublicNum(publicNum[i]);
				bitmapDhList.add(bitmapInfo);
			}
		}
		//设置匹配区域
		if (isFromLocal) {
			resultDhList = bitmapDhList;
		} else {
			if (isPcChair) {
				resultDhList = bitmapDhList.subList(0, 11);
			} else {
				bitmapDhList.subList(0, 11);
				resultDhList = bitmapDhList;
			}
		}
		
		return resultDhList;
	}

	/**
	 * 目标值比较本地库中的DH值
	 */

	public List<ImageInfo> retrieval(Context context, String targerDh,
			boolean isPcChair, boolean isFromLocal) {
		List<ImageInfo> localList = new ArrayList<ImageInfo>();
		
		localList = getLocalDh(context, isPcChair, isFromLocal);

		Map<Integer, ImageInfo> resultMap = new HashMap<Integer, ImageInfo>();
		for (int i = 0; i < localList.size(); i++) {
			int distance = getDistance(targerDh, localList.get(i).getDh());
			resultMap.put(distance, localList.get(i));
		}
		List<Integer> distance = new ArrayList<Integer>();
		for (Integer key : resultMap.keySet()) {
			distance.add(key);
		}
		int min = distance.get(0);
		for (Integer integer : distance) {
			if (integer < min) {
				min = integer;
			}
		}
		Log.d("min", "min is " + min);
		// 去掉自己
		resultMap.remove(min);
		distance.clear();
		localList.clear();
		for (Integer key : resultMap.keySet()) {
			distance.add(key);
		}
		if (!distance.isEmpty()) {
			int secondMin = distance.get(0);
			for (Integer integer : distance) {
				if (integer < secondMin) {
					secondMin = integer;
				}
			}
			Log.d("min", "secondMin is " + secondMin);
			localList.add(resultMap.get(secondMin));
			
			// 去掉自己
			distance.clear();
			resultMap.remove(secondMin);
			for (Integer key : resultMap.keySet()) {
				distance.add(key);
			}
			if (!distance.isEmpty()) {
				int thirdMin = distance.get(0);
				for (Integer integer : distance) {
					if (integer < thirdMin) {
						thirdMin = integer;
					}
				}
				Log.d("min", "thirdMin is " + thirdMin);
				localList.add(resultMap.get(thirdMin));
				
				// 去掉自己
				distance.clear();
				resultMap.remove(thirdMin);
				for (Integer key : resultMap.keySet()) {
					distance.add(key);
				}
				if (!distance.isEmpty()) {
					int forthMin = distance.get(0);
					for (Integer integer : distance) {
						if (integer < forthMin) {
							forthMin = integer;
						}
					}
					Log.d("min", "forthMin is " + forthMin);
					localList.add(resultMap.get(forthMin));
				}

			}
		}

		return localList;
	}
}
