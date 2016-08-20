package com.personal.image.utils;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


public abstract class CommonAdapter<T> extends BaseAdapter {

	protected Context context;

	protected List<T> data;

	protected int itemLayoutId;

	public CommonAdapter(Context context, List<T> data, int itemLayoutId) {
		this.context = context;
		this.data = data;
		this.itemLayoutId = itemLayoutId;
	}

	public void addItem(T t) {
		try {
			data.add(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addItem(int location, T t) {
		try {
			data.add(location, t);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean removeItem(T t) {
		try {
			return data.remove(t);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void removeItem(int location) {
		try {
			data.remove(location);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean contains(T t) {
		return data.contains(t);
	}

	public void clear() {
		data.clear();
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public T getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = getViewHolder(position, convertView, parent);
		convert(viewHolder, getItem(position), position);
		return viewHolder.getConvertView();
	}

	private ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
		return ViewHolder.get(context, convertView, parent, itemLayoutId, position);
	}

	public abstract void convert(ViewHolder holder, T item, int position);
}

