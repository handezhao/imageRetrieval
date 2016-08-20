package com.personal.image.widget;


import android.app.Dialog;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.TextView;

import com.personal.image.R;

public class PickDialog extends Dialog{

	protected PickDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public PickDialog(Context context, int theme) {
		super(context, theme);
	}
	
	private TextView tvCamera, tvAlbum;

	public PickDialog(Context context) {
		super(context, R.style.pick_dialog);
		setContentView(R.layout.dialog_pick);
		tvCamera = (TextView) findViewById(R.id.tv_camera);
		tvAlbum = (TextView) findViewById(R.id.tv_album);
		setCanceledOnTouchOutside(true);
	}
	

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		dismiss();
		return super.onTouchEvent(event);
	}

}
