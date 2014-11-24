package ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class PaintComponent extends JComponent {

	private static final long serialVersionUID = 1L;
	private Dimension currentsize;
	private BufferedImage offImg;
	private BufferedImage onImg;
	private Graphics2D offGraphic;
	private Graphics2D onGraphic;

	public PaintComponent() {
		currentsize = null;
	}

	public Graphics2D getOfflineGraphics() {
		Dimension size = getSize();
		if (size.width == 0 && size.height == 0)
			size = getPreferredSize();
		if (currentsize != null
				&& (currentsize.width != size.width || currentsize.height != size.height))
			synchronized (this) {
				offImg = null;
				onImg = null;
			}
		if (offImg == null) {
			GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			GraphicsConfiguration graphicsConfiguration = graphicsEnvironment
					.getDefaultScreenDevice().getDefaultConfiguration();
			synchronized (this) {
				onImg = graphicsConfiguration.createCompatibleImage(size.width,
						size.height, 2);
				onGraphic = onImg.createGraphics();
				offImg = graphicsConfiguration.createCompatibleImage(
						size.width, size.height, 2);
				offGraphic = offImg.createGraphics();
			}
			currentsize = size;
		}
		return offGraphic;
	}

	public void refresh(){
		BufferedImage bak;
		Graphics2D bakGraphic;
		bak = onImg;
		onImg = offImg;
		offImg = bak;
		bakGraphic = onGraphic;
		onGraphic = offGraphic;
		offGraphic = bakGraphic;
		paintComponent(bakGraphic);
		repaint();
	}

	@Override
	public void paintComponent(Graphics graphics) {
		graphics.drawImage(onImg, 0, 0, this);
	}
}