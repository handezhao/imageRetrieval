package com.personal.image.widget;

import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.personal.image.R;
import com.personal.image.utils.CommonAdapter;
import com.personal.image.utils.ImageFolder;
import com.personal.image.utils.ViewHolder;

public class ListImageDirPopupWindow extends BasePopupWindowForListView<ImageFolder> {
	private ListView mListDir;
	private DisplayImageOptions options;

	public ListImageDirPopupWindow(int width, int height, List<ImageFolder> datas, View convertView) {
		super(convertView, width, height, true, datas);
		
		options = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.ic_default_image)
			.cacheInMemory(true)
			.cacheOnDisk(false).build();
	}

	@Override
	public void initViews() {
		mListDir = (ListView) findViewById(R.id.id_list_dir);
		mListDir.setAdapter(new CommonAdapter<ImageFolder>(context, mDatas, R.layout.list_dir_item) {
			@Override
			public void convert(ViewHolder helper, ImageFolder item, int position) {
				helper.setText(R.id.id_dir_item_name, item.getName());
				helper.setImageByUrl(R.id.id_dir_item_image, item.getFirstImagePath(), options);
				if (item.getDir() == null) {
					helper.setVisibility(R.id.id_dir_item_count, View.GONE);
				} else {
					helper.setVisibility(R.id.id_dir_item_count, View.VISIBLE);
					helper.setText(R.id.id_dir_item_count, item.getCount() + "张");
				}
				
				if (item.getDir() == null && mImgDir == null) {
					helper.setVisibility(R.id.iv_chosen, View.VISIBLE);
				} else if (item.getDir() != null && item.getDir().equals(mImgDir)) {
					helper.setVisibility(R.id.iv_chosen, View.VISIBLE);
				} else {
					helper.setVisibility(R.id.iv_chosen, View.GONE);
				}
			}
		});
	}

	public interface OnImageDirSelected {
		void selected(ImageFolder floder);
	}

	private OnImageDirSelected mImageDirSelected;
	private String mImgDir;

	public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected) {
		this.mImageDirSelected = mImageDirSelected;
	}

	@Override
	public void initEvents() {
		mListDir.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (mImageDirSelected != null) {
					mImageDirSelected.selected(mDatas.get(position));
				}
			}
		});
	}

	@Override
	public void init() {

	}

	@Override
	protected void beforeInitWeNeedSomeParams(Object... params) {
	}

	public void setSelected(String mImgDir) {
		this.mImgDir = mImgDir;
	}

}
