package com.donkey.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.donkey.R;
import com.donkey.entity.XmlCommentItem;
import com.donkey.httpclient.XmlCommentItemHandler;
import com.donkey.util.AppKeys;
import com.donkey.util.NetWorkState;
import com.donkey.view.CommentDialog;
import com.donkey.view.MyListView;
import com.donkey.view.MyListView.OnRefreshListener;
import com.donkey.view.TravelCommentDialog;

public class ShowCommentActivity extends Activity {

	private MyListView commentList;
	private List<XmlCommentItem> list;
	private XmlCommentItemHandler xmlHandler;

	private ImageView iv_goback;
	private ImageView iv_reply;

	private CommentAdapter adapter;
	private ViewSwitcher switcher;

	private String currentId;
	private String tag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_comment);

		initView();
		initData();

		xmlHandler = new XmlCommentItemHandler();
		requestComment();
	}

	public void initView() {
		switcher = (ViewSwitcher) findViewById(R.id.viewswitcher_comment_content);
		commentList = new MyListView(this);
		commentList.setCacheColorHint(Color.argb(0, 0, 0, 0));
		commentList.setDivider(getResources().getDrawable(
				R.drawable.list_divider_line));
		commentList.setDividerHeight(5);
		commentList.setonRefreshListener(refreshListener);

		switcher.addView(commentList);
		switcher.addView(getLayoutInflater().inflate(R.layout.list_loading,
				null));
		switcher.showNext();

		iv_goback = (ImageView) findViewById(R.id.go_back_show_comment);
		iv_reply = (ImageView) findViewById(R.id.reply_show_comment);

		iv_goback.setOnClickListener(listener);
		iv_reply.setOnClickListener(listener);
	}

	public void initData() {
		this.tag = getIntent().getStringExtra("tag");
		this.currentId = getIntent().getStringExtra("currentId");
		Log.e("fuck!!!", currentId);
	}

	public OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.go_back_show_comment:
				ShowCommentActivity.this.finish();
				break;
			case R.id.reply_show_comment:
				Log.i("tag", tag);
				if (tag.equals("memoryComment"))
					new CommentDialog(ShowCommentActivity.this,currentId).setDisplay();
				else if (tag.equals("travelComment"))
					new TravelCommentDialog(ShowCommentActivity.this)
							.setDisplay();
				break;
			default:
				break;
			}
		}
	};

	private OnRefreshListener refreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			refreshPage();
		}
	};

	/**
	 * refresh comment page
	 * 
	 * @author zouliping
	 */
	private void refreshPage() {
		new AsyncTask<String, Integer, String>() {

			@Override
			protected String doInBackground(String... params) {
				getCommentList();
				return null;
			}

			protected void onPostExecute(String result) {
				if (adapter != null) {
					adapter.notifyDataSetChanged();
					commentList.onRefreshComplete();
				}
			}
		}.execute("begin");
	}

	/**
	 * get comment list
	 * 
	 * @author zouliping
	 */
	public void getCommentList() {
		if (!NetWorkState.isNetworkAvailable(this)) { // 判断网络连接情况
			handler.sendEmptyMessage(0);
			return;
		}
		try {
			URL url = null;
			if (tag.equals("memoryComment")) {
				// currentId = "1";
				url = new URL(AppKeys.MEMORY_COMMENT_LIST_URL + currentId);
			} else if (tag.equals("travelComment")) {
				// currentId = "1";
				url = new URL(AppKeys.TRAVEL_COMMENT_LIST_URL + currentId);
			}

			URLConnection connection = url.openConnection();
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			list = xmlHandler.getCommentItems(inputStream);

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

	private void requestComment() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				getCommentList();
			}
		};
		thread.start();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case -1:
				Toast.makeText(ShowCommentActivity.this, "无评论，快发表你的评论吧",
						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(ShowCommentActivity.this, "网络连接不可用，请检查您的网络连接",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				adapter = new CommentAdapter();
				commentList.setAdapter(adapter);
				switcher.setDisplayedChild(0);
				commentList.onRefreshComplete();
				break;
			default:
				break;
			}
		}
	};

	public class CommentAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CommentViewHolder holder;
			if (convertView == null) {
				holder = new CommentViewHolder();
				convertView = getLayoutInflater().inflate(
						R.layout.layout_comment_item, null);
				holder.tv_username = (TextView) convertView
						.findViewById(R.id.comment_username);
				holder.tv_description = (TextView) convertView
						.findViewById(R.id.comment_detail);
				holder.tv_posttimeTextView = (TextView) convertView
						.findViewById(R.id.comment_time);
				convertView.setTag(holder);
			} else {
				holder = (CommentViewHolder) convertView.getTag();
			}

			holder.tv_username.setText(list.get(position).getNickname());
			holder.tv_description.setText(list.get(position).getMcContent());
			holder.tv_posttimeTextView.setText(list.get(position)
					.getMcPubdate());

			return convertView;
		}

		public class CommentViewHolder {
			TextView tv_username;
			TextView tv_description;
			TextView tv_posttimeTextView;
		}
	}
}
