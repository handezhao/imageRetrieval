package com.personal.image;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.personal.image.utils.CommonAdapter;
import com.personal.image.utils.Constant;
import com.personal.image.utils.ImageLoaderUtil;
import com.personal.image.utils.ImageUtils;
import com.personal.image.utils.ResultBean;
import com.personal.image.utils.ViewHolder;
import com.soundcloud.android.crop.Crop;

public class ByServiceActivity extends Activity implements OnClickListener {

	public static final String URL = "http://120.24.58.80:8080/Index.aspx?method=img/upload";
	public static final int REQUEST_SELECT_TARGET_IMAGE = 10;
	private TextView tvBack;
	private ImageView ivChoose;
	private Button btRequest;
	private GridView gvResult;
	private Bitmap targetBmp;
	private CommonAdapter<ResultBean> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_by_service);
		findViewById();
		setListener();
	}

	private void findViewById() {
		tvBack = (TextView) findViewById(R.id.tv_back);
		ivChoose = (ImageView) findViewById(R.id.iv_target_image);
		btRequest = (Button) findViewById(R.id.bt_start_request);
		gvResult = (GridView) findViewById(R.id.gv_result);
	}

	private void setListener() {
		tvBack.setOnClickListener(this);
		ivChoose.setOnClickListener(this);
		btRequest.setOnClickListener(this);
		gvResult.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ResultBean info = adapter.getItem(position);
				Intent intent = new Intent();
				intent.putExtra("name", info.getName());
				intent.putExtra("patent", info.getApplyNum());
				intent.putExtra("publicnum", info.getPublicNum());
				intent.putExtra("applyperson", info.getInventor());
				intent.putExtra("bitmap", info.getUrl());
				intent.setClass(ByServiceActivity.this,
						CatServicesImageActivity.class);
				startActivity(intent);
			}
		});
	}

	public static String bitmapToBase64(Bitmap bitmap) {
		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public List<ResultBean> serviceRequest(Bitmap bitmap) {
		List<ResultBean> mlist = new ArrayList<ResultBean>();
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost request = new HttpPost(URL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("file", bitmapToBase64(bitmap)));
		params.add(new BasicNameValuePair("num", "10"));
		try {
			request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			Log.d("ByServiceActivity:", ""
					+ httpResponse.getStatusLine().getStatusCode());
			String httpResultContent = EntityUtils.toString(
					httpResponse.getEntity(), "utf-8");
			Log.d("ByServiceActivity", "httpResultContent is "
					+ httpResultContent);
			JSONObject root = JSONObject.parseObject(httpResultContent);
			Log.d("ByServiceActivity", "json is " + root);
			boolean success = root.getBooleanValue("_success");
			String status = root.getString("status");
			JSONArray data = root.getJSONArray("data");
			if (data == null) {
			} else {

				for (int i = 0; i < 2; i++) {
					ResultBean bean = new ResultBean();
					JSONObject item = data.getJSONObject(i);
					bean.setApplyNum(item.getString("Sqh"));
					bean.setInventor(item.getString("Fmr"));
					bean.setName(item.getString("Mch"));
					bean.setPublicNum(item.getString("Ggh"));
					bean.setUrl(item.getString("Img"));
					mlist.add(bean);
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mlist;
	}

	@Override
	public void onClick(View v) {
		if (v == tvBack) {
			onBackPressed();
		} else if (v == ivChoose) {
			Intent intent = new Intent(ByServiceActivity.this,
					ImagesActivity.class);
			intent.putExtra(ImagesActivity.BUNDLE_MAX, 1);
			startActivityForResult(intent, REQUEST_SELECT_TARGET_IMAGE);
		} else if (v == btRequest) {
			if (targetBmp == null) {
				Toast.makeText(ByServiceActivity.this,
						getString(R.string.warm), Toast.LENGTH_SHORT).show();
			} else {
				RequestTask task = new RequestTask(ByServiceActivity.this);
				task.execute(targetBmp);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						ivChoose.setImageBitmap(bitmap);
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
						Toast.makeText(ByServiceActivity.this,
								getString(R.string.picture_below_limit),
								Toast.LENGTH_SHORT).show();
					} else if (width > 1200 || height > 2000) {
						Toast.makeText(ByServiceActivity.this,
								getString(R.string.picture_beyond_limit),
								Toast.LENGTH_SHORT).show();
					} else {
						// 获取目标图片
						targetBmp = getBitmapFromUri(result);
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								ivChoose.setImageBitmap(targetBmp);
								Log.d("ByServiceActivity",
										"path is " + result.toString());
							}
						});
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
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

	class RequestTask extends AsyncTask<Bitmap, Integer, List<ResultBean>> {

		private final ProgressDialog dialog;

		public RequestTask(Context context) {
			dialog = new ProgressDialog(context);
		}

		@Override
		protected List<ResultBean> doInBackground(Bitmap... arg0) {

			Log.d("ByServiceActivity", "doInBackground work");
			return serviceRequest(arg0[0]);
		}

		protected void onPreExecute() {
			this.dialog.setMessage("Progress start");
			this.dialog.show();
		}

		@Override
		protected void onPostExecute(List<ResultBean> result) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			if (result.isEmpty()) {
				Toast.makeText(ByServiceActivity.this, "请求失败",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ByServiceActivity.this, "请求成功，list is" + result,
						Toast.LENGTH_SHORT).show();

				Log.d("ByServiceActivity", "result is " + result);

				adapter = new CommonAdapter<ResultBean>(ByServiceActivity.this,
						result, R.layout.item_local_images) {
					@Override
					public void convert(ViewHolder holder, ResultBean item,
							int position) {
						holder.setImageByUrl(R.id.iv_item_image, item.getUrl(),
								ImageLoaderUtil.getInstance()
										.getDefaultOptions());
						holder.setText(R.id.tv_item_name, item.getName());
					}
				};
				gvResult.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		}

	}
}
