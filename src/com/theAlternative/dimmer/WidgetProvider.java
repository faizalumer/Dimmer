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
		Intent intent = new Intent(context,DimmerService.class);
		intent.setAction(DimmerService.actionToggle);
		PendingIntent pendingIntent = PendingIntent.getService(context, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT );
		
		RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget);
		views.setOnClickPendingIntent(R.id.btn_widget_dimmer_toggle, pendingIntent);
		
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
		
		//Change the widget icon, if a broadcast was sent by the service
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		ComponentName provider = new ComponentName(context,WidgetProvider.class);
		int[] appWidgetIds = appWidgetManager.getAppWidgetIds(provider);
		if(BuildConfig.DEBUG){Log.d("faizal"," : WidgetProvider : onReceive : appWidgetIds.length=" + appWidgetIds.length);}
		RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget);
		String action = intent.getAction();
		
		if (appWidgetIds != null && appWidgetIds.length > 0) {
			if (action.equals(DimmerService.SERVICE_STARTED_INTENT_FILTER)) {
				if(BuildConfig.DEBUG){Toast.makeText(context, "Dimmer started", Toast.LENGTH_SHORT).show();}
				views.setImageViewResource(R.id.btn_widget_dimmer_toggle, R.drawable.ic_widget_on);
				appWidgetManager.updateAppWidget(appWidgetIds, views);
			}
			else if (action.equals(DimmerService.SERVICE_STOPPED_INTENT_FILTER)) {
				if(BuildConfig.DEBUG){Toast.makeText(context, "Dimmer stopped", Toast.LENGTH_SHORT).show();}
				views.setImageViewResource(R.id.btn_widget_dimmer_toggle, R.drawable.ic_widget_off);
				appWidgetManager.updateAppWidget(appWidgetIds, views);
			}
		}
				
	}
	
}