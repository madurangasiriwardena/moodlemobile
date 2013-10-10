package com.example.view;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.example.moodleandroid.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ChangeAddressActivity extends SherlockActivity {
	Button setButton;
	EditText urlEditText;
	EditText httpEditText;
	Context context;
	SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_address);
		setButton = (Button)findViewById(R.id.buttonSet);
		urlEditText = (EditText)findViewById(R.id.editTextUrl);
		httpEditText = (EditText)findViewById(R.id.editTextHttp);
		
		context = this;
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		String[] url = (preferences.getString("base_url", getString(R.string.base_url)).split("://"));
		urlEditText.setText(url[1]);
		httpEditText.setText("https");
		
		//Set the title of the action bar
		ActionBar actionbar = getSupportActionBar();
		actionbar.setTitle("Moodle");
		
		setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {       
            	SharedPreferences.Editor editor = preferences.edit();
            	String url = httpEditText.getText().toString() + "://" + urlEditText.getText().toString();
            	
            	if(url.charAt(url.length()-1) != '/'){
            		url = url + "/";
            	}
            	
    			editor.putString("base_url", url);
    			editor.putString("login_url", url+"login/index.php");
    			editor.putString("base_url_http", url);
    			editor.commit();

            	Intent result = new Intent(context, LoginActivity.class);
	            setResult(RESULT_OK, result);
            	finish();
            }
        });
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        
        return  true;

	}

}
