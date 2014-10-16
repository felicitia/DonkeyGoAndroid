package com.donkey.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.donkey.R;
import com.donkey.entity.XmlMsgItem;
import com.donkey.util.AsyncImageLoader;

public class ShenqingiListAdapter extends BaseAdapter {
	private Context c;
	private List<XmlMsgItem> list;
	private ApplyHolder holder = null;
	
	AsyncImageLoader asyncImageLoader = new AsyncImageLoader(60, 60);

	public ShenqingiListAdapter(Context c, List<XmlMsgItem> list) {
		super();
		this.c = c;
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			holder = new ApplyHolder();
			convertView = LayoutInflater.from(c).inflate(R.layout.shenqing_list, null);

		holder.tv_name = (TextView) convertView.findViewById(R.id.name_msg);
		holder.tv_content = (TextView) convertView.findViewById(R.id.content_msg);
		holder.tv_time = (TextView) convertView.findViewById(R.id.time_msg);
		holder.iv_avatar = (ImageView) convertView.findViewById(R.id.icon_msg);
			convertView.setTag(holder);
		}else{
			holder = (ApplyHolder) convertView.getTag();
		}
		holder.tv_name.setText(list.get(position).getNickname());
		holder.tv_time.setText(list.get(position).getTime());
		holder.tv_content.setText(list.get(position).getContent());

		final View tmpView = convertView;
		holder.iv_avatar.setTag(list.get(position).getAvatar());
		Drawable cacheImg = asyncImageLoader.loadDrawable(list.get(position)
				.getAvatar(), new AsyncImageLoader.ImageCallback() {
			@Override
			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
				ImageView ivByTag = (ImageView) tmpView
						.findViewWithTag(imageUrl);
				if (ivByTag != null && imageDrawable != null) {
					ivByTag.setImageDrawable(imageDrawable);
				} else {
					try {
						ivByTag.setImageResource(R.drawable.default_avatar);
					} catch (Exception e) {

					}
				}
			}
		});

		holder.iv_avatar.setImageResource(R.drawable.default_avatar);
		if (cacheImg != null) {
			holder.iv_avatar.setImageDrawable(cacheImg);
		}
		return convertView;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class ApplyHolder{
		ImageView iv_avatar;
		TextView tv_name;
		TextView tv_content;
		TextView tv_time;
	}
}
