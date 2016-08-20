package com.personal.image;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.personal.image.utils.Constant;
import com.personal.image.utils.ImageUtils;
import com.personal.image.widget.ZoomRotateImageView;

public class ZoomImageActivity extends Activity {
	
	private Button buttonSure, buttonTurn;
	private ZoomRotateImageView ivZoomratale;
	public static final String RESULT_VALUES = "result_values";
	private String uri;
	private TextView tvBack;
	private Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zoom_image);
		buttonSure = (Button) findViewById(R.id.bt_sure);
		ivZoomratale = (ZoomRotateImageView) findViewById(R.id.iv_zoomratale);
		tvBack = (TextView) findViewById(R.id.tv_back);
		buttonTurn = (Button) findViewById(R.id.bt_turn);
		
		uri = getIntent().getStringExtra(ImageActivity.BUNDLE_URLS);
		Log.d("ZoomImageActivity", "path is " + uri);
		String url;
		if (uri.startsWith("/")) {
			url = "file://" + uri;
		}
//		Bitmap bitmap = ImageUtils.getBitmapFromUri(uri);
		bitmap = ImageUtils.getBitmapFromUri(uri);
		RelativeLayout.LayoutParams params =(RelativeLayout.LayoutParams) ivZoomratale.getLayoutParams();
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int picWidth = metrics.widthPixels *4;
		int picHeight = picWidth*bitmap.getHeight()/bitmap.getWidth();
		params.width = picWidth;
		params.height = picHeight;
		ivZoomratale.setImageBitmap(bitmap);
		ivZoomratale.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//
			}
		});

		tvBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		buttonSure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (!TextUtils.isEmpty(uri)) {
					ArrayList<String> list = new ArrayList<String>();
					list.add(uri);
					Intent intent = new Intent();
					intent.putStringArrayListExtra(RESULT_VALUES, list);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					Toast.makeText(getApplication(), "请选择一张图片", Toast.LENGTH_SHORT).show();
				}
			}
		});
		buttonTurn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
//				rotateyAnimRun(ivZoomratale);
				final Bitmap turnBitmap = reverseBitmap(bitmap, 0);
				runOnUiThread(new Runnable() {
					public void run() {
						ivZoomratale.setImageBitmap(turnBitmap);
					}
				});
				String path = Constant.CACHE_ROOT + System.currentTimeMillis() + ".jpg";
				saveImage(turnBitmap, path, 80);
				File file = new File(path);  
				if (file.exists()) {
					Uri fileUri = Uri.fromFile(file);
					uri = fileUri.toString();
					if (uri.startsWith("file://")) {
						uri = uri.substring(7);
					} 
					Toast.makeText(getApplication(), "uri is " + uri, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	@SuppressLint("NewApi")
	public void rotateyAnimRun(View view)  
	{  
		 ObjectAnimator.ofFloat(view, "rotationY", 0.0F, 180.0F)//  
        .setDuration(500)//  
        .start();    
	}  
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	 /** 
     * 图片反转 
     *  
     * @param bm 
     * @param flag 
     *            0为水平反转，1为垂直反转 
     * @return 
     */  
    public static Bitmap reverseBitmap(Bitmap bmp, int flag) {  
        float[] floats = null;  
        switch (flag) {  
        case 0: // 水平反转  
            floats = new float[] { -1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f };  
            break;  
        case 1: // 垂直反转  
            floats = new float[] { 1f, 0f, 0f, 0f, -1f, 0f, 0f, 0f, 1f };  
            break;  
        }  
  
        if (floats != null) {  
            Matrix matrix = new Matrix();  
            matrix.setValues(floats);  
            return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);  
        }  
  
        return null;  
    }  
    
    public void saveImage(Bitmap bitmap, String filePath, int quality) {
        FileOutputStream fileOutputStream = null;
        try {
          fileOutputStream = new FileOutputStream(filePath);
          bitmap.compress(CompressFormat.JPEG, quality, fileOutputStream);
          fileOutputStream.flush();
          fileOutputStream.close();
          // 如果图片还没有回收，强制回收
          if (!bitmap.isRecycled()) {
            System.gc();
          }
        } catch (Exception e) {
        }
      }
}
