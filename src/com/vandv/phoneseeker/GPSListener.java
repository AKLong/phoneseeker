package com.vandv.phoneseeker;

import static com.vandv.phoneseeker.CommonUtilities.STATUS_COMPLETED;
import static com.vandv.phoneseeker.CommonUtilities.STATUS_IMPROVING;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import static com.vandv.phoneseeker.CommonUtilities.displayMessage;
import static com.vandv.phoneseeker.CommonUtilities.setPositionData;



// listener class for GPS updates
public class GPSListener implements LocationListener{

	private float accuracy;
	private boolean firstPosReceived;
	
	Context mContext;
	public GPSListener(Context context) {
		mContext=context;
		firstPosReceived=false;
	}
		
	public boolean validPositionReceived(){
		return firstPosReceived;
	}
	// if new location is received ..
	@Override
	public void onLocationChanged(Location location) {

		setPositionData(location);
		String locString = "";

		String lat = String.valueOf(location.getLatitude());
		String lon = String.valueOf(location.getLongitude());
		locString = lat + ":" + lon + " - " + location.getProvider() + " - " + String.valueOf(location.getAccuracy())  ; 

		// display message to gui..
	//	displayPosition(mContext,location);
		
		
		// post data back to server..
		
		if (location.getProvider().equals("gps")){
			// stop the GPS..
			ServerUtilities.postLocation(mContext,String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),String.valueOf(location.getAccuracy()),location.getProvider(),STATUS_COMPLETED,MainActivity.imei);
			CommonUtilities.stopGPS();
		}else{
			if (!firstPosReceived ){
				firstPosReceived=true;
				accuracy = location.getAccuracy();
				ServerUtilities.postLocation(mContext,String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),String.valueOf(location.getAccuracy()),location.getProvider(),STATUS_IMPROVING,MainActivity.imei);
			} else if (accuracy > location.getAccuracy()){
				accuracy = location.getAccuracy();
				ServerUtilities.postLocation(mContext,String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),String.valueOf(location.getAccuracy()),location.getProvider(),STATUS_IMPROVING,MainActivity.imei);
			}
		}

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

}
