package com.personal.image.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ViewHolder {

	private SparseArray<View> views = new SparseArray<View>();
	private View convertView;
	private int position;

	private ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
		convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
		convertView.setTag(this);
		this.position = position;
	}

	public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder(context, parent, layoutId, position);
		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.position = position;
		}
		return holder;
	}

	public View getConvertView() {
		return convertView;
	}

	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int viewId) {
		View view = views.get(viewId);
		if (view == null) {
			view = convertView.findViewById(viewId);
			views.put(viewId, view);
		}
		return (T) view;
	}

	public ViewHolder setText(int viewId, String text) {
		TextView view = getView(viewId);
		view.setText(text);
		return this;
	}

	public ViewHolder setImageURI(int viewId, Uri uri) {
		ImageView view = getView(viewId);
		view.setImageURI(uri);
		return this;
	}

	public ViewHolder setImageResource(int viewId, int resourceId) {
		ImageView view = getView(viewId);
		view.setImageResource(resourceId);
		return this;
	}

	public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
		ImageView view = getView(viewId);
		view.setImageBitmap(bitmap);
		return this;
	}

	public ViewHolder setImageByUrl(int viewId, String url, DisplayImageOptions options) {
		if (url.startsWith("/")) {
			url = "file://" + url;
		}

		ImageView view = getView(viewId);
		ImageLoader.getInstance().displayImage(url, view, options);	
		return this;
	}

	public int getPosition() {
		return position;
	}

	public void setVisibility(int viewId, int visibility) {
		View view = getView(viewId);
		view.setVisibility(visibility);
	}

	public void setSelected(int viewId, boolean selected) {
		getView(viewId).setSelected(selected);
	}
}
