package com.muntoo.guito;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import com.jjoe64.graphview.*;


public class MainActivity extends Activity
{
	private static final String TAG = "MainActivity";
	private static final String DEVICE_ADDRESS = "98:D3:31:60:05:C7"; // change this to your Bluetooth device address
	private ArduinoReceiver arduinoReceiver = new ArduinoReceiver();
	private Context context = this; // getApplicationContext();
	private Thread pitchThread = null;
	private double pitchInHz = -1.0;
	private Integer filteredHz = -1;
	private MovingAverage avgPitch = new MovingAverage();
	// private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private ScheduledExecutorService executor2 = Executors.newSingleThreadScheduledExecutor();

	public int counter = 0;
	public int counterGraph = 0;

	private int GRAPH_POINTS = 80;
	private int UPDATES_PER_SECOND = 10;

	String[] tuning_notes = {
			// <Concert pitch>, <Notes from top/thickest to bottom/thinnest string>
			"440, E2 A2 D3 G3 B3 E4",      // Standard Tuning
			"440, D2 A2 D3 G3 B3 E4",      // Drop D
			"440, Eb2 Ab2 Db3 Gb3 Bb3 Eb4" // E flat (Standard, half-step down)
	};

	String[] tuning_pitches = {
			// <Concert pitch>, <Notes from top/thickest to bottom/thinnest string>
			"440, 82 110 147 196 247 330", // Standard Tuning
			"440, 73 110 147 196 247 330", // Drop D
			"440, 78 104 139 185 233 311"  // E flat (Standard, half-step down)
	};

	Integer[][] tuning_ipitches = {
			// <Concert pitch>, <Notes from top/thickest to bottom/thinnest string>
			{82, 110, 147, 196, 247, 330}, // Standard Tuning
			{73, 110, 147, 196, 247, 330}, // Drop D
			{78, 104, 139, 185, 233, 311}  // E flat (Standard, half-step down)
	};

	private int currTuning = 0;
	private int currString = 0;

	private int TUNING_STANDARD = 0;
	private int TUNING_DROPD = 1;
	private int TUNING_EFLAT = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.e(TAG, "onCreate()");

		// get handles to Views and Buttons defined in our layout file
		// final TextView twCurrPitch = (TextView) findViewById(R.id.twCurrPitch);
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
				currTuning = TUNING_STANDARD;
				currString = 0;
				// Amarino.sendDataToArduino(context, DEVICE_ADDRESS, 'T', tuning_notes[TUNING_STANDARD]);
				Amarino.sendDataToArduino(context, DEVICE_ADDRESS, 'F', tuning_pitches[TUNING_STANDARD]);
			}
		});

		btnTuneDropD.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				currTuning = TUNING_DROPD;
				currString = 0;
				// Amarino.sendDataToArduino(context, DEVICE_ADDRESS, 'T', tuning_notes[TUNING_DROPD]);
				Amarino.sendDataToArduino(context, DEVICE_ADDRESS, 'F', tuning_pitches[TUNING_DROPD]);
			}
		});

		btnTuneEFlat.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				currTuning = TUNING_EFLAT;
				currString = 0;
				// Amarino.sendDataToArduino(context, DEVICE_ADDRESS, 'T', tuning_notes[TUNING_EFLAT]);
				Amarino.sendDataToArduino(context, DEVICE_ADDRESS, 'F', tuning_pitches[TUNING_EFLAT]);
			}
		});

		btnTuneNextString.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				if((currString + 1) < tuning_ipitches[currTuning].length)
				{
					++currString;
					Amarino.sendDataToArduino(context, DEVICE_ADDRESS, 'N', "");
				}
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
				//
			}
		};

		Runnable getPitchTask = new Runnable()
		{
			@Override
			public void run()
			{
				// pitchInHz = result.getPitch();

				if(pitchInHz >= 1000.0)
					pitchInHz = -1.0;

				avgPitch.push((int) pitchInHz);
				//Log.e(TAG, "getPitchTask()");

				if(counter >= 8)
				{
					counter = 0;

					//Log.e(TAG, "sendPitchTask() start");

					filteredHz = (int) avgPitch.getAverage();

					// Log.d(TAG, "raw: " + (int) (pitchInHz) + ", avg: " + filteredHz.toString());

					runOnUiThread(new Runnable(){
						public void run(){
							TextView twCurrPitch = (TextView)findViewById(R.id.twCurrPitch);
							LinearLayout info = (LinearLayout)findViewById(R.id.info);
							ProgressBar progressBar = (ProgressBar) (findViewById(R.id.progressBar));

							twCurrPitch.setText("Current Pitch: " +
									"Raw: " + (int)pitchInHz +
									", Avg: " +  filteredHz.toString());

							info.setBackgroundColor(getProgressColor(0x60));

							// define new drawable/colour
							final float[] roundedCorners = new float[]
									{ 5, 5, 5, 5, 5, 5, 5, 5 };
							ShapeDrawable shape = new ShapeDrawable(new RoundRectShape(
									roundedCorners, null, null));
							shape.getPaint().setColor(getProgressColor());
							ClipDrawable clip = new ClipDrawable(shape, Gravity.LEFT,
									ClipDrawable.HORIZONTAL);
							progressBar.setProgressDrawable(clip);

							progressBar.setBackground(getResources().getDrawable(
									android.R.drawable.progress_horizontal));

							progressBar.setProgress(0);

							if(filteredHz == -1)
							{
								progressBar.setProgress(50);
							}
							else
							{
								progressBar.setProgress(50 * (filteredHz - getGoalFrequency()) / getGoalFrequency() + 50);
							}

							progressBar.invalidate();
						}
					});

					Amarino.sendDataToArduino(context,  DEVICE_ADDRESS, 'C', "" + filteredHz);

					//Log.e(TAG, "sendPitchTask() end");
				}

				if(counterGraph >= GRAPH_POINTS / UPDATES_PER_SECOND)
				{
					counterGraph = 0;

					final GraphView graphView = new LineGraphView(context, "Pitch (Hz) vs Time (1s)");

					GraphView.GraphViewData[] data = new GraphView.GraphViewData[GRAPH_POINTS];
					Integer[] store = avgPitch.getStore();

					for (int i = 0; i < GRAPH_POINTS; i++) {
						data[i] = new GraphView.GraphViewData(1.0 * i / GRAPH_POINTS, store[i]);
					}

					GraphViewSeries seriesRaw = new GraphViewSeries("Raw", new GraphViewSeries.GraphViewSeriesStyle(getProgressColor(), 3), data);

					// Not static...?
					graphView.removeAllSeries();
					graphView.addSeries(seriesRaw);
					//graphView.addSeries(seriesAvg);
					//graphView.addSeries(seriesFiltered);
					//graphView.addSeries(seriesPitch);

					graphView.setViewPort(0.0, 1.0);
					graphView.setManualYAxisBounds(getGoalFrequency() + 30.0, getGoalFrequency() - 30.0);
					graphView.getGraphViewStyle().setNumVerticalLabels(3);
					// graphView.setScaleY(0.2f);

					runOnUiThread(new Runnable()
					{
						public void run()
						{
							LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
							layout.removeAllViews();
							layout.addView(graphView);
						}
					});
				}

				++counter;
				++counterGraph;
			}
		};

		// executor.scheduleAtFixedRate(sendPitchTask, 1010, 100, TimeUnit.MILLISECONDS);
		executor2.scheduleAtFixedRate(getPitchTask, 1000, 100 / 8, TimeUnit.MILLISECONDS);
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		Log.e(TAG, "onStart()");

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

		Log.e(TAG, "onResume()");

		pitchThread.start();

		// in order to receive broadcasted intents we need to register our receiver
		registerReceiver(arduinoReceiver, new IntentFilter(AmarinoIntent.ACTION_RECEIVED));

		// this is how you tell Amarino to connect to a specific BT device from within your own code
		Amarino.connect(context, DEVICE_ADDRESS);
	}

	@Override
	protected void onPause() {
		super.onPause();

		Log.e(TAG, "onPause()");

		pitchThread.interrupt();

		// if you connect in onStart() you must not forget to disconnect when your app is closed
		Amarino.disconnect(context, DEVICE_ADDRESS);

		// do never forget to unregister a registered receiver
		unregisterReceiver(arduinoReceiver);
	}

	@Override
	protected void onStop() {
		super.onStop();

		Log.e(TAG, "onStop()");

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
					Log.e(TAG, data);
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


	public int getGoalFrequency()
	{
		return tuning_ipitches[currTuning][currString];
	}

	private double contain(double x, double min, double max)
	{
		return x < min ? min : (x > max ? max : x);
	}

	private int getProgressColor(int alpha)
	{
		double error = 1.0 * Math.abs(filteredHz - getGoalFrequency()) / getGoalFrequency();
		// Equation of circle. Properties:
		// If error is small, dColor/derror = high
		// If error is large, dColor/derror = small
		double red = contain(255.0 * Math.sqrt(1.0 - Math.pow(1.0 - error, 2.0)), 0.0, 255.0);
		double green = 255.0 - red;
		double blue = 0.0;

		if(filteredHz == -1)
		{
			red = 0.0;
			green = 0.0;
			blue = 128.0;
		}

		return Color.argb(alpha, (int)red, (int)green, (int)blue);
	}

	private int getProgressColor()
	{
		return getProgressColor(0xFF);
	}
}
