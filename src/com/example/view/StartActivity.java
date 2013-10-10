package com.example.view;


import com.actionbarsherlock.app.SherlockActivity;
import com.example.moodleandroid.R;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;


public class StartActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String installed = preferences.getString("installed","false");
		if(!installed.equals("true")){
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString("base_url", getString(R.string.base_url));
			editor.putString("login_url", getString(R.string.login_url));
			editor.putString("base_url_http", getString(R.string.base_url_http));
			editor.putString("installed","true");
			editor.commit();
		}

		CookieSyncManager.createInstance(getApplicationContext());

		final Context context = this;
		String cookie;

		try {
			cookie = CookieManager.getInstance().getCookie(preferences.getString("base_url",""));
			if (cookie.contains("MoodleSession")) {
				Intent intent = new Intent(context, PageTemplate.class);
				startActivity(intent);
				finish();
			} else {
				Intent intent = new Intent(context, LoginActivity.class);
				intent.setData(Uri.parse(preferences.getString("base_url","")));
				startActivity(intent);
				finish();
			}
		} catch (Exception e) {
			Intent intent = new Intent(context, LoginActivity.class);
			intent.setData(Uri.parse(preferences.getString("base_url","")));
			startActivity(intent);
			finish();
			e.printStackTrace();
		}

	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.start, menu);
//		return true;
//	}

}
