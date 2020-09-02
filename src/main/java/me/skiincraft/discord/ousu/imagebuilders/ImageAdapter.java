package me.skiincraft.discord.ousu.imagebuilders;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import me.skiincraft.discord.core.textfont.CustomFont;
import me.skiincraft.discord.core.utils.ImageBuilder;
import me.skiincraft.discord.core.utils.ImageBuilder.Alignment;

public abstract class ImageAdapter {
	
	private ImageBuilder imageBuilder;
	private CustomFont customFont;
	public ImageAdapter(int width, int height) {
		imageBuilder = new ImageBuilder(getClass().getSimpleName() + "-", width, height);
		customFont = new CustomFont();
	}
	
	
	protected ImageAdapter setColor(Color color) {
		imageBuilder.getGraphic().setColor(color);
		return this;
	}
	
	protected ImageAdapter setAntialising() {
		imageBuilder.getGraphic().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		return this;
	}
	
	protected ImageAdapter image(BufferedImage image, int x, int y, Dimension size) {
		imageBuilder.drawImage(image, x, y, size);
		return this;
	}
	
	protected ImageAdapter image(BufferedImage image, int x, int y, Dimension size, Alignment align) {
		imageBuilder.drawImage(image, x, y, size, align);
		return this;
	}
	
	protected ImageAdapter image(String file, int x, int y, Dimension size) {
		this.image(new File(file), x, y, size);
		return this;
	}
	
	protected ImageAdapter image(String file, int x, int y, Dimension size, Alignment align) {
		this.image(new File(file), x, y, size, align);
		return this;
	}
	
	protected ImageAdapter image(File image, int x, int y, Dimension size) {
		try {
			imageBuilder.drawImage(image, x, y, size);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	protected ImageAdapter image(File image, int x, int y, Dimension size, Alignment align) {
		try {
			imageBuilder.drawImage(image, x, y, size, align);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	protected ImageAdapter image(URL image, int x, int y, Dimension size, Alignment align) throws IOException {
		imageBuilder.drawImage(image, x, y, size, align);
		return this;
	}
	
	protected ImageAdapter image(URL image, int x, int y, Dimension size) throws IOException {
		imageBuilder.drawImage(image, x, y, size);
		return this;
	}
	
	protected Font font(String font, int style, float size) {
		return customFont.getFont(font, style, size);
	}
	
	
	protected CustomFont getCustomFont() {
		return customFont;
	}
	
	protected ImageBuilder getImageBuilder() {
		return imageBuilder;
	}
	
	protected Dimension size() {
		return imageBuilder.getSize();
	}
	
	protected InputStream toInput(){
		try {
			return imageBuilder.buildInput();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
