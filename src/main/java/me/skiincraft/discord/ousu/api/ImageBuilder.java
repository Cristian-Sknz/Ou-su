package me.skiincraft.discord.ousu.api;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.RoundRectangle2D;
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
		this.base = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		this.size = new Dimension(w, h);

		this.imagename = imagename;
		this.graphic = base.createGraphics();
	}

	public void addImage(BufferedImage image, int x, int y, Dimension size) {
		graphic.drawImage(image, x, y, size.width, size.height, null);
	}
	
	public BufferedImage changeColorRed(BufferedImage img, int height, int width) {
        for (int y = 0; y < height; y++) 
        { 
            for (int x = 0; x < width; x++) 
            { 
                int p = img.getRGB(x,y); 
  
                int a = (p>>24)&0xff; 
                int r = (p>>16)&0xff; 
  
                // set new RGB 
                // keeping the r value same as in original 
                // image and setting g and b as 0. 
                p = (a<<24) | (r<<16) | (0<<8) | 0; 
  
                img.setRGB(x, y, p); 
            } 
        } 
        return img;
	}
	
	public BufferedImage changeColorRed(File img) {
		try {
			BufferedImage img2 = ImageIO.read(img);
			return changeColorRed(img2, img2.getHeight(), img2.getWidth());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public BufferedImage changeColorRed(URL img) {
		try {
			BufferedImage img2 = ImageIO.read(img);
			return changeColorRed(img2, img2.getHeight(), img2.getWidth());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void addRoundedImage(BufferedImage image, int x, int y, Dimension size, int cornerRadius) {
	    int w = (int) size.getWidth();
	    int h = (int) size.getHeight();
	    Graphics2D g2 = getGraphic();

	    g2.setComposite(AlphaComposite.Src);
	    g2.setColor(Color.WHITE);
	    g2.fill(new RoundRectangle2D.Float(x, y, w, h, cornerRadius, cornerRadius));
	    g2.setComposite(AlphaComposite.SrcAtop);
	    //g2.drawImage(image, x, y, null);
	    g2.drawImage(image, x, y, w, h, null);
	}
	
	
	public void addRoundedImage(File image, int x, int y, Dimension size, int cornerRadius) {
		try {
			addRoundedImage(ImageIO.read(image), x, y, size, cornerRadius);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addRoundedImage(URL image, int x, int y, Dimension size, int cornerRadius) {
		try {
			
			addRoundedImage(ImageIO.read(image), x, y, size, cornerRadius);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	public void addRightStringY(String string, int x, int y, Font font) {
		graphic.setFont(font);
		FontMetrics fm = graphic.getFontMetrics();

		y = y * 2;

		int w = (x - fm.stringWidth(string)) / 1;
		int h = (fm.getAscent() + (y - (fm.getAscent() + fm.getDescent())) / 2);

		graphic.drawString(string, w, h);
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
		File file = new File("resources/osuprofiles/" + this.imagename + ".png");
		file.mkdirs();
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
