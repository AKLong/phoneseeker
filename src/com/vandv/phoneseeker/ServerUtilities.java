package com.vandv.phoneseeker;

import static com.vandv.phoneseeker.CommonUtilities.SERVER_URL;
import static com.vandv.phoneseeker.CommonUtilities.UNREGISTER_URL;
import static com.vandv.phoneseeker.CommonUtilities.LOCATION_URL;
import static com.vandv.phoneseeker.CommonUtilities.UPDATE_STATUS_URL;
import static com.vandv.phoneseeker.CommonUtilities.ADD_LOCATION_URL;
import static com.vandv.phoneseeker.CommonUtilities.STATUS_IMPOSSIBLE;
import static com.vandv.phoneseeker.CommonUtilities.STATUS_COMPLETED;
import static com.vandv.phoneseeker.CommonUtilities.TAG;
import static com.vandv.phoneseeker.CommonUtilities.displayMessage;
import static com.vandv.phoneseeker.CommonUtilities.positionData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gcm.GCMRegistrar;
import com.vandv.phoneseeker.R;


public final class ServerUtilities {
	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();
	static // Asyntask
	AsyncTask<List<NameValuePair>, Void, Void> mPostTask;
	static // Asyntask
	AsyncTask<List<NameValuePair>, Void, String> mAddLocTask;
	static // Asyntask
	AsyncTask<List<NameValuePair>, Void, String> mPostLocTask;

	/**
	 * Register this account/device pair within the server.
	 *
	 */
	static void register(final Context context, String name, String imei, final String regId) {
		Log.i(TAG, "registering device (regId = " + regId + ")");
		String serverUrl = SERVER_URL;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("regId", regId));
		nameValuePairs.add(new BasicNameValuePair("name", name));
		nameValuePairs.add(new BasicNameValuePair("imei", imei));

		
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
		// Once GCM returns a registration id, we need to register on our server
		// As the server might be down, we will retry it a couple
		// times.
		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(TAG, "Attempt #" + i + " to register");
//			try {
				displayMessage(context, context.getString(
						R.string.server_registering, i, MAX_ATTEMPTS));
				post(serverUrl, nameValuePairs);
				GCMRegistrar.setRegisteredOnServer(context, true);
				String message = context.getString(R.string.server_registered);
				CommonUtilities.displayMessage(context, message);
				return;
/*			} catch (IOException e) {
				// Here we are simplifying and retrying on any error; in a real
				// application, it should retry only on unrecoverable errors
				// (like HTTP error code 503).
				Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					// Activity finished before we complete - exit.
					Log.d(TAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return;
				}
				// increase backoff exponentially
				backoff *= 2;
			}
*/		}
		String message = context.getString(R.string.server_register_error,
				MAX_ATTEMPTS);
		CommonUtilities.displayMessage(context, message);
	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	static void unregister(final Context context, final String imei) {
		Log.i(TAG, "unregistering device (imei = " + imei + ")");
		String serverUrl = UNREGISTER_URL;
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("imei", imei));
//try{
			post(serverUrl, nameValuePairs);
			GCMRegistrar.setRegisteredOnServer(context, false);
			String message = context.getString(R.string.server_unregistered);
			CommonUtilities.displayMessage(context, message);
/*		} catch (IOException e) {
			// At this point the device is unregistered from GCM, but still
			// registered in the server.
			// We could try to unregister again, but it is not necessary:
			// if the server tries to send a message to the device, it will get
			// a "NotRegistered" error message and should unregister the device.
			String message = context.getString(R.string.server_unregister_error,
					e.getMessage());
			CommonUtilities.displayMessage(context, message);
		}
*/	}

	static void postLocation(final Context context, String lat, String lon, String accuracy, String provider, String status, final String imei) {

		// create parameter list to send 
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("id", positionData.getServerID().toString()));
		nameValuePairs.add(new BasicNameValuePair("lat", lat));
		nameValuePairs.add(new BasicNameValuePair("lon", lon));
		nameValuePairs.add(new BasicNameValuePair("accuracy", accuracy));
		nameValuePairs.add(new BasicNameValuePair("provider", provider));
		nameValuePairs.add(new BasicNameValuePair("status", status));
		nameValuePairs.add(new BasicNameValuePair("imei", imei));

		//post data async as this is in the main thread
		mPostLocTask = new AsyncTask<List<NameValuePair>, Void, String>() {

			@Override
			protected String doInBackground(List<NameValuePair>... arg0) {
					String s = post(LOCATION_URL, arg0[0]);
					Log.d(TAG, s);
					
					for (NameValuePair nvp : arg0[0]) {
					    if (nvp.getName() == "status"){
					    	return nvp.getValue();
					    }
					}
					return null;
			}

			@Override
			protected void onPostExecute(String result) {
				mPostLocTask = null;
				if (result == STATUS_COMPLETED || result == STATUS_IMPOSSIBLE){
					WakeLocker.release();					
				}
			}

		};

		// execute post command
		mPostLocTask.execute(nameValuePairs, null, null);
	}


	static void postStatus(final Context context, String status, final String imei) {

		// create parameter list to send 
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("id",  positionData.getServerID().toString()));
		nameValuePairs.add(new BasicNameValuePair("status", status));
		nameValuePairs.add(new BasicNameValuePair("imei", imei));

		//post data async as this is in the main thread
		mPostTask = new AsyncTask<List<NameValuePair>, Void, Void>() {

			@Override
			protected Void doInBackground(List<NameValuePair>... arg0) {
					post(UPDATE_STATUS_URL, arg0[0]);
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				mPostTask = null;
			}

		};

		// execute post command
		mPostTask.execute(nameValuePairs, null, null);
	}




	/**
	 * Issue a POST request to the server.
	 *
	 * @param endpoint POST address.
	 * @param params request parameters.
	 * @return 
	 *
	 * @throws IOException propagated from POST.
	 */
	private static String post(String url, List<NameValuePair> nameValuePairs){
	

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		ArrayList<String> listItems = new ArrayList<String>();
		String resp ="";
		try {
			// Add your data
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			
			if (response.getEntity() != null) {

				resp = EntityUtils.toString(response.getEntity());
				
			}

		} 
		catch (IOException e) {
			e.printStackTrace();
			httppost.abort();
		} 
	
			return resp;
	

	}







	/*		
    		throws IOException {   	

        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);

        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
        	Log.e("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
              throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
      }



	 */    

}
