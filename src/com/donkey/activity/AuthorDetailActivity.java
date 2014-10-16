package com.donkey.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.donkey.R;
import com.donkey.util.AppKeys;
import com.donkey.util.ImageUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AuthorDetailActivity extends Activity {

	private String authorId;
	private ProgressBar progressBar;
	private JSONObject resultJS;

	private TextView tv_nickName;
	private TextView tv_gender;
	private TextView tv_email;
	private TextView tv_work;
	private TextView tv_location;
	private TextView tv_circle;
	private TextView tv_interest;
	private ImageView iv_avatar;

	private Bitmap avatarBitmap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_author_detail);

		initData();
		initView();

		new getAuthorDetailTask().execute("begin");
	}

	private void initData() {
		authorId = getIntent().getStringExtra("author_id");
	}

	private void initView() {
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		tv_nickName = (TextView) findViewById(R.id.tv_nickname);
		tv_gender = (TextView) findViewById(R.id.tv_gender);
		tv_email = (TextView) findViewById(R.id.tv_email);
		tv_work = (TextView) findViewById(R.id.tv_work);
		tv_location = (TextView) findViewById(R.id.tv_location);
		tv_circle = (TextView) findViewById(R.id.tv_circle);
		tv_interest = (TextView) findViewById(R.id.tv_interest);
		iv_avatar = (ImageView) findViewById(R.id.detail_avatar);

		((ImageView) findViewById(R.id.goback_author_detail))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						AuthorDetailActivity.this.finish();
					}
				});
	}

	private class getAuthorDetailTask extends
			AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				URL url = new URL(AppKeys.GET_AUTHOR_DETAIL + authorId);
				Log.i("authorid", authorId);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("content-type", "text/html");
				connection.setDoOutput(true);

				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					InputStream is = connection.getInputStream();
					byte[] buf = new byte[2048];
					int count = is.read(buf, 0, buf.length);
					Log.i("resultString", new String(buf, 0, count));
					resultJS = new JSONObject(new String(buf, 0, count));
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
			try {
				tv_nickName.setText(resultJS.getString("nickname"));
				tv_gender
						.setText(resultJS.getString("gender").equals("0") ? "♂"
								: "♀");
				tv_email.setText(resultJS.getString("email"));
				tv_work.setText(resultJS.getString("work"));
				tv_location.setText(resultJS.getString("location"));
				tv_circle.setText(resultJS.getString("circle"));
				tv_interest.setText(resultJS.getString("interest"));

				// 启动新线程获取头像
				new Thread(new Runnable() {
					@Override
					public void run() {
						avatarBitmap = getAvatarFromServer();
						handler.sendEmptyMessage(0);
					}
				}).start();

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			if (message.what == 0) {
				progressBar.setVisibility(View.GONE);
				if (avatarBitmap != null) {
					iv_avatar.setImageBitmap(avatarBitmap);
					Log.i("AuthorDetailActivity", "set avatar success");
				}
			}
		}
	};

	/**
	 * get the avatar of the user
	 * 
	 * @return
	 */
	private Bitmap getAvatarFromServer() {
		try {
			Log.i("AuthorDetailActivity", "get avatar from server");
			URL avatarUrl = new URL(resultJS.getString("avatar"));
			byte[] data = ImageUtil.readInputStream((InputStream) avatarUrl
					.openStream());
			Bitmap bitmap = ImageUtil.decodeSampledBitmapFromByteArray(data,
					50, 50);
			return bitmap;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
