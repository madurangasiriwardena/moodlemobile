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
import org.jsoup.select.Elements;

import android.webkit.CookieManager;

public class HtmlPage{
	InputStream is;
	boolean inputStreamLoadSuccessfull = false;
	Document doc;
	String baseUrl;
	
	public HtmlPage(InputStream is) {
		this.is = is;
		inputStreamLoadSuccessfull = true;
	}
	
	public HtmlPage(String urlString, String baseUrl) {
		this.baseUrl = baseUrl;
		try {

			final URI url = new URI(urlString);

			HttpClient httpclient = getNewHttpClient();
			HttpPost httppost = new HttpPost(url);
			
			String cookie = CookieManager.getInstance().getCookie(baseUrl);
			httppost.setHeader("Cookie",cookie);

			try {
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				doc = Jsoup.parse(is, "UTF-8", baseUrl);
				
				this.is = is;
				inputStreamLoadSuccessfull = true;
				

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
	
	public String getpageString() throws IOException{
		Elements page = doc.select("div.region-content");
		
		return page.get(0).html();
	}
	
	public int getBlockNum() throws IOException{		
		Elements block = doc.select("div.block");
		
		return block.size();
	}
	
	public boolean isLogin(){
		Elements login = doc.select("div.loginpanel");
		if(login.size()>0)
			return true;
		else
			return false;
	}

}
