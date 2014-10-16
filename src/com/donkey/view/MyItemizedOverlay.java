package com.donkey.view;

import java.util.ArrayList;

import android.R.integer;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mapOverlay = new ArrayList<OverlayItem>();
	private Context context;
	
	public MyItemizedOverlay(Drawable arg0) {
		super(arg0);
	}
	
	public MyItemizedOverlay(Drawable arg0,Context context) {
		super(arg0);
		this.context = context;
	}

	@Override
	protected OverlayItem createItem(int arg0) {
		return mapOverlay.get(arg0);
	}

	@Override
	public int size() {
		return mapOverlay.size();
	}

	@Override
	protected boolean onTap(int index){
		OverlayItem item = mapOverlay.get(index);
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(item.getTitle());
		builder.setMessage(item.getSnippet());
		builder.show();
		return true;
	}
	
	public void addOverlay(OverlayItem overlayItem){
		mapOverlay.add(overlayItem);
		this.populate();
	}
}
