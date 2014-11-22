package com.muntoo.guito;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Created by Mateen on 2014-11-21.
 */
public class MicrophoneThread extends Thread
{
	public boolean isRecording = true;  //variable to start or stop recording
	public int frequency = 101010; //the public variable that contains the frequency value "heard", it is updated continually while the thread is running.

	private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
	private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	private int blockSize = 8000;
	private int sampleRateInHz = 44100;

	public MicrophoneThread()
	{
	}


	@Override
	public void run()
	{
		try
		{
			// int bufferSize = AudioRecord.getMinBufferSize(frequency,
			// AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
			int bufferSize = AudioRecord.getMinBufferSize(
					sampleRateInHz, channelConfig, audioFormat);

			AudioRecord audioRecord = new AudioRecord(
					MediaRecorder.AudioSource.MIC, sampleRateInHz,
					channelConfig, audioFormat, bufferSize * 2);

			short[] buffer = new short[blockSize];
			double[] toTransform = new double[blockSize];

			audioRecord.startRecording();

			while(isRecording)
			{
				int bufferReadResult = audioRecord.read(buffer, 0, blockSize);

				frequency = buffer[0];

				for (int i = 0; i < blockSize && i < bufferReadResult; i++)
				{
					toTransform[i] = (double) buffer[i] / 32768.0;
				}

				// transformer.ft(toTransform);
				// publishProgress(toTransform);
			}

			audioRecord.stop();

		} catch (Throwable t)
		{
			t.printStackTrace();
			// Log.e("AudioRecord", "Recording Failed");
			frequency = 666;
		}
	}

}