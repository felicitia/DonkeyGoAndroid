package com.donkey.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.donkey.R;
import com.donkey.entity.XmlTravelItem;
import com.donkey.httpclient.XmlTravelItemHandler;
import com.donkey.login.CustomDialog;
import com.donkey.util.AppKeys;
import com.donkey.util.AppUtil;
import com.donkey.util.AsyncImageLoader;
import com.donkey.util.NetWorkState;

public class TabTravelActivity extends MemoryBaseActivity {

	private ImageView iv_more_func;
	private ImageView iv_add_plan;
	private ImageView iv_search;
	private ImageView iv_search_cancel;

	private Button checkMyPlan;
	private Button search;
	private Button checkMyAttention;

	private PopupWindow popupWindow;

	private RelativeLayout searchLayout;

	private List<XmlTravelItem> list;
	private List<XmlTravelItem> lastList;

	private XmlTravelItemHandler xmlHandler;

	private TravelAdapter adapter;

	private String nums;
	private String uid;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_travel_list);

		xmlHandler = new XmlTravelItemHandler();
		requestData();

		initData();
		initViews();

	}

	@Override
	public void setItemContent(ItemHolder holder, int position) {
		holder.tv_uname.setText(list.get(position).getUsername());
		holder.tv_1.setText(list.get(position).getTravelTitle());
		holder.tv_2.setText(list.get(position).getTravelLocation());
		holder.tv_3.setText(list.get(position).getStartTime());
		holder.tv_time.setText(TravelDetailActivity.transferLasting(list.get(
				position).getLasting()));
	}

	/**
	 * 初始化页面
	 */
	@Override
	public void initViews() {
		super.initViews();

		switcher = (ViewSwitcher) findViewById(R.id.viewswitcher_travel);
		switcher.addView(listView);
		switcher.addView(getLayoutInflater().inflate(R.layout.list_loading,
				null));
		switcher.showNext();

		iv_more_func = (ImageView) findViewById(R.id.more_func);
		iv_add_plan = (ImageView) findViewById(R.id.add_travel_plan);
		iv_search = (ImageView) findViewById(R.id.search_btn);
		iv_search_cancel = (ImageView) findViewById(R.id.cancel_btn);

		iv_more_func.setOnClickListener(onClickListener);
		iv_add_plan.setOnClickListener(onClickListener);
		iv_search.setOnClickListener(onClickListener);
		iv_search_cancel.setOnClickListener(onClickListener);

		searchLayout = (RelativeLayout) findViewById(R.id.search_layout);

	}

	/**
	 * 初始化PopupWindow
	 */
	private void initPopupWindow() {
		View view_popupWindow = getLayoutInflater().inflate(
				R.layout.layout_travel_pop_menu, null, false);// 获取自定义布局文件
		popupWindow = new PopupWindow(view_popupWindow, 140, 200, true);// 创建PopupWindow实例

		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);// 设置点击PopupWindow外部时消失
		popupWindow.setFocusable(true);// 点击menu以外地方及返回键退出

		view_popupWindow.setFocusableInTouchMode(true);
		view_popupWindow.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU && popupWindow.isShowing()) {
					popupWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		checkMyPlan = (Button) view_popupWindow
				.findViewById(R.id.check_my_plan);
		search = (Button) view_popupWindow.findViewById(R.id.search_travel);
		checkMyAttention = (Button) view_popupWindow
				.findViewById(R.id.attention_travel);

		checkMyPlan.setOnClickListener(onClickListener);
		search.setOnClickListener(onClickListener);
		checkMyAttention.setOnClickListener(onClickListener);
	}

	/**
	 * 获取PopupWindows
	 * 
	 * @author zouliping
	 */
	private void getPopupWindow() {
		if (null != popupWindow) {
			popupWindow.dismiss();
			return;
		} else {
			initPopupWindow();
		}
	}

	/**
	 * get travel list by http
	 * 
	 * @param url
	 */
	private List<XmlTravelItem> getTravelList(URL url) {
		if (!NetWorkState.isNetworkAvailable(this)) { // 判断网络连接情况
			handler.sendEmptyMessage(0);
			return null;
		}

		try {
			URLConnection connection = url.openConnection();
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			list = xmlHandler.getTravelItems(inputStream);

			return list;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void getMoreTravelList(URL url) {
		if (!NetWorkState.isNetworkAvailable(this)) { // 判断网络连接情况
			handler.sendEmptyMessage(0);
			return;
		}

		try {
			URLConnection connection = url.openConnection();
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			lastList = xmlHandler.getTravelItems(inputStream);

			if (lastList.size() == 0) {
				handler.sendEmptyMessage(-1);
			} else {
				for (XmlTravelItem xti : lastList) {
					list.add(xti);
				}
				handler.sendEmptyMessage(2);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * request all travel list
	 */
	@Override
	public void requestData() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				URL url = null;
				try {
					nums = "5";
					uid = "-1";
					url = new URL(AppKeys.GET_TRAVEL_LIST_URL.replace("$nums",
							nums).replace("$uid", uid));
					List<XmlTravelItem> allList = getTravelList(url);
					if (allList.size() == 0) {
						handler.sendEmptyMessage(-1);
					} else {
						handler.sendEmptyMessage(1);
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	/**
	 * get my travel list
	 */
	private void requestMyTravelList() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				URL url = null;
				try {
					nums = "-1";
					uid = AppUtil.currentUserId + "";
					url = new URL(AppKeys.GET_TRAVEL_LIST_URL.replace("$nums",
							nums).replace("$uid", uid));
					Log.i("my travel",
							AppKeys.GET_TRAVEL_LIST_URL.replace("$nums", nums)
									.replace("$uid", uid));
					List<XmlTravelItem> myList = getTravelList(url);
					if (myList.size() == 0) {
						handler.sendEmptyMessage(-1);
					} else {
						handler.sendEmptyMessage(1);
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	/**
	 * get more travel list
	 */
	@Override
	public void getMoreDataList() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				URL url;
				try {
					String lastId = list.get(list.size() - 1).getTravelId();
					url = new URL(AppKeys.GET_MORE_TRAVEL_LIST_URL + lastId);
					getMoreTravelList(url);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * get my attention travel
	 */
	private void getMyAttention() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					handler.sendEmptyMessage(3);
					URL url = new URL(AppKeys.GET_MY_ATTENTION_TRAVEL_URL
							+ AppUtil.currentUserId);
					List<XmlTravelItem> attentionList = getTravelList(url);
					if (attentionList.size() == 0) {
						handler.sendEmptyMessage(-1);
					} else {
						handler.sendEmptyMessage(1);
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	/**
	 * search travel
	 */
	private void searchTravel() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				URL url = null;
				try {
					EditText et_search = (EditText) findViewById(R.id.search_bar);
					String searchContent = et_search.getText().toString();

//					url = new URL(AppKeys.SEARCH_TRAVEL_URL + searchContent);
					String tmp = null;
					try {
						tmp = URLEncoder.encode(AppKeys.SEARCH_TRAVEL_URL + searchContent,"UTF-8");
						Log.e("encode",tmp);
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					List<XmlTravelItem> searchList = null;
					if(tmp!=null)
					{
						searchList = getTravelList(new URL(tmp));
						
					}
					else
					{
						Log.e("encode","null");
					}
					if (searchList.size() == 0) {
						handler.sendEmptyMessage(-1);
					} else {
						handler.sendEmptyMessage(3);
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case -1:
				Toast.makeText(TabTravelActivity.this, "服务不可用",
						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(TabTravelActivity.this, "网络连接不可用，请检查您的网络连接",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				adapter = new TravelAdapter();
				listView.setAdapter(adapter);
				switcher.setDisplayedChild(0);
				listView.onRefreshComplete();
				if (listView.getFooterViewsCount() == 0) {
					listView.addFooterView(moreView);
				}
				break;
			case 2:
				moreDataProgressBar.setVisibility(View.GONE);
				moreDataButton.setVisibility(View.VISIBLE);
				adapter.notifyDataSetChanged();
				break;
			case 3:
				adapter.notifyDataSetChanged();
				listView.removeFooterView(moreView);
			default:
				break;
			}
		}
	};

	/**
	 * 发起结伴游和更多功能的监听器
	 * 
	 */
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.more_func:
				getPopupWindow();
				popupWindow.showAsDropDown(v);
				break;
			case R.id.add_travel_plan:
				 if (AppUtil.haveLoggedin)
				RedirectAddPlanActivity();
				 else
				 new CustomDialog(TabTravelActivity.this).show();

				break;
			case R.id.check_my_plan:
				 if (AppUtil.haveLoggedin) {
				popupWindow.dismiss();
				requestMyTravelList();
				 } else
				 new CustomDialog(TabTravelActivity.this).show();

				break;
			case R.id.search_travel:
				popupWindow.dismiss();
				searchLayout.setVisibility(View.VISIBLE);

				break;
			case R.id.search_btn:
				searchTravel();
				break;
			case R.id.cancel_btn:
				searchLayout.setVisibility(View.GONE);
				requestData();
				break;
			case R.id.attention_travel:
				 if (AppUtil.haveLoggedin) {
				popupWindow.dismiss();
				getMyAttention();
				} else
					new CustomDialog(TabTravelActivity.this).show();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 重定向到发起结伴游的activity
	 */
	private void RedirectAddPlanActivity() {
		Intent intent = new Intent();
		intent.setClass(TabTravelActivity.this, AddTravelPlanActivity.class);
		startActivityForResult(intent, 0);
	}

	/**
	 * 重定向到查看结伴游详情的activity
	 */
	@Override
	public void redirectDetailActivity(int position) {
		Intent intent = new Intent();
		intent.setClass(TabTravelActivity.this, TravelDetailActivity.class);
		intent.putExtra("travelid", list.get(position).getTravelId());
		Log.i("travel id detail", list.get(position).getTravelId());
		startActivityForResult(intent, 0);
	}

	public class TravelAdapter extends BaseAdapter {

		AsyncImageLoader asyncImageLoader = new AsyncImageLoader(80, 80);

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ItemHolder holder;
			if (convertView == null) {
				holder = new ItemHolder();
				convertView = getLayoutInflater().inflate(R.layout.list_item,
						null);
				holder.iv_avatar = (ImageView) convertView
						.findViewById(R.id.avatar);
				holder.tv_uname = (TextView) convertView
						.findViewById(R.id.uname);
				holder.tv_1 = (TextView) convertView.findViewById(R.id.tv_1);
				holder.tv_2 = (TextView) convertView.findViewById(R.id.tv_2);
				holder.tv_3 = (TextView) convertView.findViewById(R.id.tv_3);
				holder.tv_time = (TextView) convertView.findViewById(R.id.time);
				convertView.setTag(holder);
			} else {
				holder = (ItemHolder) convertView.getTag();
			}
			setItemContent(holder, position);
			holder.iv_avatar.setTag(list.get(position).getAvatar());
			Drawable cacheImg = asyncImageLoader.loadDrawable(list
					.get(position).getAvatar(),
					new AsyncImageLoader.ImageCallback() {
						@Override
						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {
							ImageView ivByTag = (ImageView) listView
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
			holder.iv_avatar.setImageResource(R.drawable.default_avatar);
			if (cacheImg != null) {
				holder.iv_avatar.setImageDrawable(cacheImg);
			}
			return convertView;
		}
	}

	public class MyHolder {
		ImageView iv_avatar;
		TextView tv_uname;
		TextView tv_1;
		TextView tv_2;
		TextView tv_3;
		TextView tv_time;
	}
}
