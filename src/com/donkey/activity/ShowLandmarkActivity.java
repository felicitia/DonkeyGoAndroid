package com.donkey.activity;

import java.util.ArrayList;
import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;

import com.donkey.R;
import com.donkey.view.MyItemizedOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class ShowLandmarkActivity extends MapActivity {

	private MapView mapView;

	private ArrayList<String> latitudeList;
	private ArrayList<String> longtitudeList;
	private ArrayList<String> lmNameList;
	private ArrayList<String> lmTypeList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_show_landmark);

		initData();
		initViews();
	}

	private void initData() {
		latitudeList = getIntent().getStringArrayListExtra("latitude_list");
		longtitudeList = getIntent().getStringArrayListExtra("longtitude_list");
		lmNameList = getIntent().getStringArrayListExtra("lmName_list");
		lmTypeList = getIntent().getStringArrayListExtra("lmType_list");
	}

	private void initViews() {
		mapView = (MapView) findViewById(R.id.landmark);
		mapView.setBuiltInZoomControls(true);

		List<Overlay> mapOverlays = mapView.getOverlays();
		Drawable drawable = this.getResources()
				.getDrawable(R.drawable.location);
		MyItemizedOverlay itemizedOverlay;
		GeoPoint point;
		OverlayItem overlayitem;

		for (int i = 0; i < longtitudeList.size(); i++) {
			itemizedOverlay = new MyItemizedOverlay(drawable, this);
			point = new GeoPoint(
					(int) (Double.parseDouble(latitudeList.get(i)) * 1E6),
					(int) (Double.parseDouble(longtitudeList.get(i)) * 1E6));
			overlayitem = new OverlayItem(point, "Name", lmNameList.get(i));
			itemizedOverlay.addOverlay(overlayitem);
			mapOverlays.add(itemizedOverlay);
		}

		MapController mapController = mapView.getController();

		mapController.animateTo(new GeoPoint((int) (Double
				.parseDouble(latitudeList.get(0)) * 1E6), (int) (Double
				.parseDouble(longtitudeList.get(0)) * 1E6)));
		mapController.setZoom(6);

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
