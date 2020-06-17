package me.skiincraft.discord.ousu.exception;

public class PosCommandException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2121657198137828638L;

	public PosCommandException(String message) {
		super(message);
	}

	public PosCommandException(Throwable arg0) {
		super(arg0);
	}

	public PosCommandException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public PosCommandException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
