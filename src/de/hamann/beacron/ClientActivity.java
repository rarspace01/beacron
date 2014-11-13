package de.hamann.beacron;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayItem.HotspotPlace;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class ClientActivity extends ActionBarActivity implements
		ActionBar.TabListener {

	private static final String TAG = "Beacron - ClientActivity ";

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	private String intentURL;
	private String hostAddress;
	private View lMapView_;
	
	private OverlayItem curLocItem_;
	private MyItemizedOverlay itemizedOverlay;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client);

		// store Intent-Data
		Intent localIntent = getIntent();
		
		if(localIntent != null){
			if(localIntent.getAction() == Intent.ACTION_VIEW){
				intentURL = localIntent.getDataString();
				Log.d(TAG,"got URL: "+intentURL);
				
				intentURL = intentURL.substring(intentURL.indexOf("map.php?host=")+"map.php?host=".length());
				
				if(intentURL.matches("[0-9A-F]{12,12}")){
				
				hostAddress = intentURL;
				
				}else{
					Log.d(TAG,"tried to parse hostadress: "+intentURL);
				}
				
			}
			Log.d(TAG,"got smth");
		}
		
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
		ResourceProxy mResourceProxy = new DefaultResourceProxyImpl(
		getApplicationContext());
		
        itemizedOverlay = new MyItemizedOverlay(new ArrayList<OverlayItem>(),
                null, mResourceProxy);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.client, menu);
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
		} else if (id == R.id.menu_refresh){
			
			// start refresh if hostadress ist not empty
			if(hostAddress != null){
				Log.d(TAG,"we would start to retrieve the location from the host from the server");
				new retrieveHostLocation().execute(hostAddress);
			}
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			
			switch (position){
			case 0: return new MapFragment(position+1);
			case 1: return new RadarFragment(position+1);
			default: return new MapFragment(position+1);
			}
			
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_fragment_map).toUpperCase(l);
			case 1:
				return getString(R.string.title_fragment_radar).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public class MapFragment extends Fragment {

		private static final String ARG_SECTION_NUMBER = "section_number";

		private static final String TAG = "Fragment - Map";

		private IMapController mapController_;
		private MapView mapView_;
		private Context context_;
		private LocationManager locationManager_;
		
		public MapFragment(int sectionNumber) {
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			this.setArguments(args);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			this.context_ = getActivity();
			
			View rootView = inflater.inflate(R.layout.fragment_map, container,
					false);
			
			mapView_ = (MapView) rootView.findViewById(R.id.mapview);
			
			if(mapView_ != null){
				
			Log.d(TAG,"mapview not null");
			
			mapView_.setTileSource(TileSourceFactory.MAPNIK);
			mapView_.getOverlayManager().getTilesOverlay().setOvershootTileCache(30);
			// TileSourceFactory.
			mapView_.setMultiTouchControls(true);
			mapView_.setBuiltInZoomControls(true);
			mapController_ = mapView_.getController();
			mapController_.setZoom(15);
			
			Criteria crit = new Criteria();
			crit.setAccuracy(Criteria.ACCURACY_FINE);

			locationManager_ = (LocationManager) context_
					.getSystemService(Context.LOCATION_SERVICE);

			String provider = locationManager_.getBestProvider(crit, true);

			Location loc = locationManager_.getLastKnownLocation(provider);
			if (loc != null) {
				Log.d(TAG,"Got a location: "+loc.getLatitude()+"/"+loc.getLongitude());
				mapController_.setCenter(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
				
			}else
			{
				Log.d(TAG,"No location");
				mapController_.setCenter(new GeoPoint(53.557438,9.9988136));
			}
			
			}
			
			if(hostAddress != null){
				Log.d(TAG,"we would start to retrieve the location from the host from the server");
				new retrieveHostLocation().execute(hostAddress);
			}
			
			lMapView_ = rootView;
			
			return rootView;
		}
	}
	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public class RadarFragment extends Fragment {

		private static final String ARG_SECTION_NUMBER = "section_number";

		public RadarFragment(int sectionNumber) {
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			this.setArguments(args);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_radar, container,
					false);
			return rootView;
		}
	}
	
	private class retrieveHostLocation extends AsyncTask<String, Void, GeoPoint> {

		@Override
		protected GeoPoint doInBackground(String... params) {
			GeoPoint localPoint = null;
			
			String[] latlong;
			double lat,lng;
			
			String hostAddress = params[0];
			

			// do a post request to the site
			
			Log.d(TAG, "do a post request to the site");
			
			// Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost("http://dcdn.de/beacron/map.php");

		    try {
		        // Add your data
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        ResponseHandler<String> responseHandler=new BasicResponseHandler();
		        nameValuePairs.add(new BasicNameValuePair("host", hostAddress));
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        // Execute HTTP Post Request
		        String responseBody = httpclient.execute(httppost,responseHandler);
		        
		        if(responseBody.matches("[0-9]{1,3}([.][0-9]+)*[/][0-9]{1,3}([.][0-9]+)*")){
		        	latlong = responseBody.split("/");
		        	
		        	if(latlong != null){
		        		lat = Double.parseDouble(latlong[0]);
		        		lng = Double.parseDouble(latlong[1]);
		        		localPoint = new GeoPoint(lat, lng);
		        	}
		        	
		        }
		        
		        Log.d(TAG,"Content ["+responseBody+"]");
		        
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    }
		    
		    return localPoint;
		}
		
		protected void onPostExecute(GeoPoint finalPoint) {
			
			MapView mapView_ = (MapView) lMapView_.findViewById(R.id.mapview);
			mapView_.getController().setCenter(finalPoint);
			
			Drawable newMarker = getResources().getDrawable(R.drawable.ic_launcher);
			newMarker.setAlpha(155);
			curLocItem_ = new OverlayItem("Host location",
					"Host2 Location", finalPoint);
			curLocItem_.setMarker(newMarker);
			curLocItem_.setMarkerHotspot(HotspotPlace.CENTER);
			
			itemizedOverlay.addItem(curLocItem_);
			
			mapView_.getOverlays().clear();
			
			mapView_.getOverlays().add(itemizedOverlay);
			
			//create overlay
			

			
			
			lMapView_.invalidate();
			
		}

	}

}
