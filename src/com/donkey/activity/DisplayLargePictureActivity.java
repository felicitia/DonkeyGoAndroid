package com.donkey.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.donkey.R;
import com.donkey.util.ImageUtil;

public class DisplayLargePictureActivity extends Activity {

	private ImageView pic;
	private RelativeLayout layout;
	private ProgressBar progressBar;

	private String imgUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setTheme(R.style.translucent);
		setContentView(R.layout.layout_display_large_pic);

		initViews();
		initData();

		new GetLrgPicTask().execute(imgUrl);
	}

	private void initViews() {
		layout = (RelativeLayout) findViewById(R.id.large_layout);
		pic = (ImageView) findViewById(R.id.large_pic);
		progressBar = (ProgressBar) findViewById(R.id.large_pic_progressBar);
		// 返回按钮
		layout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DisplayLargePictureActivity.this.finish();
			}
		});
	}

	private void initData() {
		imgUrl = getIntent().getStringExtra("large_pic_url");
	}

	private class GetLrgPicTask extends AsyncTask<String, Integer, String> {
		
		private Bitmap bitmap;
		
		@Override
		protected void onPreExecute() {
			pic.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				URL imgUrl = new URL(params[0]);
				byte[] data = ImageUtil.readInputStream((InputStream) imgUrl
						.openStream());
				bitmap = ImageUtil.decodeSampledBitmapFromByteArray(
						data, 300, 300);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			pic.setImageBitmap(bitmap);
			pic.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
		}
	}
}
