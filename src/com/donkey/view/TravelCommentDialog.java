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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.donkey.R;
import com.donkey.util.AppKeys;
import com.donkey.util.AppUtil;

public class TravelCommentDialog extends Dialog{

	private Button yesButton;
	private Button cancelButton;
	private EditText commentContent;
	
	private String mCommentContent;
	private String travelId;
	private String result;
	
	private Context mContext;
	
	public TravelCommentDialog(Context context) {
		super(context);
		mContext = context;
	}

	public void setDisplay() {
		setContentView(R.layout.layout_travel_comment_dialog);
		commentContent = (EditText) findViewById(R.id.travel_comment_content);
		yesButton = (Button) findViewById(R.id.btn_yes_comment_travel);
		cancelButton = (Button) findViewById(R.id.btn_no_comment_travel);

		yesButton.setOnClickListener(listener);
		cancelButton.setOnClickListener(listener);
		setTitle("请输入你的评论：　　　　");

		show();
	}
	
	/**
	 * 评论和取消按钮的监听器
	 */
	View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_yes_comment_travel:
				mCommentContent = commentContent.getText().toString();
				travelId = "1";
				
				new commentTask().execute("begin");
				dismiss();
				break;
			case R.id.btn_no_comment_travel:
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
			
			try {
				URL url = new URL(AppKeys.ADD_TRAVEL_COMMENT_URL);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("content-type", "text/html");
				connection.setDoOutput(true);

				JSONArray array = new JSONArray();
				JSONObject jsonObject = new JSONObject();

				jsonObject.put("userid", AppUtil.currentUserId);
				jsonObject.put("travelid", travelId);
				jsonObject.put("tccontent", mCommentContent);
				jsonObject.put("tcpubdate", AppUtil.getCurrentTimeString());

				array.put(jsonObject);

				OutputStream os = connection.getOutputStream();
				os.write(array.toString().getBytes("utf-8"));

				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					InputStream is = connection.getInputStream();
					byte[] buf = new byte[2048];
					int count = is.read(buf, 0, buf.length);
					result = new String(buf, 0, count);
					Log.i("result_comment_travel", result);
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
			if (Integer.parseInt(TravelCommentDialog.this.result) == 0) {
				Toast.makeText(mContext, "评论失败！", Toast.LENGTH_SHORT).show();
			} else if(Integer.parseInt(TravelCommentDialog.this.result) == 1){
				Toast.makeText(mContext, "评论成功！", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			Toast.makeText(mContext, "取消评论！", Toast.LENGTH_SHORT).show();
		}
	}
}
