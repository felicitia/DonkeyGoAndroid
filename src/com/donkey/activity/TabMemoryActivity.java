package com.donkey.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.donkey.R;
import com.donkey.datastorage.DBManager;
import com.donkey.entity.ListItem;
import com.donkey.entity.Memory;
import com.donkey.entity.XmlMemoryItem;
import com.donkey.httpclient.XmlMemoryItemHandler;
import com.donkey.login.CustomDialog;
import com.donkey.util.AppKeys;
import com.donkey.util.AppUtil;
import com.donkey.util.AsyncImageLoader;
import com.donkey.util.NetWorkState;

public class TabMemoryActivity extends MemoryBaseActivity {

	private ImageView iv_more_function;
	private ImageView iv_add_location;
	private Button btn_checkAllMemory;
	private Button btn_checkMyMemory;
	private Button btn_addMyMemory;
	private Button btn_modifyMyMemory;

	private PopupWindow memoryWindow;

	private DBManager manager;

	private int flag = 0;

	private List<XmlMemoryItem> list;
	private List<XmlMemoryItem> lastList;
	private XmlMemoryItemHandler xmlHandler;

	private MemoryAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_memory_list);

		manager = new DBManager(TabMemoryActivity.this);

		initData();
		initViews();

		xmlHandler = new XmlMemoryItemHandler();
		requestData();

	}

	@Override
	public void setItemContent(ItemHolder holder, int position) {
		holder.tv_uname.setText(list.get(position).getNickname());
		holder.tv_1.setText(list.get(position).getTitle());
		holder.tv_2.setText(list.get(position).getLocation());
		holder.tv_3.setText(list.get(position).getContent());
		holder.tv_time.setText(list.get(position).getPubDate());
	}

	/**
	 * override the method of the base activity
	 * 
	 * @author zouliping
	 */
	@Override
	public void initViews() {
		super.initViews();

		switcher = (ViewSwitcher) findViewById(R.id.viewswitcher_memory);
		switcher.addView(listView);
		switcher.addView(getLayoutInflater().inflate(R.layout.list_loading,
				null));
		switcher.showNext();

		iv_more_function = (ImageView) findViewById(R.id.more_function);
		iv_add_location = (ImageView) findViewById(R.id.add_location);

		iv_more_function.setOnClickListener(onClickListener);
		iv_add_location.setOnClickListener(onClickListener);
	}

	@Override
	public void initData() {
		super.initData();
		adapter = new MemoryAdapter();
	}

	/**
	 * 初始化PopupWindow
	 * 
	 * @author zouliping
	 */
	private void initPopupWindow() {
		View view_memory_popupWindow = getLayoutInflater().inflate(
				R.layout.layout_memory_pop_menu, null, false);// 获取自定义布局文件
		memoryWindow = new PopupWindow(view_memory_popupWindow, 140, 300, true);// 创建PopupWindow实例
		memoryWindow.setBackgroundDrawable(new BitmapDrawable());
		memoryWindow.setOutsideTouchable(true);// 设置点击PopupWindow外部时消失
		memoryWindow.setFocusable(true);// 点击menu以外地方及返回键退出

		view_memory_popupWindow.setFocusableInTouchMode(true);
		view_memory_popupWindow.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU
						&& memoryWindow.isShowing()) {
					memoryWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		btn_checkAllMemory = (Button) view_memory_popupWindow
				.findViewById(R.id.checkAllMemory);
		btn_checkMyMemory = (Button) view_memory_popupWindow
				.findViewById(R.id.checkMyMemory);
		btn_addMyMemory = (Button) view_memory_popupWindow
				.findViewById(R.id.addMyMemory);
		btn_modifyMyMemory = (Button) view_memory_popupWindow
				.findViewById(R.id.modifyMyMemory);

		btn_checkAllMemory.setOnClickListener(onClickListener);
		btn_checkMyMemory.setOnClickListener(onClickListener);
		btn_addMyMemory.setOnClickListener(onClickListener);
		btn_modifyMyMemory.setOnClickListener(onClickListener);
	}

	/**
	 * 获取PopupWindows
	 * 
	 * @author zouliping
	 */
	private void getPopupWindow() {
		if (null != memoryWindow) {
			memoryWindow.dismiss();
			return;
		} else {
			initPopupWindow();
		}
	}

	/**
	 * 通过HTTP请求获取记忆列表
	 * 
	 * @author zouliping
	 */
	private void getMemoryList(URL url) {
		if (!NetWorkState.isNetworkAvailable(this)) { // 判断网络连接情况
			handler.sendEmptyMessage(0);
			return;
		}
		try {
			URLConnection con = url.openConnection();
			con.connect();
			InputStream input = con.getInputStream();
			list = xmlHandler.getMemoryItems(input);

			if (list.size() == 0) {
				handler.sendEmptyMessage(-1);
			} else {
				handler.sendEmptyMessage(1);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 点击底部加载更多的按钮加载更多内容
	 * 
	 * @param url
	 */
	private void getMoreMemoryList(URL url) {
		if (!NetWorkState.isNetworkAvailable(this)) {
			handler.sendEmptyMessage(0);
			return;
		}
		try {
			URLConnection con = url.openConnection();
			con.connect();
			InputStream input = con.getInputStream();
			lastList = xmlHandler.getMemoryItems(input);

			if (lastList.size() == 0) {
				handler.sendEmptyMessage(-1);
			} else {
				for (XmlMemoryItem xmi : lastList) {
					list.add(xmi);
				}
				handler.sendEmptyMessage(4);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 请求记忆列表
	 */
	@Override
	public void requestData() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					URL url = new URL(AppKeys.GET_MEMORY_LIST_URL);
					getMemoryList(url);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	/**
	 * get more memory list
	 */
	@Override
	public void getMoreDataList() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				URL url;
				try {
					String lastId = list.get(list.size() - 1).getMemoryId();
					Log.i("sinceid", lastId);
					url = new URL(AppKeys.GET_MORE_MEMORY_LIST_URL + lastId);
					getMoreMemoryList(url);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 通过http请求获取给定的uid发过的记忆列表
	 * 
	 * @param uid
	 */
	private void getMyList(String uid) {
		if (!NetWorkState.isNetworkAvailable(this)) { // 判断网络连接情况
			handler.sendEmptyMessage(0);
			return;
		}
		try {
			URL url = new URL(AppKeys.MY_MEMORY_URL + uid);
			Log.e("get my list", url.toString());
			URLConnection con = url.openConnection();
			con.connect();
			InputStream input = con.getInputStream();
			list = xmlHandler.getMemoryItems(input);

			if (list.size() == 0) {
				handler.sendEmptyMessage(2); // 用户没有发布过任何记忆
			} else {
				handler.sendEmptyMessage(3); // 成功请求我的记忆
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 请求我的记忆列表
	 */
	private void getMyList() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				getMyList(String.valueOf(AppUtil.currentUserId));
			}
		}).start();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case -1:
				Toast.makeText(TabMemoryActivity.this, "服务不可用",
						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(TabMemoryActivity.this, "网络连接不可用，请检查您的网络连接",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				listView.setAdapter(adapter);
				switcher.setDisplayedChild(0);
				listView.onRefreshComplete();
				break;
			case 2:
				Toast.makeText(TabMemoryActivity.this, "您没有发布过任何记忆",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				listView.setAdapter(adapter);
				switcher.setDisplayedChild(0);
				adapter.notifyDataSetChanged();
				break;
			case 4:
				moreDataProgressBar.setVisibility(View.GONE);
				moreDataButton.setVisibility(View.VISIBLE);
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 添加地标和更多功能的监听器
	 * 
	 * @author zouliping
	 */
	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.more_function:
				getPopupWindow();
				memoryWindow.showAsDropDown(v);
				break;
			case R.id.add_location:
				if (AppUtil.haveLoggedin) {
					redirectAddLocationActivity();
				} else {
					new CustomDialog(TabMemoryActivity.this).show();
				}

				break;
			case R.id.checkAllMemory:

				memoryWindow.dismiss();
				flag = 0;
				if (listView.getFooterViewsCount() == 0) {
					listView.addFooterView(moreView);
				}
				requestData();
				break;
			case R.id.checkMyMemory:
				if (AppUtil.haveLoggedin) {
					memoryWindow.dismiss();
					listView.removeFooterView(moreView);
					flag = 0;
					switcher.setDisplayedChild(1);
					getMyList();
				} else {
					new CustomDialog(TabMemoryActivity.this).show();
				}
				break;
			case R.id.addMyMemory:
				if (AppUtil.haveLoggedin) {
					memoryWindow.dismiss();
					redirectAddMemoryActivity();
				} else {
					new CustomDialog(TabMemoryActivity.this).show();
				}
				break;
			case R.id.modifyMyMemory:
				if (AppUtil.haveLoggedin) {
					memoryWindow.dismiss();
					flag = 2;
					ArrayList<Memory> mArrayList = manager
							.getSpecMemoryList(AppUtil.currentUserId);
					dataList = new ArrayList<ListItem>();
					ListItem item = null;
					for (Memory m : mArrayList) {
						item = new ListItem();
						item.setContent(m.getMemoryContent());
						item.setLocation(m.getMemoryLocation());
						item.setName(AppUtil.currentUserName);
						item.setTime(m.getMemoryLastModifyTime());
						item.setTitle(m.getMemoryTitle());
						dataList.add(item);
					}
					listView.setAdapter(new LoacalMemoryAdapter());
					switcher.setDisplayedChild(0);
				} else {
					new CustomDialog(TabMemoryActivity.this).show();
				}
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 重定向到记忆盒详情activity
	 * 
	 * @author zouliping
	 */
	@Override
	public void redirectDetailActivity(int position) {
		Intent intent = new Intent();
		if (flag == 0) {
			intent.setClass(TabMemoryActivity.this, MemoryDetailActivity.class);
			intent.putExtra("mem_id", list.get(position).getMemoryId());
			startActivityForResult(intent, 0);
		} else if (flag == 2) {
			Bundle bundle = new Bundle();
			bundle.putString("flag", "LocalMemory");

			XmlMemoryItem toPutItem = new XmlMemoryItem();
			ListItem originalItem = dataList.get(position);

			toPutItem.setTitle(originalItem.getTitle());
			toPutItem.setLocation(originalItem.getLocation());
			toPutItem.setContent(originalItem.getContent());

			bundle.putSerializable("memoryItem", toPutItem);
			intent.setClass(TabMemoryActivity.this, AddMemoryActivity.class);
			intent.putExtras(bundle);
			startActivityForResult(intent, 0);
		}
	}

	/**
	 * 重定向到添加地标的activity
	 * 
	 * @author zouliping
	 */
	private void redirectAddLocationActivity() {

		Intent intent = new Intent();
		intent.setClass(TabMemoryActivity.this, AddLocationActivity.class);
		startActivityForResult(intent, 0);
	}

	/**
	 * 重定向到添加新记忆的activity
	 */
	private void redirectAddMemoryActivity() {
		Bundle bundle = new Bundle();
		Intent intent = new Intent();
		bundle.putString("flag", "AddMemory");
		intent.putExtras(bundle);
		intent.setClass(TabMemoryActivity.this, AddMemoryActivity.class);
		startActivity(intent);
	}

	/**
	 * adapter for viewing the memory from server
	 * 
	 * @author liushuai
	 * 
	 */
	private class MemoryAdapter extends BaseAdapter {

		AsyncImageLoader asyncImageLoader = new AsyncImageLoader(80, 80);

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
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
			Log.e("avatar_url", list.get(position).getAvatar());
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

	/**
	 * adapter for view local memory list
	 * 
	 * @author liushuai
	 * 
	 */
	private class LoacalMemoryAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return dataList.size();
		}

		@Override
		public Object getItem(int position) {
			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ItemHolderWithoutAvatar holder;
			if (convertView == null) {
				holder = new ItemHolderWithoutAvatar();
				convertView = getLayoutInflater().inflate(
						R.layout.list_item_local, null);
				holder.tv_1 = (TextView) convertView.findViewById(R.id.tv_1);
				holder.tv_2 = (TextView) convertView.findViewById(R.id.tv_2);
				holder.tv_3 = (TextView) convertView.findViewById(R.id.tv_3);
				holder.tv_time = (TextView) convertView.findViewById(R.id.time);
				convertView.setTag(holder);
			} else {
				holder = (ItemHolderWithoutAvatar) convertView.getTag();
			}
			holder.tv_1.setText(dataList.get(position).getTitle());
			holder.tv_2.setText(dataList.get(position).getLocation());
			holder.tv_3.setText(dataList.get(position).getContent());
			holder.tv_time.setText(dataList.get(position).getTime());
			return convertView;
		}

	}

}
