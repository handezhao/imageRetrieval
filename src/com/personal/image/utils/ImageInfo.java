package com.personal.image.utils;

import android.graphics.Bitmap;

public class ImageInfo{
	
	/**
	 * 图片对应的信息
	 **/
	private String applyPerson, patentNum, name, publicNum, Dh;
	private Bitmap bitmap;
	
	public ImageInfo () {
		
	}

	public String getDh() {
		return Dh;
	}

	public void setDh(String dh) {
		Dh = dh;
	}

	public String getPublicNum() {
		return publicNum;
	}

	public void setPublicNum(String publicNum) {
		this.publicNum = publicNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApplyPerson() {
		return applyPerson;
	}

	public void setApplyPerson(String applyPerson) {
		this.applyPerson = applyPerson;
	}

	public String getPatentNum() {
		return patentNum;
	}

	public void setPatentNum(String patentNum) {
		this.patentNum = patentNum;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

}
