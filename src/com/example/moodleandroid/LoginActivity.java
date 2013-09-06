package com.example.moodleandroid;

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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.actionbarsherlock.app.SherlockActivity;

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

public class LoginActivity extends SherlockActivity {
	int i = 0;
	WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		final Context context = this;

		webView = (WebView) findViewById(R.id.webViewLogin);

		// Enable JavaScript.
		webView.getSettings().setJavaScriptEnabled(true);

		// Load the page
		Intent intent = getIntent();
		if (intent.getData() != null) {
			loadLogin();
		}

		webView.setWebViewClient(new WebViewClient() {
			
			public void onReceivedSslError(WebView view,
					SslErrorHandler handler, SslError error) {
				handler.proceed();
			}
			
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				setTitle(url);
				System.out.println("client " + url);
				if(url.equalsIgnoreCase(getString(R.string.base_url_http))){
					finish();
					Intent result = new Intent(context, PageTemplate.class);
		        	startActivity(result);
				}
				
			}
			
			
			@Override
		      public void onPageFinished(WebView view, String url) {
				i++;
//				System.out.println("finish " + url);
		        CookieSyncManager.getInstance().sync();
		        // Get the cookie from cookie jar.
		        String cookie = CookieManager.getInstance().getCookie(url);
		        if (cookie == null) {
		          return;
		        }
		      }
		});
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
			final URI url = new URI(getString(R.string.login_url));

			HttpClient httpclient = getNewHttpClient();
			HttpPost httppost = new HttpPost(url);

			try {
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				Document doc = Jsoup.parse(is, "UTF-8", getString(R.string.base_url));
				
				Elements login = doc.select("div.loginpanel");
				Document page = Jsoup.parse(login.html());
				page.head().appendElement("link").attr("rel", "stylesheet").attr("type", "text/css").attr("href", "login.css");
				page.body().getElementsByClass("desc").remove();
				page.body().getElementsByClass("forgetpass").remove();
				page.body().getElementsByTag("h2").remove();
				page.body().prependElement("h2").text("Login");
//				System.out.println(page.html());
				
				
				try {
					webView.loadDataWithBaseURL("file:///android_asset/.", page.outerHtml(), "text/html", "UTF-8", null);
//					webView.loadData(login.html(), "text/html", "UTF-8");
				} catch (Exception e) {
					// TODO Auto-generated catch block
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

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.login, menu);
//		return true;
//	}

}
