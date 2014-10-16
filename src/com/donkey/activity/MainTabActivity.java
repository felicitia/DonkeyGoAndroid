package com.donkey.activity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TabHost;

import com.donkey.R;
import com.donkey.login.CustomDialog;
import com.donkey.util.AppUtil;

public class MainTabActivity extends TabActivity {
	private TabHost tabHost;
	private RadioGroup radioGroup;
	private RelativeLayout bottom_layout;
	private ImageView img;

	int startLeft;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		initTabHost();
		initRadioGroup();

		bottom_layout = (RelativeLayout) findViewById(R.id.layout_bottom);

		img = new ImageView(this);
		img.setImageResource(R.drawable.tab_front_bg);
		bottom_layout.addView(img);
	}

	/**
	 * 初始化tabhost
	 */
	private void initTabHost() {
		tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec("memory").setIndicator("memory")
				.setContent(new Intent(this, TabMemoryActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("travel").setIndicator("travel")
				.setContent(new Intent(this, TabTravelActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("friends").setIndicator("friends")
				.setContent(new Intent(this, SubTabFriendActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("more").setIndicator("more")
				.setContent(new Intent(this, TabMoreActivity.class)));
	}

	/**
	 * 初始化radio button
	 */
	private void initRadioGroup() {
		radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
		radioGroup.setOnCheckedChangeListener(checkedChangeListener);
	}

	/**
	 * 点击radio button切换activity
	 */
	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.radio_memory:
				tabHost.setCurrentTabByTag("memory");
				AppUtil.moveFrontBg(img, startLeft, 0, 0, 0);
				startLeft = 0;
				break;
			case R.id.radio_travel:
				tabHost.setCurrentTabByTag("travel");
				AppUtil.moveFrontBg(img, startLeft, img.getWidth(), 0, 0);
				startLeft = img.getWidth();
				break;
			case R.id.radio_friends:
				if (AppUtil.haveLoggedin) {
					tabHost.setCurrentTabByTag("friends");
					AppUtil.moveFrontBg(img, startLeft, img.getWidth() * 2, 0,
							0);
					startLeft = img.getWidth() * 2;
				} else
					new CustomDialog(MainTabActivity.this).show();
				break;
			case R.id.radio_more:
				tabHost.setCurrentTabByTag("more");
				AppUtil.moveFrontBg(img, startLeft, img.getWidth() * 3, 0, 0);
				startLeft = img.getWidth() * 3;
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 按下键盘上返回按钮
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			AppUtil.getBackDialog(MainTabActivity.this);
			return false;
		} else {
			return super.dispatchKeyEvent(event);
		}
	}
}