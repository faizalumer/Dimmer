package com.theAlternative.dimmer;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class AboutActivity extends ActionBarActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		PackageInfo pInfo;
		String version;
		
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = pInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			version = "NA";
		}
		
		TextView txt_appVersion = (TextView) findViewById(R.id.txt_appVersion);
		txt_appVersion.setText(version); 
	}
		
			
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.main,menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
		case R.id.action_about:
			Intent intent = new Intent(this, );
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(menuItem);
		}
	}*/
}
