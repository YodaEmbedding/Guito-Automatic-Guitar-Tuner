package com.muntoo.guito;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;

/**
 * The Factory creates {@link AudioDispatcher} objects from the
 * configured default microphone of an Android device.
 * It depends on the android runtime and does not work on the standard Java runtime.
 *
 * @author Joren Six
 * @see AudioDispatcher
 */
public class AudioDispatcherFactory {

	/**
	 * Create a new AudioDispatcher connected to the default microphone.
	 *
	 * @param sampleRate
	 *            The requested sample rate.
	 * @param audioBufferSize
	 *            The size of the audio buffer (in samples).
	 *
	 * @param bufferOverlap
	 *            The size of the overlap (in samples).
	 * @return A new AudioDispatcher
	 */
	public static AudioDispatcher fromDefaultMicrophone(final int sampleRate,
	                                                    final int audioBufferSize, final int bufferOverlap) {
		int minAudioBufferSize = AudioRecord.getMinBufferSize(sampleRate,
				android.media.AudioFormat.CHANNEL_IN_MONO,
				android.media.AudioFormat.ENCODING_PCM_16BIT);
		int minAudioBufferSizeInSamples =  minAudioBufferSize/2;
		if(minAudioBufferSizeInSamples <= audioBufferSize ){
			AudioRecord audioInputStream = new AudioRecord(
					MediaRecorder.AudioSource.MIC, sampleRate,
					android.media.AudioFormat.CHANNEL_IN_MONO,
					android.media.AudioFormat.ENCODING_PCM_16BIT,
					audioBufferSize * 2);

			TarsosDSPAudioFormat format = new TarsosDSPAudioFormat(sampleRate, 16,1, true, false);

			TarsosDSPAudioInputStream audioStream = new AndroidAudioInputStream(audioInputStream, format);
			//start recording ! Opens the stream.
			audioInputStream.startRecording();

			// [Mateen's edit] Wait for stream to become available
			while(audioInputStream.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING);

			return new AudioDispatcher(audioStream,audioBufferSize,bufferOverlap);
		}else{
			new IllegalArgumentException("Buffer size too small should be at least " + (minAudioBufferSize *2));
			return null;
		}


	}
}
