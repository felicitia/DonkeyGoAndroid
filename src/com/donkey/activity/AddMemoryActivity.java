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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.donkey.R;
import com.donkey.datastorage.DBManager;
import com.donkey.entity.Memory;
import com.donkey.entity.XmlMemoryItem;
import com.donkey.util.AppKeys;
import com.donkey.util.AppUtil;

public class AddMemoryActivity extends Activity {

	// private Spinner provinceSpinner;
	// private Spinner citySpinner;
	private EditText etLocation;

	private EditText etMemoryTitle;
	private EditText etMemoryContent;

	private RadioButton rbPubMemory;
	private RadioButton rbSaveMemory;
	private RadioButton rbAddLocation;
	private RadioButton rbViewLocation;

	private DBManager dbManager;

	private String flag;
	private XmlMemoryItem item;

	private String result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_add_memory);

		dbManager = new DBManager(AddMemoryActivity.this);

		initViews();
		initData();
	}

	private void initViews() {
		// title content
		etMemoryTitle = (EditText) findViewById(R.id.add_memory_title_et);
		// memory content
		etMemoryContent = (EditText) findViewById(R.id.add_memory_content);

		// 初始化spinner
		// provinceSpinner = (Spinner) findViewById(R.id.spinner_province);
		// citySpinner = (Spinner) findViewById(R.id.spinner_city);
		// ArrayAdapter<CharSequence> provinceAdapter = ArrayAdapter
		// .createFromResource(AddMemoryActivity.this,
		// R.array.provence_name,
		// android.R.layout.simple_spinner_item);
		// provinceAdapter
		// .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// provinceSpinner.setAdapter(provinceAdapter);
		// provinceSpinner.setOnItemSelectedListener(new
		// OnItemSelectedListener() {
		// @Override
		// public void onItemSelected(AdapterView<?> parent, View view,
		// int position, long id) {
		// Spinner s = (Spinner) parent;
		// String pro = (String) s.getItemAtPosition(position);
		// ArrayAdapter<CharSequence> cityAdapter = null;
		// // 处理省的市的显示
		// if (pro.equals("内蒙古")) {
		// cityAdapter = ArrayAdapter.createFromResource(
		// AddMemoryActivity.this,
		// R.array.neimenggu_city_name,
		// android.R.layout.simple_spinner_item);
		// } else if (pro.equals("直辖市")) {
		// cityAdapter = ArrayAdapter.createFromResource(
		// AddMemoryActivity.this, R.array.zhixiashi_name,
		// android.R.layout.simple_spinner_item);
		// } else if (pro.equals("福建")) {
		// cityAdapter = ArrayAdapter.createFromResource(
		// AddMemoryActivity.this, R.array.fujian_city_name,
		// android.R.layout.simple_spinner_item);
		// } else if (pro.equals("浙江")) {
		// cityAdapter = ArrayAdapter.createFromResource(
		// AddMemoryActivity.this, R.array.zhejiang_city_name,
		// android.R.layout.simple_spinner_item);
		// } else if (pro.equals("河南")) {
		// cityAdapter = ArrayAdapter.createFromResource(
		// AddMemoryActivity.this, R.array.henan_city_name,
		// android.R.layout.simple_spinner_item);
		// }
		// cityAdapter
		// .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// citySpinner.setAdapter(cityAdapter);
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> arg0) {
		// }
		// });

		etLocation = (EditText) findViewById(R.id.add_memory_location_et);

		// 返回按钮
		ImageView iv_back = (ImageView) findViewById(R.id.go_back_add_meomry);
		iv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AddMemoryActivity.this.finish();
			}
		});

		// save memory on local sqlite
		rbSaveMemory = (RadioButton) findViewById(R.id.save_memory);
		rbAddLocation = (RadioButton) findViewById(R.id.add_location);
		rbPubMemory = (RadioButton) findViewById(R.id.pub_memory);
		rbViewLocation = (RadioButton) findViewById(R.id.view_location);

		rbSaveMemory.setOnClickListener(listener);
		rbAddLocation.setOnClickListener(listener);
		rbPubMemory.setOnClickListener(listener);
		rbViewLocation.setOnClickListener(listener);
	}

	private void initData() {
		flag = getIntent().getStringExtra("flag");
		if ("LocalMemory".equals(flag)) {
			item = (XmlMemoryItem) getIntent().getSerializableExtra(
					"memoryItem");
			etMemoryTitle.setText(item.getTitle());
			etMemoryContent.setText(item.getContent());
			etLocation.setText(item.getLocation());
			if (dbManager.getLandmarkCount() == 0) {
				Toast.makeText(AddMemoryActivity.this, "本记忆无地标",
						Toast.LENGTH_SHORT).show();
			}
		} else if ("AddMemory".equals(flag)) {

		}
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.save_memory:
				Memory m = new Memory();
				m.setMemoryUserId(AppUtil.currentUserId);
				m.setMemoryTitle(etMemoryTitle.getText().toString());
				m.setMemoryContent(etMemoryContent.getText().toString());
				// m.setMemoryLocation(provinceSpinner.getSelectedItem()
				// .toString()
				// + " "
				// + citySpinner.getSelectedItem().toString());
				m.setMemoryLocation(etLocation.getText().toString());
				m.setMemoryLastModifyTime(AppUtil.getCurrentTimeString());
				dbManager.insertOneMemory(m);
				AddMemoryActivity.this.finish();
				Toast.makeText(AddMemoryActivity.this, "记忆创建成功",
						Toast.LENGTH_SHORT).show();
				break;
			case R.id.view_location:
				Intent intent = new Intent();
				intent.setClass(AddMemoryActivity.this,
						ShowLandmarkActivity.class);
				startActivityForResult(intent, 0);
				break;
			case R.id.add_location:
				Intent i = new Intent();
				i.setClass(AddMemoryActivity.this, AddLocationActivity.class);
				startActivityForResult(i, 0);
				break;
			case R.id.pub_memory:
				new addMemoryTask().execute("begin");
				break;
			default:
				break;
			}

		}
	};

	/**
	 * pub memory
	 * 
	 * @author zouliping
	 */
	private void pubMemory() {
		try {
			URL url = new URL(AppKeys.ADD_MEMORY_URL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("content-type", "text/html");
			connection.setDoOutput(true);

			JSONArray array = new JSONArray();
			JSONObject jsonObject = new JSONObject();

			jsonObject.put("userid", AppUtil.currentUserId);
			jsonObject.put("title", etMemoryTitle.getText().toString());
			jsonObject.put("pubtime", AppUtil.getCurrentTimeString());
			jsonObject.put("content", etMemoryContent.getText().toString());
			// jsonObject.put("location", provinceSpinner.getSelectedItem()
			// .toString()
			// + " "
			// + citySpinner.getSelectedItem().toString());
			jsonObject.put("location", etLocation.getText().toString());

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
	}

	/**
	 * add memory task
	 * 
	 * @author zouliping
	 * 
	 */
	private class addMemoryTask extends AsyncTask<String, Integer, String> {

		ProgressDialog dlg;

		@Override
		protected void onPreExecute() {
			dlg = new ProgressDialog(AddMemoryActivity.this);
			dlg.setTitle("发布记忆");
			dlg.setMessage("正在发布记忆，请稍候");
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
			pubMemory();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			dlg.dismiss();
			try {
				if (Integer.parseInt(AddMemoryActivity.this.result) == 0) {
					Toast.makeText(AddMemoryActivity.this, "发布失败",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(AddMemoryActivity.this, "成功发布",
							Toast.LENGTH_SHORT).show();
					AddMemoryActivity.this.finish();
				}
			} catch (NumberFormatException e) {
				Toast.makeText(AddMemoryActivity.this, "发布失败",
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onCancelled() {
			Toast.makeText(AddMemoryActivity.this, "取消发布", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dbManager != null) {
			dbManager.closeDB();
		}
	}
}
