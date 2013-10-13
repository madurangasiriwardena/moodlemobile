package com.example.services;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.example.moodleandroid.R;
import com.example.moodleandroid.R.string;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.property.DateStart;
import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Events;
import android.webkit.CookieManager;

public class CalenderSync extends IntentService  {

	SharedPreferences preferences;
	String calenderUrl;

	public CalenderSync() {
		super("CalenderSync");
		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		calenderUrl = getCalengerAdderess();

		downLoadIcal(calenderUrl);
		readIcal();
		
	}
	
	private void downLoadIcal(String urlString){
		String FILENAME = "mycalendar.ics";
		
		FileOutputStream fos;
		try {
			URL u = new URL(calenderUrl);
			URLConnection conn = u.openConnection();
		    int contentLength = conn.getContentLength();
		    
		    DataInputStream stream = new DataInputStream(u.openStream());

	        byte[] buffer = new byte[contentLength];
	        stream.readFully(buffer);
	        stream.close();
	        
	        fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(buffer);
			fos.close();
		    
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void readIcal(){
		String FILENAME = "mycalendar.ics";
		FileInputStream fis;
		
		try{
			fis = openFileInput(FILENAME);
			
			int content;
			String myCalendarString = null;
			while ((content = fis.read()) != -1) {
				if(myCalendarString == null){
					myCalendarString = (char) content+ "";
				}else{
					myCalendarString += (char) content;
				}
			}
			fis.close();
			
			System.out.print(myCalendarString);
			
			List<ICalendar> ical = Biweekly.parse(myCalendarString).all();
			VEvent event = ical.get(0).getEvents().get(0);
			
			setEvent(event);
			
			String summary = event.getSummary().getValue();
			System.out.println(summary);
			DateStart start = event.getDateStart();
			System.out.println(start.getRawComponents());

			
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		} 

	}
	
	private Document loadCalenderPage(){
		try {
			String urlString = preferences.getString("base_url_http", getString(R.string.base_url_http)) + "calendar/view.php";
			final URI url = new URI(urlString);
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			
			String baseUrl = preferences.getString("base_url", getString(R.string.base_url));
			String cookie = CookieManager.getInstance().getCookie(baseUrl);
			httppost.setHeader("Cookie", cookie);
			
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			Document page = Jsoup.parse(is, "UTF-8", baseUrl);
			
			return page;
			
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String getCalengerAdderess(){
		Document page = loadCalenderPage();
		Document pageContent = Jsoup.parse(page.select("div.region-content").get(0).html());
		String url  = pageContent.select("a[href*=export_execute]").get(0).attr("href");
		
		return url;
	}
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void setEvent(VEvent event){
		long startMilis = 0;
	    int mCalId = 1;
	    long endMilis = 0;
	    Calendar beginTime = Calendar.getInstance();
	    beginTime.set(2013, 10, 29, 9, 10);
	    startMilis = beginTime.getTimeInMillis();  
	    Calendar endTime = Calendar.getInstance();
	    endTime.set(2013, 10, 30, 10,10);
	    endMilis = endTime.getTimeInMillis();

	    ContentResolver cr = getContentResolver();
	    ContentValues values = new ContentValues();
	    values.put(Events.CALENDAR_ID, mCalId);
	    values.put(Events.DTSTART, startMilis);
	    values.put(Events.DTEND, endMilis);
	    values.put(Events.TITLE,"Special Event");
	    values.put(Events.DESCRIPTION, "Group Activity");
	    values.put(Events.EVENT_TIMEZONE, "America/Los_Angeles");
	    Uri uri = cr.insert(Events.CONTENT_URI, values);
	    System.out.println("added");
	}

}
