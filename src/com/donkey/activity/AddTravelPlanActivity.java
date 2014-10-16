package com.donkey.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.donkey.R;
import com.donkey.util.AppKeys;
import com.donkey.util.AppUtil;

public class AddTravelPlanActivity extends Activity {

	private Spinner lastingSpinner;

	private ImageView iv_goback;
	private EditText time;
	private EditText title;
	private EditText detail;
	private EditText city;
	private Button submit;

	private String selectedTime;
	private String setTime;
	private static String currentTime;
	private static String current;

	private String result;

	private int mYear;
	private int mMonth;
	private int mDay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_add_travel_plan);

		init();
	}

	/**
	 * 完成初始化工作
	 */
	private void init() {
		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		time = (EditText) findViewById(R.id.et);
		setTime = String.valueOf(mYear) + "年" + (mMonth + 1) + "月" + mDay + "日";
		selectedTime = String.valueOf(mYear) + "-" + (mMonth + 1) + "-" + mDay;
		currentTime = selectedTime;
		current = setTime;
		time.setText(setTime);
		time.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new DatePickerDialog(AddTravelPlanActivity.this,
						mDateSetListener, mYear, mMonth, mDay).show();
			}
		});
		time.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus == true) {
					hideIM(v);
					new DatePickerDialog(AddTravelPlanActivity.this,
							mDateSetListener, mYear, mMonth, mDay).show();
				}
			}
		});

		iv_goback = (ImageView) findViewById(R.id.go_back_add_meomry);

		iv_goback.setOnClickListener(listener);

		lastingSpinner = (Spinner) findViewById(R.id.spinner_lasting);
		ArrayAdapter<CharSequence> lastingAdapter = ArrayAdapter
				.createFromResource(AddTravelPlanActivity.this,
						R.array.lasting_day,
						android.R.layout.simple_spinner_item);
		lastingAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		lastingSpinner.setAdapter(lastingAdapter);
		lastingSpinner.setPrompt("结伴游历时");

		submit = (Button) findViewById(R.id.submit_travel_plan);
		submit.setOnClickListener(listener);
		title = (EditText) findViewById(R.id.travel_title);
		detail = (EditText) findViewById(R.id.travel_detail);
		city = (EditText) findViewById(R.id.travel_city);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			System.exit(0);
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 选择日期
	 */
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;

			selectedTime = String.valueOf(mYear) + "-" + (mMonth + 1) + "-"
					+ mDay;
			setTime = String.valueOf(mYear) + "年" + (mMonth + 1) + "月" + mDay
					+ "日";
			time.setText(setTime);

			if (!AppUtil.isDateAfter(selectedTime, currentTime)) {

			} else {
				time.setText(current);
				Toast.makeText(AddTravelPlanActivity.this, "请选择晚于今天的日期",
						Toast.LENGTH_SHORT).show();
			}
		}
	};

	/**
	 * 隐藏输入法
	 * 
	 * @param edt
	 */
	protected void hideIM(View edt) {
		try {
			InputMethodManager im = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
			IBinder windowToken = edt.getWindowToken();
			if (windowToken != null) {
				im.hideSoftInputFromInputMethod(windowToken, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.go_back_add_meomry:
				AddTravelPlanActivity.this.finish();
				break;
			case R.id.submit_travel_plan:
				new addTravelPlanTask().execute("begin");
				break;
			default:
				break;
			}
		}
	};

	/**
	 * pub travel plan
	 * 
	 * @author zouliping
	 */
	private void pubTravel() {
		try {
			URL url = new URL(AppKeys.ADD_TRAVEL_PLAN);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("content-type", "text/html");
			connection.setDoOutput(true);

			JSONArray array = new JSONArray();
			JSONObject jsonObject = new JSONObject();

			jsonObject.put("uid", AppUtil.currentUserId);
			jsonObject.put("traveltitle", title.getText().toString());
			jsonObject.put("starttime", selectedTime);
			Log.i("lasting", lastingSpinner.getSelectedItemId()+"");
			jsonObject.put("lasting", lastingSpinner.getSelectedItemId() + "");
			jsonObject.put("travelpubdate", AppUtil.getCurrentTimeString());
			jsonObject.put("travelcontent", detail.getText().toString());
			jsonObject.put("travellocation", city.getText().toString());

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
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * add travel plan task
	 * 
	 * @author zouliping
	 * 
	 */
	private class addTravelPlanTask extends AsyncTask<String, Integer, String> {

		ProgressDialog dlg;

		@Override
		protected void onPreExecute() {
			dlg = new ProgressDialog(AddTravelPlanActivity.this);
			dlg.setTitle("发布结伴游");
			dlg.setMessage("正在发布结伴游，请稍候");
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
			pubTravel();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			dlg.dismiss();
			if (Integer.parseInt(AddTravelPlanActivity.this.result) == 0) {
				Toast.makeText(AddTravelPlanActivity.this, "发布失败",
						Toast.LENGTH_SHORT).show();
			} else if(Integer.parseInt(AddTravelPlanActivity.this.result) == 1){
				Toast.makeText(AddTravelPlanActivity.this, "成功发布",
						Toast.LENGTH_SHORT).show();
				AddTravelPlanActivity.this.finish();
			}
		}

		@Override
		protected void onCancelled() {
			Toast.makeText(AddTravelPlanActivity.this, "取消发布",
					Toast.LENGTH_SHORT).show();
		}
	}
}
