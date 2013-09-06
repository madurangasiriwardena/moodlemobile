package com.example.moodleandroid;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuInflater;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import android.support.v7.app.ActionBar;

public class PageTemplate extends SherlockActivity {
	HtmlPage page;
	Context context;
	WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page_template);
		context = this;

		page = new HtmlPage(getString(R.string.base_url),
				getString(R.string.base_url));

		webView = (WebView) findViewById(R.id.webViewPage);

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				System.out.println("DDD URL: " + url.toString());
				page = new HtmlPage(url.toString(),
						getString(R.string.base_url));
				try {
					view.loadData(page.getpageString(), "text/html", "UTF-8");
					System.out.println("Blocks in : " + url + " = "
							+ page.getBlockNum());

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return true;
			}
		});

		try {

			webView.loadData(page.getpageString(), "text/html", "UTF-8");
			System.out.println("Blocks in : " + getString(R.string.base_url)
					+ " = " + page.getBlockNum());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		SubMenu subNevigate = menu.addSubMenu("Go to");
		ArrayList<String> headers = page.getBlockHeaders();
		for(int i=0; i<headers.size(); i++){
			subNevigate.add(headers.get(i));
    	}
        
        subNevigate.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        
        SubMenu subOptions = menu.addSubMenu("Options");
        subOptions.add("Profile").setIcon(R.drawable.profile);
        subOptions.add("Home").setIcon(R.drawable.home);
        subOptions.add("Refresh").setIcon(R.drawable.refresh);
        subOptions.add("Logout").setIcon(R.drawable.logout);
        
        subOptions.getItem()
        	.setIcon(R.drawable.menu)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        
        return true;

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.toString().equalsIgnoreCase("Options")){
			
		}else if(item.toString().equalsIgnoreCase("Go to")){
			
		}else if(item.toString().equalsIgnoreCase("Profile")){
			
		}else if(item.toString().equalsIgnoreCase("Home")){
			
		}else if(item.toString().equalsIgnoreCase("Refresh")){
			
		}else if(item.toString().equalsIgnoreCase("Logout")){
			
		}else{
			if(item.toString().equalsIgnoreCase("Page Content")){			
				try {
					String pageString = page.getpageString();
					Document page = Jsoup.parse(pageString);
					webView.loadDataWithBaseURL("file:///android_asset/.", page.outerHtml(), "text/html", "UTF-8", null);
					return true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			String pageString = page.getBlock(item.toString());
	    	Document page = Jsoup.parse(pageString);
	    	page.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "app.css");
	    	try {
				webView.loadDataWithBaseURL("file:///android_asset/.", page.outerHtml(), "text/html", "UTF-8", null);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
    	
    	return true;
		
		
	}

}
