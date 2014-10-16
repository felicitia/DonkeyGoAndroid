package com.donkey.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.donkey.R;
import com.donkey.adapter.FriendListAdapter;

/**
 * 好友Activity，显示好友列表
 * @author Felicitia 
 *
 */
public class TabFriendsActivity extends ListActivity{

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.friend);
        FriendListAdapter adapter = new FriendListAdapter(this,getData());
//        SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.friend_list,
//                new String[]{"title","place","content","icon","time","name"},
//                new int[]{R.id.title,R.id.place,R.id.content,R.id.icon,R.id.time,R.id.name});
        setListAdapter(adapter);
    }
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		if(id==0)
		{
			openDialog("好友记忆","好友记忆详细信息","确认");
		}
	}

	private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "标题：仙女山123");
        map.put("place", "地点：");
        map.put("content", "内容：，风...");
        map.put("time", "2012-4-2 00:43:43");
        map.put("icon",R.drawable.icon);
        map.put("name", "赵逸雪");
     
        return list;
    }
	private void openDialog(String title, String message, String ok){//打开对话框
		 
		AlertDialog.Builder ad=new AlertDialog.Builder(TabFriendsActivity.this);
		ad.setTitle(title);//设置对话框标题
		ad.setMessage(message);//设置对话框内容
		ad.setPositiveButton(ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
				// TODO Auto-generated method stub
			}
		});
		ad.show();//显示对话框
	}
}
