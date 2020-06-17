package me.skiincraft.discord.ousu.utils;

import java.io.InputStream;

import javax.annotation.Nonnull;

public class InputStreamFile {

	private String filename;
	private String extension;
	private InputStream input;
	
	@Nonnull
	public InputStreamFile(InputStream input, String filename, String extension) {
		if (extension.charAt(0) == '.') {
			extension.replace(".", "");
		}
		this.filename = filename;
		this.extension = "."+ extension;
		this.input = input;
	}

	public String getFilename() {
		return filename;
	}

	public String getExtension() {
		return extension;
	}
	
	public String getFullname() {
		return filename + extension;
	}
	
	public InputStream getInputStream() {
		return input;
	}
}
