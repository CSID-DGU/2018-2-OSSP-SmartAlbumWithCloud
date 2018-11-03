package com.SmartAlbumWithCloud.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

import com.SmartAlbumWithCloud.album.AppManager;




public class BaseActivity extends Activity {
    //Whether teh application destroys the flag
	protected boolean isDestroy;
	// a flag that prevents duplicate click settings，
	// This is set to false when clicking to open other activities,
	// Set to true in the OnResume event
	private boolean clickable=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isDestroy=false;
		//Set no title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Vertical Display
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// Add Activity to the task
		AppManager.getAppManager().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		isDestroy=true;
		// End Activity & Remove from Stack
		AppManager.getAppManager().finishActivity(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Set the click flag to clickable each time you return to the interface
		clickable=true;
	}

	/**
	 * Is it currently possible to click
	 * @return
	 */
	protected boolean isClickable(){
		return  clickable;
	}

	/**
	 * Lock click
	 */
	protected void lockClick(){
		clickable=false;
	}



	@Override
	public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
		if(isClickable()) {
			lockClick();
			super.startActivityForResult(intent, requestCode,options);
		}
	}
}
