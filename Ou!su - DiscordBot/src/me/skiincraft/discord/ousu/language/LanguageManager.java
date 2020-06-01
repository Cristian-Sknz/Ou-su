package me.skiincraft.discord.ousu.language;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;

import com.google.gson.Gson;

import me.skiincraft.discord.ousu.OusuBot;

public class LanguageManager {

	public enum Language {
		Portuguese("PT_BR.json", "BR", new Locale("pt")), English("EN_US.json", "EN", new Locale("en"));// Spanish("ES_ES.json", "ES");

		private String fileName;
		private String countrycode;
		private Locale locale;

		Language(String fileName, String countrycode, Locale locale) {
			this.fileName = fileName;
			this.countrycode = countrycode;
			this.locale = locale;
		}
		
		public Locale getLocale() {
			return locale;
		}

		public String getFileName() {
			return fileName;
		}

		public String getLanguageCode() {
			return fileName.replace(".json", "").replace("_", "-");
		}

		public String getCountrycode() {
			return countrycode;
		}
	}

	private LanguageBase langClass;
	private Language lang;

	public LanguageManager(Language lang) {
		this.lang = lang;
		Gson gson = new Gson();
		InputStream in = OusuBot.class.getResourceAsStream("language/" + lang.getFileName());

		try (Reader reader = new InputStreamReader(in)) {
			this.langClass = gson.fromJson(reader, LanguageBase.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Language getLanguage() {
		return lang;
	}

	public String translatedTitles(String lineresult) {
		try {
			return langClass.getTitles().get(lineresult).replace("{l}", "\n");
		} catch (NullPointerException e) {
			System.out.println("Não foi possivel encontrar a tradução: \n" + lineresult + " em " + lang);
			System.out.println("//Help");
		}
		return lineresult;
	}

	public String translatedHelp(String lineresult) {
		try {
			return langClass.getHelp().get(lineresult).replace("{l}", "\n");
		} catch (NullPointerException e) {
			System.out.println("Não foi possivel encontrar a tradução: \n" + lineresult + " em " + lang);
			System.out.println("//Help");
		}
		return lineresult;
	}

	public String translatedOsuMessages(String lineresult) {
		try {
			return langClass.getOsuMessages().get(lineresult).replace("{l}", "\n");
		} catch (NullPointerException e) {
			System.out.println("Não foi possivel encontrar a tradução: \n" + lineresult + " em " + lang);
			System.out.println("//OsuMessages");
		}
		return lineresult;

	}

	public String translatedMessages(String lineresult) {
		try {
			return langClass.getMessages().get(lineresult).replace("{l}", "\n");
		} catch (NullPointerException e) {
			System.out.println("Não foi possivel encontrar a tradução: \n" + lineresult + " em " + lang);
			System.out.println("//Messages");
		}
		return lineresult;
	}

	public String translatedEmbeds(String lineresult) {
		try {
			return langClass.getEmbeds().get(lineresult).replace("{l}", "\n");
		} catch (NullPointerException e) {
			System.out.println("Não foi possivel encontrar a tradução: \n" + lineresult + " em " + lang);
			System.out.println("//Embeds");
		}
		return lineresult;
	}

	public String translatedBot(String lineresult) {
		try {
			return langClass.getBot().get(lineresult).replace("{l}", "\n");
		} catch (NullPointerException e) {
			System.out.println("Não foi possivel encontrar a tradução: \n" + lineresult + " em " + lang);
			System.out.println("//Messages");
		}
		return lineresult;
	}

	public String[] translatedArrayHelp(String lineresult) {
		try {
			return langClass.getHelp().get(lineresult).replace("{l}", "\n").split("\n");
		} catch (NullPointerException e) {
			System.out.println("Não foi possivel encontrar a tradução: \n" + lineresult + " em " + lang);
			System.out.println("//Help");
		}
		return new String[] { lineresult };
	}

	public String[] translatedArrayOsuMessages(String lineresult) {
		try {
			return langClass.getOsuMessages().get(lineresult).replace("{l}", "\n").split("\n");
		} catch (NullPointerException e) {
			System.out.println("Não foi possivel encontrar a tradução: \n" + lineresult + " em " + lang);
			System.out.println("//Help");
		}
		return new String[] { lineresult };
	}

	public String[] translatedArrayMessages(String lineresult) {
		try {
			return langClass.getMessages().get(lineresult).replace("{l}", "\n").split("\n");
		} catch (NullPointerException e) {
			System.out.println("Não foi possivel encontrar a tradução: \n" + lineresult + " em " + lang);
			System.out.println("//Help");
		}
		return new String[] { lineresult };
	}

	public String[] translatedArrayEmbeds(String lineresult) {
		try {
			return langClass.getEmbeds().get(lineresult).replace("{l}", "\n").split("\n");
		} catch (NullPointerException e) {
			System.out.println("Não foi possivel encontrar a tradução: \n" + lineresult + " em " + lang);
			System.out.println("//Help");
		}
		return new String[] { lineresult };
	}

	public String[] translatedArrayBot(String lineresult) {
		try {
			return langClass.getBot().get(lineresult).replace("{l}", "\n").split("\n");
		} catch (NullPointerException e) {
			System.out.println("Não foi possivel encontrar a tradução: \n" + lineresult + " em " + lang);
			System.out.println("//Help");
		}
		return new String[] { lineresult };
	}
}
