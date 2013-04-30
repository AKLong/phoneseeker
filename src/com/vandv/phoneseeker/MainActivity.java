package com.vandv.phoneseeker;

import static com.vandv.phoneseeker.CommonUtilities.SHOW_POSITION_ACTION;
import static com.vandv.phoneseeker.CommonUtilities.START_GPS_ACTION;
import static com.vandv.phoneseeker.CommonUtilities.IP_MESSAGE;
import static com.vandv.phoneseeker.CommonUtilities.STATUS_MESSAGE;
import static com.vandv.phoneseeker.CommonUtilities.LOC_MESSAGE;
import static com.vandv.phoneseeker.CommonUtilities.SENDER_ID;
import static com.vandv.phoneseeker.CommonUtilities.positionData;
import static com.vandv.phoneseeker.CommonUtilities.setPositionData;
import static com.vandv.phoneseeker.CommonUtilities.initGPS;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.vandv.phoneseeker.R;

public class MainActivity extends Activity  {
	static // label to display gcm messages

	TextView lblMessage;
	TextView gpsLatText;
	TextView gpsLonText;
	TextView netLatText;
	TextView netLonText;
	TextView netAccuracyText;
	TextView gpsAccuracyText;
	TextView ipText;
	TextView dateText;
	TextView statusText;

	// Asyntask
	AsyncTask<Void, Void, Void> mRegisterTask;

	// Alert dialog manager
	AlertDialogManager alert = new AlertDialogManager();

	// Connection detector
	ConnectionDetector cd;

	public static String imei;
	private static Context context;
	//    public static GPSPosition gps;

	@Override
	public void onPause() {
		super.onPause();  // Always call the superclass method first
	}

	@Override
	protected void onStop() {
		super.onStop();  // Always call the superclass method first
	}

	@Override
	protected void onResume() {
		super.onResume();  // Always call the superclass method first

	}

	@Override
	protected void onRestart() {
		super.onRestart();  // Always call the superclass method first
		// Activity being restarted from stopped state    
	}


	@Override
	protected void onStart() {
		super.onStart();  // Always call the superclass method first

		// Activity being restarted from stopped state    
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putCharSequence("labelText", lblMessage.getText());
	}


	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		lblMessage.setText(savedInstanceState.getCharSequence("labelText"));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        MainActivity.context = getApplicationContext();

		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(MainActivity.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}

		// get the phones imei number
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		imei = telephonyManager.getDeviceId();		

		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);

		lblMessage = (TextView) findViewById(R.id.lblMessage);
		gpsLatText = (TextView) findViewById(R.id.gpsLatText);
		gpsLonText = (TextView) findViewById(R.id.gpsLongText);
		gpsAccuracyText = (TextView) findViewById(R.id.gpsAccuracyText);
		netLatText = (TextView) findViewById(R.id.netLatText);
		netLonText = (TextView) findViewById(R.id.netLongText);
		netAccuracyText = (TextView) findViewById(R.id.netAccuracyText);
		ipText = (TextView) findViewById(R.id.iptext);
		dateText = (TextView) findViewById(R.id.dateText);
		statusText = (TextView) findViewById(R.id.statusText);
		setupDB();

		registerReceiver(mUpdateDisplayReceiver, new IntentFilter(SHOW_POSITION_ACTION));
//		registerReceiver(mIPMessageReceiver, new IntentFilter(SHOW_IP_ACTION));
//		registerReceiver(mStatusMessageReceiver, new IntentFilter(SHOW_STATUS_ACTION));
		registerReceiver(mGPSStarter, new IntentFilter(START_GPS_ACTION));

		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		//		final String 	regId="";

		// Check if regid already presents
		if (regId.equals("")) {
			// Registration is not present, register now with GCM			

			Intent nextScreen = new Intent(MainActivity.this, RegisterActivity.class);
			startActivityForResult(nextScreen, 1);

//			
			
		} else {
			// Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.				
				//				Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						// Register on our server
						// On server creates a new user
						ServerUtilities.register(context, "", imei, regId);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}
		//		gps = new  GPSPosition(this);
	}		

	public void unregister(View v){
		GCMRegistrar.unregister(this);
		Intent nextScreen = new Intent(MainActivity.this, RegisterActivity.class);
		startActivityForResult(nextScreen, 1);
	}
	
	void setupDB(){

		DatabaseHandler db = new DatabaseHandler(this);
		db.getLastPosition();
		if (positionData.getGPSLat() != null){
			gpsLonText.setText(positionData.getGPSLon().toString());
			gpsLatText.setText(positionData.getGPSLat().toString());
			gpsAccuracyText.setText(positionData.getGPSAccuracy().toString());
			netLonText.setText(positionData.getNetworkLon().toString());
			netLatText.setText(positionData.getNetworkLat().toString());
			netAccuracyText.setText(positionData.getNetworkAccuracy().toString());
		statusText.setText(positionData.getStatus());
		ipText.setText(positionData.getIP());
		dateText.setText(positionData.getDate());
		}
	}
	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mUpdateDisplayReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
//			Location loc = (Location) intent.getExtras().getParcelable(LOC_MESSAGE);
			
			//setPositionData(loc);
			// Waking up mobile if it is sleeping
			//			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take appropriate action on this message
			 * depending upon your app requirement
			 * For now i am just displaying it on the screen
			 * */

			// Showing received message
			//			lblMessage.append(newMessage + "\n");			
			//			Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

			gpsLonText.setText(positionData.getGPSLon().toString());
			gpsLatText.setText(positionData.getGPSLat().toString());
			gpsAccuracyText.setText(positionData.getGPSAccuracy().toString());
			netLonText.setText(positionData.getNetworkLon().toString());
			netLatText.setText(positionData.getNetworkLat().toString());
			netAccuracyText.setText(positionData.getNetworkAccuracy().toString());

			ipText.setText(positionData.getIP());
			dateText.setText(positionData.getDate());
			statusText.setText(positionData.getStatus());
			// Releasing wake lock
			//			WakeLocker.release();
		}
	};

	private final BroadcastReceiver mIPMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String ip = intent.getExtras().getString(IP_MESSAGE);	
			ipText.setText(ip);
			dateText.setText(positionData.getDate());
		}
	};

	private final BroadcastReceiver mStatusMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String status = intent.getExtras().getString(STATUS_MESSAGE);	
			statusText.setText(status);
		}
	};


	/**
	 * Receiving start GPS message from GCM Intent service
	 * */
		private final BroadcastReceiver mGPSStarter = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			initGPS(context);
		}
	};
	 
	//	private final BroadcastReceiver mGPSStopper = new BroadcastReceiver() {
	//		@Override
	//		public void onReceive(Context context, Intent intent) {
	//			gps.stopGPS();
	//			WakeLocker.release();
	//		}
	//	};


	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			unregisterReceiver(mUpdateDisplayReceiver);
//			unregisterReceiver(mIPMessageReceiver);
			//			unregisterReceiver(mGPSStarter);
			//			unregisterReceiver(mGPSStopper);
			GCMRegistrar.onDestroy(this);
			//			 System.runFinalizersOnExit(true);
			System.runFinalization();
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}





}



