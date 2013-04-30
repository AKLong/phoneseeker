package com.vandv.phoneseeker;

import static com.vandv.phoneseeker.CommonUtilities.GPS_MAX_TIME;
import static com.vandv.phoneseeker.CommonUtilities.STATUS_COMPLETED;
import static com.vandv.phoneseeker.CommonUtilities.STATUS_POSITION_TIMEOUT;
import static com.vandv.phoneseeker.CommonUtilities.positionData;
import static com.vandv.phoneseeker.CommonUtilities.setPositionStatus;
import static com.vandv.phoneseeker.CommonUtilities.updateDB;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public final class CommonUtilities {
	
	// give your server registration url here
//	   static final String BASE_DIR = "http://pi.3lovelane.co.uk/gcm/";
	   static final String BASE_DIR = "http://phoneseeker.3lovelane.co.uk/";
	   static final String SERVER_URL = BASE_DIR + "register.php"; 
	   static final String UNREGISTER_URL = BASE_DIR + "unregister.php"; 
	   static final String LOCATION_URL = BASE_DIR + "setlocation.php"; 
	   static final String ADD_LOCATION_URL = BASE_DIR + "addlocation.php"; 
	   static final String UPDATE_STATUS_URL = BASE_DIR + "updatestatus.php"; 

	   static final String STATUS_COMPLETED = "completed"; 
	   static final String STATUS_WAITING = "waitingForSatellite"; 
	   static final String STATUS_IMPROVING = "improvingAccuracy"; 
	   static final String STATUS_IMPOSSIBLE = "noPositionAvailable"; 
	   static final String STATUS_REQUEST_IMPOSSIBLE = "alreadyServicingRequest"; 
	   static final String STATUS_POSITION_TIMEOUT = "positionRequestTimeout"; 
	   
    // Google project id
    static final String SENDER_ID = "7571892405"; 

    static final int GPS_MAX_TIME = 30000;
    
	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * Tag used on log messages.
     */
    static final String TAG = "VandV GCM";

    static final String SHOW_POSITION_ACTION =
            "com.vandv.phoneseeker.POSITION_RECEIVED";
//    static final String SHOW_IP_ACTION =
//            "com.vandv.phoneseeker.IP_RECEIVED";
//    static final String SHOW_STATUS_ACTION =
//            "com.vandv.phoneseeker.SHOW_STATUS";

    static final String START_GPS_ACTION =    "com.vandv.phoneseeker.startGPSbroadcast";
    static final String STOP_GPS_ACTION =    "com.vandv.phoneseeker.stopGPSbroadcast";
    static final String IP_MESSAGE = "ip";
    static final String LOC_MESSAGE = "location";
    static final String STATUS_MESSAGE = "status";
    public static GPSPosition gps;

    static DatabaseHandler db;
//    static Long dbID;
    static Context mContext;
    static PositionData positionData;
    
    
      
    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
    }
 
 /*    
    static void displayIP(Context context, String ip) {
        Intent intent = new Intent(SHOW_IP_ACTION);
        intent.putExtra(IP_MESSAGE, ip);
        context.sendBroadcast(intent);
    }

    static void displayStatus(Context context, String status) {
    	positionData.setStatus(status);
    	updateDB();
        Intent intent = new Intent(SHOW_STATUS_ACTION);
        intent.putExtra(STATUS_MESSAGE, status);
        context.sendBroadcast(intent);
    }
   */ 
    static void displayPositionData(Context context) {
        Intent intent = new Intent(SHOW_POSITION_ACTION);
//        intent.putExtra(LOC_MESSAGE, positionData);
        context.sendBroadcast(intent);
//       	displayStatus(mContext,"Improving Accuracy");

    }
    
    static void setPositionData(Location location){
    	
    	if (location.getProvider().equals("gps")){
    	positionData.setGPSLat(location.getLatitude());
		positionData.setGPSLon(location.getLongitude());
		positionData.setGPSAccuracy(location.getAccuracy());
    }else{
    	positionData.setNetworkLat(location.getLatitude());
		positionData.setNetworkLon(location.getLongitude());
		positionData.setNetworkAccuracy(location.getAccuracy());
    }
		positionData.setSource(location.getProvider());
		updateDB();
    }

    static void setPositionStatus(String status) {
    	positionData.setStatus(status);
    	updateDB();
    }

    static void newPositionRequest(Context context, String ip , String id){
    	
    	// create new position data
        positionData = new PositionData();
        // set ip address
        positionData.setIP(ip);

        // set server id
        positionData.setServerID(Long.parseLong(id));

        // get handle to database
     	db = new DatabaseHandler(context);
     	
     	// add new position to database and update id in positonData
     	positionData.setID(db.addPositionData());
     	
     	mContext=context;
     	displayPositionData(context);
     	

    }
    
    static void updateDB() {
    	db = new DatabaseHandler(mContext);
		db.updatePositionData();
     	displayPositionData(mContext);

    }

     /**
     * Broadcast a message for main activity to start GPS.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     */
  
    

    static void initGPS(Context context) {
    	mContext = context;
//      	db = new DatabaseHandler(context);
    	if (gps==null){
    		gps = new  GPSPosition(context);
    	}
    		setPositionStatus("Waiting....");

    	Toast.makeText(mContext, "Waiting", Toast.LENGTH_LONG).show();

    	setPositionStatus("Starting GPS");
    	gps.startGPS(context);   	


}

    
    static void startGPS(Context context) {
    	
    	
//    	displayStatus(mContext,"Waiting for GPS");
    	setPositionStatus("Starting GPS");
    	gps.startGPS(context);   	
    	
    }
    static void stopGPS() {

    	gps.stopGPS();
//    	displayStatus(mContext,"Completed");
    	setPositionStatus("Completed");
		db.close();

    }
    static void cancelGPS() {

    	if (gps.gpsRunning==true){
    		gps.cancelGPS();
    	}
    }

}
