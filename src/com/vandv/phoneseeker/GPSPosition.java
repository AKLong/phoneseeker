package com.vandv.phoneseeker;


import static com.vandv.phoneseeker.CommonUtilities.cancelGPS;
import static com.vandv.phoneseeker.CommonUtilities.displayMessage;
import static com.vandv.phoneseeker.CommonUtilities.setPositionStatus;
import static com.vandv.phoneseeker.CommonUtilities.GPS_MAX_TIME;
import static com.vandv.phoneseeker.CommonUtilities.STATUS_COMPLETED;
import static com.vandv.phoneseeker.CommonUtilities.STATUS_IMPOSSIBLE;
import static com.vandv.phoneseeker.CommonUtilities.STATUS_POSITION_TIMEOUT;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public  class GPSPosition extends Service {


	private final Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	boolean canGetLocation = false;

	boolean gpsRunning = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude
	GPSListener g = null;
	GPSListener n = null;
	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 5; // 5 seconds

	// Declaring a Location Manager
	protected LocationManager locationManager;

	private  Handler stopGPSHandler = new Handler(){
		public void handleMessage(Message m) {
			cancelGPS();	

			}
		
	};

	public void cancelGPS(){			
			boolean goodData = false;
			if (n!=null){ goodData = (goodData || n.validPositionReceived());}
			if (g!=null){ goodData = (goodData || g.validPositionReceived());}

			stopGPS();
			
			if (goodData){
				ServerUtilities.postStatus(mContext,STATUS_COMPLETED, MainActivity.imei);
				setPositionStatus("Completed");
			} else {
				ServerUtilities.postStatus(mContext,STATUS_POSITION_TIMEOUT, MainActivity.imei);
				setPositionStatus("Timeout");
				
			}
		}
		

	public GPSPosition(Context context) {
		this.mContext = context;

		
	}

	public void startGPS(Context context){

		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			g = new GPSListener(context);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, g);
		}
		
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
			n = new GPSListener(context);
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000,0,n);
		}

		if (n==null && g==null){
			ServerUtilities.postStatus(context,STATUS_IMPOSSIBLE, MainActivity.imei);
		}else{
			gpsRunning=true;
			Message stopMessage = new Message();
			stopMessage.what = 1;

			stopGPSHandler.sendMessageDelayed(stopMessage, GPS_MAX_TIME);
		}
		
	}
	public void stopGPS(){
		if (g!=null){
			locationManager.removeUpdates(g);	
			g = null;
		}

		if (n!=null){
			locationManager.removeUpdates(n);		
			n = null;
		}
		gpsRunning=false;
		stopGPSHandler.removeMessages(1);
		WakeLocker.release();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	




}
