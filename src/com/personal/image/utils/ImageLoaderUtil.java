package com.personal.image.utils;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration.Builder;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.personal.image.BuildConfig;
import com.personal.image.R;

public class ImageLoaderUtil {

	public static final String TAG = "ImageLoaderUtil";

	private static ImageLoaderUtil instance;

	private ImageLoaderUtil() {}

	public synchronized static ImageLoaderUtil getInstance() {
		if (instance == null) {
			instance = new ImageLoaderUtil();
		}
		return instance;
	}

	public DisplayImageOptions getOptions(int resourceId, boolean saveOnDisk) {
		return new DisplayImageOptions.Builder()
		.showImageForEmptyUri(resourceId)
		.showImageOnFail(resourceId)
		.cacheInMemory(saveOnDisk)
		.cacheOnDisk(saveOnDisk)
		.build();
	}

	public DisplayImageOptions getOptions(Drawable drawable, boolean saveOnDisk) {
		return new DisplayImageOptions.Builder()
		.showImageForEmptyUri(drawable) 
		.showImageOnFail(drawable)
		.cacheInMemory(saveOnDisk)
		.cacheOnDisk(saveOnDisk)
		.build();
	}

	public DisplayImageOptions getImageOption(Drawable drawable, ImageScaleType imageScaleType, boolean saveOnDisk) {
		return new DisplayImageOptions.Builder()
		.showImageForEmptyUri(drawable)
		.showImageOnFail(drawable)
		.cacheInMemory(saveOnDisk)
		.cacheOnDisk(saveOnDisk) 
		.imageScaleType(imageScaleType)
		.build();
	}

	public DisplayImageOptions getDefaultOptions() {
		return new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.ic_picture_error) 
		.showImageOnFail(R.drawable.ic_picture_error)
		.cacheInMemory(true)
		.cacheOnDisk(true) 
		.build(); 
	}
	

	public DisplayImageOptions getDownloadOptions() {
		return new DisplayImageOptions.Builder()
		.cacheInMemory(false)
		.cacheOnDisk(false) 
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.imageScaleType(ImageScaleType.NONE)
		.build(); 
	}

	public DisplayImageOptions getScaledOptions() {
		return new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.ic_picture_error)
		.showImageOnFail(R.drawable.ic_picture_error)
		.cacheInMemory(true)
		.cacheOnDisk(true)
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
	}



	public void initializeImageLoaderConfig(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context, "MyPictures/cache/image/");

		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.bitmapConfig(Bitmap.Config.RGB_565)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

		Builder builder = new ImageLoaderConfiguration.Builder(context).memoryCacheExtraOptions(480, 480)
				.threadPoolSize(10)
				.threadPriority(Thread.NORM_PRIORITY)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.memoryCache(new UsingFreqLimitedMemoryCache(4 * 1024 * 1024))
				.memoryCacheSize(4 * 1024 * 1024) 
				.diskCacheSize(512 * 1024 * 1024)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.defaultDisplayImageOptions(options)
				.diskCache(new UnlimitedDiskCache(cacheDir))
				.imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000));

		if (BuildConfig.DEBUG) {
			builder.writeDebugLogs();
		}

		ImageLoader.getInstance().init(builder.build());
	}

	/**
	 * 
	 */
	public interface OnCallBackListener {

		public void setOnCallBackListener(Bitmap bitmap, String url);
	}
}

