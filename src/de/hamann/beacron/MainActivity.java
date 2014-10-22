package de.hamann.beacron;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity {
	private final static int REQUEST_ENABLE_BT = 1;
	private static final String TAG = "Beacron";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
	
	public boolean onHostButton(View view){
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
		}
		
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
		Log.d(TAG,"Local BT:"+mBluetoothAdapter.getAddress());
		
		//mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(name, uuid)
		
		UUID testUUID = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
		
		try{
			BluetoothServerSocket listener = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("TestDevice", testUUID);
			BluetoothSocket socket = listener.accept();
			
			if(socket != null){
				Log.d(TAG, "connection accepted");
				Log.d(TAG,"Strength: "+socket.getRemoteDevice().EXTRA_RSSI);
			} else {
				Log.e(TAG, "no connection found");
			}
			
		} catch(IOException e){
			e.printStackTrace();
		}
		
		
		return true;
	}
	
	public boolean onConnectButton(View view){
		
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
		    // Device does not support Bluetooth
		}
		
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
		//mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(name, uuid)
		
		UUID testUUID = UUID.fromString("38400000-8cf0-11bd-b23e-10b96e4ef00d");
		
		try{
			
			BluetoothDevice remoteDevice = mBluetoothAdapter.getRemoteDevice("98:D6:F7:6E:9B:11");
			
			
			BluetoothSocket socket= remoteDevice.createInsecureRfcommSocketToServiceRecord(testUUID);
			
			
			if(socket != null){
				Log.d(TAG, "connection accepted");
				Log.d(TAG,"Strength Remote: "+socket.getRemoteDevice().EXTRA_RSSI);
			} else {
				Log.e(TAG, "no connection found");
			}
			
		} catch(IOException e){
			e.printStackTrace();
		}
		
		
		return true;
	}
}
