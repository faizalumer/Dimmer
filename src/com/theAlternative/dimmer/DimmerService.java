package com.theAlternative.dimmer;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class DimmerService extends Service {
	
	OverlayHandler overlayHandler;
	final static int DEFAULT_TIMER_MINS = 30;
	final static String SERVICE_STARTED_INTENT_FILTER = "com.theAlternative.dimmer.DIMMER_SERVICE_STARTED";
	final static String SERVICE_STOPPED_INTENT_FILTER = "com.theAlternative.dimmer.DIMMER_SERVICE_STOPPED";
	//variable to hold the instance of this service.
	private static DimmerService instance = null;
	
	public static final String actionToggle = "ACTION_TOGGLE";
	public static final String actionUpdateTimer = "ACTION_UPDATE_TIMER";
	
	public static boolean isInstanceCreated(){
		return instance != null;
	}
	
	@Override
	public void onCreate(){
		if(BuildConfig.DEBUG) {Log.d("faizal","DimmerService : onCreate");}
		overlayHandler = new OverlayHandler(this);
		instance = this;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId){
		if(BuildConfig.DEBUG) {Log.d("faizal","DimmerService : onStartCommand");}
		
		String action = intent.getAction();
		if(action != null && action.equals(actionToggle)){
			if(BuildConfig.DEBUG) {Log.d("faizal","DimmerService : onStartCommand : actionToggle");}
			if(OverlayHandler.isVisible()){
				if(BuildConfig.DEBUG) {Log.d("faizal","DimmerService : onStartCommand : actionToggle - calling stopself");}
				stopSelf();
			}
			else{
				if(BuildConfig.DEBUG) {Log.d("faizal","DimmerService : onStartCommand : actionToggle - creating overlay");}
				overlayHandler.createOverlay();
				createNotification();
				createTurnOffTimer();
				sendBroadcast(new Intent(DimmerService.SERVICE_STARTED_INTENT_FILTER));
			}
		}
		
		else if(action != null && action.equals(actionUpdateTimer)){
			if(BuildConfig.DEBUG) {Log.d("faizal","DimmerService : onStartCommand : actionUpdateTimer");}
			if(OverlayHandler.isVisible()){
				if(BuildConfig.DEBUG) {Log.d("faizal","DimmerService : onStartCommand : actionUpdateTimer - restarting timer");}
				//create timer to turn off service
				createTurnOffTimer();
			}
			else{
				if(BuildConfig.DEBUG) {Log.d("faizal","DimmerService : onStartCommand : actionUpdateTimer - overlay not visible. not expected");}
				/*overlayHandler.createOverlay();
				createNotification();
				sendBroadcast(new Intent(Constants.SERVICE_STARTED_INTENT_FILTER));*/
			}
		}

		else if(action == null){
		
			overlayHandler.createOverlay();
			createNotification();
			createTurnOffTimer();
			sendBroadcast(new Intent(DimmerService.SERVICE_STARTED_INTENT_FILTER));
		}
		
		return START_NOT_STICKY;
	}
	
	private void createTurnOffTimer(){
		if(BuildConfig.DEBUG) {Log.d("faizal","DimmerService : createTurnOffTimer");}
		
		//take timer value from SharedPreferences if available
		SharedPreferences settings = getSharedPreferences(Constants.PREFERENCES_NAME, 0);
		int timerValue = settings.getInt(Constants.PREFERENCES_KEY_TIMER,DimmerService.DEFAULT_TIMER_MINS);
		
		//if the timer value is -1, it is the option "Never"
		//So don't create it and just return
		if(timerValue == -1){ 
			if(BuildConfig.DEBUG) {Log.d("faizal","DimmerService : createTurnOffTimer : timervalue is -1");}
			return;
		}
		
		Calendar cal = Calendar.getInstance();
		Intent intent = new Intent(this,StopServiceReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 4, intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() + (1000 * timerValue * 60)
				, pendingIntent);
		
		Toast.makeText(this, "Dimmer will turn off in " + timerValue + " minutes", Toast.LENGTH_SHORT).show();
	}
	
	private void cancelTurnOffTimer(){
		if(BuildConfig.DEBUG) {Log.d("faizal","DimmerService : cancelTurnOffTimer");}
		Intent intent = new Intent(this,StopServiceReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 4, intent, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(pendingIntent);	
	}
	
	private void createNotification(){
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle("Dimmer is ON");
		
		//.setContentText("Swipe to turn OFF");
		
		Intent contentIntent = new Intent(this,MainActivity.class);
		PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, contentIntent, 0);
		mBuilder.setContentIntent(contentPendingIntent);
		
		//Builder.addAction is ignored in pre 4.1 devices
		//so on older devices, users have to click on notification 
		//to open the activity and then click OFF.
		Intent deleteIntent = new Intent(this,StopServiceReceiver.class);
		PendingIntent deletePendingIntent = PendingIntent.getBroadcast(this, 1, deleteIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		//mBuilder.setDeleteIntent(deletePendingIntent);
		mBuilder.addAction(R.drawable.ic_action_cancel, "Turn OFF", deletePendingIntent);
		
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Notification mNotification = mBuilder.build();
		//mNotification.flags = Notification.FLAG_ONGOING_EVENT;
		mNotificationManager.notify(Constants.NOTIFICATION_ID, mNotification);
		startForeground(Constants.NOTIFICATION_ID, mNotification);
		
	}
	
	/*private void setDimButtons(boolean dimButtons) {
		//WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		Window window = getWindow();
	    LayoutParams layoutParams = window.getAttributes();
	    float val = dimButtons ? 0 : -1;
	    try {
	        Field buttonBrightness = layoutParams.getClass().getField(
	                "buttonBrightness");
	        buttonBrightness.set(layoutParams, val);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    window.setAttributes(layoutParams);
	}*/
	
	
	
	@Override
	public void onDestroy(){
		if(BuildConfig.DEBUG) {Log.d("faizal","DimmerService: onDestroy");}
		overlayHandler.removeOverlay();
		stopForeground(true);
		cancelTurnOffTimer();
		instance = null;
		sendBroadcast(new Intent(DimmerService.SERVICE_STOPPED_INTENT_FILTER));
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
}