package com.example.moodleandroid.test;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.test.InstrumentationTestCase;

import com.example.controller.HtmlPage;


public class HtmlPageTest extends InstrumentationTestCase {
	
	
	public void testGetBlockNum(){

		try {
			InputStream is = getInstrumentation().getContext().getResources().getAssets().open("homePage/homePage.htm");
			HtmlPage page = new HtmlPage(is, "http://10.0.2.2");
			assertEquals(page.getBlockNum(),  7);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void testGetLogOutUrl(){
		try {
			InputStream is = getInstrumentation().getContext().getResources().getAssets().open("homePage/homePage.htm");
			
			HtmlPage page = new HtmlPage(is, "http://10.0.2.2");
			assertEquals(page.getLogOutUrl(),  "http://online.mrt.ac.lk/login/logout.php?sesskey=xa0JTwOeca");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void testGetProfileUrl(){
		try {
			InputStream is = getInstrumentation().getContext().getResources().getAssets().open("homePage/homePage.htm");
			
			HtmlPage page = new HtmlPage(is, "http://10.0.2.2");
			assertEquals(page.getProfileUrl(),  "http://online.mrt.ac.lk/user/profile.php?id=1110");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void testGetBlockHeaders(){
		try {
			InputStream is = getInstrumentation().getContext().getResources().getAssets().open("homePage/homePage.htm");
			
			ArrayList<String> headers = new ArrayList<String>();
			headers.add("Page Content");
			headers.add("Main menu");
			headers.add("Navigation");
			headers.add("Administration");
			headers.add("My Active Course List");
			headers.add("My Archive Course List");
			headers.add("Calendar");
			
			HtmlPage page = new HtmlPage(is, "http://10.0.2.2");
			assertEquals(page.getBlockHeaders(),  headers);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void testIsLogin(){
		try {
			InputStream is = getInstrumentation().getContext().getResources().getAssets().open("homePage/homePage.htm");
			HtmlPage page = new HtmlPage(is, "http://10.0.2.2");
			assertEquals(page.isLogin(),  false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
