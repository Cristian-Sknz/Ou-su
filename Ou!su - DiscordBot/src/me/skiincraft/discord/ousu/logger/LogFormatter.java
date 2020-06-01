package me.skiincraft.discord.ousu.logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

	private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

	@Override
	public String format(LogRecord record) {

		StringBuffer builder = new StringBuffer(1000);
		builder.append("[").append(df.format(new Date(record.getMillis()))).append("] �");
		builder.append("[").append("OusuBot").append("] - ");
		builder.append("[").append(record.getLevel()).append("] - ");
		builder.append(formatMessage(record));
		builder.append("\n");
		return builder.toString();
	}

	@Override
	public String getHead(Handler h) {
		return super.getHead(h);
	}

	@Override
	public String getTail(Handler h) {
		return super.getTail(h);
	}
}
