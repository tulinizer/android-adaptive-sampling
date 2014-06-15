package com.example.scheduledtask;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	private SensorManager mSensorManager;

	/* declare axis objects */
	private TextView _xCoor;
	private TextView _yCoor;
	private TextView _zCoor;

	private SaveToExternalStorage saveToStorage;

	/* TODO bu "<?>" ne işe yarar ? */
	private ScheduledFuture<?> sensorPeriodHandle;

	/*
	 * start the thread every PERIOD seconds that means sensors won't listen for
	 * PERIOD - SLEEP seconds
	 */
	private int PERIOD = 90;
	/*
	 * thread sleeps for SLEEP seconds that means sensors will listen for SLEEP
	 * seconds
	 */
	private int SLEEP = 30000;

	private final ScheduledExecutorService scheduler = Executors
			.newScheduledThreadPool(1);

	public void schedule() {

		final Runnable listenSensors = new Runnable() {
			public void run() {

				RegisterSensor();

				/* Sleep for SLEEP seconds */
				try {
					Thread.sleep(SLEEP);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				unRegisterSensor();

			};
		};

		/* listensensors runs every PEROID seconds */
		sensorPeriodHandle = scheduler.scheduleAtFixedRate(listenSensors, 0,
				PERIOD, SECONDS);

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		_xCoor = (TextView) findViewById(R.id.axeX);
		_yCoor = (TextView) findViewById(R.id.axeY);
		_zCoor = (TextView) findViewById(R.id.axeZ);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

	}

	@Override
	public void onResume() {
		super.onResume();

		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		schedule();
		// handler.post(processSensors);
	}

	@Override
	public void onPause() {
		// handler.removeCallbacks(processSensors);

		/* cancel the periodic sensor listening */
		sensorPeriodHandle.cancel(true);

		/* unregister listeners */
		mSensorManager.unregisterListener(this);

		super.onPause();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		/* check sensor type */
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];

			double d;
			d = Math.sqrt(x * x + y * y + z * z);
			d = Double.parseDouble(String.format(Locale.US, "%.2f", d));

			String coordinates = Double.toString(d) + " ";

			String mydate = java.text.DateFormat.getDateTimeInstance().format(
					Calendar.getInstance().getTime());

			/* save coordinates */
			saveToStorage = new SaveToExternalStorage(coordinates,
					"Log_accelerometer.txt");
			saveToStorage.save();

			/* save date */
			saveToStorage = new SaveToExternalStorage("Date: " + mydate + "\n"
					+ "x: ", "Log_accelerometer.txt");
			saveToStorage.save();

			_xCoor.setText("X: " + x);
			_yCoor.setText("Y: " + y);
			_zCoor.setText("Z: " + z);

		}

	}
}