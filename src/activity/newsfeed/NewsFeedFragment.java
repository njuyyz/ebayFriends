package activity.newsfeed;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import util.GetRequest;
import activity.newsfeed.PullToRefreshListView.OnRefreshListener;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ebay.ebayfriend.R;
import com.nostra13.universalimageloader.core.ImageLoader;

public class NewsFeedFragment extends Fragment {

	private PullToRefreshListView lv;
	private List<NewsFeedItem> itemList;
	private NewsFeedItemAdapter adapter;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.newsfeed, container, false);
		TextView windowTitleView = (TextView) getActivity().findViewById(
				R.id.window_title);
		windowTitleView.setText("News Feeding");
		itemList = new ArrayList<NewsFeedItem>();
		lv = (PullToRefreshListView) view.findViewById(R.id.listview);
		adapter = new NewsFeedItemAdapter(getActivity(), itemList, imageLoader,
				lv);
		lv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				new GetDataTask(adapter, lv).execute();
			}
		});
		lv.setAdapter(adapter);
		new GetDataTask(adapter, lv).execute();
		return view;
	}

	private class GetDataTask extends AsyncTask<Void, Void, List<NewsFeedItem>> {
		private NewsFeedItemAdapter adapter;
		private PullToRefreshListView lv;
		
		public GetDataTask(NewsFeedItemAdapter adapter, PullToRefreshListView lv) {
			this.adapter = adapter;
			this.lv = lv;
		}
		@Override
		protected List<NewsFeedItem> doInBackground(Void... params) {
			// Simulates a background job.
			List<NewsFeedItem> list = new ArrayList<NewsFeedItem>();
			String getURL = Constants.GET_NEWSFEED_URL_PREFIX + 0;
			GetRequest getRequest = new GetRequest(getURL);
			String jsonResult = getRequest.getContent();
			
			if (jsonResult == null) {
				Log.e("NewsFeedFragment", "Json Parse Error");
			} else {
				try {
					JSONArray itemArray = new JSONArray(jsonResult);
					for (int i = 0; i < itemArray.length(); i++) {
						JSONObject item = itemArray.getJSONObject(i);
						String imageURL = item.getString("picture");
						String voiceURL = item.getString("voice");
						JSONObject person = item.getJSONObject("author");
						String portraitURL = person.getString("portrait");
						String authorName = person.getString("name");
						NewsFeedItem newsFeedItem = new NewsFeedItem(imageURL,
								portraitURL, authorName, voiceURL);
						list.add(newsFeedItem);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					Log.e("NewsFeedFragment", "Parse Json Error");
				}
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<NewsFeedItem> result) {
			super.onPostExecute(result);
			adapter.updateList(result);
			lv.onRefreshComplete();
		}
	}
}
