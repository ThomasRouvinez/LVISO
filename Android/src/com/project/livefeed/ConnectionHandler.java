package com.project.livefeed;

import java.util.HashMap;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.project.livefeed.util.SystemUiHider;
import com.thingmagic.*;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class ConnectionHandler extends Activity {

	// ----------------------------------------------------------------------
	// Variables.
	// ----------------------------------------------------------------------

	private static final boolean AUTO_HIDE = true;
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final boolean TOGGLE_ON_CLICK = true;
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
	private static final String ACTION_USB_PERMISSION = "com.project.livefeed.ConnectionHandler.USB_PERMISSION"; 
	private SystemUiHider mSystemUiHider;
	private FT_Device ftDev = null;
	private D2xxManager ftD2xx = null;

	// GUI related variables.
	private Context context;
	private Button connectionButton;

	// ThingMagic related variables.
	private String uriString = "";

	// ----------------------------------------------------------------------
	// Android loop.
	// ----------------------------------------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_connection_handler);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);
		context = this;

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
		.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
			// Cached values.
			int mControlsHeight;
			int mShortAnimTime;

			@Override
			@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
			public void onVisibilityChange(boolean visible) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
					// If the ViewPropertyAnimator API is available
					// (Honeycomb MR2 and later), use it to animate the
					// in-layout UI controls at the bottom of the
					// screen.
					if (mControlsHeight == 0) {
						mControlsHeight = controlsView.getHeight();
					}
					if (mShortAnimTime == 0) {
						mShortAnimTime = getResources().getInteger(
								android.R.integer.config_shortAnimTime);
					}
					controlsView
					.animate()
					.translationY(visible ? 0 : mControlsHeight)
					.setDuration(mShortAnimTime);
				} else {
					// If the ViewPropertyAnimator APIs aren't
					// available, simply show or hide the in-layout UI
					// controls.
					controlsView.setVisibility(visible ? View.VISIBLE
							: View.GONE);
				}

				if (visible && AUTO_HIDE) {
					// Schedule a hide().
					delayedHide(AUTO_HIDE_DELAY_MILLIS);
				}
			}
		});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});

		// Listener for the connection button.
		findViewById(R.id.connection_button).setOnTouchListener(
				mDelayHideTouchListener);

		// ----------------------------------------------------------------------------
		// Connection button.
		// ----------------------------------------------------------------------------

		this.connectionButton = (Button)this.findViewById(R.id.connection_button);
		this.connectionButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					String usbDeviceName = "";
					PendingIntent mPermissionIntent;

					// Find USB device attached.
					UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
					HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
					Set<String> keySet = deviceList.keySet();

					for(String key : keySet){
						usbDeviceName = deviceList.get(key).getDeviceName();

						// Get USB authorizations.
						if(!manager.hasPermission(deviceList.get(key))){
							mPermissionIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, new Intent( 
									ACTION_USB_PERMISSION), 0); 
							manager.requestPermission(deviceList.get(key), mPermissionIntent);
						}

						// Determine if the FTDI driver is required.
						int deviceClass = deviceList.get(key).getDeviceClass();
						
						if(deviceClass == 0){
							ftDev = useFTDIdriver((Activity) context, deviceList.get(key));
	        			}
						
						new AndroidUsbReflection(manager, ftDev, deviceList.get(key), deviceClass);
					}

					// Create a serial reader object.
					usbDeviceName = "eapi:///"+ usbDeviceName.split("/")[1];

					Reader reader = null;
					reader.create(usbDeviceName);

					// If succeeded, then open the wizard.
					Intent wizardIntent = new Intent(ConnectionHandler.this, Wizard.class);
					ConnectionHandler.this.startActivity(wizardIntent);
					//finish();

				} catch (Exception e) {
					//AndroidUtlils.toastLong(context, "No reader found");
					AndroidUtlils.toastLong(context, e.toString());
				}

			}
		});
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	public FT_Device useFTDIdriver(Activity activity, UsbDevice device) throws Exception {
		Context context = activity.getApplicationContext();		
		
		if(ftD2xx == null) {
            try {            	         	
                ftD2xx = D2xxManager.getInstance(context);
            } catch (D2xxManager.D2xxException ex) {
            	System.out.println(ex.getMessage());
            }
        }
		try{
		  if(ftDev == null) {			 
	            int devCount = 0;
	            devCount = ftD2xx.createDeviceInfoList(context);
	            D2xxManager.FtDeviceInfoListNode[] deviceList = new D2xxManager.FtDeviceInfoListNode[devCount];
	            ftD2xx.getDeviceInfoList(devCount, deviceList);
	            ftDev = ftD2xx.openByIndex(context, 0);
	            if(ftDev == null)
	            	throw new Exception();
	        }
		}
		catch(Exception ex){
			throw new Exception();
		}
		
		return ftDev;
	}
}
