package com.example.lizejun.globalactivity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.apache.http.client.utils.URIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lizejun on 2015/04/13.
 */
public class SuggestionAdapter extends BaseAdapter {

	private static final String TAG = "Search.SuggestionAdapter";
	private ArrayList<List<LocalBaseBean>> mSourceList;
	private LayoutInflater mInflater;
	private Context mContext;

	public SuggestionAdapter(Context context) {
		super();
		mContext = context;
		mSourceList = new ArrayList<List<LocalBaseBean>>();
		mInflater = LayoutInflater.from(context);
	}

	public void addSource(List<LocalBaseBean> sourceList) {
		mSourceList.add(sourceList);
	}


	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public int getCount() {
		int total = 0;
		for (int i = 0; i < mSourceList.size(); i++) {
			total += mSourceList.get(i).size();
		}
		return total;
	}

	@Override
	public LocalBaseBean getItem(int position) {
		int total = 0;
		for (int i = 0; i < mSourceList.size(); i++) {
			if (position < (mSourceList.get(i).size() + total)) {
				return mSourceList.get(i).get(position - total);
			}
			total += mSourceList.get(i).size();
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SuggestionViewHolder suggestionViewHolder = null;
		if (convertView == null) {
			Log.d(TAG, "inflate");
			convertView = mInflater.inflate(R.layout.suggestion, null);
			suggestionViewHolder = new SuggestionViewHolder();
			suggestionViewHolder.title = (TextView) convertView.findViewById(R.id.text_view_title_list);
			suggestionViewHolder.subTitle = (TextView) convertView.findViewById(R.id.text_view_sub_title_list);
			convertView.setTag(suggestionViewHolder);
		} else {
			suggestionViewHolder = (SuggestionViewHolder) convertView.getTag();
		}
		final LocalBaseBean baseBean = getItem(position);
		suggestionViewHolder.title.setText(baseBean.getTitle());
		suggestionViewHolder.subTitle.setText(baseBean.getSub_title());
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(baseBean.getAction());
				intent.setComponent(baseBean.getComponentName());
				Uri data = baseBean.getUri();
				intent.setData(data);
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}

	static class SuggestionViewHolder {
		TextView title;
		TextView subTitle;
	}
}
