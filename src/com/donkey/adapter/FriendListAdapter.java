package com.donkey.adapter;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.donkey.R;

public class FriendListAdapter extends BaseAdapter {
	private Context c;
    private  List<Map<String, Object>> list;
   

	public FriendListAdapter(Context c, List<Map<String, Object>> list) {
	super();
	this.c = c;
	this.list = list;
}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = LayoutInflater.from(c).inflate(R.layout.friend_list, null);
		TextView title=(TextView) v.findViewById(R.id.title);
		TextView name=(TextView) v.findViewById(R.id.name);
		TextView place=(TextView) v.findViewById(R.id.place);
		TextView content=(TextView) v.findViewById(R.id.content);
		TextView time=(TextView) v.findViewById(R.id.time);
		ImageView icon=(ImageView) v.findViewById(R.id.icon);
		title.setText(list.get(position).get("title").toString());
		name.setText(list.get(position).get("name").toString());
		time.setText(list.get(position).get("time").toString());
		content.setText(list.get(position).get("content").toString());
		place.setText(list.get(position).get("place").toString());
		icon.setBackgroundResource(Integer.parseInt(list.get(position).get("icon").toString()));
		ImageView message = (ImageView) v.findViewById(R.id.message);
		message.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder ad = new AlertDialog.Builder(c);
				ad.setTitle("发送消息");
				ad.setView(new EditText(c));
				ad.setPositiveButton("发送", null);
				ad.show();
			}
		});
		return v;
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

}
