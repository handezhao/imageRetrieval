package com.personal.image.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.personal.image.R;

public class ButtonTitleBarView extends RelativeLayout {

	private RelativeLayout rlLeft;

	private ImageView ivLeft;

	private TextView tvLeft;

	private Button buttonRight;

	public ButtonTitleBarView(Context context) {
		super(context);
		init(context, null, 0);
	}

	public ButtonTitleBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public ButtonTitleBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs, defStyle);
	}

	private void init(Context context, AttributeSet attrs, int defStyle) {
		ViewUtils.inflaterView(context, this, R.layout.button_titlebar_view);

		rlLeft = (RelativeLayout) findViewById(R.id.rl_left);
		ivLeft = (ImageView) findViewById(R.id.iv_left);
		tvLeft = (TextView) findViewById(R.id.tv_left);
		buttonRight = (Button) findViewById(R.id.button_right);

		if (attrs == null) {
			return;
		}

		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.SecondTitleBarView, defStyle, 0);

		if (a != null) {
			int count = a.getIndexCount();
			int index = 0;

			for (int i = 0; i < count; i++) {
				index = a.getIndex(i);
				switch (index) {
				case R.styleable.SecondTitleBarView_left_image:
					if (a.getResourceId(index, -1) == -1) {
						hideLeftImage();
					} else {
						setLeftImage(a.getResourceId(index, -1));
					}
					break;
				case R.styleable.SecondTitleBarView_left_text:
					String left_text = a.getString(index);
					setLeftText(left_text);
					break;
				case R.styleable.SecondTitleBarView_right_text:
					String right_text = a.getString(index);
					buttonRight.setText(right_text);
					break;
				}
			}
			a.recycle();
		}
	}

	public void hideLeftImage() {
		ivLeft.setVisibility(View.GONE);
	}

	public void setLeftImage(int id) {
		ivLeft.setBackgroundResource(id);
		showLeftImage();
	}

	public void showLeftImage() {
		ivLeft.setVisibility(View.VISIBLE);
	}

	public void setLeftText(String str) {
		if (TextUtils.isEmpty(str)) {
			tvLeft.setVisibility(View.GONE);
		} else {
			tvLeft.setText(str);
			tvLeft.setVisibility(View.VISIBLE);
		}
	}

	public void hideRightButton() {
		buttonRight.setVisibility(View.GONE);
	}

	public void setRightButtonText(String str) {	
		if (TextUtils.isEmpty(str)) {
			buttonRight.setVisibility(View.GONE);
		} else {	
			buttonRight.setText(str);
			buttonRight.setVisibility(View.VISIBLE);
		}
	}

	public void setButtonEnable(boolean enable) {
		buttonRight.setEnabled(enable);
	}

	public void setBackgroundResource(int resId) {
		buttonRight.setBackgroundResource(resId);
	}

	public void setLeftListener(OnClickListener listener) {
		rlLeft.setOnClickListener(listener);
	}

	public void setButtonListener(OnClickListener listener) {
		buttonRight.setOnClickListener(listener);
	}
	
	 static class ViewUtils {
		    public static  View inflaterView(Context context, ViewGroup root, int id) {
		        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        return inflater.inflate(id, root);
		    }
		}
}