package me.skiincraft.discord.ousu.utils;

import me.skiincraft.discord.core.common.CustomFont;
import me.skiincraft.discord.ousu.common.ImageBuilder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public abstract class ImageAdapter {
	
	private final ImageBuilder imageBuilder;
	public ImageAdapter(int width, int height) {
		imageBuilder = new ImageBuilder(getClass().getSimpleName() + "-", width, height);
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
	
	protected ImageAdapter image(BufferedImage image, int x, int y, Dimension size, ImageBuilder.Alignment align) {
		imageBuilder.drawImage(image, x, y, size, align);
		return this;
	}
	
	protected ImageAdapter image(String file, int x, int y, Dimension size) {
		this.image(new File(file), x, y, size);
		return this;
	}
	
	protected ImageAdapter image(String file, int x, int y, Dimension size, ImageBuilder.Alignment align) {
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
	
	protected ImageAdapter image(File image, int x, int y, Dimension size, ImageBuilder.Alignment align) {
		try {
			imageBuilder.drawImage(image, x, y, size, align);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	protected ImageAdapter image(URL image, int x, int y, Dimension size, ImageBuilder.Alignment align) throws IOException {
		imageBuilder.drawImage(image, x, y, size, align);
		return this;
	}
	
	protected ImageAdapter image(URL image, int x, int y, Dimension size) throws IOException {
		imageBuilder.drawImage(image, x, y, size);
		return this;
	}
	
	protected Font font(String font, float size) {
		return CustomFont.getFont(font, Font.PLAIN, size);
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
