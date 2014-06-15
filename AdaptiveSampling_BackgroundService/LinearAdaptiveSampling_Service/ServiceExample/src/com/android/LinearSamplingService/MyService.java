package com.android.LinearSamplingService;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Calendar;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service implements SensorEventListener {

	private static final String TAG = "MyService";
	private SensorManager mSensorManager;
	private double gravityX, gravityY, gravityZ = 0;
	private double linear_accelerationX, linear_accelerationY,
			linear_accelerationZ = 0;

	public int move, notMove = 0;

	/* TODO bu "<?>" ne işe yarar ? */
	private ScheduledFuture<?> sensorPeriodHandle;

	public Runnable listenSensors;
	/*
	 * start the thread every PERIOD seconds that means sensors won't listen for
	 * PERIOD - SLEEP seconds
	 */
	public int PERIOD = 90;
	/*
	 * thread sleeps for SLEEP seconds that means sensors will listen for SLEEP
	 * seconds
	 */
	public int SLEEP = 30000;

	public Timer timer;
	private ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {

		Toast.makeText(this, "Congrats! MyService Created", Toast.LENGTH_LONG)
				.show();
		Log.d(TAG, "onCreate");
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	}

	// @Override
	// public void onResume() {
	// super.onResume();
	//
	// mSensorManager.registerListener(this,
	// mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
	// SensorManager.SENSOR_DELAY_NORMAL);
	//
	// schedule();
	// }

	// @Override
	// public void onPause() {
	// /* unregister listeners */
	// mSensorManager.unregisterListener(this);
	//
	// super.onPause();
	// }
	//
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.layout.linear_sample, menu);
	// return true;
	// }

	@Override
	public void onStart(Intent intent, int startId) {
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);

		schedule();

	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "MyService Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");

		mSensorManager.unregisterListener(this);

	}

	public void onSensorChanged(SensorEvent event) {
		// In this example, alpha is calculated as t / (t + dT),
		// where t is the low-pass filter's time-constant and
		// dT is the event delivery rate.

		final double alpha = 0.8;

		// Isolate the force of gravity with the low-pass filter.
		gravityX = alpha * gravityX + (1 - alpha) * event.values[0];
		gravityY = alpha * gravityY + (1 - alpha) * event.values[1];
		gravityZ = alpha * gravityZ + (1 - alpha) * event.values[2];

		// Remove the gravity contribution with the high-pass filter.
		linear_accelerationX = event.values[0] - gravityX;
		linear_accelerationY = event.values[1] - gravityY;
		linear_accelerationZ = event.values[2] - gravityZ;

		// xCoor.setText("X: " + linear_accelerationX);
		// yCoor.setText("Y: " + linear_accelerationY);
		// zCoor.setText("Z: " + linear_accelerationZ);

		// moveNotMove.setText("Did I move? (If positive, yes): "
		// + (move - notMove));
		// wait.setText("last waiting time: " + (PERIOD - 30));

		double om = Math.sqrt(linear_accelerationX * linear_accelerationX
				+ linear_accelerationY * linear_accelerationY
				+ linear_accelerationZ * linear_accelerationZ);

		// omega.setText("O: " + om);

		String mydate = java.text.DateFormat.getDateTimeInstance().format(
				Calendar.getInstance().getTime());

		// time.setText("time: " +mydate);

		 /* save coordinates */
		 SaveToExternalStorage saveToStorage = new SaveToExternalStorage("wait: " + (PERIOD - 30),
		 "linearSampling.txt");
		 saveToStorage.save();
		
		 /* save date */
		 saveToStorage = new SaveToExternalStorage("Date: " + mydate + "\n",
		 "linearSampling.txt");
		 saveToStorage.save();

		if (om < 0.09)
			notMove++;
		// MOVE = false;
		else
			move++;
		// MOVE = true;

	}

	public void schedule() {

		listenSensors = new Runnable() {
			public void run() {
				move = 0;
				notMove = 0;

				RegisterSensor();

				/* Sleep for SLEEP seconds sensing happens here */
				try {
					Thread.sleep(SLEEP);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				unRegisterSensor();

				/* interesting event, decrease waiting period */
				if (move > notMove && PERIOD > 45) {
					PERIOD = PERIOD - ((PERIOD - 30) / 2);
					scheduler.shutdownNow();
					setupTimer(PERIOD);
				}

				/* not interesting event increase waiting period */
				else if (move < notMove && PERIOD < 150) {
					PERIOD = PERIOD + (PERIOD - 30);
					scheduler.shutdownNow();
					setupTimer(PERIOD);
				}

			};
		};

		/* listensensors runs every PERIOD seconds */
		sensorPeriodHandle = scheduler.scheduleAtFixedRate(listenSensors, 0,
				PERIOD, SECONDS);

	}

	public void setupTimer(long duration) {

		scheduler = Executors.newScheduledThreadPool(1);

		scheduler.scheduleAtFixedRate(listenSensors, (PERIOD - 30), duration,
				SECONDS);
	}

	public void RegisterSensor() {
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	public void unRegisterSensor() {
		mSensorManager.unregisterListener(this);

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
}
