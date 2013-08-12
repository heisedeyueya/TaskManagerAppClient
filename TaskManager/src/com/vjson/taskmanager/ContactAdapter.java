package com.vjson.taskmanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ContactAdapter extends BaseAdapter {
	private List<Contact> mList = null;
	private Context mContext;

	public ContactAdapter(Context context) {
		mContext = context;

		mList = new ArrayList<Contact>();
	}

	public void addItems(List<Contact> contacts) {
		mList.addAll(contacts);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Contact getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.contact_list_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.tel = (TextView) convertView.findViewById(R.id.tel);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.populateView(getItem(position));

		return convertView;
	}

	static class ViewHolder {
		TextView name;
		TextView tel;

		void populateView(Contact contact) {
			name.setText(contact.name);
			tel.setText(contact.tel);
		}
	}

}
