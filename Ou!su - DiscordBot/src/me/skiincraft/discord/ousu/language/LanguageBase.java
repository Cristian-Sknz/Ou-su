package me.skiincraft.discord.ousu.language;

import java.util.Map;

public class LanguageBase {

	private Map<String, String> Help;
	private Map<String, String> OsuMessages;
	private Map<String, String> Messages;
	private Map<String, String> Embeds;
	private Map<String, String> Titles;
	private Map<String, String> Bot;

	public LanguageBase() {
		super();
	}

	public Map<String, String> getHelp() {
		return Help;
	}

	public void setHelp(Map<String, String> help) {
		Help = help;
	}

	public Map<String, String> getOsuMessages() {
		return OsuMessages;
	}

	public void setOsuMessages(Map<String, String> osuMessages) {
		OsuMessages = osuMessages;
	}

	public Map<String, String> getMessages() {
		return Messages;
	}

	public void setMessages(Map<String, String> messages) {
		Messages = messages;
	}

	public Map<String, String> getEmbeds() {
		return Embeds;
	}

	public void setEmbeds(Map<String, String> embeds) {
		Embeds = embeds;
	}

	public Map<String, String> getBot() {
		return Bot;
	}

	public void setBot(Map<String, String> bot) {
		Bot = bot;
	}

	public Map<String, String> getTitles() {
		return Titles;
	}

	public void setTitles(Map<String, String> titles) {
		Titles = titles;
	}
}
