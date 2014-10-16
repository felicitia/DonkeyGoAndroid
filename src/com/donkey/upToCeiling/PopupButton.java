package com.donkey.upToCeiling;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.donkey.R;
import com.donkey.activity.MemoryBaseActivity;

//传入参数：Activity， ListView 和 整个界面的布局的id
public class PopupButton {
	private ImageButton upButton;
	private PopupWindow mPopupWindowBig;


	public PopupButton(final Activity activity, final ListView list,
			final int id) {
		LayoutInflater inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View dialogView = inflater.inflate(R.layout.layout_up_button,
				null, false);
		upButton = (ImageButton) dialogView.findViewById(R.id.upButton);
		// 单击回到ListView的第一个ListItem
		upButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!list.isStackFromBottom())
					list.setStackFromBottom(true);
				list.setStackFromBottom(false);
			}
		});
		// 建立浮动按钮
		mPopupWindowBig = new PopupWindow(dialogView,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mPopupWindowBig.setBackgroundDrawable(new BitmapDrawable());
		list.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem >= 1) {
						mPopupWindowBig.showAtLocation(
								activity.findViewById(id), Gravity.RIGHT
										| Gravity.BOTTOM, 0, 72);
				} else if (firstVisibleItem <= 0) {
					mPopupWindowBig.dismiss();
				}
			}
		});
	}

	public void dismiss() {
		this.mPopupWindowBig.dismiss();
	}
}
