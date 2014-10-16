package com.donkey.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListActivity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TabHost.OnTabChangeListener;

import com.donkey.R;
import com.donkey.adapter.FriendListAdapter;


/**
 * 驴友圈的子tab的activity，包括好友/关注，
 * 消息以及好友申请
 * @author Felicitia
 */
public class SubTabFriendActivity extends Activity {

	public static int subTabType = 0;
	private TabHost tabhost = null;
	Context context = null;
    LocalActivityManager manager = null;
    ViewPager pager = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_subtab);
		tabhost = (TabHost) findViewById(R.id.friend_subtab);
		context = SubTabFriendActivity.this;
		 pager = (ViewPager) findViewById(R.id.viewpage);
		//		   切换Intent的话需如下三行，并给setup传参数
		 manager = new	 LocalActivityManager(this, false);
		 manager.dispatchCreate(savedInstanceState);
		 tabhost.setup(manager);
		initTab();
		initPager();
		 //初始化设置一次标签背景
        updateTabBackground(tabhost);
        //选择时背景更改。
        tabhost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                updateTabBackground(tabhost);
                if ("haoyou".equals(tabId)) {
                    pager.setCurrentItem(0);
                    subTabType = 0;
                } else if ("xiaoxi".equals(tabId)) {
                    pager.setCurrentItem(1);
                    subTabType = 1;
                } else if ("shenqing".equals(tabId)) {
                    pager.setCurrentItem(2);
                    subTabType = 2;
                } else {
                    pager.setCurrentItem(3);
                    subTabType = 3;
                }
            }
        });
	}

	
	/**
     * 更新Tab标签的背景图
     * @param tabHost
     */
    private void updateTabBackground(final TabHost tabHost) {
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            View vvv = tabHost.getTabWidget().getChildAt(i);
            if (tabHost.getCurrentTab() == i) {
                //选中后的背景
                vvv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bgtab));
            } else {
                //非选择的背景
                vvv.setBackgroundDrawable(getResources().getDrawable(R.drawable.bgblue));
            }
        }
    }
    
    private View getView(String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
    }
	/**
	 * 初始化Tab
	 */
	public void initTab() {
		tabhost.addTab(tabhost
				.newTabSpec("haoyou")
				.setIndicator("", getResources().getDrawable(R.drawable.friend))
				.setContent(new Intent(this,TabHaoyouActivity.class)));
		tabhost.addTab(tabhost
				.newTabSpec("xiaoxi")
				.setIndicator("", getResources().getDrawable(R.drawable.xiaoxi))
				.setContent(new Intent(this,TabXiaoxiActivity.class)));
		tabhost.addTab(tabhost
				.newTabSpec("shenqing")
				.setIndicator("",
						getResources().getDrawable(R.drawable.shenqing))
				.setContent(new Intent(this,TabShenqingActivity.class)));

		 tabhost.setOnTabChangedListener(new OnTabChangeListener() {

             @Override
             public void onTabChanged(String tabId) {
                 if ("haoyou".equals(tabId)) {
                     pager.setCurrentItem(0);
                 } else if ("xiaoxi".equals(tabId)) {
                     pager.setCurrentItem(1);
                 } else if ("shenqing".equals(tabId)) {
                     pager.setCurrentItem(2);
                 } 
             }
         });

	}

	private void initPager()
    {
        final ArrayList<View> list = new ArrayList<View>();
        Intent intent = new Intent(context, TabHaoyouActivity.class);
        list.add(getView("A", intent));
        Intent intent2 = new Intent(context, TabXiaoxiActivity.class);
        list.add(getView("B", intent2));
        Intent intent3 = new Intent(context, TabShenqingActivity.class);
        list.add(getView("C", intent3));
        
        pager.setAdapter(new PagerAdapter() {

            @Override
            public void destroyItem(View arg0, int arg1, Object arg2) {
                ViewPager pViewPager = ((ViewPager) arg0);
                pViewPager.removeView(list.get(arg1));
            }

            @Override
            public void finishUpdate(View arg0) {
            }

            @Override
            public int getCount() {
                return list.size();
            }

            @Override
            public Object instantiateItem(View arg0, int arg1) {
                ViewPager pViewPager = ((ViewPager) arg0);
                pViewPager.addView(list.get(arg1));
                return list.get(arg1);
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public void restoreState(Parcelable arg0, ClassLoader arg1) {
            }

            @Override
            public Parcelable saveState() {
                return null;
            }

            @Override
            public void startUpdate(View arg0) {
            }
        });

        pager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                tabhost.setCurrentTab(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }
}
