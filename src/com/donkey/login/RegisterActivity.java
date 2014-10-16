package com.donkey.login;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.donkey.R;
import com.donkey.util.AppKeys;
import com.donkey.util.AppUtil;

public class RegisterActivity extends Activity {

	private EditText idEditText;
	private EditText passwordEditText;
	private EditText checkPasswordEditText;
	private EditText emailEditText;
	private EditText nicknameEditText;
	private RadioButton maleButton, femaleButton;
	private EditText workEditText;
	private EditText liveLocationEditText;
	private EditText circleEditText;
	private CheckBox eatBox;
	private CheckBox humanBox;
	private CheckBox naturalBox;

	private Button fileButton;
	private Button tijiaoButton;

	private Bitmap avatar;

	private boolean flag;

	private String result;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_register);

		flag = false;
		avatar = BitmapFactory.decodeResource(getResources(),
				R.drawable.default_avatar);

		initViews();

	}

	/**
	 * init some views，and set listener for them
	 */
	private void initViews() {
		nicknameEditText = (EditText) this.findViewById(R.id.nickname);
		tijiaoButton = (Button) this.findViewById(R.id.tijiaoButton);
		fileButton = (Button) this.findViewById(R.id.fileButton);
		idEditText = (EditText) this.findViewById(R.id.id);
		passwordEditText = (EditText) this.findViewById(R.id.password);
		checkPasswordEditText = (EditText) this
				.findViewById(R.id.checkpassword);
		emailEditText = (EditText) this.findViewById(R.id.email);
		maleButton = (RadioButton) this.findViewById(R.id.radio_male);
		femaleButton = (RadioButton) this.findViewById(R.id.radio_female);
		workEditText = (EditText) this.findViewById(R.id.work);
		liveLocationEditText = (EditText) this.findViewById(R.id.live_location);
		circleEditText = (EditText) this.findViewById(R.id.circle);
		eatBox = (CheckBox) this.findViewById(R.id.eatRadioButton);
		humanBox = (CheckBox) this.findViewById(R.id.humanRadioButton);
		naturalBox = (CheckBox) this.findViewById(R.id.naturalRadioButton);

		idEditText
				.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							// 此处为得到焦点时的处理内容
						} else {
							// 此处为失去焦点时的处理内容
							if (idEditText.getText().toString() != null) {
								if (idEditText.getText().toString()
										.equals("rukata"))
									Toast.makeText(RegisterActivity.this,
											"账号已存在!", Toast.LENGTH_SHORT)
											.show();
							}
						}
					}

				});
		checkPasswordEditText
				.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {

					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						if (hasFocus) {
							// 此处为得到焦点时的处理内容
						} else {
							// 此处为失去焦点时的处理内容
							if (passwordEditText.getText().toString() != null) {
								if (!(passwordEditText.getText().toString()
										.equals(checkPasswordEditText.getText()
												.toString()))) {
									Toast.makeText(RegisterActivity.this,
											"密码不一致!", Toast.LENGTH_SHORT)
											.show();
								}
							}
						}
					}
				});
		emailEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					// 此处为得到焦点时的处理内容
				} else {
					if (passwordEditText.getText().toString() != null) {
						String check = "\\w+@\\w+\\.(com\\.cn)|\\w+@\\w+\\.(com|cn)";
						Pattern regex = Pattern.compile(check);
						Matcher matcher = regex.matcher(emailEditText.getText()
								.toString());
						if (!matcher.matches()) {
							Toast.makeText(RegisterActivity.this, "邮箱格式不正确",
									Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
		});
		fileButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.addCategory(Intent.CATEGORY_OPENABLE);
				intent.setType("image/*");

				intent.putExtra("crop", "true");
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("outputX", 50);
				intent.putExtra("outputY", 50);
				intent.putExtra("return-data", true);

				startActivityForResult(intent, 0);
			}
		});

		// 提交按钮
		tijiaoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new RegisterTask().execute("begin");
			}
		});

		((ImageView) findViewById(R.id.go_back_register))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
	}

	/**
	 * AsyncTask used for register new common user
	 * 
	 * @author liushuai
	 * 
	 */
	private class RegisterTask extends AsyncTask<String, Integer, String> {

		ProgressDialog dlg;

		@Override
		protected void onPreExecute() {
			dlg = new ProgressDialog(RegisterActivity.this);
			dlg.setTitle("注册新用户");
			dlg.setMessage("正在提交您的信息，请稍后...");
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
				URL url = new URL(AppKeys.REGISTER_URL);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("content-type", "text/html");
				connection.setDoOutput(true);

				JSONArray array = new JSONArray();
				JSONObject jsonObject = new JSONObject();

				jsonObject.put("username", idEditText.getText().toString());
				jsonObject
						.put("userpwd", passwordEditText.getText().toString());
				jsonObject.put("email", emailEditText.getText().toString());

				String tmpNickName = nicknameEditText.getText().toString();
				jsonObject.put("nickname",
						tmpNickName == null || "".equals(tmpNickName) ? ""
								: tmpNickName);

				jsonObject.put("gender", maleButton.isChecked() ? 1 : 0);

				String tmpJob = workEditText.getText().toString();
				jsonObject.put("job", tmpJob == null || "".equals(tmpJob) ? ""
						: tmpJob);
				String tmpCircle = circleEditText.getText().toString();
				jsonObject.put("circle",
						tmpCircle == null || "".equals(tmpCircle) ? ""
								: tmpCircle);

				jsonObject.put("location", liveLocationEditText.getText()
						.toString());

				jsonObject.put("interest", getInterestInt());

				jsonObject.put("avatar", AppUtil.encodeBase64(avatar));

				array.put(jsonObject);

				OutputStream os = connection.getOutputStream();
				Log.e("register json", array.toString());
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
			if (Integer.parseInt(RegisterActivity.this.result) == -1) {
				Toast.makeText(RegisterActivity.this,
						"注册失败！",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RegisterActivity.this,
						"注册成功",
						Toast.LENGTH_SHORT).show();
				RegisterActivity.this.finish();
			}
		}

		@Override
		protected void onCancelled() {
			Toast.makeText(RegisterActivity.this, "取消登录", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * build a integer stands for the user's interest by the user's choice
	 * 
	 * @return
	 */
	private int getInterestInt() {
		int eat = 4;
		int human = 2;
		int natural = 1;
		int interest = 0;
		if (eatBox.isChecked()) {
			interest += eat;
		}
		if (humanBox.isChecked()) {
			interest += human;
		}
		if (naturalBox.isChecked()) {
			interest += natural;
		}
		return interest;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println(resultCode);
		Bitmap cameraBitmap = null;
		if(data != null)
			cameraBitmap = (Bitmap) data.getExtras().get("data");
		if(cameraBitmap != null)
			avatar = cameraBitmap;
		else
			cameraBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.default_avatar);
		((ImageView) findViewById(R.id.choose_avatar_iv))
		.setImageBitmap(cameraBitmap);
		super.onActivityResult(requestCode, resultCode, data);
	}
}
