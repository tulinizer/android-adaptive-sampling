package com.android.ExponentialSamplingService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	//start the service
	public void onClickStartServie(View V)
	{
		//start the service from here //MyService is your service class name
		
		startService(new Intent(this, MyService.class));
	//	startActivity(new Intent(this,LinearSample.class));
	}
	//Stop the started service
	public void onClickStopService(View V)
	{
		//Stop the running service from here//MyService is your service class name
		//Service will only stop if it is already running.
		stopService(new Intent(this, MyService.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
