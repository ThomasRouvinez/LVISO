package com.project.livefeed;

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
import com.project.livefeed.Objects.ForwardData;
import com.project.livefeed.Objects.RecordData;

public class Wizard extends Activity{
	
	public static String serverAddress = "http://192.168.1.43:80/hubnet";

	// Handler and thread management for background activities.
	private Handler handler;

	// GUI-related variables.
	private Button buttonApply;
	private Context context;

	// Additional information storage.
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

		// Buttons management.
		this.buttonApply = (Button)this.findViewById(R.id.ButtonApply);
		this.buttonApply.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RecordData values[] = new RecordData[2];
				values[0] = new RecordData("tag1test-457389", 18.2);
				values[1] = new RecordData("tag2test-786784", 12);
						
				ForwardData sample = new ForwardData(null, "122", "4", values);
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