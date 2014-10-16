package com.donkey.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.donkey.R;
import com.donkey.util.AppKeys;
import com.donkey.util.AppUtil;

public class CommentDialog extends Dialog {

	private Button yesButton;
	private Button cancelButton;
	private EditText commentContent;
	private RatingBar ratingBar;

	private String mCommentContent;
	private String rank;
	private String memId;
	private String result;

	private Context mContext;

	public CommentDialog(Context context,String mId) {
		super(context);
		mContext = context;
		this.memId = mId;
	}

	public void setDisplay() {
		setContentView(R.layout.layout_comment_dialog);
		commentContent = (EditText) findViewById(R.id.comment_content_et);
		yesButton = (Button) findViewById(R.id.btn_yes_comment);
		cancelButton = (Button) findViewById(R.id.btn_no_comment);
		ratingBar = (RatingBar) findViewById(R.id.ratingbar);

		yesButton.setOnClickListener(listener);
		cancelButton.setOnClickListener(listener);
		ratingBar.setOnRatingBarChangeListener(ratingListener);
		setTitle("请输入你的评论并打分：　　　　");

		show();
	}

	/**
	 * 评论和取消按钮的监听器
	 */
	View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_yes_comment:
				mCommentContent = commentContent.getText().toString();
//				memId = "1";
				
				new commentTask().execute(mCommentContent,rank,memId);
				dismiss();
				break;
			case R.id.btn_no_comment:
				dismiss();
				break;
			default:
				break;
			}
		}
	};

	private class commentTask extends AsyncTask<String, Integer, String> {

		ProgressDialog dlg;

		@Override
		protected void onPreExecute() {
			dlg = new ProgressDialog(mContext);
			dlg.setTitle("评论");
			dlg.setMessage("正在发布评论，请稍等片刻！");
			dlg.setCancelable(false);
			dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dlg.setButton("取消", new OnClickListener() {

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
			String content = params[0];
			String mRank = params[1];
			String mId = params[2];
			
			try {
				URL url = new URL(AppKeys.MEMORY_COMMENT_URL);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("content-type", "text/html");
				connection.setDoOutput(true);

				JSONArray array = new JSONArray();
				JSONObject jsonObject = new JSONObject();

				jsonObject.put("userid", AppUtil.currentUserId);
				jsonObject.put("memoryid", mId);
				jsonObject.put("mccontent", content);
				jsonObject.put("mcrank", mRank);
				jsonObject.put("mcpubdate", AppUtil.getCurrentTimeString());

				array.put(jsonObject);

				OutputStream os = connection.getOutputStream();
				os.write(array.toString().getBytes("utf-8"));

				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					InputStream is = connection.getInputStream();
					byte[] buf = new byte[2048];
					int count = is.read(buf, 0, buf.length);
					result = new String(buf, 0, count);
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
			if (Integer.parseInt(CommentDialog.this.result) == 1) {
				Toast.makeText(mContext, "评论成功！", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, "评论失败！", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			Toast.makeText(mContext, "取消评论！", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * RatingBar的监听器
	 */
	private RatingBar.OnRatingBarChangeListener ratingListener = new RatingBar.OnRatingBarChangeListener() {

		@Override
		public void onRatingChanged(RatingBar ratingBar, float rating,
				boolean fromUser) {
			rank = String.valueOf(rating);
			Toast.makeText(getContext(), "rating" + String.valueOf(rating),
					Toast.LENGTH_SHORT).show();
		}
	};
}
