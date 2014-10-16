package com.donkey.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.donkey.R;
import com.donkey.adapter.HaoyouListExAdapter;
import com.donkey.entity.XmlGroupDetail;
import com.donkey.entity.XmlGroupItem;
import com.donkey.httpclient.XmlGroupDetailHandler;
import com.donkey.httpclient.XmlGroupItemHandler;
import com.donkey.util.AppKeys;
import com.donkey.util.AppUtil;
import com.donkey.util.NetWorkState;

/**
 * 好友Activity，显示好友/关注列表
 * 
 * @author Felicitia
 * 
 */
public class TabHaoyouActivity extends Activity {

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		requestData();
	}

	private ExpandableListView expListView = null;
	private ArrayList<String> groups = null;
	private List<List<Map<String, Object>>> children = null;
	private HaoyouListExAdapter adapter = null;

	public static String gid;
	public static int groupPosition;

	private List<XmlGroupItem> list;
	private List<XmlGroupDetail> detailList;
	private XmlGroupItemHandler xmlHandler;
	private XmlGroupDetailHandler xmlDetailHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.friend);

		initExpListView();
		initGroup();
		initChildren();

		adapter = new HaoyouListExAdapter(TabHaoyouActivity.this);

		setListeners();
		xmlHandler = new XmlGroupItemHandler();
		xmlDetailHandler = new XmlGroupDetailHandler();
		

		registerForContextMenu(expListView);
	}

	
	private void initExpListView() {
		expListView = (ExpandableListView) findViewById(R.id.uselistExpLV);
		expListView.setCacheColorHint(Color.argb(0, 0, 0, 0));
		expListView.setDivider(getResources().getDrawable(
				R.drawable.list_divider_line));
	}

	private void initGroup() {
		groups = new ArrayList<String>();
	}

	private void initChildren() {
		children = new ArrayList<List<Map<String, Object>>>();
	}

	private void setListeners() {
		/**
		 * child点击事件
		 */
		expListView
				.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

					@Override
					public boolean onChildClick(ExpandableListView parent,
							View v, int groupPosition, int childPosition,
							long id) {
						// 跳转到好友详情
						Intent intent = new Intent(TabHaoyouActivity.this,
								AuthorDetailActivity.class);
						intent.putExtra(
								"author_id",
								(String) children.get(groupPosition)
										.get(childPosition).get("friendId"));
						return true;
					}
				});

		/**
		 * 组点击展开事件
		 */
		expListView
				.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
					@Override
					public void onGroupExpand(int groupPosition) {
						TabHaoyouActivity.groupPosition = groupPosition;
						TabHaoyouActivity.gid = list.get(groupPosition)
								.getGid();
						requestDetail();
					}
				});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add("移动好友到...");
		menu.add("新建分组");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			Toast.makeText(TabHaoyouActivity.this, "移动到...", Toast.LENGTH_SHORT)
					.show();
			AlertDialog.Builder builder = new AlertDialog.Builder(
					TabHaoyouActivity.this).setTitle("选择分组");
			final CharSequence[] itemSequences = new CharSequence[list.size()];
			for (int i = 0; i < list.size(); i++) {
				itemSequences[i] = list.get(i).getGname();
			}
			builder.setSingleChoiceItems(itemSequences, 0,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Toast.makeText(TabHaoyouActivity.this,
									itemSequences[which], Toast.LENGTH_LONG)
									.show();
						}
					});

			return true;
		case 1:
			Toast.makeText(TabHaoyouActivity.this, "新建分组", Toast.LENGTH_SHORT)
					.show();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private List<Map<String, Object>> getData(List<XmlGroupDetail> detailList) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		XmlGroupDetail detail = null;
		if (detailList == null) {
			Log.i("detailList", "null");
		}
		for (int i = 0; i < detailList.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			detail = detailList.get(i);

			Log.e("title", i + detail.getTitle());

			map.put("title", detail.getTitle());
			map.put("location", detail.getLocation());
			map.put("content", detail.getContent());
			map.put("pubDate", detail.getPubDate());
			map.put("avatar", detail.getAvatar());
			map.put("nickname", detail.getNickname());
			map.put("memoryId", detail.getMemoryId());
			map.put("state", detail.getState());
			map.put("friendId", detail.getFriendId());

			list.add(map);

		}
		Log.i("detail list", "" + list.size());
		return list;
	}

	/**
	 * 获取好友的列表的List(静态的)
	 * 
	 * @return
	 */
	private List<Map<String, Object>> getHaoyou() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("title", "标题：仙女山");
		map.put("location", "地点：中国.重庆.江津");
		map.put("content", "内容：这山很美，风...");
		map.put("pubDate", "2012-4-2 00:43:43");
		map.put("icon", R.drawable.icon);
		map.put("nickname", "赵逸雪");
		list.add(map);
		list.add(map);
		list.add(map);
		list.add(map);
		list.add(map);
		return list;
	}

	private void getGroupList(URL url) {
		Log.e("group_list_url", url.toString());
		if (!NetWorkState.isNetworkAvailable(this)) { // 判断网络连接情况
			handler.sendEmptyMessage(0);
			return;
		}
		try {
			URLConnection con = url.openConnection();
			con.connect();
			InputStream input = con.getInputStream();
			if (input == null) {
				Log.i("input", "null");
			}
			list = xmlHandler.getGroupItems(input);

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

	private void getGroupDetail(URL url) {
		Log.e("group_detail_url", url.toString());
		if (!NetWorkState.isNetworkAvailable(this)) { // 判断网络连接情况
			detailHandler.sendEmptyMessage(0);
			return;
		}
		try {
			URLConnection con = url.openConnection();
			con.connect();
			InputStream input = con.getInputStream();
			if (input == null) {
				Log.i("detail input", "null");
			}
			detailList = xmlDetailHandler.getGroupDetails(input);

			if (detailList.size() == 0) {
				detailHandler.sendEmptyMessage(-1);
			} else {
				detailHandler.sendEmptyMessage(1);
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
	 * handler
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case -1:
				Toast.makeText(TabHaoyouActivity.this, "服务不可用",
						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(TabHaoyouActivity.this, "网络连接不可用，请检查您的网络连接",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				setGroups();
				adapter.setGroups(groups);
				expListView.setAdapter(adapter);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * detail handler
	 */
	private Handler detailHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case -1:
				Toast.makeText(TabHaoyouActivity.this, "服务不可用",
						Toast.LENGTH_SHORT).show();
				break;
			case 0:
				Toast.makeText(TabHaoyouActivity.this, "网络连接不可用，请检查您的网络连接",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				setChildren();
				if (children == null) {
					Log.i("1:children", "null");
				} else {
					Log.i("1:children", "" + children.size());
				}
				adapter.setChildren(children);
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 请求分组列表
	 */
	public void requestData() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					URL url = new URL(AppKeys.GET_GROUP_LIST_URL
							+ AppUtil.currentUserId);
					getGroupList(url);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	/**
	 * 请求组内详情
	 */
	public void requestDetail() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					URL url = new URL(AppKeys.GET_GROUP_DETAIL_URL.replace(
							"$user_id", AppUtil.currentUserId + "")
							+ TabHaoyouActivity.gid);
					// Log.i("url",url.toString());
					getGroupDetail(url);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	public void setGroups() {
		if (list == null) {
			Log.i("list", "null");
		} else {
			for (int i = 0; i < list.size(); i++) {
				groups.add(list.get(i).getGname());
				children.add(null);
			}
		}
	}

	public void setChildren() {
		if (children == null) {
			Log.i("children", "null");
		} else {
			children.set(TabHaoyouActivity.groupPosition, getData(detailList));
		}
	}
}
