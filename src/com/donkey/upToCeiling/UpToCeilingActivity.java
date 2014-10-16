package com.donkey.upToCeiling;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import com.donkey.R;
public class UpToCeilingActivity extends Activity {
	private ListView list;
	private Button showButton;
	private Button hideButton;
	private ImageButton imageButton;
	private PopupWindow mPopupWindowBig;
	@Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.layout_listview);  
        
//        showButton = (Button)findViewById(R.id.showButton);        
//        showButton.setOnClickListener(new OnClickListener(){
//        	@Override
//        	public void onClick(View v) {
//        		Context mContext = UpToCeilingActivity.this;
//        		imageButton=new ImageButton(mContext);
//        		imageButton.setImageResource(R.drawable.ic_launcher);
//        		imageButton.setOnClickListener(new OnClickListener(){
//        			@Override
//        			public void onClick(View v) {
//        				list.setSelection(0);				   
//        			}	            
//        		});
//        		mPopupWindowBig = new PopupWindow(imageButton, LayoutParams.WRAP_CONTENT,
//        				LayoutParams.WRAP_CONTENT);	
//        		mPopupWindowBig.showAtLocation(findViewById(R.id.LinearLayout01), Gravity.CENTER, 0, 0);
//        	}  
//	    });
        
        
        //绑定Layout里面的ListView  
        list = (ListView)findViewById(R.id.ListViewDemo01);  
          
        //生成动态数组，加入数据  
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();  
        for(int i=0;i<30;i++)  
        {  
            HashMap<String, Object> map = new HashMap<String, Object>();  
            map.put("ItemImage", R.drawable.default_avatar);//图像资源的ID  
            map.put("ItemTitle", "Level "+i);  
            map.put("ItemText", "Finished in 1 Min 54 Secs, 70 Moves! ");  
            listItem.add(map);  
        }  
        //生成适配器的Item和动态数组对应的元素  
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,listItem,//数据源   
            R.layout.layout_listitem,//ListItem的XML实现  
            //动态数组与ImageItem对应的子项          
            new String[] {"ItemImage","ItemTitle", "ItemText"},   
            //ImageItem的XML文件里面的一个ImageView,两个TextView ID  
            new int[] {R.id.ItemImage,R.id.ItemTitle,R.id.ItemText}  
        );  
         
        //添加并且显示  
        list.setAdapter(listItemAdapter);  
          
        //添加点击  
        list.setOnItemClickListener(new OnItemClickListener() {  
  
            @Override  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                    long arg3) {  
                setTitle("点击第"+arg2+"个项目");  
            }  
        });  
          
      //添加长按点击  
        list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {  
              
            @Override  
            public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
                menu.setHeaderTitle("长按菜单-ContextMenu");     
                menu.add(0, 0, 0, "弹出长按菜单0");  
                menu.add(0, 1, 0, "弹出长按菜单1");     
            }  
        });   
        PopupButton upButton = new PopupButton(UpToCeilingActivity.this, list, R.id.LinearLayout01);
       
//        mPopupWindowBig = upButton.getmPopupWindowBig();
//        mPopupWindowBig.showAtLocation(findViewById(R.id.LinearLayout01), Gravity.CENTER, 0, 0);

   	}  
      
    //长按菜单响应函数  
    @Override  
    public boolean onContextItemSelected(MenuItem item) {  
        setTitle("点击了长按菜单里面的第"+item.getItemId()+"个项目");   
        return super.onContextItemSelected(item);  
    }  
}