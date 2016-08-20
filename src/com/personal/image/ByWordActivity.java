package com.personal.image;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.personal.image.utils.CommonAdapter;
import com.personal.image.utils.ImageInfo;
import com.personal.image.utils.RetrievalHelper;
import com.personal.image.utils.ViewHolder;

public class ByWordActivity extends Activity implements OnClickListener {
	/**
	 *只做简单粗略的检索
	 *不支持模糊检索
	 *当搜索有多个满足时只显示一个
	 */
	public static final String TAG = "ByWordActivity";
	private Button buttonStartRequest;
	private EditText etPublicNum, etApplyPerson, etPatentNum, etName;
//	private ImageView ivResultImage;
	private ImageInfo imageInfo = new ImageInfo();
	private TextView tvBack;
	private List<ImageInfo> dotaList;
//	private List<Bitmap> resultList = new ArrayList<Bitmap>();
	private List<ImageInfo> resultList = new ArrayList<ImageInfo>();
	private RetrievalHelper retrievalHelper;
	private GridView gvResult;
	private CommonAdapter<ImageInfo> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_by_word);
		findViewById();
		setListener();
		retrievalHelper = RetrievalHelper.getInstance();
		dotaList = retrievalHelper.initDataList(ByWordActivity.this);
		Log.d(TAG, "dotaList size is " + dotaList.size());
	}

	private void setListener() {
		buttonStartRequest.setOnClickListener(this);
		tvBack.setOnClickListener(this);
		etPublicNum.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				String content = etPublicNum.getText().toString();
				if (TextUtils.isEmpty(content)) {
				} else {
					imageInfo.setPublicNum(content);
					initImageInfo();
					resultList.clear();
					resultList = imageRetrievalByWord(imageInfo);
				}
			}
		});
		
		etApplyPerson.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				String content = etApplyPerson.getText().toString();
				if (TextUtils.isEmpty(content)) {
				} else {
					imageInfo.setApplyPerson(content);
					initImageInfo();
					resultList.clear();
					resultList = imageRetrievalByWord(imageInfo);
				}
			}
		});
		etPatentNum.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				String content = etPatentNum.getText().toString();
				if (TextUtils.isEmpty(content)) {
				} else {
					imageInfo.setPatentNum(content);
					initImageInfo();
					resultList.clear();
					resultList = imageRetrievalByWord(imageInfo);
				}
			}
		});
		
		etName.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				String content = etName.getText().toString();
				if (TextUtils.isEmpty(content)) {
				} else {
					imageInfo.setName(content);
					initImageInfo();
					resultList.clear();
					resultList = imageRetrievalByWord(imageInfo);
				}
			}
		});
		
		gvResult.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				 ImageInfo info = adapter.getItem(position);
				 Intent intent = new Intent();
				 intent.putExtra("name", info.getName());
				 intent.putExtra("patent", info.getPatentNum());
				 intent.putExtra("publicnum", info.getPublicNum());
				 intent.putExtra("applyperson", info.getApplyPerson());
				 intent.putExtra("bitmap", info.getBitmap());
				 intent.setClass(ByWordActivity.this, CatImageActivity.class);
				 startActivity(intent);
			}
		});
	}
	

	private void initImageInfo() {
		if (TextUtils.isEmpty(imageInfo.getApplyPerson())) {
			imageInfo.setApplyPerson("null");
		}
		if (TextUtils.isEmpty(imageInfo.getName())) {
			imageInfo.setName("null");
		}
		if (TextUtils.isEmpty(imageInfo.getPatentNum())) {
			imageInfo.setPatentNum("null");
		}
		if (TextUtils.isEmpty(imageInfo.getPublicNum())) {
			imageInfo.setPublicNum("null");
		}
	}

	private void findViewById() {
		buttonStartRequest = (Button) findViewById(R.id.bt_start_request);
		etPublicNum = (EditText) findViewById(R.id.et_request_public_num);
		etApplyPerson = (EditText) findViewById(R.id.et_apply_person);
		etPatentNum = (EditText) findViewById(R.id.et_request_patent_num);
		etName = (EditText) findViewById(R.id.et_request_name);
		gvResult = (GridView) findViewById(R.id.gv_result);
		tvBack = (TextView) findViewById(R.id.tv_back);
	}
	
	private ArrayList<Bitmap> imageRetrieval(ImageInfo info) {
		ArrayList<Bitmap> list = new ArrayList<Bitmap>();
		ArrayList<ImageInfo> imageInfoList = new ArrayList<ImageInfo>();
		
		for (int i = 0; i < dotaList.size(); i++) {
			if ((info.getName().equals(dotaList.get(i).getName())) || (info.getApplyPerson().equals(dotaList.get(i).getApplyPerson())) || 
					(info.getPatentNum().equals(dotaList.get(i).getPatentNum())) || (info.getPublicNum().equals(dotaList.get(i).getPublicNum()))) {
				list.add(dotaList.get(i).getBitmap());
				imageInfoList.add(dotaList.get(i));
			}
			
			if ((dotaList.get(i).getName().contains(info.getName())) || (dotaList.get(i).getApplyPerson().contains(info.getApplyPerson())) || 
					(dotaList.get(i).getPatentNum().contains(info.getPatentNum())) || (dotaList.get(i).getPublicNum().contains(info.getPublicNum()))) {
				list.add(dotaList.get(i).getBitmap());
				imageInfoList.add(dotaList.get(i));
			}
		}
		return list;
	} 
	
	private ArrayList<ImageInfo> imageRetrievalByWord(ImageInfo info) {
		ArrayList<Bitmap> list = new ArrayList<Bitmap>();
		ArrayList<ImageInfo> imageInfoList = new ArrayList<ImageInfo>();
		
		for (int i = 0; i < dotaList.size(); i++) {
			if ((info.getName().equals(dotaList.get(i).getName())) || (info.getApplyPerson().equals(dotaList.get(i).getApplyPerson())) || 
					(info.getPatentNum().equals(dotaList.get(i).getPatentNum())) || (info.getPublicNum().equals(dotaList.get(i).getPublicNum()))) {
				list.add(dotaList.get(i).getBitmap());
				imageInfoList.add(dotaList.get(i));
			}
			
			if ((dotaList.get(i).getName().contains(info.getName())) || (dotaList.get(i).getApplyPerson().contains(info.getApplyPerson())) || 
					(dotaList.get(i).getPatentNum().contains(info.getPatentNum())) || (dotaList.get(i).getPublicNum().contains(info.getPublicNum()))) {
				list.add(dotaList.get(i).getBitmap());
				imageInfoList.add(dotaList.get(i));
			}
		}
		return imageInfoList;
	} 
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void onClick(View view) {
		
		if (view == buttonStartRequest) {
			if (resultList.isEmpty()) {
				Toast.makeText(getApplication(), getResources().getString(R.string.no_result), Toast.LENGTH_SHORT).show();
			} else {
				adapter = new CommonAdapter<ImageInfo>(ByWordActivity.this, resultList, R.layout.item_local_images) {
					
					@Override
					public void convert(ViewHolder holder, ImageInfo item, int position) {
						holder.setImageBitmap(R.id.iv_item_image, item.getBitmap());
						holder.setText(R.id.tv_item_name, item.getName());
					}
				};
				gvResult.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
		} else if (view == tvBack) {
			onBackPressed();
		}
	}

}
