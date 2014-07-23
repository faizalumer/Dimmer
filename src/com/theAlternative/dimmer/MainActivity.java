package com.theAlternative.dimmer;

//import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends ActionBarActivity {
	
	ServiceStoppedReceiver serviceStoppedReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		serviceStoppedReceiver = new ServiceStoppedReceiver();
		
		//Setup the timer choices drop down
		Spinner spinner = (Spinner) findViewById(R.id.spn_timer);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.timer_choices, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		initTimerSpinner();
		spinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			boolean isInitialized = false;

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				//if this is the first call to onItemSelected, do nothing
				//because it gets called automatically by the framework in onCreate()
				if(!isInitialized){
					if(BuildConfig.DEBUG){Log.d("faizal","Spinner : onItemSelected : first time");}
					isInitialized = true;
					return;
				}
				if(BuildConfig.DEBUG){Log.d("faizal","Spinner : onItemSelected");}
				
				//get the value of the selected option
				String selected = (String) getResources().getStringArray(R.array.timer_choices_values)[pos];
				
				//Add the selected timer value to sharedPreferences
				SharedPreferences settings = getSharedPreferences(Constants.PREFERENCES_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt(Constants.PREFERENCES_KEY_TIMER, Integer.valueOf(selected));
				editor.commit();
				if(BuildConfig.DEBUG){Toast.makeText(MainActivity.this, selected, Toast.LENGTH_SHORT).show();}
				
				//if the service is already running, tell the service to update the timer.
				if(DimmerService.isInstanceCreated()){
					Intent intent = new Intent(MainActivity.this, DimmerService.class);
					intent.setAction(DimmerService.actionUpdateTimer);
					startService(intent);
				}
				
					
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				if(BuildConfig.DEBUG){Toast.makeText(MainActivity.this, "no selection", Toast.LENGTH_SHORT).show();}
				
			}
			
		});
		
		//Setup the brightness adjustment seekbar
		SeekBar seekBar = (SeekBar) findViewById(R.id.skb_Dimmer);
		seekBar.setMax(OverlayHandler.MAX_BRIGHTNESS_PERCENT);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
			OverlayHandler overlayHandler= new OverlayHandler(getApplicationContext());
			int brightness;
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				overlayHandler.changeOverlayAlpha(seekBar.getMax() - progress);
				brightness = progress;
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if(BuildConfig.DEBUG) {Log.d("faizal","onStopTrackingTouch-"+brightness+", alpha-"+(255*(brightness/100.00)));}
				
				//Add the brightness level to sharedPreferences
				SharedPreferences settings = getSharedPreferences(Constants.PREFERENCES_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt(Constants.PREFERENCES_KEY_BRIGHTNESS, brightness);
				editor.commit();
				
			}
			
		});
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main,menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case R.id.action_about:
			Intent intent = new Intent(this,AboutActivity.class);
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(menuItem);
		}
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		if(BuildConfig.DEBUG){Log.d("faizal","onPause");}
		unregisterReceiver(serviceStoppedReceiver);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		if(BuildConfig.DEBUG){Log.d("faizal","onResume");}
		initToggleButton();
		initSeekBarProgress();
		registerReceiver(serviceStoppedReceiver, new IntentFilter(DimmerService.SERVICE_STOPPED_INTENT_FILTER));
	}
	
	@Override
	protected void onDestroy(){
		if(BuildConfig.DEBUG) {Log.d("faizal","MainActivity : onDestroy");}
		super.onDestroy();
	}
	
	
	public void onToggleClicked(View view){
		Intent intent = new Intent(this, DimmerService.class);
		if(DimmerService.isInstanceCreated()){
			stopService(intent);
		}
		else{
			startService(intent);
		}
		
	}
	
	/*private boolean isServiceRunning(Class<?> serviceClass){
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for(RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
			//Log.d("faizal","running service - " + service.service.getClassName());
			if(serviceClass.getName().equals(service.service.getClassName())){
				Log.d("faizal","running service - " + service.service.getClassName());
				return true;
			}
		}
		return false;
	}*/
	
	private void initToggleButton(){
		ToggleButton btn = (ToggleButton) findViewById(R.id.btn_Dimmer);
		
		if(DimmerService.isInstanceCreated()){
			btn.setChecked(true);
		}
		else{
			btn.setChecked(false);
		}
	}
	
	private void initTimerSpinner(){
		//get seekbar object
		Spinner spinner = (Spinner) findViewById(R.id.spn_timer);
		
		//Use previously stored value if available, otherwise set default value
		SharedPreferences settings = getSharedPreferences(Constants.PREFERENCES_NAME, 0);
		int timerValue = settings.getInt(Constants.PREFERENCES_KEY_TIMER,DimmerService.DEFAULT_TIMER_MINS);
		String[] values = getResources().getStringArray(R.array.timer_choices_values);
		for(int i=0; i< values.length; i++){
			if(Integer.valueOf(values[i]) == timerValue){
				spinner.setSelection(i);
				break;
			}
		}
		
	}
	
	private void initSeekBarProgress(){
		//get seekbar object
		SeekBar seekBar = (SeekBar) findViewById(R.id.skb_Dimmer);
		
		//Use previously store value if available, otherwise set default value
		SharedPreferences settings = getSharedPreferences(Constants.PREFERENCES_NAME, 0);
		int progress = settings.getInt(Constants.PREFERENCES_KEY_BRIGHTNESS,OverlayHandler.DEFAULT_BRIGHTNESS_PERCENT);
		seekBar.setProgress(progress);
	}
	
	private class ServiceStoppedReceiver extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context context, Intent intent){
			ToggleButton btn = (ToggleButton) findViewById(R.id.btn_Dimmer);
			btn.setChecked(false);
			
		}
	}
}
