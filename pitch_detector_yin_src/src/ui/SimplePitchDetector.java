package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ui.Yin.DetectedPitchHandler;

//Referenced classes of package detector:
//         PaintComponent

public class SimplePitchDetector extends JFrame {

	class AudioInputProcessor implements Runnable {

		private final int sampleRate;
		private final double audioBufferSize;

		public AudioInputProcessor(){
			sampleRate = 22050; //Hz
			audioBufferSize = 0.1;//Seconds
		}

		public void run() {
			javax.sound.sampled.Mixer.Info selected = (javax.sound.sampled.Mixer.Info) mixer_selector.getSelectedItem();
			if (selected == null)
				return;
			try {
				Mixer mixer = AudioSystem.getMixer(selected);
				AudioFormat format = new AudioFormat(sampleRate, 16, 1, true,false);
				DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class,format);
				TargetDataLine line = (TargetDataLine) mixer.getLine(dataLineInfo);
				int numberOfSamples = (int) (audioBufferSize * sampleRate);
				line.open(format, numberOfSamples);
				line.start();
				AudioInputStream ais = new AudioInputStream(line);
				if(fileName != null){
					ais = AudioSystem.getAudioInputStream(new File(fileName));

				}
				AudioFloatInputStream afis = AudioFloatInputStream.getInputStream(ais);
				processAudio(afis);
				line.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			aiprocessor = null;
		}

		public void processAudio(AudioFloatInputStream afis) throws IOException, UnsupportedAudioFileException {

			Yin.processStream(afis,new DetectedPitchHandler() {

				final double pitch_history[] = new double[600];
				int pitch_history_pos = 0;

				@Override
				public void handleDetectedPitch(float time,float pitch) {
					Graphics2D g = painter.getOfflineGraphics();
					int w = painter.getWidth();
					int h = painter.getHeight();
					g.setColor(Color.BLACK);
					g.fillRect(0, 0, w, h);
					boolean noteDetected = pitch != -1;
					double detectedNote = 69D + (12D * Math.log(pitch / 440D)) / Math.log(2D);
					//noteDetected = noteDetected && Math.abs(detectedNote - Math.round(detectedNote)) > 0.3;

					if (pitch_history_pos == pitch_history.length)
						pitch_history_pos = 0;

					g.setColor(Color.WHITE);

					g.drawString((new StringBuilder("Note: ")).append(detectedNote).toString(), 20, 20);
					g.drawString((new StringBuilder("Freq: ")).append(pitch).toString(), 20, 40);

					pitch_history[pitch_history_pos] = noteDetected ? detectedNote : 0.0;


					int jj = pitch_history_pos;

					for (int i = 0; i < pitch_history.length; i++) {
						double d = pitch_history[jj];
						float rr = 1;
						float gg = 1;
						float bb = 1;
						g.setColor(new Color(rr, gg, bb));
						if (d != 0.0D) {
							int x = (int) (((double) i / (double) pitch_history.length) * w);
							int y = (int) (h - ((d - 20D) / 120D) * h);
							g.drawRect(x, y, 1, 1);
						}
						if (++jj == pitch_history.length)
							jj = 0;
					}

					pitch_history_pos++;
					painter.refresh();
				}
			});
		}
	}



	public void startAction() {
		if (aiprocessor != null) {
			return;
		} else {
			aiprocessor = new AudioInputProcessor();
			(new Thread(aiprocessor)).start();
			return;
		}
	}

	public void stopAction() {
		if (aiprocessor == null) {
			return;
		} else {
			Yin.stop();
			return;
		}
	}

	private static final long serialVersionUID = 1L;
	volatile AudioInputProcessor aiprocessor;
	PaintComponent painter;
	JComboBox mixer_selector;
	String fileName = null;

	public SimplePitchDetector(String fileName) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		aiprocessor = null;
		this.fileName = fileName;

		painter = new PaintComponent();
		setSize(800, 600);
		setLocationRelativeTo(null);
		setTitle("Pitch detector" + fileName == null ? "" : " " + fileName);
		setDefaultCloseOperation(3);
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		add(main);
		JPanel buttonpanel = new JPanel();
		buttonpanel.setLayout(new FlowLayout(0));
		main.add("North", buttonpanel);
		ArrayList<javax.sound.sampled.Mixer.Info> capMixers = new ArrayList<javax.sound.sampled.Mixer.Info>();
		javax.sound.sampled.Mixer.Info mixers[] = AudioSystem.getMixerInfo();
		for (int i = 0; i < mixers.length; i++) {
			javax.sound.sampled.Mixer.Info mixerinfo = mixers[i];
			if (AudioSystem.getMixer(mixerinfo).getTargetLineInfo().length != 0)
				capMixers.add(mixerinfo);
		}

		mixer_selector = new JComboBox(capMixers.toArray());
		buttonpanel.add(mixer_selector);
		JButton startbutton = new JButton("Start");
		startbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startAction();
			}
		});
		buttonpanel.add(startbutton);
		JButton stopbutton = new JButton("stop");
		stopbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopAction();
			}
		});
		buttonpanel.add(stopbutton);
		main.add("Center", painter);
	}

	public static void main(String args[]) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		if(args.length==0 | args.length == 2){
			String fileName = args.length==0 ? null : args[1];
			SimplePitchDetector frame = new SimplePitchDetector(fileName);
			frame.setVisible(true);
		} else {
			Yin.main(args);
		}
	}

}
