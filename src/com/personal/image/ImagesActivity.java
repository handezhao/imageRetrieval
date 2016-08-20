package com.personal.image;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.personal.image.utils.Constant;
import com.personal.image.utils.ImageFolder;
import com.personal.image.utils.ImageUtils;
import com.personal.image.utils.ImagesAdapter;
import com.personal.image.utils.OnCheckedChangedListener;
import com.personal.image.utils.Utils;
import com.personal.image.widget.ButtonTitleBarView;
import com.personal.image.widget.ListImageDirPopupWindow;
import com.personal.image.widget.ListImageDirPopupWindow.OnImageDirSelected;


public class ImagesActivity extends Activity implements OnCheckedChangedListener, OnImageDirSelected {

	protected static final String TAG = "ImagesActivity";

	public static final int REQUEST_CODE_CAMERA = 12;
	public static final String IS_ZOOM = "is_zoom";

	private String imageDirectory;

	private List<String> images = new ArrayList<String>();
	private List<String> totalImages = new ArrayList<String>();

	private ButtonTitleBarView titleBar;
	private RelativeLayout layoutBottomBar;

	private TextView tvDirectory;
	private TextView tvImageCount;

	private GridView gvImage;
	private ImagesAdapter adapter;

	private List<ImageFolder> imageFloders = new ArrayList<ImageFolder>();
	private ListImageDirPopupWindow listImageDirPopupWindow;

	public static final String BUNDLE_VALUES = "select_values";
	public static final String BUNDLE_MAX = "max_value";

	private int maximumCount;

	private Uri cameraUri;

	File destination;

	private Handler handler = new Handler(Looper.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
//			hideProgress();
			data2View();
			initListDirPopupWindw();
		}
	};

	/**
	 * 为View绑定数据
	 */
	private void data2View() {
		fillWithImagesInDir(null);
		adapter = new ImagesAdapter(this, images, R.layout.item_images_grid, this, maximumCount);
		adapter.addCamera();
		adapter.setCameraClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {							
				File temporary = new File(Constant.CACHE_ROOT, "temporary");
				if (temporary.exists()) {			
				} else {
					temporary.mkdirs();
				}			

				destination = new File(temporary, (System.currentTimeMillis() + ".jpeg"));

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
				intent.addCategory(Intent.CATEGORY_DEFAULT);
				intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);

				ContentValues contentValues = new ContentValues(2);  
				contentValues.put(MediaStore.Images.Media.DATA, destination.getAbsolutePath());  
				contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");  
				cameraUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);  

				intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);  
				startActivityForResult(intent, REQUEST_CODE_CAMERA);  
			}
		});
		gvImage.setAdapter(adapter);
		tvImageCount.setText(images.size() + "张");
	};

	private void fillWithImagesInDir(String dir) {
		images.clear();
		if (dir != null) {
			for (String filename : totalImages) {
				if (filename.contains(dir)) {
					images.add(filename);
				}
			}
		} else {
			images.addAll(totalImages);
		}
	}

	private void initListDirPopupWindw() {
		listImageDirPopupWindow = new ListImageDirPopupWindow(LayoutParams.MATCH_PARENT, (int) (Utils.getScreenHeight(this) * 0.8), imageFloders, LayoutInflater.from(getApplicationContext()).inflate(R.layout.list_dir, null));
		listImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		listImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_images);
		findViewByID();
		setListener();
		processIntent();
		initView();
		getImages();
	}

	
	private void findViewByID() {
		titleBar = (ButtonTitleBarView) findViewById(R.id.titlebar);
		layoutBottomBar = (RelativeLayout) findViewById(R.id.layout_bottom_bar);
		gvImage = (GridView) findViewById(R.id.gv_image);
		tvDirectory = (TextView) findViewById(R.id.tv_directory);
		tvImageCount = (TextView) findViewById(R.id.tv_image_count);
	}

	private void setListener() {
		titleBar.setLeftListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				failFinish();
			}
		});

		titleBar.setButtonListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				successFinish();
			}
		});

		layoutBottomBar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listImageDirPopupWindow.setAnimationStyle(R.style.AnimBottom);
				listImageDirPopupWindow.setSelected(imageDirectory);
				listImageDirPopupWindow.showAsDropDown(layoutBottomBar, 0, 0);

				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 0.3f;
				getWindow().setAttributes(lp);
			}
		});
	}

	private void initView() {
		titleBar.setLeftText(getString(R.string.select_image));
		if (maximumCount == 1) {
			titleBar.hideRightButton();
		} else {
			titleBar.setRightButtonText(getString(R.string.ok));
		}
	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(ImagesActivity.this, "SD卡不可用",Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
//		showProgress("正在加载...");

		new Thread(new Runnable() {
			@Override
			public void run() {

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = ImagesActivity.this.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "+
						MediaStore.Images.Media.MIME_TYPE + "=? or "
						+ MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?", new String[] {
						"image/jpeg", "image/jpg", "image/png", "image/gif" }, MediaStore.Images.Media.DATE_MODIFIED + " DESC");

				Log.e(TAG, mCursor.getCount() + "");

				HashMap<String, Integer> counter = new HashMap<String, Integer>();
				HashMap<String, String> firstImgs = new HashMap<String, String>();
				int c = 0;

				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

					File file = new File(path);
					if (!file.exists()) {
						continue;
					}

					// 获取该图片的父路径名
					File parentFile = file.getParentFile();
					if (parentFile == null) {
						continue;
					}

					totalImages.add(path);

					String dirPath = parentFile.getAbsolutePath();
					if (counter.containsKey(dirPath)) {
						c = counter.get(dirPath);
					} else {
						c = 0;
						firstImgs.put(dirPath, path);
					}

					c ++;
					counter.put(dirPath, c);
				}

				if (totalImages.size() > 0) {
					ImageFolder imageFloder = new ImageFolder();
					imageFloder.setDir(null);
					imageFloder.setFirstImagePath(totalImages.get(0));
					imageFloder.setCount(totalImages.size());
					imageFloders.add(imageFloder);
				}

				Iterator<Entry<String, Integer>> iter = counter.entrySet().iterator();
				while (iter.hasNext()) {
					Entry<String, Integer> e = iter.next();

					ImageFolder imageFloder = new ImageFolder();
					imageFloder.setDir(e.getKey());
					imageFloder.setFirstImagePath(firstImgs.get(e.getKey()));
					imageFloder.setCount(e.getValue());
					imageFloders.add(imageFloder);
				}

				Collections.sort(imageFloders, new Comparator<ImageFolder>() {
					@Override
					public int compare(ImageFolder lhs, ImageFolder rhs) {
						if (lhs.getDir() == null) {
							return -1;
						}
						if (rhs.getDir() == null) {
							return 1;
						}

						if (lhs.getCount() > rhs.getCount()) {
							return -1;
						} else if (lhs.getCount() < rhs.getCount()) {
							return 1;
						}
						return 0;
					}
				});

				mCursor.close();

				firstImgs = null;
				counter = null;

				handler.sendEmptyMessage(0x110);
			}
		}).start();
	}

	private void processIntent() {
		maximumCount = getIntent().getIntExtra(BUNDLE_MAX, 0);
		if (maximumCount < 1) {
			maximumCount = 1;
		}
	}

	@Override
	public void onChecked(View view, boolean checked) {
		if (maximumCount == 1 && checked) {
			successFinish();
		} else if (maximumCount > 1) {
			if (adapter.getValues().size() > 0) {
				titleBar.setRightButtonText(getString(R.string.ok) + "(" + adapter.getValues().size() + "/" + maximumCount + ")");
			} else {
				titleBar.setRightButtonText(getString(R.string.ok));
			}

		}
	}

	private void successFinish() {
		Intent intent = new Intent();
		intent.putStringArrayListExtra(BUNDLE_VALUES, (ArrayList<String>) adapter.getValues());
		setResult(RESULT_OK, intent);
		finish();
	}

	private void failFinish() {
		setResult(RESULT_CANCELED);
		finish();
	}

	@Override
	public void selected(ImageFolder folder) {
		String dir = folder.getDir();
		if (dir == null) {
			fillWithImagesInDir(null);
		} else {
			fillWithImagesInDir(dir);
		}

		imageDirectory = dir;
		tvImageCount.setText("共 " + folder.getCount() + " 张");
		tvDirectory.setText(folder.getName());
		listImageDirPopupWindow.dismiss();
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (Activity.RESULT_OK == resultCode && REQUEST_CODE_CAMERA == requestCode) {

			if (destination.exists()) {
				ArrayList<String> list = new ArrayList<String>();
				list.add(destination.getAbsolutePath());
             // 某些机型会旋转拍摄的照片，我们将其还原
				int degree = ImageUtils.getBitmapDegree(destination.getAbsolutePath());

				if (degree != 0) {
					ImageUtils.rotateBitmapByDegree(destination.getAbsolutePath(), degree);
				}

				Intent intent = new Intent();
				intent.putStringArrayListExtra(BUNDLE_VALUES, list);
				setResult(RESULT_OK, intent);
				finish();
			}
		} else if (Activity.RESULT_OK == resultCode && ImagesAdapter.REQUEST_CODE_ZOOM == requestCode) {
			Intent intent = new Intent();
			intent.putStringArrayListExtra(BUNDLE_VALUES, data.getStringArrayListExtra(ZoomImageActivity.RESULT_VALUES));
			intent.putExtra(IS_ZOOM, true);
			setResult(RESULT_OK, intent);
			finish();
		}
	}
}

