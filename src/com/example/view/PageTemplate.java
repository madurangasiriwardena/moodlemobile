package com.example.view;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.example.controller.HtmlPage;
import com.example.moodleandroid.R;
import com.example.moodleandroid.R.drawable;
import com.example.moodleandroid.R.id;
import com.example.moodleandroid.R.layout;
import com.example.moodleandroid.R.string;

@SuppressLint("NewApi")
public class PageTemplate extends SherlockActivity {
	HtmlPage page;
	Context context;
	WebView webView;
	String currentUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page_template);
		context = this;
		
		currentUrl = getString(R.string.base_url);

		webView = (WebView) findViewById(R.id.webViewPage);
//		webView.getSettings().setJavaScriptEnabled(true);

		
		//override the clicks on webview and avoid from default action
		webView.setWebViewClient(new WebViewClient() {
			@SuppressLint("NewApi")
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				
				currentUrl = url;
				LoadPageTask lpt = new LoadPageTask();
				try {
					page = lpt.execute(new String[]{url.toString(), getString(R.string.base_url)}).get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}				

				return true;
			}
		});
		
		LoadPageTask lpt = new LoadPageTask();
		try {
			page = lpt.execute(new String[]{getString(R.string.base_url), getString(R.string.base_url)}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		//Set the title of the action bar
		ActionBar actionbar = getSupportActionBar();
		actionbar.setTitle("Moodle");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

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
		String itemString = item.getTitle().toString();
		if(itemString.equalsIgnoreCase("Options")){
			
			
		}else if(itemString.equalsIgnoreCase("Go to")){
			
		}else if(itemString.equalsIgnoreCase("Profile")){
			String profile = page.getProfileUrl();
			currentUrl = profile;
			
			LoadPageTask lpt = new LoadPageTask();
			try {
				page = lpt.execute(new String[]{profile, getString(R.string.base_url)}).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			

			
		}else if(itemString.equalsIgnoreCase("Home")){
			currentUrl = getString(R.string.base_url);
			
			LoadPageTask lpt = new LoadPageTask();
			try {
				page = lpt.execute(new String[]{getString(R.string.base_url), getString(R.string.base_url)}).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			
			
			
		}else if(itemString.equalsIgnoreCase("Refresh")){
			
			LoadPageTask lpt = new LoadPageTask();
			try {
				page = lpt.execute(new String[]{currentUrl, getString(R.string.base_url)}).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			
			
		}else if(itemString.equalsIgnoreCase("Logout")){
			String logout = page.getLogOutUrl();
			
			LoadPageTask lpt = new LoadPageTask();
			try {
				page = lpt.execute(new String[]{logout, getString(R.string.base_url)}).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			
			Intent intent = new Intent(context, LoginActivity.class);
			intent.setData(Uri.parse(getString(R.string.base_url)));
			startActivity(intent);
			finish();
			
		}else{
			if(itemString.equalsIgnoreCase("Page Content")){			
				try {
					String pageString = page.getpageString();
					Document page = Jsoup.parse(pageString);
					page = addCssHeaders(page);
					webView.loadDataWithBaseURL("file:///android_asset/.", page.toString(), "text/html", "UTF-8", null);
					invalidateOptionsMenu();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			String pageString = page.getBlock(itemString);
	    	Document page = Jsoup.parse(pageString);
	    	page = addCssHeaders(page);
	    	try {
				webView.loadDataWithBaseURL("file:///android_asset/.", page.toString(), "text/html", "UTF-8", null);
				invalidateOptionsMenu();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
    	
    	return true;
		
	}
	
	//Create a separate thread to load the web page
	private class LoadPageTask extends AsyncTask<String, HtmlPage, HtmlPage> {
	     protected HtmlPage doInBackground(String... urls) {
	    	 page = new HtmlPage(urls[0], urls[1]);
	    	 
	         return page;
	     }

	     protected void onPostExecute(HtmlPage page) {
				Document pageContent = Jsoup.parse(page.getpageString());
				pageContent = addCssHeaders(pageContent);
				webView.loadDataWithBaseURL("file:///android_asset/.", pageContent.toString(), "text/html", "UTF-8", null);
				invalidateOptionsMenu(); 
	     }
	}
	
	//Add css stylesheet headers to the page header.
	private Document addCssHeaders(Document page){
		page.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "app.css");
    	page.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "cssfonts.css");
    	page.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "cssgrids.css");
    	page.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "cssbase.css");
    	return page;
	}

}
