package com.personal.image.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class ImageUtils {

	public static Bitmap scaleImage(String path, float targetWidth,
			float targetHeight) {
		Bitmap bitmap = null;

		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;

			bitmap = BitmapFactory.decodeFile(path, options);
			int w = options.outWidth;
			int h = options.outHeight;

			if (w > h && w > targetWidth) {
				options.inSampleSize = (int) (options.outWidth / targetWidth);
			} else if (w < h && h > targetHeight) {
				options.inSampleSize = (int) (options.outHeight / targetHeight);
			}

			if (options.inSampleSize <= 0) {
				options.inSampleSize = 1;
			}

			options.inPreferredConfig = Bitmap.Config.RGB_565;
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFile(path, options);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	public static int[] getBitmapBounds(String path) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeFile(path, options);
			return new int[] { options.outWidth, options.outHeight };
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bitmap != null) {
				bitmap.recycle();
			}
		}

		return new int[] { 0, 0 };
	}

	/**
	 * 读取图片的旋转的角度
	 *
	 * @param path
	 *            图片绝对路径
	 * @return 图片的旋转角度
	 */
	public static int getBitmapDegree(String path) {
		int degree = 0;
		try {
			// 从指定路径下读取图片，并获取其EXIF信息
			ExifInterface exifInterface = new ExifInterface(path);
			// 获取图片的旋转信息
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return degree;
	}

	public static void rotateBitmapByDegree(String path, int degree) {
		Bitmap returnBm = null;
		Bitmap bm = BitmapFactory.decodeFile(path);
		// 根据旋转角度，生成旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		try {
			// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
			returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
		}
		if (returnBm == null) {
			returnBm = bm;
		}
		if (bm != returnBm) {
			bm.recycle();
		}

		File file = new File(path);
		file.delete();

		compressImage(returnBm, 100, path);

	}

	public static void compressImage(Bitmap bitmap, int quality,
			String destination) {
		FileOutputStream fos = null;

		try {
			File file = new File(destination);
			if (file.exists()) {
				file.delete();
			}

			file.createNewFile();
			fos = new FileOutputStream(file);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			fos.write(baos.toByteArray());
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public static Bitmap getBitmapFromUri(String uri) {
		try {
			// 读取uri所在的图片
			Bitmap bitmap = BitmapFactory.decodeFile(uri, getBitmapOption(2));
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Options getBitmapOption(int inSampleSize) {
		System.gc();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inSampleSize = inSampleSize;
		return options;
	}
}
