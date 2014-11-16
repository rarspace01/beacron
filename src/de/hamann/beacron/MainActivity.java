package de.hamann.beacron;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
//import android.os.AsyncTask;

public class MainActivity extends ActionBarActivity {
	private static final String TAG = "Beacron - MainActivity ";

	
	private boolean onHostButtonIsStart=true;
	private LocationManager locationManager;
	
	private Button onHostButton;
	private Button onShareButton;


	
	private BluetoothAdapter mBluetoothAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		onHostButton = (Button)findViewById(R.id.btnHost);
		onShareButton = (Button)findViewById(R.id.btnShare);
		
		//check for GPS & network location provider first
		
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	    
		// enable GPS

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Toast.makeText(this, getResources().getString(R.string.needGPS),
                    Toast.LENGTH_LONG).show();
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		       startActivity(intent);
		}
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
        	mBluetoothAdapter.cancelDiscovery();
        }

    }

	public boolean onHostButton(View view) {
		
		onHostButton.setEnabled(false);
		
		if(onHostButtonIsStart){
			
		onShareButton.setEnabled(true);	
		
		onHostButton.setText(R.string.stopHost);	
		
		// do the BT stuff

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			// Device does not support Bluetooth
		}else{

		if (!mBluetoothAdapter.isEnabled()) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
			startActivity(discoverableIntent);

		} else {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
			startActivity(discoverableIntent);
		}

		Log.d(TAG,"Starting Service");
		
		// use this to start and trigger a service
		Intent i= new Intent(this, BeacronLocationUpdateService.class);
		// potentially add data to the intent
		
		i.putExtra("hostBT", mBluetoothAdapter.getAddress());
		this.startService(i); 
		}
		
		}else{
			onHostButton.setText(R.string.startHost);
			onShareButton.setEnabled(false);
			//stop service
			// use this to start and trigger a service
			Intent i= new Intent(this, BeacronLocationUpdateService.class);
			// potentially add data to the intent
			
			this.stopService(i); 
		}
		
		//change Button State
		onHostButtonIsStart=!onHostButtonIsStart;
		
		onHostButton.setEnabled(true);
		
		
		
		return true;
	}


//	private class RetrieveRouteTask extends AsyncTask<String, Void, Void> {
//
//		@Override
//		protected Void doInBackground(String... params) {
//			String deviceAddress = params[0];
//			return null;
//
//			// start BT discovery here
//			
//
//		}
//
//		protected void onPostExecute() {
//			//after
//		}
//
//	}

	
	
	public void onShareButton(View view) {

		Intent sendIntent = new Intent();
		
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		
		if (mBluetoothAdapter != null) {
			
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, "Somebody invited you to a Beacron Session - follow the URL: http://dcdn.de/beacron/map.php?host="+
					mBluetoothAdapter.getAddress().replace(":", "")+" "
					+ " Greetings");
			sendIntent.setType("text/plain");
			// sendIntent.setType("text/html");
			startActivity(sendIntent);
		}

		


	}
}
