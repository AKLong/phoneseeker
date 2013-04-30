package com.vandv.phoneseeker;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import static com.vandv.phoneseeker.CommonUtilities.setPositionStatus;
import static com.vandv.phoneseeker.CommonUtilities.startGPS;


public class timerHandler extends Handler{
	
    @Override
    public void handleMessage(Message msg) {

    	setPositionStatus("Starting GPS....");
    //	startGPS();


    }

}
