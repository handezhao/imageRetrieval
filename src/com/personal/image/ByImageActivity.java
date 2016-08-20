package com.personal.image;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.personal.image.utils.CommonAdapter;
import com.personal.image.utils.Constant;
import com.personal.image.utils.ImageInfo;
import com.personal.image.utils.ImageLoaderUtil;
import com.personal.image.utils.ImageUtils;
import com.personal.image.utils.RetrievalHelper;
import com.personal.image.utils.ViewHolder;
import com.soundcloud.android.crop.Crop;

public class ByImageActivity extends Activity {

	public static final int REQUEST_SELECT_TARGET_IMAGE = 1;
	public static final int REQUEST_SELECT_LOCAL_IMAGE = 2;
	public static final String TAG = "ByImageActivity";

	private ImageView ivTargetImage, ivLocalImage, ivShow;
	private TextView tvPath, tvBack;
	private RetrievalHelper helper;
	private Bitmap targetBmp;
	private Button buttonStart;
	boolean isPcChair = false;
	boolean isFromLocal = true;
	private GridView gvResult;
	private String targetBitmapDh;
	private CommonAdapter<ImageInfo> adapter;
	private List<ImageInfo> resultList = new ArrayList<ImageInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_by_image);
		findViewById();
		initData();
		setListener();
	}

	private void initData() {
		helper = RetrievalHelper.getInstance();
	}

	private void findViewById() {
		ivTargetImage = (ImageView) findViewById(R.id.iv_target_image);
		tvPath = (TextView) findViewById(R.id.tv_path);
		tvBack = (TextView) findViewById(R.id.tv_back);
		buttonStart = (Button) findViewById(R.id.bt_start_request);
		ivLocalImage = (ImageView) findViewById(R.id.iv_local_image);
		gvResult = (GridView) findViewById(R.id.gv_result);
		ivShow = (ImageView) findViewById(R.id.iv_show);
	}

	private void setListener() {
		ivTargetImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// PickDialog pickDialog = new PickDialog(ByImageActivity.this);
				// pickDialog.show();
				Intent intent = new Intent(ByImageActivity.this,
						ImagesActivity.class);
				intent.putExtra(ImagesActivity.BUNDLE_MAX, 1);
				startActivityForResult(intent, REQUEST_SELECT_TARGET_IMAGE);
			}
		});

		tvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});

		buttonStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (TextUtils.isEmpty(targetBitmapDh)) {
					Toast.makeText(getApplication(),
							getResources().getString(R.string.warm),
							Toast.LENGTH_SHORT).show();
				} else {
					// 将目标值与本地库对比
					resultList = helper.retrieval(getApplication(),
							targetBitmapDh, isPcChair, isFromLocal);
					Log.d(TAG, "targetDh is: " + targetBitmapDh);
					if (resultList.isEmpty()) {
					} else {
						adapter = new CommonAdapter<ImageInfo>(
								ByImageActivity.this, resultList,
								R.layout.item_local_images) {

							@Override
							public void convert(ViewHolder holder,
									ImageInfo item, int position) {
								holder.setImageBitmap(R.id.iv_item_image,
										item.getBitmap());
								holder.setText(R.id.tv_item_name,
										item.getName());
							}
						};
						gvResult.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					}

				}
			}
		});

		ivLocalImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setClass(ByImageActivity.this, LocalImagesActivity.class);
				// startActivityForResult(intent, REQUEST_SELECT_LOCAL_IMAGE);
				startActivityForResult(intent, REQUEST_SELECT_LOCAL_IMAGE);

			}
		});

		gvResult.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ImageInfo info = adapter.getItem(position);
				Intent intent = new Intent();
				intent.putExtra("name", info.getName());
				intent.putExtra("patent", info.getPatentNum());
				intent.putExtra("publicnum", info.getPublicNum());
				intent.putExtra("applyperson", info.getApplyPerson());
				intent.putExtra("bitmap", info.getBitmap());
				intent.setClass(ByImageActivity.this, CatImageActivity.class);
				startActivity(intent);
			}
		});

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			final Intent data) {

		if (REQUEST_SELECT_TARGET_IMAGE == requestCode) { // 获取一个照片
			if (data == null) {
				return;
			}

			ArrayList<String> values = data
					.getStringArrayListExtra(ImagesActivity.BUNDLE_VALUES);
			if (values == null || values.size() != 1) {
				return;
			}

			String compressDestination = Constant.CACHE_ROOT
					+ System.currentTimeMillis() + ".jpg";
			final Bitmap bitmap = ImageUtils.scaleImage(values.get(0), 480.0f,
					800.0f);
			ImageUtils.compressImage(bitmap, 50, compressDestination);
			// bitmap.recycle();

			String avatar = Constant.CACHE_ROOT + System.currentTimeMillis()
					+ ".jpg";
			boolean isZoom = data
					.getBooleanExtra(ImagesActivity.IS_ZOOM, false);
			// 区分裁剪
			if (isZoom) {
				targetBmp = bitmap;
				targetBitmapDh = helper.getDhash(targetBmp);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ivShow.setImageBitmap(bitmap);
					}
				});
			} else {
				Crop.of(Uri.fromFile(new File(compressDestination)),
						Uri.fromFile(new File(avatar))).asSquare()
						.withMaxSize(800, 800).start(this);
			}
		} else if (Crop.REQUEST_CROP == requestCode) {
			if (data == null) {
				return;
			}

			try {
				final Uri result = Crop.getOutput(data);

				File file = new File(new URI(result.toString()));
				Log.d("ByImageActivity", "file is " + file);
				if (file.exists()) {
					int[] bounds = ImageUtils.getBitmapBounds(file
							.getAbsolutePath());
					int width = bounds[0];
					int height = bounds[1];
					// 限制最小尺寸
					if (width < 50 || height < 50) {
						Toast.makeText(ByImageActivity.this,
								getString(R.string.picture_below_limit),
								Toast.LENGTH_SHORT).show();
					} else if (width > 1200 || height > 2000) {
						Toast.makeText(ByImageActivity.this,
								getString(R.string.picture_beyond_limit),
								Toast.LENGTH_SHORT).show();
					} else {
						// 获取目标图片
						targetBmp = getBitmapFromUri(result);
						targetBitmapDh = helper.getDhash(targetBmp);
						isFromLocal = true;
						isPcChair = false;
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								ImageLoader.getInstance().displayImage(
										result.toString(),
										ivShow,
										ImageLoaderUtil.getInstance()
												.getDefaultOptions());
								tvPath.setText("path is: " + result.toString());
								Log.d("ByImageActivity",
										"path is " + result.toString());
							}
						});
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (Crop.REQUEST_CROP_PC == requestCode) {
			if (data == null) {
				return;
			}

			try {
				final Uri result = Crop.getOutput(data);

				File file = new File(new URI(result.toString()));

				if (file.exists()) {
					int[] bounds = ImageUtils.getBitmapBounds(file
							.getAbsolutePath());
					int width = bounds[0];
					int height = bounds[1];
					// 限制最小尺寸
					if (width < 50 || height < 50) {
						Toast.makeText(ByImageActivity.this,
								getString(R.string.picture_below_limit),
								Toast.LENGTH_SHORT).show();
					} else if (width > 1200 || height > 2000) {
						Toast.makeText(ByImageActivity.this,
								getString(R.string.picture_beyond_limit),
								Toast.LENGTH_SHORT).show();
					} else {
						// 获取目标图片
						targetBmp = getBitmapFromUri(result);
						targetBitmapDh = helper.getDhash(targetBmp);
						isFromLocal = false;
						isPcChair = true;
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								ImageLoader.getInstance().displayImage(
										result.toString(),
										ivShow,
										ImageLoaderUtil.getInstance()
												.getDefaultOptions());
								tvPath.setText("path is: " + result.toString());
							}
						});
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (Crop.REQUEST_CROP_TS == requestCode) {
			if (data == null) {
				return;
			}

			try {
				final Uri result = Crop.getOutput(data);

				File file = new File(new URI(result.toString()));

				if (file.exists()) {
					int[] bounds = ImageUtils.getBitmapBounds(file
							.getAbsolutePath());
					int width = bounds[0];
					int height = bounds[1];
					// 限制最小尺寸
					if (width < 50 || height < 50) {
						Toast.makeText(ByImageActivity.this,
								getString(R.string.picture_below_limit),
								Toast.LENGTH_SHORT).show();
					} else if (width > 1200 || height > 2000) {
						Toast.makeText(ByImageActivity.this,
								getString(R.string.picture_beyond_limit),
								Toast.LENGTH_SHORT).show();
					} else {
						// 获取目标图片
						targetBmp = getBitmapFromUri(result);
						targetBitmapDh = helper.getDhash(targetBmp);
						isFromLocal = false;
						isPcChair = false;
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								ImageLoader.getInstance().displayImage(
										result.toString(),
										ivShow,
										ImageLoaderUtil.getInstance()
												.getDefaultOptions());
								tvPath.setText("path is: " + result.toString());
							}
						});
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (REQUEST_SELECT_LOCAL_IMAGE == requestCode) {
			if (data == null) {
				return;
			}
			// targetBitmapDh = data.getStringExtra("dhValue");
			// isPcChair = data.getBooleanExtra("isPcChair", false);
			// isFromLocal = data.getBooleanExtra("isFromLocal", true);
			// runOnUiThread(new Runnable() {
			//
			// @Override
			// public void run() {
			// ivLocalImage.setImageBitmap((Bitmap) data
			// .getParcelableExtra("bitmap"));
			// tvPath.setText("targetBitmapDh is " + targetBitmapDh);
			// }
			// });

			Bitmap bitmap = data.getParcelableExtra("bitmap");
			boolean isPcChair = data.getBooleanExtra("isPcChair", false);

			String path = Constant.CACHE_ROOT + System.currentTimeMillis()
					+ ".jpg";
			ImageUtils.compressImage(bitmap, 50, path);
			bitmap.recycle();

			String avatar = Constant.CACHE_ROOT + System.currentTimeMillis()
					+ ".jpg";
			if (isPcChair) {
				Crop.of(Uri.fromFile(new File(path)),
						Uri.fromFile(new File(avatar))).asSquare()
						.withMaxSize(800, 800)
						.start(this, Crop.REQUEST_CROP_PC);
			} else {
				Crop.of(Uri.fromFile(new File(path)),
						Uri.fromFile(new File(avatar))).asSquare()
						.withMaxSize(800, 800)
						.start(this, Crop.REQUEST_CROP_TS);
			}
		}
	}

	private Bitmap getBitmapFromUri(Uri uri) {
		try {
			// 读取uri所在的图片
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), uri);
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
