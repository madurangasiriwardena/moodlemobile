package com.example.moodleandroid;

import java.io.IOException;

import com.actionbarsherlock.app.SherlockActivity;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class StartActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);

		CookieSyncManager.createInstance(getApplicationContext());

		final Context context = this;
		String cookie;

		try {
			cookie = CookieManager.getInstance().getCookie(getString(R.string.base_url));
			if (cookie.contains("MoodleSession")) {
				Intent intent = new Intent(context, PageTemplate.class);
				startActivity(intent);
				finish();
			} else {
				Intent intent = new Intent(context, LoginActivity.class);
				intent.setData(Uri.parse(getString(R.string.base_url)));
				startActivity(intent);
				finish();
			}
		} catch (Exception e) {
			Intent intent = new Intent(context, LoginActivity.class);
			intent.setData(Uri.parse(getString(R.string.base_url)));
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
