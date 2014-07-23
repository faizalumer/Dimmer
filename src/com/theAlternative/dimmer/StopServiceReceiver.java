package com.theAlternative.dimmer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StopServiceReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		if(BuildConfig.DEBUG) {Log.d("faizal","StopServiceReceiver");}
		Intent intent1 = new Intent(context, DimmerService.class);
		context.stopService(intent1);
	}
	
}