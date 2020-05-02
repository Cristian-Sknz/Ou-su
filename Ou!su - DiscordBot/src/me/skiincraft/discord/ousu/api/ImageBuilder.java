package me.skiincraft.discord.ousu.api;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageBuilder {

	private BufferedImage base;
	private String imagename;

	private Dimension size;
	private Graphics2D graphic;

	public ImageBuilder(String imagename, int w, int h) {
		this.base = new BufferedImage(w, h, 2);
		this.size = new Dimension(w, h);

		this.imagename = imagename;
		this.graphic = base.createGraphics();
	}

	public void addImage(BufferedImage image, int x, int y, Dimension size) {
		graphic.drawImage(image, x, y, size.width, size.height, null);
	}

	public void addImage(File image, int x, int y, Dimension size) throws IOException {
		graphic.drawImage(ImageIO.read(image), x, y, size.width, size.height, null);
	}

	public void addImage(URL url, int x, int y, Dimension size) throws IOException {
		graphic.drawImage(ImageIO.read(url), x, y, size.width, size.height, null);
	}

	public BufferedImage blurredImage(BufferedImage image) {
		int radius = 1;
		int size = radius * 2 + 1;
		float weight = 1.0f / (size * size);
		float[] data = new float[size * size];

		for (int i = 0; i < data.length; i++) {
			data[i] = weight;
		}

		Kernel kernel = new Kernel(size, size, data);
		ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
		// tbi is BufferedImage
		BufferedImage i = op.filter(image, null);
		return i;
	}

	public void addString(String string, int x, int y, int fontSize) {
		graphic.setFont(new Font("Arial", Font.PLAIN, fontSize));
		graphic.drawString(string, x, y);
	}

	public void addString(String string, int x, int y, Font font) {
		graphic.setFont(font);
		graphic.drawString(string, x, y);
	}

	public void addCentralizedString(String string, int x, int y, int fontSize) {
		graphic.setFont(new Font("Arial", Font.PLAIN, fontSize));
		FontMetrics fm = graphic.getFontMetrics();

		x = x * 2;
		y = y * 2;

		int w = (x - fm.stringWidth(string)) / 2;
		int h = (fm.getAscent() + (y - (fm.getAscent() + fm.getDescent())) / 2);

		graphic.drawString(string, w, h);
	}

	public void addCentralizedString(String string, int x, int y, Font font) {
		graphic.setFont(font);
		FontMetrics fm = graphic.getFontMetrics();

		x = x * 2;
		y = y * 2;

		int w = (x - fm.stringWidth(string)) / 2;
		int h = (fm.getAscent() + (y - (fm.getAscent() + fm.getDescent())) / 2);

		graphic.drawString(string, w, h);
	}

	public void addCentralizedStringY(String string, int x, int y, Font font) {
		graphic.setFont(font);
		FontMetrics fm = graphic.getFontMetrics();

		y = y * 2;

		int h = (fm.getAscent() + (y - (fm.getAscent() + fm.getDescent())) / 2);

		graphic.drawString(string, x, h);
	}

	public void addCentralizedStringX(String string, int x, int y, Font font) {
		graphic.setFont(font);
		FontMetrics fm = graphic.getFontMetrics();

		x = x * 2;

		int w = (x - fm.stringWidth(string)) / 2;

		graphic.drawString(string, w, y);
	}

	public Image build() {
		graphic.dispose();

		return base;
	}

	public File buildFile() throws IOException {
		graphic.dispose();
		File file = new File("resource/osuprofiles/" + this.imagename + ".png");
		ImageIO.write(base, "png", file);

		return file;
	}

	public InputStream buildInput() throws IOException {
		graphic.dispose();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(base, "png", os);
		InputStream in = new ByteArrayInputStream(os.toByteArray());
		return in;
	}

	public String getImageame() {
		return imagename;
	}

	public Dimension getSize() {
		return size;
	}

	public Graphics2D getGraphic() {
		return graphic;
	}
}
