package com.personal.image;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.personal.image.utils.Constant;

public class MainActivity extends Activity implements OnClickListener {

	public ImageLoader imageLoader = ImageLoader.getInstance();
	private RelativeLayout rlByWord, rlByImage, rlByWeb, rlByService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		File destDir = new File(Constant.CACHE_ROOT);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		setContentView(R.layout.activity_main);
		findview();
		setListener();
	}

	private void setListener() {
		rlByWord.setOnClickListener(this);
		rlByImage.setOnClickListener(this);
		rlByWeb.setOnClickListener(this);
		rlByService.setOnClickListener(this);
	}

	private void findview() {
		rlByWord = (RelativeLayout) findViewById(R.id.rltakecamera);
		rlByImage = (RelativeLayout) findViewById(R.id.rlsearch);
		rlByWeb = (RelativeLayout) findViewById(R.id.rlcourse);
		rlByService = (RelativeLayout) findViewById(R.id.rlnet);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rltakecamera:
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, ByImageActivity.class);
			startActivity(intent);
			break;

		case R.id.rlsearch:
			Intent intentword = new Intent();
			intentword.setClass(MainActivity.this, ByWordActivity.class);
			startActivity(intentword);
			break;

		case R.id.rlcourse:
			Intent intentservice = new Intent();

			 intentservice.setClass(MainActivity.this,
			 ByServiceActivity.class);
			 startActivity(intentservice);
			 break;
			 
		case R.id.rlnet:
			Intent intentNet = new Intent();
			intentNet.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse("http://fei.guangdongip.gov.cn:8081/");
			intentNet.setData(content_url);
			startActivity(intentNet);
			break;

		default:
			break;
		}
	}

}
