package com.donkey.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.donkey.R;
import com.donkey.activity.TabHaoyouActivity;
import com.donkey.util.AppKeys;
import com.donkey.util.AppUtil;
import com.donkey.util.AsyncImageLoader;

public class HaoyouListExAdapter extends BaseExpandableListAdapter {
	private ArrayList<String> groups = null;
	private List<List<Map<String, Object>>> children = null;
	private TabHaoyouActivity c = null;
	AsyncImageLoader asyncImageLoader = new AsyncImageLoader(80, 80);
	View v;

	/**
	 * 好友exAdapter的构造函数
	 * 
	 * @param groups
	 * @param children
	 * @param c
	 */
	public HaoyouListExAdapter(Context c) {
		super();
		this.c = (TabHaoyouActivity) c;
	}

	public void setGroups(ArrayList<String> groups) {
		this.groups = groups;
	}

	public void setChildren(List<List<Map<String, Object>>> children) {
		this.children = children;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return children.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// Log.i("getChildView", "called....");
		v = LayoutInflater.from(c).inflate(R.layout.friend_list, null);
		TextView title = (TextView) v.findViewById(R.id.title);
		TextView name = (TextView) v.findViewById(R.id.name);
		TextView place = (TextView) v.findViewById(R.id.place);
		TextView content = (TextView) v.findViewById(R.id.content);
		TextView time = (TextView) v.findViewById(R.id.time);
		ImageView icon = (ImageView) v.findViewById(R.id.icon);
		ImageView message = (ImageView) v.findViewById(R.id.message);
		final int childPos = childPosition;
//		if (groupPosition == 0) {
			message.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder ad = new AlertDialog.Builder(c
							.getParent());
					ad.setTitle("发送消息");
					final EditText msgEt = new EditText(c);
					ad.setView(msgEt);
					ad.setPositiveButton("发送",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									new SendMsgTask().execute(
											(String) children.get(0)
													.get(childPos)
													.get("friendId"), msgEt
													.getText().toString());
								}
							});
					ad.show();
				}
			});
//		} 
			/**
			 * 我的关注不可发消息
			 */
			if (TabHaoyouActivity.gid .equals("1")) {
			message.setVisibility(View.GONE);
			message.setClickable(false);
		}
		if (children == null) {
			Log.e("children", "null");
		} else {
			Log.e("title",
					children.get(groupPosition).get(childPosition).get("title")
							.toString());
		}
		title.setText(children.get(groupPosition).get(childPosition)
				.get("title").toString());
		name.setText(children.get(groupPosition).get(childPosition)
				.get("nickname").toString());
		time.setText(children.get(groupPosition).get(childPosition)
				.get("pubDate").toString());
		content.setText(children.get(groupPosition).get(childPosition)
				.get("content").toString());
		place.setText(children.get(groupPosition).get(childPosition)
				.get("location").toString());
		/**
		 * 头像处理
		 */
		icon.setTag(children.get(groupPosition).get(childPosition)
				.get("avatar").toString());
		Log.e("fuck!!!",
				children.get(groupPosition).get(childPosition).get("avatar")
						.toString());
		Drawable cacheImg = asyncImageLoader.loadDrawable(
				children.get(groupPosition).get(childPosition).get("avatar")
						.toString(), new AsyncImageLoader.ImageCallback() {
					@Override
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl) {
						ImageView ivByTag = (ImageView) v
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
		icon.setImageResource(R.drawable.default_avatar);
		if (cacheImg != null) {
			icon.setImageDrawable(cacheImg);
		}
		return v;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (children == null) {
			Log.i("children", "null");
			return 0;
		}
		if (children.get(groupPosition) == null) {
			return 0;
		}
		return children.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		TextView textView = new TextView(c);
		textView.setTextSize(30);
		textView.setTextColor(Color.BLACK);
		textView.setText("    " + getGroup(groupPosition).toString());
		// textView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // TODO Auto-generated method stub
		// c.requestDetail();
		// }
		// });
		return textView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private class SendMsgTask extends AsyncTask<String, Integer, String> {
		ProgressDialog dlg;
		private String sendResult;

		@Override
		protected void onPreExecute() {
			dlg = new ProgressDialog(c);
			dlg.setTitle("信息");
			dlg.setMessage("正在发送消息，请稍后...");
			dlg.setCancelable(false);
			dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dlg.setButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dlg.dismiss();
					cancel(true);

				}
			});
			// dlg.show();
		}

		@Override
		protected String doInBackground(String... params) {
			String friendId = params[0];
			String msgContent = params[1];
			try {
				URL url = new URL(AppKeys.SEND_MSG_TO_FRIEND_URL);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("content-type", "text/html");
				connection.setDoOutput(true);

				JSONArray array = new JSONArray();
				JSONObject jsonObject = new JSONObject();

				jsonObject.put("user_id", AppUtil.currentUserId);
				jsonObject.put("friend_id", friendId);
				jsonObject.put("content", msgContent);

				array.put(jsonObject);

				OutputStream os = connection.getOutputStream();
				os.write(array.toString().getBytes("utf-8"));

				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					InputStream is = connection.getInputStream();
					byte[] buf = new byte[2048];
					int count = is.read(buf, 0, buf.length);
					sendResult = new String(buf, 0, count);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			dlg.dismiss();
			if (Integer.parseInt(sendResult) == 1) {
				Toast.makeText(c, "发送成功", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(c, "发送失败", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			Toast.makeText(c, "取消发送", Toast.LENGTH_SHORT).show();
		}
	}

}
