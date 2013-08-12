package com.vjson.taskmanager;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;

public class MainActivity extends BaseActivity {
	private ListView mListView;
	private ScanTask mScanTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setupViews();

		mScanTask = new ScanTask();
		mScanTask.execute();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		mScanTask.cancel(true);
	}

	private void setupViews() {
		mListView = (ListView) findViewById(R.id.list);
		mListView.setAdapter(new ContactAdapter(getApplicationContext()));
	}

	class ScanTask extends AsyncTask<Void, Void, List<Contact>> {
		ProgressDialog dialog = null;

		public ScanTask() {
			dialog = new ProgressDialog(MainActivity.this);
			dialog.setMessage("正在扫描...");
		}

		@Override
		protected void onPreExecute() {
			if (!isCancelled()) {
				dialog.show();
			}
		}

		@Override
		protected List<Contact> doInBackground(Void... params) {
			List<Contact> list = null;
			Cursor cursor = getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					null,
					null,
					null,
					ContactsContract.Contacts.DISPLAY_NAME
							+ " COLLATE LOCALIZED ASC");
			if (cursor.moveToFirst()) {
				list = new ArrayList<Contact>();
				int idIndex = cursor
						.getColumnIndex(ContactsContract.Contacts._ID);

				int nameIndex = cursor
						.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
				Contact contact = null;
				try {
					do {
						contact = new Contact();

						String name = cursor.getString(nameIndex);
						contact.name = name;
						String contactId = cursor.getString(idIndex);
						int phoneCount = cursor
								.getInt(cursor
										.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

						if (phoneCount > 0) {
							Cursor phones = getContentResolver()
									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
											null,
											ContactsContract.CommonDataKinds.Phone.CONTACT_ID
													+ " = " + contactId, null,
											null);
							try {
								if (phones.moveToFirst()) {
									// do {
									String phoneNumber = phones
											.getString(phones
													.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
									contact.tel = phoneNumber;
									// String phoneType = phones
									// .getString(phones
									// .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
									// } while (phones.moveToNext());
								}
							} catch (Exception e) {
								e.printStackTrace();
							} finally {
								if (phones != null)
									phones.close();
							}
						}

						list.add(contact);
					} while (cursor.moveToNext());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (cursor != null) {
						cursor.close();
					}
				}

			}
			return list;
		}

		@Override
		protected void onPostExecute(List<Contact> result) {
			if (!isCancelled()) {
				dialog.dismiss();
				if (result != null) {
					ContactAdapter adapter = (ContactAdapter) mListView
							.getAdapter();

					adapter.addItems(result);
				}
			}
		}

	}

}
