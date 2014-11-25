package com.project.livefeed;

import org.apache.http.HttpResponse;
import com.project.livefeed.Objects.ForwardData;
import com.project.livefeed.Objects.ZoneInfo;
import HTTP.HTTPUtils;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Wizard extends Activity{
	
	public static String serverAddress = "http://192.168.1.43:80/hubnet";

	// Handler and thread management for background activities.
	private Handler handler;

	// GUI-related variables.
	private Button buttonApply;
	private Context context;

	// Additional information storage.
	private ZoneInfo zoneInfo;
	private EditText zoneID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wizard);
		context = this;

		// Setup the handler.
		handler = new Handler();

		// Get the edit text fields.
		this.zoneID = (EditText)this.findViewById(R.id.editTextZoneID);

		// Instantiate the object storing the information.
		zoneInfo = new ZoneInfo();

		// Buttons management.
		this.buttonApply = (Button)this.findViewById(R.id.ButtonApply);
		this.buttonApply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ForwardData sample = new ForwardData(1, "testTag", 0.5, null);
				new HTTPUtils().execute(sample);
			}
		});
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
			// Disable the apply button.
			buttonApply.setVisibility(View.GONE);
			buttonApply.setEnabled(false);

			// Start reading.
			AndroidUtlils.toastShort(context, "Start Reading");
			handler.postDelayed(runnable, 3000);
			return true;
		case R.id.stopAction:
			// Disable the apply button.
			buttonApply.setVisibility(View.VISIBLE);
			buttonApply.setEnabled(true);

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
			
			// Transmit the values.

			// Restart the loop.
			handler.postDelayed(this, 3000);
		}
	};
}