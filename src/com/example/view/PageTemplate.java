package com.example.view;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.example.controller.HtmlPage;
import com.example.moodleandroid.CalenderSync;
import com.example.moodleandroid.R;

@SuppressLint("NewApi")
public class PageTemplate extends SherlockActivity {
	HtmlPage page;
	Context context;
	WebView webView;
	String currentUrl;
	private List<String> urlHistory = new ArrayList<String>();
	String itemInGoToMenu = "Page Content";
	SharedPreferences preferences;
	String base_url;
	String login_url;
	String base_url_http;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_page_template);
		context = this;
		
		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		base_url = preferences.getString("base_url",getString(R.string.base_url));
		login_url = preferences.getString("login_url", getString(R.string.login_url));
		base_url_http = preferences.getString("base_url_http", getString(R.string.base_url_http));
		
		Intent mServiceIntent = new Intent(context, CalenderSync.class);
		startService(mServiceIntent);
		
		currentUrl = base_url;
		urlHistory.add(0, currentUrl);

		webView = (WebView) findViewById(R.id.webViewPage);
//		webView.getSettings().setJavaScriptEnabled(true);
		
		webView.setWebChromeClient(new WebChromeClient());
		
		//override the clicks on webview and avoid from default action
		webView.setWebViewClient(new WebViewClient() {
			@SuppressLint("NewApi")
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				
				currentUrl = url;
				System.out.println(url);
				urlHistory.add(0, url);
				LoadPageTask lpt = new LoadPageTask();
				try {
					page = lpt.execute(new String[]{url.toString(), base_url}).get();
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
			page = lpt.execute(new String[]{base_url, base_url}).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		//Set the title of the action bar
		ActionBar actionbar = getSupportActionBar();
		actionbar.setTitle("Moodle");

	}
	
	//load the previous page when the back button clicked.
	//current url is in the 0th position. remove the 0th position to get the previous url.
	//get the 0th item in the the arraylist of urlHistory and load the page.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:
			if (urlHistory.size() == 1) {
				finish();
				return true;
			} else if (urlHistory.size() > 1) {
				urlHistory.remove(0);

				// load up the previous url
				LoadPageTask lpt = new LoadPageTask();
				try {
					page = lpt.execute(new String[] { urlHistory.get(0),base_url }).get();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}

				return true;
			} else
				return false;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		SubMenu subBack = menu.addSubMenu("Back");
        subBack.getItem()
    		.setIcon(R.drawable.back)
    		.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		SubMenu subNevigate = menu.addSubMenu("Go to");
		ArrayList<String> headers = page.getBlockHeaders();
		for(int i=0; i<headers.size(); i++){
			MenuItem mi = subNevigate.add(headers.get(i));
			
			if(headers.get(i).equalsIgnoreCase(itemInGoToMenu)){
				mi.setIcon(R.drawable.right_circular);
			}
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
			
		}else if(itemString.equalsIgnoreCase("Back")){
			//load the previous page when the back button in the action bar clicked.
			//current url is in the 0th position. remove the 0th position to get the previous url.
			//get the 0th item in the the arraylist of urlHistory and load the page.
				if (urlHistory.size() == 1) {
		            finish();
		        } else if (urlHistory.size() > 1) {
		        	urlHistory.remove(0);

		            // load up the previous url
		            
		            LoadPageTask lpt = new LoadPageTask();
					try {
						page = lpt.execute(new String[]{urlHistory.get(0), base_url}).get();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
		        } 

		}else if(itemString.equalsIgnoreCase("Profile")){
			String profile = page.getProfileUrl();
			currentUrl = profile;
			urlHistory.add(0, currentUrl);
			
			LoadPageTask lpt = new LoadPageTask();
			try {
				page = lpt.execute(new String[]{profile, base_url}).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			

			
		}else if(itemString.equalsIgnoreCase("Home")){
			currentUrl = getString(R.string.base_url);
			urlHistory.add(0, currentUrl);
			
			LoadPageTask lpt = new LoadPageTask();
			try {
				page = lpt.execute(new String[]{base_url, base_url}).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			
			
			
		}else if(itemString.equalsIgnoreCase("Refresh")){
			
			LoadPageTask lpt = new LoadPageTask();
			try {
				page = lpt.execute(new String[]{currentUrl, base_url}).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			
			
		}else if(itemString.equalsIgnoreCase("Logout")){
			String logout = page.getLogOutUrl();
			
			LoadPageTask lpt = new LoadPageTask();
			try {
				page = lpt.execute(new String[]{logout, base_url}).get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
			
			Intent intent = new Intent(context, LoginActivity.class);
			intent.setData(Uri.parse(base_url));
			startActivity(intent);
			finish();
			
		}else{
			if(itemString.equalsIgnoreCase("Page Content")){
				itemInGoToMenu = itemString;
				try {
					String pageString = page.getpageString();
					Document page = Jsoup.parse(pageString);
					page.head().append(this.page.getJavaScript());
					page = addCssHeaders(page);
					page = addScriptHeaders(page);
					webView.loadDataWithBaseURL("file:///android_asset/.", page.toString(), "text/html", "UTF-8", null);
					invalidateOptionsMenu();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			String pageString = page.getBlock(itemString);
			itemInGoToMenu = itemString;
	    	Document page = Jsoup.parse(pageString);
	    	page.head().append(this.page.getJavaScript());
	    	page = addCssHeaders(page);
	    	page = addScriptHeaders(page);
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
	    	 if(page.isLogin()){
	    		 finish();
					Intent intent = new Intent(context, LoginActivity.class);
					intent.setData(Uri.parse(base_url));
		        	startActivity(intent);
	    	 }
				Document pageContent = Jsoup.parse(page.getpageString());
				pageContent.head().append(page.getJavaScript());
				pageContent = addCssHeaders(pageContent);
				pageContent = addScriptHeaders(pageContent);
				webView.loadDataWithBaseURL("file:///android_asset/.", pageContent.toString(), "text/html", "UTF-8", null);
				invalidateOptionsMenu(); 
				
	     }
	     
	     @Override
         protected void onPreExecute() {
	    	 itemInGoToMenu = "Page Content";
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
	
	//Add javaScript headers to the page header.
		private Document addScriptHeaders(Document page){
			page.head().appendElement("script").attr("type", "text/javascript").attr("src", "javascript-static.js");
			page.head().appendElement("script").attr("type", "text/javascript").attr("src", "loader-min.js");
			page.head().appendElement("script").attr("type", "text/javascript").attr("src", "simpleyui-min.js");
	    	return page;
		}

}
