package com.personal.image.utils;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.personal.image.ImageActivity;
import com.personal.image.ImagesActivity;
import com.personal.image.R;
import com.personal.image.ZoomImageActivity;

public class ImagesAdapter extends CommonAdapter<String> {

	/**
	 * 用户选择的图片，存储为图片的完整路径 可以选择多张图片，根据传入的数值动态调整
	 */
	private List<String> mSelectedImage = new ArrayList<String>();
	
	public static final int REQUEST_CODE_ZOOM = 10;

	private OnCheckedChangedListener listener;

	private OnClickListener cameraClickListener;

	private int max;

	private Context context;

	private DisplayImageOptions options;

	public ImagesAdapter(Activity context, List<String> datas,
			int itemLayoutId, OnCheckedChangedListener listener, int max) {
		super(context, datas, itemLayoutId);
		this.listener = listener;
		this.max = max;
		this.context = context;

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_default_image)
				.cacheInMemory(true).cacheOnDisk(false)
				.bitmapConfig(Config.RGB_565).build();
	}

	public void addCamera() {
		addItem(0, "camera");
	}

	public void setCameraClickListener(OnClickListener cameraClickListener) {
		this.cameraClickListener = cameraClickListener;
	}

	private void displayImage(ViewHolder helper, String item) {
		helper.setImageByUrl(R.id.id_item_image, item, options);
	}

	private void setStatus(ViewHolder helper, String item, boolean checked) {
		final ImageView id_item_image = helper.getView(R.id.id_item_image);
		final ImageButton id_item_select = helper.getView(R.id.id_item_select);

		if (max == 1) {
			id_item_select.setVisibility(View.GONE);
		} else {
			id_item_select.setVisibility(View.VISIBLE);
		}

		if (mSelectedImage.size() >= max && !checked) {
			id_item_select.setEnabled(false);
		}

		// 设置no_selected
		id_item_select.setSelected(checked);

		id_item_image.setColorFilter(checked ? Color.parseColor("#77000000")
				: Color.parseColor("#00000000"));
	}

	@Override
	public void convert(final ViewHolder helper, final String item, int position) {
		final ImageView id_item_image = helper.getView(R.id.id_item_image);
		final ImageButton id_item_select = helper.getView(R.id.id_item_select);
		final LinearLayout item_select_container = helper
				.getView(R.id.item_select_container);

		if (item.equals("camera")) {
			helper.setImageByUrl(R.id.id_item_image, "", options);
			id_item_image.setScaleType(ScaleType.CENTER_CROP);
			id_item_image.setBackgroundColor(Color.TRANSPARENT);
			id_item_image.setImageResource(R.drawable.ic_take_photo);
			item_select_container.setVisibility(View.GONE);
			id_item_select.setVisibility(View.GONE);

			id_item_image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (cameraClickListener != null) {
						cameraClickListener.onClick(v);
					}
				}
			});
		} else {
			id_item_image.setScaleType(ScaleType.CENTER_CROP);
			id_item_image.setBackgroundColor(Color.TRANSPARENT);
			item_select_container.setVisibility(View.VISIBLE);
			id_item_select.setVisibility(View.VISIBLE);

			// 设置ImageView的点击事件
			id_item_image.setOnClickListener(new OnClickListener() {
				// 选择，则将图片变暗，反之则反之
				@Override
				public void onClick(final View v) {
					if (max == 1) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								context);
						builder.setIcon(R.drawable.ic_launcher);
						builder.setTitle("选择一个处理方式");
						// 指定下拉列表的显示数据
						final String[] cities = { "裁剪", "反转" };
						// 设置一个下拉的列表选择项
						builder.setItems(cities,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (which == 0) {
											if (mSelectedImage.contains(item)) {
												// 已经选择过该图片
												mSelectedImage.remove(item);
												setStatus(helper, item, false);
												listener.onChecked(v, false);
											} else {
												// 未选择该图片
												mSelectedImage.add(item);
												setStatus(helper, item, true);
												listener.onChecked(v, true);
											}
										} else if (which == 1) {
											Intent intent = new Intent(context, ZoomImageActivity.class);
											intent.putExtra(ImageActivity.BUNDLE_URLS, item);
//											ArrayList<String> urls = new ArrayList<String>();
//											urls.add(item);
//											intent.putStringArrayListExtra(ImageActivity.BUNDLE_URLS, urls);
											((ImagesActivity)context).startActivityForResult(intent, REQUEST_CODE_ZOOM);
										}
									}
								});
						builder.show();
						//
						//
					} else {
						Intent intent = new Intent(context, ImageActivity.class);
						ArrayList<String> urls = new ArrayList<String>();
						urls.add(item);
						intent.putStringArrayListExtra(
								ImageActivity.BUNDLE_URLS, urls);
						context.startActivity(intent);
					}
				}
			});

			OnClickListener click = new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSelectedImage.contains(item)) {
						mSelectedImage.remove(item);
						setStatus(helper, item, false);
						listener.onChecked(v, false);
					} else if (mSelectedImage.size() < max) {
						mSelectedImage.add(item);
						setStatus(helper, item, true);
						listener.onChecked(v, true);
					}
				}
			};
			id_item_select.setOnClickListener(click);
			item_select_container.setOnClickListener(click);

			displayImage(helper, item);

			if (mSelectedImage.contains(item)) {
				setStatus(helper, item, true);
			} else {
				setStatus(helper, item, false);
			}
		}
	}
	
	public List<String> getValues() {
		return mSelectedImage;
	}
}
