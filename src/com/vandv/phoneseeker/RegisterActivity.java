package com.vandv.phoneseeker;

import com.google.android.gcm.GCMRegistrar;
import com.vandv.phoneseeker.R;
import static com.vandv.phoneseeker.CommonUtilities.SENDER_ID;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RegisterActivity extends Activity{

	static TextView userName;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		userName = (TextView) findViewById(R.id.userName);
		
		
	}
	
	public void register(View v){
		GCMRegistrar.register(this, SENDER_ID);
		onBackPressed();
	}


}
