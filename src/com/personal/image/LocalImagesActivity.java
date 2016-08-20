package com.personal.image;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.personal.image.utils.CommonAdapter;
import com.personal.image.utils.ImageInfo;
import com.personal.image.utils.RetrievalHelper;
import com.personal.image.utils.Utils;
import com.personal.image.utils.ViewHolder;

public class LocalImagesActivity extends Activity {

	private GridView gvLocalImages;
	private List<ImageInfo> dhList = new ArrayList<ImageInfo>();
	private CommonAdapter<ImageInfo> adapter;
	private TextView tvBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_local_images);
		gvLocalImages = (GridView) findViewById(R.id.gv_local_images);
		tvBack = (TextView) findViewById(R.id.tv_back);
		dhList = RetrievalHelper.getInstance().getLocalDh(getApplication());
		adapter = new CommonAdapter<ImageInfo>(LocalImagesActivity.this, dhList,
				R.layout.item_local_images) {

			@Override
			public void convert(ViewHolder holder, final ImageInfo item, final int position) {
				holder.setImageBitmap(R.id.iv_item_image, item.getBitmap());
				holder.setText(R.id.tv_item_name, item.getDh());
				final ImageView id_item_image = holder.getView(R.id.iv_item_image);
				id_item_image.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
						intent.putExtra("bitmap", item.getBitmap());
						intent.putExtra("dhValue", item.getDh());
						intent.putExtra("isFromLocal", false);
						if (position < 11) {
							intent.putExtra("isPcChair", true);
						}
						setResult(RESULT_OK, intent);
						finish();
					}
				});

			}
		};
		gvLocalImages.setAdapter(adapter);
		gvLocalImages.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Intent intent = new Intent();
//				ImageInfo  imageInfo = adapter.getItem(position);
//				intent.putExtra("bitmap", imageInfo.getBitmap());
//				intent.putExtra("dhValue", imageInfo.getDh());
//				intent.putExtra("isFromLocal", false);
//				if (position < 11) {
//					intent.putExtra("isPcChair", true);
//				} 
//				LocalImagesActivity.this.setResult(RESULT_OK, intent);
//				LocalImagesActivity.this.finish();
				
				
			}
		});
		
		tvBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
	
}
