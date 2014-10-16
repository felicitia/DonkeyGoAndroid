package com.donkey.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.donkey.R;
import com.donkey.util.AppKeys;
import com.donkey.util.AppUtil;

public class TravelDetailActivity extends Activity {

	private ImageView iv_goback;
	private ImageView iv_share;

	private String title = "来自DonkeyGo";
	private String result;
	private String attentionResult;

	private String uid;
	private String travelId;
	private String travelTitle;
	private String travelPubdate;
	private String startTime;
	private String lasting;
	private String travelContent;
	private String travelLocation;
	private String username;
	private String travelCommentCount;
	private String travelFollowCount;

	private String tID;

	private PopupWindow travelWindow;

	private Button btn_comment;
	private Button btn_attention;
	private Button btn_share;
	
	private ProgressBar progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_travel_detail);

		initViews();
		initData();

		requestDetail();
	}

	/**
	 * 完成初始化工作
	 */
	private void initViews() {
		progressBar = (ProgressBar) findViewById(R.id.progressBar1);
		
		iv_goback = (ImageView) findViewById(R.id.goback_travel_detail);
		iv_share = (ImageView) findViewById(R.id.share_travel_detail);

		iv_goback.setOnClickListener(listener);
		iv_share.setOnClickListener(listener);
	}

	private void initData() {
		tID = getIntent().getStringExtra("travelid").toString();
	}

	private void initPopupWindow() {
		View view = getLayoutInflater().inflate(
				R.layout.layout_travel_detail_menu, null, false);
		travelWindow = new PopupWindow(view, 140, 300, true);
		travelWindow.setBackgroundDrawable(new BitmapDrawable());
		travelWindow.setOutsideTouchable(true);
		travelWindow.setFocusable(true);

		view.setFocusableInTouchMode(true);
		view.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU
						&& travelWindow.isShowing()) {
					travelWindow.dismiss();
					return true;
				}
				return false;
			}
		});

		btn_attention = (Button) view.findViewById(R.id.attention_travel);
		btn_comment = (Button) view.findViewById(R.id.comment_travel);
		btn_share = (Button) view.findViewById(R.id.share_travel);

		btn_attention.setOnClickListener(listener);
		btn_comment.setOnClickListener(listener);
		btn_share.setOnClickListener(listener);
	}

	private void getPopupWindow() {
		if (null != travelWindow) {
			travelWindow.dismiss();
			return;
		} else {
			initPopupWindow();
		}
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.goback_travel_detail:
				TravelDetailActivity.this.finish();
				break;
			case R.id.share_travel_detail:
				getPopupWindow();
				travelWindow.showAsDropDown(v);
				break;
			case R.id.attention_travel:
				travelWindow.dismiss();
				new attentionTask().execute("begin");
				break;
			case R.id.comment_travel:
				travelWindow.dismiss();
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				String tag = "travelComment";

				bundle.putString("tag", tag);
				bundle.putString("currentId", tID);

				intent.setClass(TravelDetailActivity.this,
						ShowCommentActivity.class);
				intent.putExtras(bundle);

				startActivityForResult(intent, 0);
				break;
			case R.id.share_travel:
				travelWindow.dismiss();
				shareContent("#DonkeyGo结伴游#" + username + "发起结伴游："
						+ travelTitle + "。" + travelContent);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 打开分享界面
	 * 
	 * @param 要分享的内容
	 */
	private void shareContent(String shareContent) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, title);
		intent.putExtra(Intent.EXTRA_TEXT, shareContent);
		startActivity(Intent.createChooser(intent, title));
	}

	/**
	 * get travel detail
	 */
	private void getTravelDetail() {
		URL url;
		try {
			url = new URL(AppKeys.TRAVEL_DETAIL_URL + tID);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = connection.getInputStream();
				byte[] buf = new byte[2048];
				int count = is.read(buf, 0, buf.length);
				result = new String(buf, 0, count);

				JSONArray array = new JSONArray(result);
				JSONObject jsonObject = array.getJSONObject(0);

				uid = jsonObject.getString("uid").toString();
				title = jsonObject.getString("traveltitle").toString();
				travelCommentCount = jsonObject.getString("travelcommentcount")
						.toString();
				travelContent = jsonObject.getString("travelcontent")
						.toString();
				travelFollowCount = jsonObject.getString("travelfollowcount")
						.toString();
				travelId = jsonObject.getString("travelid").toString();
				travelLocation = jsonObject.getString("travellocation")
						.toString();
				travelPubdate = jsonObject.getString("travelpubdate")
						.toString();
				travelTitle = jsonObject.getString("traveltitle").toString();
				username = jsonObject.getString("username").toString();
				startTime = jsonObject.getString("starttime").toString();
				lasting = jsonObject.getString("lasting").toString();

				handler.sendEmptyMessage(1);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void requestDetail() {
		Thread thread = new Thread() {
			@Override
			public void run() {
				handler.sendEmptyMessage(2);
				getTravelDetail();
			}
		};
		thread.start();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				refreshUI();
				break;
			case 2:
				progressBar.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * refresh ui
	 */
	private void refreshUI() {
		((TextView) findViewById(R.id.travel_title_detail))
				.setText(travelTitle);
		((TextView) findViewById(R.id.travel_user_detail)).setText(username);
		((TextView) findViewById(R.id.travel_location_detail))
				.setText(travelLocation);
		((TextView) findViewById(R.id.travel_date_detail)).setText(startTime);
		((TextView) findViewById(R.id.travel_lasting_detail))
				.setText(transferLasting(lasting));
		((TextView) findViewById(R.id.travel_content_detail))
				.setText(travelContent);
		((TextView) findViewById(R.id.travel_comment_count_detail))
				.setText(travelCommentCount);
		((TextView) findViewById(R.id.travel_follow_count_detail))
				.setText(travelFollowCount);
		
		progressBar.setVisibility(View.GONE);
	}

	/**
	 * 将lasting转换为具体天数
	 * 
	 * @param lasting
	 *            lasting id
	 * @return lasting time
	 */
	public static String transferLasting(String lasting) {
		if (lasting.equals("0")) {
			return "一天";
		} else if (lasting.equals("1")) {
			return "三到五天";
		} else if (lasting.equals("2")) {
			return "十五天";
		} else if (lasting.equals("3")) {
			return "一个月";
		}
		return null;
	}

	/**
	 * add travel attention
	 * 
	 * @author zouliping
	 * 
	 */
	private class attentionTask extends AsyncTask<String, Integer, String> {

		ProgressDialog dlg;

		@Override
		protected void onPreExecute() {
			dlg = new ProgressDialog(TravelDetailActivity.this);
			dlg.setTitle("关注");
			dlg.setMessage("正在此结伴游加为关注，请稍候");
			dlg.setCancelable(false);
			dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dlg.setButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dlg.dismiss();
					cancel(true);
				}
			});
			dlg.show();
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				URL url = new URL(AppKeys.ADD_TRAVEL_ATTENTION_URL);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("content-type", "text/html");
				connection.setDoOutput(true);

				JSONArray array = new JSONArray();
				JSONObject jsonObject = new JSONObject();

				jsonObject.put("userid", AppUtil.currentUserId);
				jsonObject.put("travelid", tID);

				array.put(jsonObject);

				OutputStream os = connection.getOutputStream();
				os.write(array.toString().getBytes("utf-8"));

				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					InputStream is = connection.getInputStream();
					byte[] buf = new byte[2048];
					int count = is.read(buf, 0, buf.length);
					attentionResult = new String(buf, 0, count);
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
			if (Integer.parseInt(attentionResult) == 1) {
				Toast.makeText(TravelDetailActivity.this, "关注成功",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(TravelDetailActivity.this, "关注失败",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			Toast.makeText(TravelDetailActivity.this, "取消关注",
					Toast.LENGTH_SHORT);
		}
	}
}
