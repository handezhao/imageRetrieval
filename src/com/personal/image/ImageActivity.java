package com.personal.image;

import java.io.File;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.personal.image.utils.ImageLoaderUtil;
import com.personal.image.widget.HackyViewPager;

public class ImageActivity extends Activity {

	public static final String TAG = "ImageActivity";

	public static final int REQUEST_CODE_FORWARD = 1;
	private static final int REQUEST_CODE_CONTEXT_MENU = 2;

	public static final String BUNDLE_URLS = "bundle_urls"; // 大图数组
	public static final String BUNDLE_INDEX = "bundle_index"; // 数组下标
	public static final String BUNDLE_THUMBNAIL = "bundle_thumbnail";

	protected TextView tv_message;
	private HackyViewPager view_pager;
	private ImagePagerAdapter adapter;

	private ArrayList<String> uris;
	private String thumbnailPath;
	private int currentIndex = 0;

	private static final String ISLOCKED_ARG = "isLocked";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		findViewByID();
		processIntent();
		initData();
	}

	private void findViewByID() {
		tv_message = (TextView) findViewById(R.id.tv_message);
		view_pager = (HackyViewPager) findViewById(R.id.view_pager);
	}

	private void processIntent() {
		Intent intent = getIntent();
		uris = intent.getStringArrayListExtra(BUNDLE_URLS);
		if (uris == null || uris.isEmpty()) {
			finish();
			return;
		}

		thumbnailPath = intent.getStringExtra(BUNDLE_THUMBNAIL);
		currentIndex = intent.getIntExtra(BUNDLE_INDEX, 0);
		if (currentIndex >= uris.size()) {
			currentIndex = uris.size() - 1;
		} else if (currentIndex < 0) {
			currentIndex = 0;
		}
	}

	private void initData() {
		if (uris == null || uris.size() == 0 || currentIndex < 0
				|| currentIndex >= uris.size()) {
			finish();
			return;
		}

		adapter = new ImagePagerAdapter(uris.toArray(new String[0]),
				currentIndex, thumbnailPath, tv_message, this);
		view_pager.setAdapter(adapter);
		view_pager.setCurrentItem(currentIndex);
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
		outState.putBoolean(ISLOCKED_ARG, view_pager.isLocked());
		super.onSaveInstanceState(outState);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

//
//		if (requestCode == REQUEST_CODE_CONTEXT_MENU) {
//			if (resultCode == ContextMenuActivity.RESULT_CODE_SAVA_MENU) {
//				if (!CommonUtils.existsSDCard()) {
//					UserToast
//							.show(R.string.sd_card_is_not_available_can_not_be_saved);
//				}
//
//				try {
//					String url = uris.get(view_pager.getCurrentItem());
//					Console.d(TAG, "url is " + url);
//
//					ImageLoader imageLoader = ImageLoader.getInstance();
//					File file = imageLoader.getDiskCache().get(url);
//					Console.d(TAG, "file length " + file.length());
//
//					String path = createStorageImagePath();
//					File destination = new File(path);
//					IOHelper.copyFile(file, destination);
//					sendBroadcast(new Intent(
//							Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
//							Uri.fromFile(destination)));
//					UserToast.show(getString(R.string.save_successed)
//							+ System.getProperty("line.separator") + path);

					/*
					 * try {
					 * MediaStore.Images.Media.insertImage(getContentResolver(),
					 * path, "title", "description"); } catch (Exception e) {
					 * Console.printStackTrace(e); }
//					 */
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}

//	private String createStorageImagePath() {
//		File file = new File(Environment.getExternalStorageDirectory(),
//				"gcwds/file/image/" + System.currentTimeMillis() + ".jpg");
//		File dir = file.getParentFile();
//		if (!dir.exists()) {
//			if (!dir.mkdirs()) {
//				return null;
//			}
//		}
//
//		return file.getAbsolutePath();
//	}

	static class ImagePagerAdapter extends PagerAdapter implements
			ImageLoadingListener, ImageLoadingProgressListener {

		private String[] urls;
		private String thumbnailPath;
		private TextView tvMessage;
		private int currentIndex;

		private boolean isLoading = false;
		private boolean success = false;

		private View.OnClickListener onClick;
		private View.OnLongClickListener onLongClick;

		private DisplayImageOptions options;
		private DisplayImageOptions optionsThumbnail;

		public ImagePagerAdapter(String[] urls, int currentIndex,
				String thumbnailPath, TextView tv_message,
				final Activity activity) {
			super();
			this.urls = urls;
			this.currentIndex = currentIndex;
			this.thumbnailPath = thumbnailPath;
			this.tvMessage = tv_message;

			ImageScaleType scaleType;
			long size = Runtime.getRuntime().freeMemory();

			/*
			 * EXACTLY: 图像将完全按比例缩小的目标大小 EXACTLY_STRETCHED: 图片会缩放到目标大小完全
			 * IN_SAMPLE_INT: 图像将被二次采样的整数倍 IN_SAMPLE_POWER_OF_2:
			 * 图片将降低2倍，直到下一减少步骤，使图像更小的目标大小 NONE: 图片不会调整 NONE_SAFE:
			 * 只有在图片大于最大可接受的纹理大小(2048x2048)时才缩放
			 */
			if (1024 * 1024 * 4 < size) {
				scaleType = ImageScaleType.NONE_SAFE;
			} else {
				scaleType = ImageScaleType.EXACTLY;
			}

			options = ImageLoaderUtil.getInstance().getImageOption(null,
					scaleType, true);
			if (TextUtils.isEmpty(thumbnailPath)) {
			} else {
				File file = new File(thumbnailPath);
				if (file.exists()) {
					optionsThumbnail = ImageLoaderUtil.getInstance()
							.getImageOption(
									Drawable.createFromPath(thumbnailPath),
									scaleType, true);
				}
			}

			onClick = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					activity.finish();
				}
			};

//			onLongClick = new View.OnLongClickListener() {
//
//				@Override
//				public boolean onLongClick(View v) {
//					if (!success) {
//						return false;
//					}
//
//					Intent intent = new Intent(activity,
//							ContextMenuActivity.class);
//					intent.putExtra(ContextMenuActivity.BUNDLE_TYPE,
//							ContextMenuActivity.MENU_BIG_IMAGE);
//					activity.startActivityForResult(intent,
//							REQUEST_CODE_CONTEXT_MENU);
//					return false;
//				}
//			};
		}

		@Override
		public int getCount() {
			return urls.length;
		}

		@SuppressWarnings("deprecation")
		@Override
		public View instantiateItem(ViewGroup container, int position) {
			PhotoView photoView = new PhotoView(container.getContext());
			// 设置显示图片时的最大放大倍数
			float scale = photoView.getMaxScale();
			photoView.setMaxScale(2 * scale);

			String url = urls[position];
			if (url.startsWith("/")) {
				url = "file://" + url;
			}

			ImageLoader imageLoader = ImageLoader.getInstance();
			if (position == currentIndex && optionsThumbnail != null) {
				imageLoader.displayImage(url, photoView, optionsThumbnail,
						this, this);
			} else {
				imageLoader.displayImage(url, photoView, options, this, this);
			}

			container.addView(photoView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			photoView.setOnClickListener(onClick);
			photoView.setOnLongClickListener(onLongClick);
			photoView
					.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
						@Override
						public void onViewTap(View paramView, float paramFloat1, float paramFloat2) {
							onClick.onClick(paramView);
						}
					});

			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void onLoadingStarted(String imageUri, View view) {
			tvMessage.setVisibility(View.VISIBLE);
			tvMessage.setText("正在下载");
			isLoading = true;
			success = false;
		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			tvMessage.setText("下载图片失败");
			isLoading = false;
			success = false;
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			tvMessage.setVisibility(View.GONE);
			isLoading = false;
			success = true;
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			tvMessage.setVisibility(View.GONE);
			isLoading = false;
			success = false;
		}

		@Override
		public void onProgressUpdate(String imageUri, View view, int current,
				int total) {
			tvMessage.setText(String.format("正在下载 %.1f%%", current * 100.0
					/ total));
		}
	}
}