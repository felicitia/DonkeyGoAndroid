package com.donkey.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.donkey.R;
import com.donkey.entity.ListItem;
import com.donkey.login.CustomDialog;
import com.donkey.util.AppUtil;
import com.donkey.view.MyListView;
import com.donkey.view.MyListView.OnRefreshListener;

public abstract class MemoryBaseActivity extends Activity {

	protected MyListView listView;
	protected View moreView; // ListView底部的加载更多的View

	// 底部加载更多的View的按钮和进度条
	protected Button moreDataButton;
	protected ProgressBar moreDataProgressBar;

	protected final int MaxDataNum = 10; // 设置数据最大的加载条数，即每次加载10条
	protected int lastVisibleIndex; // 最后可见条目的索引

	protected MyAdapter mAdapter;

	protected List<ListItem> dataList;

	protected ViewSwitcher switcher;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	/**
	 * @author liushuai
	 * @description 初始化一些必要的数据
	 */
	public void initData() {
		mAdapter = new MyAdapter();

		dataList = new ArrayList<ListItem>();
		ListItem item;
		for (int i = 0; i < 20; i++) {
			item = new ListItem();
			item.setName("王小" + i);
			item.setTitle("标题：" + "第" + i + "个title");
			item.setLocation("地点：" + "第" + i + "个location");
			item.setContent("内容：" + "第" + i + "个content");
			item.setTime("2012-07-25 11:45");
			dataList.add(item);
		}
	}

	/**
	 * @author liushuai
	 * @description 初始化一些View
	 */
	public void initViews() {
		// 初始化ListView
		listView = new MyListView(this);
		listView.setCacheColorHint(Color.argb(0, 0, 0, 0));
		listView.setonRefreshListener(refreshListener);
		listView.setOnItemClickListener(listener);
		listView.setDivider(getResources().getDrawable(
				R.drawable.list_divider_line));
		listView.setDividerHeight(5);

		// 实例化底部的布局
		moreView = getLayoutInflater().inflate(R.layout.moredata, null);
		moreDataButton = (Button) moreView.findViewById(R.id.bt_load);
		moreDataProgressBar = (ProgressBar) moreView.findViewById(R.id.pg);

		// 为ListView加上底部的布局
		listView.addFooterView(moreView);
		listView.setAdapter(mAdapter);

		moreDataButton.setOnClickListener(l);
	}

	/**
	 * 展示listview,注意在子类的UI线程被调用
	 */
	protected void showListview() {
		switcher.setDisplayedChild(0);
	}

	/**
	 * 设置List的Item的内容，在子类里面重写
	 */
	public abstract void setItemContent(ItemHolder holder, int position);

	public class ItemHolder {
		ImageView iv_avatar;
		TextView tv_uname;
		TextView tv_1;
		TextView tv_2;
		TextView tv_3;
		TextView tv_time;
	}

	public class ItemHolderWithoutAvatar {
		TextView tv_1;
		TextView tv_2;
		TextView tv_3;
		TextView tv_time;
	}

	public OnClickListener l = new OnClickListener() {

		@Override
		public void onClick(View v) {
			moreDataProgressBar.setVisibility(View.VISIBLE);
			moreDataButton.setVisibility(View.GONE);

			getMoreDataList();
		}
	};

	/**
	 * 下拉刷新监听器
	 */
	public OnRefreshListener refreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			new AsyncTask<String, Integer, String>() {
				@Override
				protected String doInBackground(String... params) {
					try {
						requestData();
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					Toast.makeText(MemoryBaseActivity.this, "下拉刷新完毕",
							Toast.LENGTH_SHORT).show();
					listView.onRefreshComplete();
				}
			}.execute("begin");
		}
	};

	/**
	 * listview的adapter
	 * 
	 * @author liushuai
	 */
	public class MyAdapter extends BaseAdapter {

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
			return convertView;
		}
	}

	/**
	 * item点击事件监听器
	 * 
	 * @author zouliping
	 */
	public OnItemClickListener listener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position == 0) {
				return;
			}
			
			if (AppUtil.haveLoggedin) {
				// TODO 在这里打开显示具体的页面
				Log.e("hava login?", AppUtil.currentUserId + "");
				redirectDetailActivity(position - 1);
			} else
				new CustomDialog(MemoryBaseActivity.this).show();
		}
	};

	/**
	 * 重定向到具体item内容的activity
	 * 
	 * @param position
	 * @author zouliping
	 */
	public abstract void redirectDetailActivity(int position);

	/**
	 * get more data
	 */
	public abstract void getMoreDataList();

	/**
	 * request data
	 */
	public abstract void requestData();
}
