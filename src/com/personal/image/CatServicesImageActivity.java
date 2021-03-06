package com.personal.image;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.personal.image.utils.ImageLoaderUtil;

public class CatServicesImageActivity extends Activity {

	private TextView tvBack, tvName, tvPatentNum, tvApplyPerson, tvPublicNum;
	private ImageView ivImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cat_image);
		findviewById();
		tvName.setText("产品名： " + getIntent().getStringExtra("name"));
		tvPatentNum.setText("发明人： " + getIntent().getStringExtra("applyperson"));
		tvApplyPerson.setText("申请号： " + getIntent().getStringExtra("patent"));
		tvPublicNum.setText("公开号： " + getIntent().getStringExtra("publicnum"));
		ImageLoader.getInstance().displayImage(getIntent().getStringExtra("bitmap"), ivImage,
				ImageLoaderUtil.getInstance().getDefaultOptions());

		tvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	private void findviewById() {
		tvBack = (TextView) findViewById(R.id.tv_back);
		tvName = (TextView) findViewById(R.id.tv_name);
		tvPatentNum = (TextView) findViewById(R.id.tv_patent_num);
		tvApplyPerson = (TextView) findViewById(R.id.tv_apply_person);
		tvPublicNum = (TextView) findViewById(R.id.tv_public_num);
		ivImage = (ImageView) findViewById(R.id.iv_target_image);

	}
}
