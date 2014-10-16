package com.donkey.login;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.donkey.R;
import com.donkey.util.AppKeys;
import com.donkey.util.AppUtil;

public class CustomDialog extends Dialog {
	private Context context;

	private String result;
	private String loginId;
	private String loginName;

	private EditText etUsername;
	private EditText etPwd;

	public CustomDialog(Context context) {
		super(context);
		this.context = context;
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.layout_login);
		this.setTitle("用户登录");
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		TextView textView = (TextView) this.findViewById(R.id.register);
		Intent intent = new Intent();
		intent.setClass(this.context, RegisterActivity.class);
		if (this.getContext() != null) {
			new CustomTextView().setClickableTextView(this.getContext(),
					textView, "还没有账号？快来注册吧！", intent);
		} else {
			Log.e("LoginDialog", "dialog.getContext() is null");
		}

		etUsername = (EditText) findViewById(R.id.et_login_id);
		etPwd = (EditText) findViewById(R.id.et_login_pwd);

		((Button) findViewById(R.id.login_btn))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						loginId = etUsername.getText().toString();
						loginName = etPwd.getText().toString();
						new LoginTask().execute(loginId, loginName);
					}
				});
	}

	private class LoginTask extends AsyncTask<String, Integer, String> {

		ProgressDialog dlg;

		@Override
		protected void onPreExecute() {
			dlg = new ProgressDialog(context);
			dlg.setTitle("登录");
			dlg.setMessage("正在登录，请稍后...");
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
			String uname = params[0];
			String pwd = params[1];
			try {
				URL url = new URL(AppKeys.LOGIN_URL);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("content-type", "text/html");
				connection.setDoOutput(true);

				JSONArray array = new JSONArray();
				JSONObject jsonObject = new JSONObject();

				jsonObject.put("username", uname);
				jsonObject.put("userpwd", pwd);

				array.put(jsonObject);

				OutputStream os = connection.getOutputStream();
				os.write(array.toString().getBytes("utf-8"));

				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					InputStream is = connection.getInputStream();
					byte[] buf = new byte[2048];
					int count = is.read(buf, 0, buf.length);
					result = new String(buf, 0, count);
					Log.e("qqqqqqqq", result + "");
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
			if (Integer.parseInt(CustomDialog.this.result) == -1) {
				Toast.makeText(context, "登录失败了哇！", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, "恭喜，登录成功!", Toast.LENGTH_SHORT).show();
				CustomDialog.this.dismiss();
				AppUtil.currentUserId = Integer.parseInt(CustomDialog.this.result);
				AppUtil.haveLoggedin = true;
			}
		}

		@Override
		protected void onCancelled() {
			Toast.makeText(context, "取消登录", Toast.LENGTH_SHORT).show();
		}

		// /**
		// * 显示对话框
		// *
		// * @param message
		// */
		// private void showMsg(String message) {
		// new AlertDialog.Builder(context)
		// .setTitle("消息")
		// .setMessage(message)
		// .setNegativeButton("关闭",
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// dialog.dismiss();
		// }
		// }).show();
		// }

	}

	class CustomTextView {
		/**
		 * make textview a clickable textview<br>
		 * Note: make true the order of textList and intentList are mapped
		 * 
		 * @param context
		 * @param textView
		 * @param text
		 * @param intent
		 */
		public void setClickableTextView(Context context, TextView textView,
				String text, Intent intent) {
			try {
				SpannableStringBuilder builder = new SpannableStringBuilder(
						text);
				builder.setSpan(getClickableSpan(context, intent), 0,
						text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				textView.setText(builder);
				textView.setMovementMethod(LinkMovementMethod.getInstance());
			} catch (Exception ee) {
				Log.i("CustomTextView", ee.toString());
			}
		}

		public void setClickableTextView(Context context, TextView textView,
				List<String> textList, List<Intent> intentList) {
			if (textList == null || intentList == null) {
				return;
			}
			SpannableStringBuilder builder = new SpannableStringBuilder();
			int end = -1, length = -1;
			int size = textList.size();
			Intent intent;
			for (int i = 0; i < size; i++) {
				String text = textList.get(i);
				if (TextUtils.isEmpty(text)) {
					continue;
				}
				builder.append(textList.get(i));
				if ((intent = intentList.get(i)) != null) {
					end = builder.length();
					length = textList.get(i).length();
					builder.setSpan(getClickableSpan(context, intent), end
							- length, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				builder.append(" ");
			}
			textView.setText(builder);
			textView.setFocusable(true);
			textView.setMovementMethod(LinkMovementMethod.getInstance());
		}

		/**
		 * return a custom ClickableSpan
		 * 
		 * @param context
		 * @param intent
		 * @return
		 */
		public MyClickableSpan getClickableSpan(Context context, Intent intent) {
			return new MyClickableSpan(context, intent);
		}

	}

	class MyClickableSpan extends ClickableSpan {
		int color = -1;
		private Context context;
		private Intent intent;

		public MyClickableSpan(Context context, Intent intent) {
			this(-1, context, intent);
		}

		public MyClickableSpan(int color, Context context, Intent intent) {
			if (color != -1) {
				this.color = color;
			}
			this.context = context;
			this.intent = intent;
		}

		@Override
		public void onClick(View widget) {
			// TODO Auto-generated method stub
			context.startActivity(intent);
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			if (color == -1) {
				ds.setColor(ds.linkColor);
			} else {
				ds.setColor(color);
			}
			ds.setUnderlineText(false);
		}
	}
}