package com.example.moodleandroid;

import android.net.http.SslError;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LoginActivity extends Activity {
	int i = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		System.out.println("login start");
		
		final Context context = this;

		WebView webView = (WebView) findViewById(R.id.webViewLogin);

		// Enable JavaScript.
		webView.getSettings().setJavaScriptEnabled(true);

		// Load the page
		Intent intent = getIntent();
		if (intent.getData() != null) {
			webView.loadUrl(getString(R.string.login_url));
		}

//		webView.setWebChromeClient(new WebChromeClient() {
//			// Show loading progress in activity's title bar.
//			@Override
//			public void onProgressChanged(WebView view, int progress) {
//				setProgress(progress * 100);
//			}
//		});
		webView.setWebViewClient(new WebViewClient() {
			// When start to load page, show url in activity's title bar
			
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed();
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				setTitle(url);
			}
			
			
			@Override
		      public void onPageFinished(WebView view, String url) {
				i++;
				System.out.println("onpagefinish");
		        CookieSyncManager.getInstance().sync();
		        // Get the cookie from cookie jar.
		        String cookie = CookieManager.getInstance().getCookie(url);
		        System.out.println("cookie");
		        if (cookie == null) {
		        	System.out.println("null");
		          return;
		        }
		        // Cookie is a string like NAME=VALUE [; NAME=VALUE]
		        String[] pairs = cookie.split(";");
		        for (int i = 0; i < pairs.length; ++i) {
		          String[] parts = pairs[i].split("=", 2);
		          // If token is found, return it to the calling activity.
		          if (parts.length == 2 &&
		             parts[0].equalsIgnoreCase("MoodleSession")) {
//		            Intent result = new Intent(context, PageLoad.class);
//		            result.putExtra("token", parts[1]);
//		            setResult(RESULT_OK, result);
//		            finish();
		            
		          }
		        }
		        
		        if(i==2){
		        	Intent result = new Intent(context, PageTemplate.class);
		        	startActivity(result);
		        }
//		        finish();
		      }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
