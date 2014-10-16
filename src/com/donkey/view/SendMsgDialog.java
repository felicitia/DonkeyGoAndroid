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
import android.widget.Toast;

import com.donkey.R;
import com.donkey.util.AppKeys;
import com.donkey.util.AppUtil;

public class SendMsgDialog extends Dialog {

	private Context mContext;

	private Button yesButton;
	private Button cancelButton;
	private EditText content;

	private String addFriendResult;
	private String authorId;
	private String msg;

	public SendMsgDialog(Context context,String authorId) {
		super(context);
		this.mContext = context;
		this.authorId = authorId; 
	}
	
	public void setDisplay(){
		setContentView(R.layout.layout_add_friend_message);
		content = (EditText) findViewById(R.id.msg_content_et);
		yesButton = (Button) findViewById(R.id.btn_yes_msg);
		cancelButton = (Button) findViewById(R.id.btn_no_msg);
		
		yesButton.setOnClickListener(listener);
		cancelButton.setOnClickListener(listener);
		
		setTitle("加好友");
		
		show();
	}
	
	/**
	 * 评论和取消按钮的监听器
	 */
	View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_yes_msg:
				msg = content.getText().toString();
				
				new addFriendTask().execute("begin");
				dismiss();
				break;
			case R.id.btn_no_msg:
				dismiss();
				break;
			default:
				break;
			}
		}
	};
	
	private class addFriendTask extends AsyncTask<String, Integer, String> {

		ProgressDialog dlg;

		@Override
		protected void onPreExecute() {
			dlg = new ProgressDialog(mContext);
			dlg.setTitle("加好友");
			dlg.setMessage("正在加好友，请稍候");
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
				URL url = new URL(AppKeys.ADD_FRIEND_URL);
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod("POST");
				urlConnection.setRequestProperty("content-type", "text/html");
				urlConnection.setDoOutput(true);

				JSONArray array = new JSONArray();
				JSONObject jsonObject = new JSONObject();

				jsonObject.put("uid", AppUtil.currentUserId);
				jsonObject.put("authorid", authorId);
				jsonObject.put("message", msg);
				jsonObject.put("time", AppUtil.getCurrentTimeString());

				array.put(jsonObject);

				OutputStream os = urlConnection.getOutputStream();
				os.write(array.toString().getBytes("utf-8"));

				if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					InputStream is = urlConnection.getInputStream();
					byte[] buf = new byte[2048];
					int count = is.read(buf, 0, buf.length);
					addFriendResult = new String(buf, 0, count);
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
			if (Integer.parseInt(addFriendResult) == 0) {
				Toast.makeText(mContext, "加好友失败", Toast.LENGTH_SHORT).show();
			} else if (Integer.parseInt(addFriendResult) == 1) {
				Toast.makeText(mContext, "加好友成功", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			Toast.makeText(mContext, "取消", Toast.LENGTH_SHORT);
		}
	}
}
