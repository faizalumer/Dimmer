package com.theAlternative.dimmer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetProvider extends AppWidgetProvider{
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
		if(BuildConfig.DEBUG){Log.d("faizal"," : WidgetProvider : onUpdate : appWidgetsIds.length=" + appWidgetIds.length);}
		//create a pending intent to toggle the service
		Intent intent = new Intent(context,DimmerService.class);
		intent.setAction(DimmerService.actionToggle);
		PendingIntent pendingIntent = PendingIntent.getService(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT );
		
		//set the on click action for the widget with the pending intent created above
		RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget);
		views.setOnClickPendingIntent(R.id.btn_widget_dimmer_toggle, pendingIntent);
		
		//update the widget icon depending on whether it is ON or OFF
		if(DimmerService.isInstanceCreated()){
			views.setImageViewResource(R.id.btn_widget_dimmer_toggle, R.drawable.ic_widget_on);
		}
		else{
			views.setImageViewResource(R.id.btn_widget_dimmer_toggle, R.drawable.ic_widget_off);
		}
		
		appWidgetManager.updateAppWidget(appWidgetIds, views);
	}
	
	@Override
	public void onReceive(Context context, Intent intent){
		super.onReceive(context, intent);
		if(BuildConfig.DEBUG){Log.d("faizal"," : WidgetProvider : onReceive : " + intent.getAction());}
		
		//get all the widgets
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName provider = new ComponentName(context,WidgetProvider.class);
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
		if(BuildConfig.DEBUG){Log.d("faizal"," : WidgetProvider : onReceive : appWidgetIds.length=" + appWidgetIds.length);}
		//RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget);
		
		//update the widget
		String action = intent.getAction();
		if (appWidgetIds != null && appWidgetIds.length > 0 && (action.equals(DimmerService.SERVICE_STARTED_INTENT_FILTER) || action.equals(DimmerService.SERVICE_STOPPED_INTENT_FILTER))) {
			onUpdate(context,appWidgetManager,appWidgetIds);
			/*//create a pending intent to toggle the service
			Intent intent1 = new Intent(context,DimmerService.class);
			intent.setAction(DimmerService.actionToggle);
			PendingIntent pendingIntent = PendingIntent.getService(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT );
			
			if (action.equals(DimmerService.SERVICE_STARTED_INTENT_FILTER)) {
				if(BuildConfig.DEBUG){Log.d("faizal","WidgetProvider : onReceive : started");}
				views.setImageViewResource(R.id.btn_widget_dimmer_toggle, R.drawable.ic_widget_on);
				//refresh the pending intent.
				//this happens in onUpdate(), but it gets burned after some time
				views.setOnClickPendingIntent(R.id.btn_widget_dimmer_toggle, pendingIntent); 
				appWidgetManager.updateAppWidget(appWidgetIds, views);
			}
			else if (action.equals(DimmerService.SERVICE_STOPPED_INTENT_FILTER)) {
				if(BuildConfig.DEBUG){Log.d("faizal","WidgetProvider : onReceive : stopped");}
				views.setImageViewResource(R.id.btn_widget_dimmer_toggle, R.drawable.ic_widget_off);
				appWidgetManager.updateAppWidget(appWidgetIds, views);
			}*/
		}
				
	}
	
}