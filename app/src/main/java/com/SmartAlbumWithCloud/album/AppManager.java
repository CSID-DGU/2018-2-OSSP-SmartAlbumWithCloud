package com.SmartAlbumWithCloud.album;

import android.app.Activity;
import android.content.Context;

import java.util.Stack;



public class AppManager {
	private static Stack<Activity> activityStack;
	private static AppManager instance;
	private AppManager(){}
	/**
	 * Single Instance
	 */
	public static AppManager getAppManager(){
		if(instance==null){
			instance=new AppManager();
		}
		return instance;
	}
	/**
	 * Add Activity to the stack
	 */
	public void addActivity(Activity activity){
		if(activityStack==null){
			activityStack=new Stack<Activity>();
		}
		activityStack.add(activity);
	}
	/**
	 * Get the current Activity (the last one in the stack)
	 */
	public Activity currentActivity(){
		Activity activity=activityStack.lastElement();
		return activity;
	}
	/**
	 * End the current Activity(the last one in the stack)
	 */
	public void finishActivity(){
		Activity activity=activityStack.lastElement();
		finishActivity(activity);
	}
	/**
	 * End of specified Activity
	 */
	public void finishActivity(Activity activity){
//		// The app will close all, clean up the cache
//		if(activityStack.size()==1){
//			((AppContext)activity.getApplication()).clearWebViewCache();
//
//		}
		if(activity!=null){
			activityStack.remove(activity);
			activity.finish();
			activity=null;
		}
	}
	/**
	 * End the activity of the specified class name
	 */
	public void finishActivity(Class<?> cls){
		for (Activity activity : activityStack) {
			if(activity.getClass().equals(cls) ){
				finishActivity(activity);
			}
		}
	}
	//Get the activity of the specified class name
	public Activity getActivity(Class<?> cls){
		for (Activity activity : activityStack) {
			if(activity.getClass().equals(cls) ){
				return activity ;
			}
	  }
		return null;
	}
	
	/**
	 * End all Activities
	 */
	public void finishAllActivity(){
		for (int i = 0, size = activityStack.size(); i < size; i++){
			if (null != activityStack.get(i)){
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}
	/**
	 * Exit the app
	 */
	public void AppExit(Context context) {
		try {
			finishAllActivity();
			System.exit(0);
		} catch (Exception e) {	}
	}

}