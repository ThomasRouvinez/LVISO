package com.project.livefeed;

import HTTP.HTTPUtils;
import android.app.Activity;
import android.app.Application;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.project.livefeed.Objects.ForwardData;
import com.project.livefeed.Objects.RecordData;

public class Wizard extends Activity{

	public static String serverAddress = "http://192.168.1.44:80/hubnet";
	public static SharedPreferences preferences;
	public static Context context;

	// Handler and thread management for background activities.
	private Handler handler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wizard);
		context = this;

		// Setup the handler.
		handler = new Handler();

		// Display the fragment as the main content.
		FragmentManager mFragmentManager = getFragmentManager();
		FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
		PrefsFragment mPrefsFragment = new PrefsFragment();
		mFragmentTransaction.replace(android.R.id.content, mPrefsFragment);
		mFragmentTransaction.commit();

		preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from an XML resource
			addPreferencesFromResource(R.xml.preference);
			PreferenceManager.getDefaultSharedPreferences(Wizard.context).registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if (key.equals("serverip_preference")) {
				String address = sharedPreferences.getString("serverip_preference", "");
				
				if(address != ""){
					Wizard.serverAddress = "http://" + address + ":80/hubnet";
				}
			}
		}
	}

	// -----------------------------------------------------------------------
	// Action bar management.
	// -----------------------------------------------------------------------

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.wizard_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.startAction:
			// Start reading.
			AndroidUtlils.toastShort(context, "Start Reading");
			handler.postDelayed(runnable, (long) (1000 * Float.parseFloat(preferences.getString("datarate_preference", "5"))));
			return true;

		case R.id.stopAction:
			// Stop reading.
			AndroidUtlils.toastShort(context, "Stop Reading");
			handler.removeCallbacks(runnable);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// -----------------------------------------------------------------------
	// Timed task management.
	// -----------------------------------------------------------------------

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// Test.
			AndroidUtlils.toastShort(context, "Read Test...");

			// Read values from reader.

			// Pack values.
			RecordData values[] = new RecordData[2]; 
			values[0] = new RecordData("tag1test-457389", 18.2);
			values[1] = new RecordData("tag2test-786784", 12);
			ForwardData sample = new ForwardData(preferences.getString("eventid_preference", "-1"), preferences.getString("sensorid_preference", "-1"), values);

			// Transmit the values.
			new HTTPUtils().execute(sample);

			// Restart the loop.
			handler.postDelayed(this, (long) (1000 * Float.parseFloat(preferences.getString("datarate_preference", "5"))));
		}
	};
}