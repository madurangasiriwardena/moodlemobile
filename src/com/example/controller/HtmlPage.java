package com.example.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.util.ArrayList;

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

public class HtmlPage {
	InputStream is;
	boolean inputStreamLoadSuccessfull = false;
	Document doc;
	String baseUrl;
	ArrayList<String> javaScripts = new ArrayList<String>();

	public HtmlPage(InputStream is) {
		try {
			doc = Jsoup.parse(is, "UTF-8", baseUrl);
			
			this.is = is;
			inputStreamLoadSuccessfull = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	public HtmlPage(String urlString, String baseUrl) {
		loadPage(urlString, baseUrl);
	}

	// load the required web page from HttpPost
	private void loadPage(String urlString, String baseUrl) {
		this.baseUrl = baseUrl;
		try {
			final URI url = new URI(urlString);

			HttpClient httpclient = getNewHttpClient();
			HttpPost httppost = new HttpPost(url);

			try {
				String cookie = CookieManager.getInstance().getCookie(baseUrl);
				httppost.setHeader("Cookie", cookie);

			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				InputStream is = entity.getContent();
				doc = Jsoup.parse(is, "UTF-8", baseUrl);

				this.is = is;
				inputStreamLoadSuccessfull = true;
//				javaScripts = getJavaScriptWithSrc();

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

	// The security certificate presented by the Moodle server is not accepted
	// by the application.
	// So this tells the application to accept any kind of certificate.
	// This won't be a matter as this application only connects to the Moodle.
	public HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	// Get the page content are of a moodle page
	public String getpageString() {
		Elements page = doc.select("div.region-content");

		String pageString = page.get(0).html();
//		pageString += getJavaScript();
		
		return pageString;
	}

	// Get the number of blocks including the page content area.
	public int getBlockNum() throws IOException {
		Elements block = doc.select("div.block");

		return (block.size() + 1);
	}

	public boolean isLogin() {
		Elements login = doc.select("div.loginpanel");
		if (login.size() > 0)
			return true;
		else
			return false;
	}

	// Get the headers of the blocks
	public ArrayList<String> getBlockHeaders() {
		ArrayList<String> headers = new ArrayList<String>();

		headers.add("Page Content");

		Elements block = doc.select("div.block");
		for (int i = 0; i < block.size(); i++) {
			String text = block.get(i).getElementsByTag("h2").text();
			if (!text.equalsIgnoreCase("")) {
				headers.add(text);
			}

		}

		return headers;
	}

	// Get the block having the given header
	public String getBlock(String itemHeader) {

		if (itemHeader.equalsIgnoreCase("Page Content")) {
			return getpageString();
		}

		Elements block = doc.select("div.block");
		for (int i = 0; i < block.size(); i++) {
			String text = block.get(i).getElementsByTag("h2").text();
			if (text.equalsIgnoreCase(itemHeader)) {
				String blockString = block.get(i).html();
//				blockString += getJavaScript();

				return blockString;
			}

		}
		return "";
	}

	// Get the logout url from the web page
	public String getLogOutUrl() {
		Elements logininfo = doc.select("div.logininfo");
		Elements logininfourls = Jsoup.parse(logininfo.html()).body()
				.getElementsByTag("a");
		return logininfourls.get(1).attr("href");
	}

	// Get the url for the profile of the user
	public String getProfileUrl() {
		Elements logininfo = doc.select("div.logininfo");
		Elements logininfourls = Jsoup.parse(logininfo.html()).body()
				.getElementsByTag("a");
		return logininfourls.get(0).attr("href");
	}

	// Get the javaScripts included in the script tags.
	public String getJavaScript() {
		Elements script = doc.getElementsByTag("script");
		String scriptString = "";
		for (int i = 0; i < script.size(); i++) {
			if (script.get(i).toString().contains("type=\"text/javascript\"")
					&& !script.get(i).toString().contains("src=")) {
				scriptString += script.get(i);
			}
		}
//		System.out.println(scriptString);
		
//		for(int i=0; i<javaScripts.size(); i++){
//			scriptString += "<script type=\"text/javascript\">" + javaScripts.get(i) + "</script>";
//		}


		return scriptString;
	}

//	// Get the javaScript urls
//	public ArrayList<String> getJavaScriptWithSrc() {
//		ArrayList<String> url = new ArrayList<String>();
//		ArrayList<String> scriptString = new ArrayList<String>();
//		Elements script = doc.head().select("script[src*=.js]");
//
//		for (int i = 0; i < script.size(); i++) {
//			String currentUrl = script.get(i).attr("src").toString();
//
//			if (currentUrl.contains("?")) {
//				String temp[] = currentUrl.split("\\?");
//				if (temp[1].contains("&")) {
//					String urltemp[] = temp[1].split("&");
//					for (int j = 0; j < urltemp.length; j++) {
//						url.add(temp[0] + "?" + urltemp[j]);
//					}
//				} else {
//					url.add(temp[1]);
//				}
//			} else {
//				url.add(script.get(i).attr("src").toString());
//			}
//		}
//
//		for (int i = 0; i < url.size(); i++) {
//			scriptString.add(loadJavaScript(url.get(i)));
//		}
//
//		return scriptString;
//	}
//
//	// load the required web page from HttpPost
//	public String loadJavaScript(String urlString) {
//		String javaScript = "";
//		try {
//			final URI url = new URI(urlString);
//
//			HttpClient httpclient = getNewHttpClient();
//			HttpPost httppost = new HttpPost(url);
//
//			try {
//				String cookie = CookieManager.getInstance().getCookie(baseUrl);
//				httppost.setHeader("Cookie", cookie);
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//			try {
//				HttpResponse response = httpclient.execute(httppost);
//				HttpEntity entity = response.getEntity();
//				InputStream is = entity.getContent();
//				javaScript = getStringFromInputStream(is);
//
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			} catch (ClientProtocolException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}
//
//		return javaScript;
//	}
//
//	// convert InputStream to String
//	private static String getStringFromInputStream(InputStream stream) {
//
//		BufferedReader br = null;
//		StringBuilder sb = new StringBuilder();
//
//		String line;
//		try {
//
//			br = new BufferedReader(new InputStreamReader(stream));
//			while ((line = br.readLine()) != null) {
////				System.out.println(line);
//				sb.append(line);
//			}
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (br != null) {
//				try {
//					br.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//		return sb.toString();
//
//	}
//
}
