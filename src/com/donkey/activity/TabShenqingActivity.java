package com.donkey.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

import com.donkey.R;
import com.donkey.adapter.ShenqingiListAdapter;
import com.donkey.entity.XmlMsgItem;
import com.donkey.httpclient.XmlMsgItemHandler;
import com.donkey.util.AppKeys;
import com.donkey.util.AppUtil;
import com.donkey.util.NetWorkState;

public class TabShenqingActivity extends ListActivity {

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		requestMsg();
	}

	private ShenqingiListAdapter adapter = null;

	private List<XmlMsgItem> list;
	private XmlMsgItemHandler xmlHandler;

	private String mResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.xiaoxi_shenqing);

		this.getListView().setDivider(getResources().getDrawable(
				R.drawable.list_divider_line));
		this.getListView().setDividerHeight(5);
		
		xmlHandler = new XmlMsgItemHandler();
	
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		final String friendId = list.get(position).getFriendId();

		AlertDialog.Builder ad = new AlertDialog.Builder(getParent());
		ad.setTitle("好友申请");
		ad.setMessage(list.get(position).getNickname() + "想加你为好友，并对你说："
				+ list.get(position).getContent());
		ad.setPositiveButton("同意", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					URL url = new URL(AppKeys.HANDLE_FRIEND_APPLY_URL.replace(
							"$userid", AppUtil.currentUserId + "").replace(
							"$comuser", friendId)
							+ "1");
					addOrRejectFriend(url);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		});
		ad.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					URL url = new URL(AppKeys.HANDLE_FRIEND_APPLY_URL.replace(
							"$userid", AppUtil.currentUserId + "").replace(
							"$comuser", friendId)
							+ "0");
					addOrRejectFriend(url);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		});
		ad.show();
	}

	/**
	 * 获取消息列表的list
	 * 
	 * @return message list
	 * 
	 * @author zouliping
	 */
	private List<XmlMsgItem> getMsgList() {
		if (!NetWorkState.isNetworkAvailable(this)) { // 判断网络连接情况
			handler.sendEmptyMessage(0);
			return null;
		}
		try {
			URL url = new URL(AppKeys.GET_APPLY_LIST_URL.replace("$userid",
					AppUtil.currentUserId + ""));
			Log.i("apply url", url.toString());
			URLConnection connection = url.openConnection();
			InputStream inputStream = connection.getInputStream();
			list = xmlHandler.getMsgItems(inputStream);

			Log.i("apply list size", list.size() + "");
			if (list.size() == 0) {
				handler.sendEmptyMessage(-1);
			} else {
				handler.sendEmptyMessage(1);
			}
			return list;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * request apply list
	 * 
	 */
	private void requestMsg() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				getMsgList();
			}
		};
		thread.start();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case -1:
//				Toast.makeText(TabShenqingActivity.this, "服务不可用",
//						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(TabShenqingActivity.this, "网络连接不可用，请检查您的网络连接",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				adapter = new ShenqingiListAdapter(TabShenqingActivity.this,
						list);
				setListAdapter(adapter);
				adapter.notifyDataSetChanged();
				break;
			case 2:
				Toast.makeText(TabShenqingActivity.this, "拒绝好友请求成功",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(TabShenqingActivity.this, "加为好友成功",
						Toast.LENGTH_SHORT).show();
				break;
			case 4:
				Toast.makeText(TabShenqingActivity.this, "请求失败",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * add or reject friend
	 * 
	 * @param url
	 */
	private void addOrRejectFriend(final URL url) {
		try {
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						HttpURLConnection urlConnection = (HttpURLConnection) url
								.openConnection();
						urlConnection.connect();

						if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
							InputStream is = urlConnection.getInputStream();
							byte[] buf = new byte[2048];
							int count = is.read(buf, 0, buf.length);
							mResult = new String(buf, 0, count);

							int result = Integer.parseInt(mResult);
							Log.i("friend result", mResult);
							if (result == 1) {
								handler.sendEmptyMessage(3);
							} else if (result == 2) {
								handler.sendEmptyMessage(2);
							} else {
								handler.sendEmptyMessage(4);
							}

						}
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			thread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
