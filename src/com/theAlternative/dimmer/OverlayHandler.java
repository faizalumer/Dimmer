package com.theAlternative.dimmer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class OverlayHandler{
	static FrameLayout mFrameLayout;
	Context mContext;
	final static int DEFAULT_BRIGHTNESS_PERCENT = 50;
	public static final int MAX_BRIGHTNESS_PERCENT = 95; 
	
	//constructor
	OverlayHandler(Context context){
		mContext = context;
	}
	
	public void createOverlay(){
		mFrameLayout = new FrameLayout(mContext);
		mFrameLayout.setBackgroundColor(mContext.getResources().getColor(R.color.black));
		
		//take alpha from SharedPreferences if available
		SharedPreferences settings = mContext.getSharedPreferences(Constants.PREFERENCES_NAME, 0);
		int brightness = settings.getInt(Constants.PREFERENCES_KEY_BRIGHTNESS,OverlayHandler.DEFAULT_BRIGHTNESS_PERCENT);
		if(BuildConfig.DEBUG){Log.d("faizal","createOverlay() : multiplier-" + brightness+", alpha-"+(255*(brightness/100.00)));}
		changeOverlayAlpha(MAX_BRIGHTNESS_PERCENT - brightness);
		
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
				WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
				PixelFormat.TRANSLUCENT
				);
		WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(mFrameLayout, params);
		
	}
	
	public void removeOverlay() {
		((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).removeView(mFrameLayout);
		mFrameLayout = null;
	}
	
	/*public void toggle(){
		if (mFrameLayout != null) {
			removeOverlay();
		}
		else{
			createOverlay();
		}
	}*/
	
	public void changeOverlayAlpha(final int multiplier){
		if(mFrameLayout != null){
			mFrameLayout.getBackground().setAlpha((int) (255*(multiplier/100.00)));
		}
		
		
	}
	
	public static boolean isVisible(){
		if(mFrameLayout != null) {
			return true;
		}
		else return false;
	}
	

}