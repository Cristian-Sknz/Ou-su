package me.skiincraft.discord.ousu.logger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Logging {

	private static final Logger debugLogger = Logger.getLogger("Logging");
	private static Logger log;
	private String name;
	private FileHandler debugFileHandler;

	public Logging() {
		this.name = "ousubot";
		Date dt = new Date();
		SimpleDateFormat dflog = new SimpleDateFormat("yyyy-MM-dd");
		String time = dflog.format(dt);

		log = Logger.getLogger("OusuBot");
		log.setUseParentHandlers(false);

		int in = lognumber();

		try {

			debugFileHandler = new FileHandler("logs" + "/" + name + "_" + time + "-" + in + ".log", true);
			LogFormatter formatter = new LogFormatter();

			debugLogger.addHandler(debugFileHandler);
			debugLogger.setUseParentHandlers(false);
			debugFileHandler.setFormatter(formatter);

			System.out.println("INICIANDO SISTEMA DE LOGGINGS");
			debugLogger.log(Level.OFF, "INICIANDO SISTEMA DE LOGGINGS");

		} catch (IOException ex) {
			debug(null, ex, false);
		} catch (SecurityException ex) {
			debug(null, ex, false);
		}
	}

	/**
	 * Logs a message
	 *
	 * @param level  The level to log
	 * @param msg    The message to log
	 * @param toFile log to own log?
	 */
	public void debug(Level level, String msg, boolean toFile) {
		if (toFile) {
			if (debugLogger != null) {
				System.out.println(msg);
				debugLogger.log(level, msg);
			}
		}
		log.log(level, msg);
	}

	/**
	 * Logs a Exception
	 *
	 * @param msg       The message to log
	 * @param exception the exception
	 * @param toFile    log to own log?
	 */
	public void debug(String msg, Throwable exception, boolean toFile) {
		if (toFile) {
			if (debugLogger != null) {
				System.out.println(msg);
				debugLogger.log(Level.SEVERE, msg, exception);
			}
		}
		log.log(Level.SEVERE, msg, exception);
	}

	/**
	 * Gets the logger for your plugin
	 *
	 * @param name The plugin to apply
	 */

	public int lognumber() {
		Date dt = new Date();
		SimpleDateFormat dflog = new SimpleDateFormat("yyyy-MM-dd");
		String time = dflog.format(dt);
		int i = 0;
		for (i = 1; i < 1000; i++) {
			File diretorio = new File("logs" + "/" + name + "_" + time + "-" + i + ".log");
			if (!diretorio.exists()) {
				return i;
			}
		}
		return 1;

	}
}