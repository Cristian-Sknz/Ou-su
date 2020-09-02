package me.skiincraft.discord.ousu.exceptions;

@SuppressWarnings("serial")
public class SearchException extends RuntimeException {

	private String message;

	public SearchException(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
