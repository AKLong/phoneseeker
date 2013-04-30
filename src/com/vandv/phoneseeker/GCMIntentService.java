package com.vandv.phoneseeker;

import static com.vandv.phoneseeker.CommonUtilities.SENDER_ID;
import static com.vandv.phoneseeker.CommonUtilities.initGPS;
import static com.vandv.phoneseeker.CommonUtilities.cancelGPS;
import static com.vandv.phoneseeker.CommonUtilities.newPositionRequest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.vandv.phoneseeker.R;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";
	String ipAddress;
    String imei;
	static Context mContext;
 
	public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
 //       displayMessage(context, "Your device registred with GCM");
        Log.d("NAME", MainActivity.imei);
        ServerUtilities.register(context, RegisterActivity.userName.getText().toString(), MainActivity.imei, registrationId);
    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
 //       displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, MainActivity.imei);

    }

    
	private static Handler startGPSHandler = new Handler(){
		public void handleMessage(Message m) {
			initGPS(mContext);	

			}
		
	};
	private static Handler stopGPSHandler = new Handler(){
		public void handleMessage(Message m) {
			cancelGPS();	

			}
		
	};

	
    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
		WakeLocker.acquire(getApplicationContext());

        ipAddress = intent.getExtras().getString("ip");
        String id = intent.getExtras().getString("id");
        mContext = context;
        newPositionRequest(context,ipAddress,id);
 //************************************
		// get the phones imei number
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		imei = telephonyManager.getDeviceId();		
		
		// this is a frig! For some reason GPS cannot be called immediately, so call it after 1 second.
		Message m = new Message();
		m.what = 1;
	  	startGPSHandler.sendMessageDelayed(m, 1000);
		
		Message stopMessage = new Message();
		stopMessage.what = 1;

//		stopGPSHandler.sendMessageDelayed(stopMessage, 30000);
		


        generateNotification(context, "Position Request reveived from " + ipAddress);
        		
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
//        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
  //      displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
 //       displayMessage(context, getString(R.string.gcm_recoverable_error,
 //               errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        
        Notification noti = new Notification.Builder(context)
        .setContentTitle("New alert")
        .setContentText(message)
        .setSmallIcon(icon)
        .setContentIntent(pIntent)
        .build();
    
        noti.defaults |= Notification.DEFAULT_VIBRATE;

        // Hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        noti.flags |= Notification.FLAG_SHOW_LIGHTS;
        noti.ledARGB = Color.argb(255, 25, 235, 50);
        noti.ledOffMS=900;
        noti.ledOnMS=100;
         notificationManager.notify(0, noti); 
        

    }

 
  
}
