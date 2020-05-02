package me.skiincraft.discord.ousu.language;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.google.gson.Gson;

import me.skiincraft.discord.ousu.OusuBot;

public class LanguageManager {

	public enum Language {
		Portuguese("PT_BR.json"), English("EN_US.json"), Spanish("ES_ES.json");

		private String fileName;

		Language(String fileName) {
			this.fileName = fileName;
		}

		public String getFileName() {
			return fileName;
		}
	}

	private LanguageBase langClass;

	public LanguageManager(Language lang) {
		Gson gson = new Gson();
		InputStream in = OusuBot.class.getResourceAsStream("language/" + lang.getFileName());

		try (Reader reader = new InputStreamReader(in)) {
			this.langClass = gson.fromJson(reader, LanguageBase.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String translatedHelp(String lineresult) {
		return langClass.getHelp().get(lineresult).replace("{l}", "\n");
	}

	public String translatedOsuMessages(String lineresult) {
		return langClass.getOsuMessages().get(lineresult).replace("{l}", "\n");
	}

	public String translatedMessages(String lineresult) {
		return langClass.getMessages().get(lineresult).replace("{l}", "\n");
	}

	public String translatedEmbeds(String lineresult) {
		return langClass.getEmbeds().get(lineresult).replace("{l}", "\n");
	}

	public String translatedBot(String lineresult) {
		return langClass.getBot().get(lineresult).replace("{l}", "\n");
	}

	public String[] translatedArrayHelp(String lineresult) {
		return langClass.getHelp().get(lineresult).replace("{l}", "\n").split("\n");
	}

	public String[] translatedArrayOsuMessages(String lineresult) {
		return langClass.getOsuMessages().get(lineresult).replace("{l}", "\n").split("\n");
	}

	public String[] translatedArrayMessages(String lineresult) {
		return langClass.getMessages().get(lineresult).replace("{l}", "\n").split("\n");
	}

	public String[] translatedArrayEmbeds(String lineresult) {
		return langClass.getEmbeds().get(lineresult).replace("{l}", "\n").split("\n");
	}

	public String[] translatedArrayBot(String lineresult) {
		return langClass.getBot().get(lineresult).replace("{l}", "\n").split("\n");
	}
}
