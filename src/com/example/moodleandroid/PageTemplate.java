package com.example.moodleandroid;

import java.io.IOException;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class PageTemplate extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page_template);
		final Context context = this;
		
        HtmlPage page = new HtmlPage(getString(R.string.base_url), getString(R.string.base_url));
        
        WebView webView = (WebView)findViewById(R.id.webViewPage);
//		webView.setWebViewClient(new WebViewClient());
        webView.setWebViewClient(new WebViewClient(){
        	@Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                System.out.println("DDD URL: " + url.toString());
                HtmlPage page = new HtmlPage(url.toString(), getString(R.string.base_url));
                try {
					view.loadData(page.getpageString(), "text/html", "UTF-8");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                return true;
            }
        });

		
		try {
			webView.loadData(page.getpageString(), "text/html", "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.page_template, menu);
		return true;
	}

}
