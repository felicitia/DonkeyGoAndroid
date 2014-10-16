package com.donkey.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.donkey.R;
import com.donkey.entity.XmlMemoryItem;
import com.donkey.httpclient.XmlMemoryItemHandler;
import com.donkey.util.AppKeys;
import com.donkey.util.AppUtil;
import com.donkey.util.NetWorkState;

public class AddLocationActivity extends Activity {

	private Spinner spinner;
	private Button btn_add_location;
	private Button addMediaButton;
	private EditText etLandmarkTitle;
	private Spinner refMemSpinner;
	private ImageView iv_media;

	private ArrayAdapter<CharSequence> adapter;
	private ArrayAdapter<String> refMemAdapter;

	private Bitmap bitmap;

	private String memId = "-1";
	private String longtitude = "23.33";
	private String latitude = "42.55";
	private String meidaType = "1";

	private String result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_add_loaction);

		new SelectMemoryTask().execute("begin");
		// new Thread(new Runnable() {
		// @Override
		// public void run() {
		// Log.i("AddLocationActivity", "begin get current location");
		// LocationManager lm = (LocationManager)
		// getSystemService(LOCATION_SERVICE);
		// Location currentLocation = lm
		// .getLastKnownLocation(LocationManager.GPS_PROVIDER);
		// if (currentLocation == null) {
		// currentLocation = lm
		// .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		// }
		// Log.i("current location",
		// "longtitude:" + currentLocation.getLongitude()
		// + " latitude:" + currentLocation.getLatitude());
		// Message msg = handler.obtainMessage(0, currentLocation);
		// handler.sendMessage(msg);
		// }
		// }).start();
		
		bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.default_avatar);

		initViews();
	}

	// Handler handler = new Handler() {
	// @Override
	// public void handleMessage(Message message) {
	// Location curLocation = (Location) message.obj;
	// longtitude = String.valueOf(curLocation.getLongitude());
	// latitude = String.valueOf(curLocation.getLatitude());
	// Log.i("location", "longtitude:" + longtitude + " latitude:"
	// + latitude);
	// }
	// };

	private void initViews() {
		etLandmarkTitle = (EditText) findViewById(R.id.landmark_title);

		// 初始化spinner，并为其指定数组资源
		spinner = (Spinner) findViewById(R.id.loaction_spinner);
		adapter = ArrayAdapter.createFromResource(AddLocationActivity.this,
				R.array.loction_type, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setPrompt("地标类型");

		refMemSpinner = (Spinner) findViewById(R.id.refer_memory_content);
		List<String> hintArray = new ArrayList<String>();
		hintArray.add("点击选择关联记忆");
		refMemAdapter = new ArrayAdapter<String>(AddLocationActivity.this,
				android.R.layout.simple_spinner_item, hintArray);

		iv_media = (ImageView) findViewById(R.id.pic_media);
		// iv_media.setImageBitmap(AppUtil.decodeFromBase64(""));

		// 返回按钮
		ImageView iv_goback = (ImageView) findViewById(R.id.go_back_add_location);
		iv_goback.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AddLocationActivity.this.finish();
			}
		});

		btn_add_location = (Button) findViewById(R.id.submit_add_location);
		btn_add_location.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!"-1".equals(memId)) {
					new AddLocationTask().execute("begin");
				} else {
					Toast.makeText(AddLocationActivity.this, "您没有发布过任何记忆",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		addMediaButton = (Button) findViewById(R.id.add_media_location);
		addMediaButton.setOnClickListener(new View.OnClickListener() {
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
	}

//	/**
//	 * 添加图片
//	 */
//	private void takePhoto() {
//		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//		AppUtil.MakeDir(AppKeys.PIC_FOLDER_NAME);
//
//		File photo = new File(AppKeys.PIC_FOLDER_NAME, new Date().getTime()
//				+ ".jpg");
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
//		imageUri = Uri.fromFile(photo);
//		if (imageUri != null) {
//			Log.i("image uri not null", "not null");
//			startActivityForResult(intent, TAKE_PICTURE);
//		}
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		switch (requestCode) {
//		case TAKE_PICTURE:
//			if (resultCode == Activity.RESULT_OK) {
//				Uri selectedImage = imageUri;
//				if(selectedImage == null)
//					Log.e("selected image", "is null");
//				ContentResolver cr = getContentResolver();
//				if (cr != null) {
//					cr.notifyChange(selectedImage, null);
//					try {
//						bitmap = android.provider.MediaStore.Images.Media
//								.getBitmap(cr, selectedImage);
//
//						iv_media.setImageBitmap(bitmap);
//						Toast.makeText(this, selectedImage.toString(),
//								Toast.LENGTH_LONG).show();
//					} catch (Exception e) {
//						Toast.makeText(this, "Failed to load",
//								Toast.LENGTH_SHORT).show();
//						Log.e("Camera", e.toString());
//					}
//				}
//			}
//		}
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Bitmap cameraBitmap = null;
		if(data != null)
			cameraBitmap = (Bitmap) data.getExtras().get("data");
		if(cameraBitmap != null)
			bitmap = cameraBitmap;
		else
			cameraBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.default_avatar);
		((ImageView) findViewById(R.id.pic_media))
		.setImageBitmap(cameraBitmap);
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	/**
	 * add location task
	 * 
	 * @author zouliping
	 * 
	 */
	private class AddLocationTask extends AsyncTask<String, Integer, String> {

		ProgressDialog dlg;

		@Override
		protected void onPreExecute() {
			dlg = new ProgressDialog(AddLocationActivity.this);
			dlg.setTitle("添加地标");
			dlg.setMessage("正在添加地标，请稍候");
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
				URL url = new URL(AppKeys.ADD_LOCATION_URL);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("content-type", "text/html");
				connection.setDoOutput(true);

				JSONArray array = new JSONArray();
				JSONObject jsonObject = new JSONObject();

				jsonObject.put("memoryid", memId);
				jsonObject.put("landmarkname", etLandmarkTitle.getText()
						.toString());
				jsonObject
						.put("landmarktype", spinner.getSelectedItemId() + "");
				jsonObject.put("mediatype", meidaType);
				jsonObject.put("mediapath", AppUtil.encodeBase64(bitmap));
				jsonObject.put("longtitude", longtitude);
				jsonObject.put("latitude", latitude);

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
			if (Integer.parseInt(AddLocationActivity.this.result) == -1) {
				Toast.makeText(AddLocationActivity.this, "添加失败！",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(AddLocationActivity.this, "添加成功！",
						Toast.LENGTH_SHORT).show();
				AddLocationActivity.this.finish();
			}
		}

		@Override
		protected void onCancelled() {
			Toast.makeText(AddLocationActivity.this, "取消发布", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * 拉取用户已发布的记忆让用户选择的task
	 * 
	 * @author liushuai
	 * 
	 */
	private class SelectMemoryTask extends AsyncTask<String, Integer, String> {

		ProgressDialog dlg;
		List<XmlMemoryItem> tmpMemoryList;
		XmlMemoryItemHandler xmlHandler = new XmlMemoryItemHandler();
		boolean flag = false;

		@Override
		protected void onPreExecute() {
			if (!NetWorkState.isNetworkAvailable(AddLocationActivity.this)) { // 判断网络连接情况
				Toast.makeText(AddLocationActivity.this, "网络连接不可用，请检查您的网络连接",
						Toast.LENGTH_SHORT).show();
				cancel(true);
				return;
			}
			dlg = new ProgressDialog(AddLocationActivity.this);
			dlg.setTitle("信息");
			dlg.setMessage("正在查询您发布过的记忆，请稍候");
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
				URL url = new URL(AppKeys.MY_MEMORY_URL + AppUtil.currentUserId);
				// Log.e("fuck!!!!", AppKeys.MY_MEMORY_URL +
				// AppUtil.currentUserId);
				URLConnection con = url.openConnection();
				con.connect();
				InputStream input = con.getInputStream();
				tmpMemoryList = xmlHandler.getMemoryItems(input);
				Log.e("......", tmpMemoryList.size() + "");

				if (tmpMemoryList.size() == 0) {
					flag = false; // 用户没有发布过任何记忆
				} else {
					flag = true; // 成功请求我的记忆
				}
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
			dlg.dismiss();
			if (flag) {
				List<String> myMemory = new ArrayList<String>();
				for (XmlMemoryItem item : tmpMemoryList) {
					myMemory.add(item.getTitle());
				}
				refMemAdapter = new ArrayAdapter<String>(
						AddLocationActivity.this,
						android.R.layout.simple_spinner_item, myMemory);
				refMemSpinner.setAdapter(refMemAdapter);
				refMemSpinner
						.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> parent,
									View view, int position, long id) {
								memId = tmpMemoryList.get(position)
										.getMemoryId();
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {

							}
						});
			} else {
				Toast.makeText(AddLocationActivity.this,
						"您没有发布过任何记忆，请先创建记忆然后再添加地标", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onCancelled() {
			Toast.makeText(AddLocationActivity.this, "查询失败", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
