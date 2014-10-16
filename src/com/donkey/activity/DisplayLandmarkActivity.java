package com.donkey.activity;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import com.donkey.R;
import com.donkey.entity.LandMarkItem;
import com.donkey.httpclient.XmlLandmarkItemHandler;
import com.donkey.util.AppKeys;
import com.donkey.util.AsyncImageLoader;
import com.donkey.util.NetWorkState;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class DisplayLandmarkActivity extends Activity {

	private String memId;
	private ViewSwitcher switcher;
	private ListView lmListView;

	private List<LandMarkItem> lmList;
	private XmlLandmarkItemHandler xmlHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_display_landmark);

		initData();
		initViews();

		new Thread(new Runnable() {
			@Override
			public void run() {
				getLmList();
			}
		}).start();
	}

	private void initData() {
		memId = getIntent().getStringExtra("mem_id");
		xmlHandler = new XmlLandmarkItemHandler();
	}

	private void initViews() {
		switcher = (ViewSwitcher) findViewById(R.id.viewswitcher_landmark);
		lmListView = new ListView(DisplayLandmarkActivity.this);
		lmListView.setCacheColorHint(Color.argb(0, 0, 0, 0));
		lmListView.setDivider(getResources().getDrawable(
				R.drawable.list_divider_line));
		lmListView.setDividerHeight(5);
		lmListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Intent intent = new Intent(
								DisplayLandmarkActivity.this,
								DisplayLargePictureActivity.class);
						intent.putExtra("large_pic_url", lmList.get(position)
								.getMediaPath());
						startActivity(intent);
					}
				});
		switcher.addView(lmListView);
		switcher.addView(getLayoutInflater().inflate(R.layout.list_loading,
				null));
		switcher.showNext();

		((ImageView) findViewById(R.id.goback_dislay_landmark))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});

		((ImageView) findViewById(R.id.view_landmark_on_map))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(
								DisplayLandmarkActivity.this,
								ShowLandmarkActivity.class);
						ArrayList<String> latitudeList = new ArrayList<String>();
						ArrayList<String> longtitudeList = new ArrayList<String>();
						ArrayList<String> lmNameList = new ArrayList<String>();
						ArrayList<String> lmTypeList = new ArrayList<String>();
						for (LandMarkItem lmi : lmList) {
							latitudeList.add(lmi.getLatitude());
							longtitudeList.add(lmi.getLongtitude());
							lmNameList.add(lmi.getLmName());
							lmTypeList.add(lmi.getLmType());
						}
						Bundle bundle = new Bundle();
						bundle.putStringArrayList("latitude_list", latitudeList);
						bundle.putStringArrayList("longtitude_list",
								longtitudeList);
						bundle.putStringArrayList("lmName_list", lmNameList);
						bundle.putStringArrayList("lmType_list", lmTypeList);

						intent.putExtras(bundle);

						startActivity(intent);
					}
				});
	}

	/**
	 * 通过http请求获取地标列表
	 */
	private void getLmList() {
		if (!NetWorkState.isNetworkAvailable(this)) { // 判断网络连接情况
			handler.sendEmptyMessage(0);
			return;
		}
		try {
			URL url = new URL(AppKeys.LANDMARK_LIST_URL + memId);
			URLConnection con = url.openConnection();
			con.connect();
			lmList = xmlHandler.getLandMarkItems(con.getInputStream());

			if (lmList.size() == 0) {
				handler.sendEmptyMessage(1);
			} else {
				handler.sendEmptyMessage(2);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case -1:
				Toast.makeText(DisplayLandmarkActivity.this, "服务不可用",
						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(DisplayLandmarkActivity.this,
						"网络连接不可用，请检查您的网络连接", Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(DisplayLandmarkActivity.this, "本记忆还未添加过任何地标",
						Toast.LENGTH_SHORT).show();
				break;
			case 2:
				lmListView.setAdapter(new LmAdapter());
				switcher.setDisplayedChild(0);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 显示地标的ListView的Adapter
	 * 
	 * @author liushuai
	 * 
	 */
	private class LmAdapter extends BaseAdapter {

		AsyncImageLoader asyncImageLoader = new AsyncImageLoader(80, 80);

		@Override
		public int getCount() {
			return lmList.size();
		}

		@Override
		public Object getItem(int position) {
			return lmList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LmHolder holder;
			if (convertView == null) {
				holder = new LmHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.layout_landmark_list_item, null);
				holder.tv_lmName = (TextView) convertView
						.findViewById(R.id.tv_lm_name);
				holder.tv_lmType = (TextView) convertView
						.findViewById(R.id.tv_lm_type);
				holder.iv_lmPic = (ImageView) convertView
						.findViewById(R.id.landmark_pic);
				convertView.setTag(holder);
			} else {
				holder = (LmHolder) convertView.getTag();
			}
			// TODO 在这里设置地标的图片
			holder.tv_lmName.setText(lmList.get(position).getLmName());
			holder.tv_lmType.setText(getLmTypeString(lmList.get(position)
					.getLmType()));
			holder.iv_lmPic.setTag(lmList.get(position).getMediaPath());
			Drawable cacheImg = asyncImageLoader.loadDrawable(
					lmList.get(position).getMediaPath(),
					new AsyncImageLoader.ImageCallback() {
						@Override
						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {
							ImageView ivByTag = (ImageView) lmListView
									.findViewWithTag(imageUrl);
							if (ivByTag != null && imageDrawable != null) {
								ivByTag.setImageDrawable(imageDrawable);
							} else {
								try {
									ivByTag.setImageResource(R.drawable.default_avatar);
								} catch (Exception e) {

								}
							}
						}
					});
			holder.iv_lmPic.setImageResource(R.drawable.default_avatar);
			if (cacheImg != null) {
				holder.iv_lmPic.setImageDrawable(cacheImg);
			}
			return convertView;
		}

		private String getLmTypeString(String lmType) {
			try {
				switch (Integer.parseInt(lmType)) {
				case 0:
					return "景点";
				case 1:
					return "餐饮";
				case 2:
					return "路标";
				default:
					return "未知";
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return "未知";
			}
		}

		class LmHolder {
			ImageView iv_lmPic;
			TextView tv_lmName;
			TextView tv_lmType;
		}
	}
}
