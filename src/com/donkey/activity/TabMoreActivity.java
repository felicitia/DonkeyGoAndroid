package com.donkey.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.donkey.R;
import com.donkey.login.CustomDialog;
import com.donkey.login.RegisterActivity;

public class TabMoreActivity extends Activity {


	private Button login;
	private Button register;
	private Button about;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_more);
		
		initViews();
	}
	
	public void initViews(){
		login = (Button) findViewById(R.id.login_more);
		register = (Button) findViewById(R.id.register_more);
		about = (Button) findViewById(R.id.about_more);
		
		login.setOnClickListener(listener);
		register.setOnClickListener(listener);
		about.setOnClickListener(listener);
	}
	
	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.login_more:
				CustomDialog dialog = new CustomDialog(TabMoreActivity.this);
				dialog.show();
				break;
			case R.id.register_more:
				Intent intent = new Intent();
				intent.setClass(TabMoreActivity.this, RegisterActivity.class);
				startActivity(intent);
				break;
			case R.id.about_more:
				getDialog();
				break;
			default:
				break;
			}
		}
	};
	
	private void getDialog(){
		AlertDialog.Builder ad = new AlertDialog.Builder(TabMoreActivity.this);
		ad.setTitle("关于");
		ad.setMessage("本产品DonkeyGo是专属于您的梦想版旅游社交软件，感谢您的使用！");
		ad.setPositiveButton("知道了",null);
		ad.show();
	}
}
