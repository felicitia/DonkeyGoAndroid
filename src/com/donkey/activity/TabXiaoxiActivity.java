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

public class TabXiaoxiActivity extends ListActivity {

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
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		final String tmpMsgId = list.get(position).getId();

		AlertDialog.Builder ad = new AlertDialog.Builder(getParent());
		ad.setTitle("好友消息");
		ad.setMessage(list.get(position).getNickname() + "对你说："
				+ list.get(position).getContent());
		ad.setPositiveButton("已读", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					URL url = new URL(AppKeys.READ_MESSAGE_URL + tmpMsgId);
					readMessage(url);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		});
		ad.setNegativeButton("取消", null);
		ad.show();
	}

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
			URL url = new URL(AppKeys.GET_MSG_LIST_URL.replace("$userid",
					AppUtil.currentUserId + ""));
			Log.i("msg url", url.toString());
			URLConnection connection = url.openConnection();
			InputStream inputStream = connection.getInputStream();
			list = xmlHandler.getMsgItems(inputStream);

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
	 * request message list
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
//				Toast.makeText(TabXiaoxiActivity.this, "服务不可用",
//						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(TabXiaoxiActivity.this, "网络连接不可用，请检查您的网络连接",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				adapter = new ShenqingiListAdapter(TabXiaoxiActivity.this, list);
				setListAdapter(adapter);
				adapter.notifyDataSetChanged();
				break;
			case 3:
				Toast.makeText(TabXiaoxiActivity.this, "消息已读",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * read message
	 * 
	 * @param url
	 */
	private void readMessage(final URL url) {
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
							if (result == 1) {
								handler.sendEmptyMessage(3);
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
