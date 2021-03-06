package activity.post;

import java.util.ArrayList;
import java.util.HashMap;

import util.PostNewUtil;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import bean.PurchasedItem;

import com.ebay.ebayfriend.R;

public class AttachItemFragment extends Fragment {

	private ArrayList<HashMap<String, Object>> itemList = new ArrayList<HashMap<String, Object>>() ;
	private ListView lv;
	private ItemListHandler itemListHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.attachitem, container, false);
		TextView windowTitleView = (TextView) getActivity().findViewById(
				R.id.post_title);
		windowTitleView.setText("select item");

		itemListHandler = new ItemListHandler();
		Thread getItemListThread = new GetItemListThread();
		getItemListThread.start();

		lv = (ListView) view.findViewById(R.id.purchased_list);

		return view;
	}

	private void switchToPicProcessFragment(PurchasedItem pi) {
		PicProcessFragment fragment = new PicProcessFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("purchasedItem", pi);
		fragment.setArguments(bundle);
		FragmentTransaction transaction = getFragmentManager()
				.beginTransaction();
		transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
		transaction.replace(R.id.post_content, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
		Log.e("AttachItemFragment","switch to picProcessFragment");
	}

	private void showItemList(){

		SimpleAdapter listItemAdapter = new SimpleAdapter(getActivity(),
				itemList, R.layout.puchased_item,
				new String[] { "date", "name" }, new int[] {
						R.id.purchasedItem_date, R.id.purchasedItem_name });
		lv.setAdapter(listItemAdapter);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				String name = ((TextView) view
//						.findViewById(R.id.purchasedItem_name)).getText()
//						.toString();
//				String date = ((TextView) view
//						.findViewById(R.id.purchasedItem_date)).getText()
//						.toString();

				HashMap<String, Object> hashMap= itemList.get(position);
				String name = (String)hashMap.get("name");
				String url = (String)hashMap.get("url");
				switchToPicProcessFragment(new PurchasedItem(name, url));

			}

		});
	}


	class GetItemListThread extends Thread {
		@Override
		public void run() {
			itemList = PostNewUtil.getItemList();
			/**
			 * test codes start
			 */
//			itemList = new ArrayList<HashMap<String,Object>>();
//			HashMap<String, Object> map = new HashMap<String, Object>();
//			map.put("name", "name");
//			map.put("url", "id");
//			map.put("date"," date");
//			itemList.add(map);
			/**
			 * test codes end
			 */
			
			itemListHandler.sendMessage(new Message());
			
		}
	}
	@SuppressLint("HandlerLeak")
	class ItemListHandler extends Handler{
		
		public ItemListHandler(){
			
		}
		public ItemListHandler(Looper l){
			
		}
		@Override
		public void handleMessage(Message msg) {
			showItemList();
		}
	}
}



