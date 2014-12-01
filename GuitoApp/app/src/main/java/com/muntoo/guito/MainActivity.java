package com.muntoo.guito;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.Timer;

import at.abraxas.amarino.Amarino;
import at.abraxas.amarino.AmarinoIntent;

//import be.tarsos.dsp.*;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm;


public class MainActivity extends Activity
{
	private static final String TAG = "MainActivity";
	private static final String DEVICE_ADDRESS = "98:D3:31:60:05:C7"; // change this to your Bluetooth device address
	private ArduinoReceiver arduinoReceiver = new ArduinoReceiver();
	private Context context = this; // getApplicationContext();
	private Thread pitchThread = null;
	private double pitchInHz = -1.0;
	private MovingAverage avgPitch = new MovingAverage();
	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
	// private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	String[] tuning_notes = {
		// <Concert pitch>, <Notes from top/thickest to bottom/thinnest string>
		"440, E2 A2 D3 G3 B3 E4", // Standard Tuning
		"440, D2 A2 D3 G3 B3 E4", // Drop D
		"440, Eb2 Ab2 Db3 Gb3 Bb3 Eb4" // E flat (Standard, half-step down)
	};

	private int TUNING_STANDARD = 0;
	private int TUNING_DROPD = 1;
	private int TUNING_EFLAT = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// get handles to Views and Buttons defined in our layout file
		final TextView numCurrentPitch = (TextView) findViewById(R.id.numCurrentPitch);
		Button btnTuneStandard = (Button) findViewById(R.id.btnTuneStandard);
		Button btnTuneDropD = (Button) findViewById(R.id.btnTuneDropD);
		Button btnTuneEFlat = (Button) findViewById(R.id.btnTuneEFlat);
		Button btnTuneNextString = (Button) findViewById(R.id.btnNextString);
		Button btnTuneStop = (Button) findViewById(R.id.btnStop);

		// UI listeners
		btnTuneStandard.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				TextView numCurrentPitch = (TextView) findViewById(R.id.numCurrentPitch);
				numCurrentPitch.setText("" + (int) avgPitch.getAverage());
				Amarino.sendDataToArduino(context, DEVICE_ADDRESS, 'T', tuning_notes[TUNING_STANDARD]);
			}
		});

		btnTuneDropD.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Amarino.sendDataToArduino(context, DEVICE_ADDRESS, 'T', tuning_notes[TUNING_DROPD]);
			}
		});

		btnTuneEFlat.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Amarino.sendDataToArduino(context, DEVICE_ADDRESS, 'T', tuning_notes[TUNING_EFLAT]);
			}
		});

		btnTuneNextString.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Amarino.sendDataToArduino(context, DEVICE_ADDRESS, 'N', "");
			}
		});

		btnTuneStop.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				Amarino.sendDataToArduino(context, DEVICE_ADDRESS, 'X', "");
			}
		});

		// Bluetooth thread
		Runnable sendPitchTask = new Runnable()
		{
			@Override
			public void run()
			{
				Log.e(TAG, "sendPitchTask() start");

				TextView numCurrentPitch = (TextView) findViewById(R.id.numCurrentPitch);
				numCurrentPitch.setText("" + (int) avgPitch.getAverage());

				Amarino.sendDataToArduino(context,  DEVICE_ADDRESS, 'C', (int)avgPitch.getAverage());

				Log.e(TAG, "sendPitchTask() end");
			}
		};

		Runnable getPitchTask = new Runnable()
		{
			@Override
			public void run()
			{
				// pitchInHz = result.getPitch();
				avgPitch.push(pitchInHz);
				Log.e(TAG, "getPitchTask()");
			}
		};

		executor.scheduleAtFixedRate(sendPitchTask, 1000, 100, TimeUnit.MILLISECONDS);
		executor.scheduleAtFixedRate(getPitchTask, 1000, 100 / 8, TimeUnit.MILLISECONDS);
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		// Set up pitch tracker
		AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);

		PitchDetectionHandler pdh = new PitchDetectionHandler() {
			@Override
			public void handlePitch(PitchDetectionResult result, AudioEvent e) {
				pitchInHz = result.getPitch();
			}
		};

		AudioProcessor p = new PitchProcessor(PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
		dispatcher.addAudioProcessor(p);

		pitchThread = new Thread(dispatcher, "Audio Dispatcher");
		pitchThread.setPriority(Thread.NORM_PRIORITY);
	}

	@Override
	protected void onResume() {
		super.onResume();

		pitchThread.start();

		// in order to receive broadcasted intents we need to register our receiver
		registerReceiver(arduinoReceiver, new IntentFilter(AmarinoIntent.ACTION_RECEIVED));

		// this is how you tell Amarino to connect to a specific BT device from within your own code
		Amarino.connect(context, DEVICE_ADDRESS);
	}

	@Override
	protected void onPause() {
		super.onPause();

		pitchThread.interrupt();

		// if you connect in onStart() you must not forget to disconnect when your app is closed
		Amarino.disconnect(context, DEVICE_ADDRESS);

		// do never forget to unregister a registered receiver
		unregisterReceiver(arduinoReceiver);
	}

	@Override
	protected void onStop() {
		super.onStop();


	}



	/**
	 * ArduinoReceiver is responsible for catching broadcasted Amarino
	 * events.
	 * <p/>
	 * It extracts data from the intent and updates the graph accordingly.
	 */
	public class ArduinoReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			// Amarino.sendDataToArduino(context, DEVICE_ADDRESS, 'A', "test");

			// the device address from which the data was sent, we don't need it here but to demonstrate how you retrieve it
			final String address = intent.getStringExtra(AmarinoIntent.EXTRA_DEVICE_ADDRESS);

			// the type of data which is added to the intent
			final int dataType = intent.getIntExtra(AmarinoIntent.EXTRA_DATA_TYPE, -1);

			// we only expect String data though, but it is better to check if really string was sent
			// later Amarino will support different data types, so far data comes always as string and
			// you have to parse the data to the type you have sent from Arduino, like it is shown below
			if (dataType == AmarinoIntent.STRING_EXTRA)
			{
				String data = intent.getStringExtra(AmarinoIntent.EXTRA_DATA);

				if (data != null)
				{
					// TextView numCurrentPitch = (TextView) findViewById(R.id.numCurrentPitch);
                    // numCurrentPitch.setText(data);
				}
			}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
