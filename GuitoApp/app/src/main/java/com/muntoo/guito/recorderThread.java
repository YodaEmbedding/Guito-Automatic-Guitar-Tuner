package com.muntoo.guito;


//Author xdebugx.net (Jeremiah McLeod) 8-8-2010

import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.media.AudioFormat;


class recorderThread extends Thread
{
	public boolean recording;  //variable to start or stop recording
	public int frequency; //the public variable that contains the frequency value "heard", it is updated continually while the thread is running.

	public recorderThread()
	{
	}

	@Override
	public void run()
	{
		AudioRecord recorder;
		int numCrossing, p;
		short audioData[];
		int bufferSize;

		bufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT) * 3; //get the buffer size to use with this audio record

		recorder = new AudioRecord(AudioSource.MIC, 8000, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, bufferSize); //instantiate the AudioRecorder

		recording = true; //variable to use start or stop recording
		audioData = new short[bufferSize]; //short array that pcm data is put into.


		while (recording)
		{  //loop while recording is needed
			if (recorder.getState() == android.media.AudioRecord.STATE_INITIALIZED) // check to see if the recorder has initialized yet.
				if (recorder.getRecordingState() == android.media.AudioRecord.RECORDSTATE_STOPPED)
					recorder.startRecording();  //check to see if the Recorder has stopped or is not recording, and make it record.

				else
				{

					recorder.read(audioData, 0, bufferSize); //read the PCM audio data into the audioData array

					//Now we need to decode the PCM data using the Zero Crossings Method

					numCrossing = 0; //initialize your number of zero crossings to 0
					for (p = 0; p < bufferSize / 4; p += 4)
					{
						if (audioData[p] > 0 && audioData[p + 1] <= 0) numCrossing++;
						if (audioData[p] < 0 && audioData[p + 1] >= 0) numCrossing++;
						if (audioData[p + 1] > 0 && audioData[p + 2] <= 0) numCrossing++;
						if (audioData[p + 1] < 0 && audioData[p + 2] >= 0) numCrossing++;
						if (audioData[p + 2] > 0 && audioData[p + 3] <= 0) numCrossing++;
						if (audioData[p + 2] < 0 && audioData[p + 3] >= 0) numCrossing++;
						if (audioData[p + 3] > 0 && audioData[p + 4] <= 0) numCrossing++;
						if (audioData[p + 3] < 0 && audioData[p + 4] >= 0) numCrossing++;
					}//for p

					for (p = (bufferSize / 4) * 4; p < bufferSize - 1; p++)
					{
						if (audioData[p] > 0 && audioData[p + 1] <= 0) numCrossing++;
						if (audioData[p] < 0 && audioData[p + 1] >= 0) numCrossing++;
					}


					frequency = (8000 / bufferSize) * (numCrossing / 2);  // Set the audio Frequency to half the number of zero crossings, times the number of samples our buffersize is per second.

				}//else recorder started

		} //while recording

		if (recorder.getState() == android.media.AudioRecord.RECORDSTATE_RECORDING)
			recorder.stop(); //stop the recorder before ending the thread
		recorder.release(); //release the recorders resources
		recorder = null; //set the recorder to be garbage collected.

	}//run


}//recorderThread



