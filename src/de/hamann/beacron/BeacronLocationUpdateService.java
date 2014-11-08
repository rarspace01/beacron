package de.hamann.beacron;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class BeacronLocationUpdateService extends Service implements LocationListener {

	private LocationManager locationManager;
	private Bundle intentExtras;
	private String localBTaddress="";
	
	private static final String TAG = "Beacron - BeacronLocationUpdateService ";

	

	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	    
		intentExtras = intent.getExtras();
		if(intentExtras != null){
			localBTaddress = intentExtras.getString("hostBT");
		}
		  
		Criteria crit = new Criteria();
	    crit.setAccuracy(Criteria.ACCURACY_FINE);
	    
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    
		// start location updates
		//locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 5000, 0, this);
		}else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 5000, 0, this);
		}
		
		Log.d(TAG, "Going to report following BT HW address: "+localBTaddress);
		
	    return Service.START_NOT_STICKY;
	    
	  }

	  @Override
	  public IBinder onBind(Intent intent) {
	  //TODO for communication return IBinder implementation
	    return null;
	  }

	@Override
	public void onLocationChanged(Location location) {
		
		// start async task
		
		new updateLocationTask().execute(location);

		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	private class updateLocationTask extends AsyncTask<Location, Void, Void> {

		@Override
		protected Void doInBackground(Location... params) {
			Location location = params[0];
			

			// do a post request to the site
			
			Log.d(TAG, "do a post request to the site");
			
			// Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost("http://dcdn.de/beacron/push.php");

		    try {
		        // Add your data
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        ResponseHandler<String> responseHandler=new BasicResponseHandler();
		        nameValuePairs.add(new BasicNameValuePair("hwaddress", localBTaddress));
		        nameValuePairs.add(new BasicNameValuePair("lat", ""+location.getLatitude()));
		        nameValuePairs.add(new BasicNameValuePair("lng", ""+location.getLongitude()));
		        nameValuePairs.add(new BasicNameValuePair("acc", ""+location.getAccuracy()));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        // Execute HTTP Post Request
		        String responseBody = httpclient.execute(httppost,responseHandler);
		        
		        Log.d(TAG,"Content: ["+responseBody+"]");
		        
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    }
		    
		    return null;
		}

//		protected void onPostExecute() {
//			//after
//		}

	}
	
}

