package me.skiincraft.discord.ousu.exception;

@SuppressWarnings("serial")
public class SearchNotFoundException extends RuntimeException {

	private String message;
	
	public SearchNotFoundException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
}
