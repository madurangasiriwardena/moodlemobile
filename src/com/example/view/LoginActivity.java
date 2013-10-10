package com.example.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.example.controller.MySSLSocketFactory;
import com.example.moodleandroid.R;

import android.net.http.SslError;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

@SuppressLint("NewApi")
public class LoginActivity extends SherlockActivity {
	int i = 0;
	WebView webView;
	SharedPreferences preferences;
	String base_url;
	String login_url;
	String base_url_http;
	Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		context =this;
		
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		base_url = preferences.getString("base_url", getString(R.string.base_url));
		login_url = preferences.getString("login_url", getString(R.string.login_url));
		base_url_http = preferences.getString("base_url_http", getString(R.string.base_url_http));
		
		if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
		
		final Context context = this;

		webView = (WebView) findViewById(R.id.webViewLogin);

		// Enable JavaScript.
//		webView.getSettings().setJavaScriptEnabled(true);

		// Load the page
		Intent intent = getIntent();
		if (intent.getData() != null) {
			loadLogin();
		}

		//override the clicks on webview and avoid from default action
		webView.setWebViewClient(new WebViewClient() {
			
			//handle the exceptions caused by the https connection. proceed with ssl errors
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed();
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				setTitle(url);
				if(url.equalsIgnoreCase(base_url_http)){
					finish();
					Intent result = new Intent(context, PageTemplate.class);
		        	startActivity(result);
				}
				
			}
			
		});
		
		//set the title for the actionbar
		ActionBar actionbar = getSupportActionBar();
		actionbar.setTitle("Moodle");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        
        SubMenu subSettings = menu.addSubMenu("Settings");
        subSettings.add("Change Address");
        
        subSettings.getItem()
        	.setIcon(R.drawable.settings)
        	.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        
        return true;

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String itemString = item.getTitle().toString();
		if(itemString.equalsIgnoreCase("Settings")){
			
		}else if(itemString.equalsIgnoreCase("Change Address")){
			Intent intent = new Intent(context, ChangeAddressActivity.class);
			startActivityForResult(intent, 0);
			
		}
		
		return true;
	}
	
	@Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    switch (requestCode) {
	      case 0:
	        if (resultCode != RESULT_OK || data == null) {
	          return;
	        }
	        base_url = preferences.getString("base_url", getString(R.string.base_url));
			login_url = preferences.getString("login_url", getString(R.string.login_url));
			base_url_http = preferences.getString("base_url_http", getString(R.string.base_url_http));
	        loadLogin();

	        return;
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}
	  
	
	public HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}
	
	private void loadLogin(){
		try {
			//load the page from httppost
			final URI url = new URI(login_url);

			HttpClient httpclient;
			if(login_url.contains("https")){
				httpclient = getNewHttpClient();
			}else{
				httpclient = new DefaultHttpClient();
			}
			HttpPost httppost = new HttpPost(url);

			try {
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				Document doc = Jsoup.parse(is, "UTF-8", base_url);
				
				Elements login = doc.select("div.loginpanel");
				Document page = Jsoup.parse(login.html());
				page.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "login.css");
				page.body().getElementsByClass("desc").remove();
				page.body().getElementsByClass("forgetpass").remove();
				page.body().getElementsByTag("h2").remove();
				page.body().prependElement("h2").text("Login");

				
				
				try {
					webView.loadDataWithBaseURL("file:///android_asset/.", page.outerHtml(), "text/html", "UTF-8", null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

}
