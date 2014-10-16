package com.donkey.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.donkey.R;
import com.donkey.util.AppKeys;
import com.donkey.util.AppUtil;
import com.donkey.view.SendMsgDialog;

public class MemoryDetailActivity extends Activity {

	private ImageView iv_goback;
	private ImageView iv_more;

	private RadioButton rb_author_detail;
	private RadioButton rb_add_friend;
	private RadioButton rb_add_attention;
	private RadioButton rb_reply;

	private TextView tv_memory_title;
	private TextView tv_memory_content;
	private TextView tv_memory_pubDate;
	private TextView tv_memory_comment_count;
	private TextView tv_memory_avg_score;
	private TextView tv_landmark_count;
	private TextView tv_memory_location;
	
	private ProgressBar progressBar;

	private String title;
	private String content;
	private String pubDate;
	private int commentCount;
	private double avgScore;
	private String memId;
	private String location;
	private int lanmarkCount;
	private String authorId;

	private String result;
	private String mResult;

	private Thread thread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_memory_detail);

		init();
	}

	/**
	 * 初始化工作
	 */
	private void init() {
		progressBar = (ProgressBar) findViewById(R.id.progressBar2);
		
		iv_goback = (ImageView) findViewById(R.id.goback_memory_detail);
		iv_more = (ImageView) findViewById(R.id.more_memory_detail);

		iv_goback.setOnClickListener(listener);
		iv_more.setOnClickListener(listener);

		rb_add_attention = (RadioButton) findViewById(R.id.rb_add_attention);
		rb_add_friend = (RadioButton) findViewById(R.id.rb_add_friend);
		rb_author_detail = (RadioButton) findViewById(R.id.rb_author_detail);
		rb_reply = (RadioButton) findViewById(R.id.rb_reply);

		rb_add_attention.setOnClickListener(listener);
		rb_add_friend.setOnClickListener(listener);
		rb_author_detail.setOnClickListener(listener);
		rb_reply.setOnClickListener(listener);

		tv_memory_avg_score = (TextView) findViewById(R.id.memory_avg_score);
		tv_memory_comment_count = (TextView) findViewById(R.id.memory_comment_count);
		tv_memory_content = (TextView) findViewById(R.id.memory_content);
		tv_memory_pubDate = (TextView) findViewById(R.id.memory_date);
		tv_memory_title = (TextView) findViewById(R.id.memory_title);
		tv_landmark_count = (TextView) findViewById(R.id.memory_landmark_count);
		tv_memory_location = (TextView) findViewById(R.id.memory_location);
		tv_landmark_count = (TextView) findViewById(R.id.memory_landmark_count);

		thread = getDetailInstance();
		thread.start();
	}

	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.goback_memory_detail:
				MemoryDetailActivity.this.finish();
				break;
			case R.id.rb_add_attention:
				addAttention();
				break;
			case R.id.rb_add_friend:
				new SendMsgDialog(MemoryDetailActivity.this, authorId)
						.setDisplay();
				break;
			case R.id.rb_author_detail:
				Log.i("memoty detail", "start author detail");
				Intent intent_1 = new Intent(MemoryDetailActivity.this,
						AuthorDetailActivity.class);
				intent_1.putExtra("author_id", authorId);
				startActivity(intent_1);
				break;
			case R.id.rb_reply:
				Intent intent_2 = new Intent();
				Bundle bundle = new Bundle();
				String tag = "memoryComment";

				bundle.putString("tag", tag);
				bundle.putString("currentId", memId);

				intent_2.setClass(MemoryDetailActivity.this,
						ShowCommentActivity.class);
				intent_2.putExtras(bundle);

				startActivity(intent_2);
				break;
			case R.id.more_memory_detail: // 显示地标的按钮
				Intent intent_3 = new Intent(MemoryDetailActivity.this,
						DisplayLandmarkActivity.class);
				intent_3.putExtra("mem_id", memId);
				startActivity(intent_3);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * get memory detail
	 * 
	 * @author zouliping
	 */
	private void getMemoryDetail() {
		try {
			memId = getIntent().getStringExtra("mem_id");
			 Log.i("mem id", memId);
			URL url = new URL(AppKeys.MEMORY_DETAIL_URL + memId);
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

				title = jsonObject.getString("title").toString();
				content = jsonObject.getString("content").toString();
				pubDate = jsonObject.getString("pubdate").toString();
				commentCount = jsonObject.getInt("commentcount");
				avgScore = jsonObject.getDouble("avgscore");
				memId = jsonObject.getString("memoryid").toString();
				location = jsonObject.getString("location").toString();
				lanmarkCount = jsonObject.getInt("landmarkcount");
				authorId = jsonObject.getString("authorid").toString();
				
				handler.sendEmptyMessage(3);
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
	 * memory detail instance
	 * 
	 * @return thread
	 */
	private Thread getDetailInstance() {
		return new Thread() {
			@Override
			public void run() {
				handler.sendEmptyMessage(1);
				getMemoryDetail();
			}
		};
	}

	/**
	 * 更新UI
	 */
	private void refreshDetailUI() {
		tv_memory_avg_score.setText(avgScore + "");
		tv_memory_comment_count.setText(commentCount + "");
		tv_memory_content.setText(content);
		tv_memory_pubDate.setText(pubDate);
		tv_memory_title.setText(title);
		tv_landmark_count.setText(lanmarkCount + "");
		tv_memory_location.setText(location);
		
		progressBar.setVisibility(View.GONE);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				progressBar.setVisibility(View.VISIBLE);
				break;
			case 2:
				Log.e("mresult", mResult);
				if (Integer.parseInt(mResult) == 1) {
					Toast.makeText(MemoryDetailActivity.this, "加作者为关注成功",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(MemoryDetailActivity.this, "关注失败",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case 3:
				refreshDetailUI();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * add author attention
	 * 
	 * @author zouliping
	 */
	private void addAttention() {
		try {
			Thread thread = new Thread() {
				@Override
				public void run() {
					try {
						String userId = AppUtil.currentUserId + "";
						URL url = new URL(
								AppKeys.ADD_MEMORY_AUTHOR_ATTENTION_URL
										.replace("$userid", userId) + authorId);
						HttpURLConnection urlConnection = (HttpURLConnection) url
								.openConnection();
						urlConnection.connect();

						if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
							InputStream is = urlConnection.getInputStream();
							byte[] buf = new byte[2048];
							int count = is.read(buf, 0, buf.length);
							mResult = new String(buf, 0, count);

							handler.sendEmptyMessage(2);
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
			Toast.makeText(MemoryDetailActivity.this, "你已经关注了该作者!",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

}
