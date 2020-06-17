package me.skiincraft.discord.ousu.abstractcore;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.api.CooldownManager;
import me.skiincraft.discord.ousu.customemoji.OusuEmojis;
import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.sqlite.GuildsDB;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public abstract class Commands extends ChannelUtils {

	private String prefix;
	private String command;
	private List<String> aliases;
	private String usage;

	private String[] args;
	private String label;
	private GuildMessageReceivedEvent event;

	private TextChannel channel;
	private String userid;

	private LanguageManager getLang;
	
	@Override
	public TextChannel getTextChannel() {
		return this.channel;
	}

	public Commands(String prefix, String command) {
		this.prefix = prefix;
		this.command = command;
		this.aliases = null;
		this.usage = null;
	}

	public Commands(String prefix, String command, String usage, List<String> aliases) {
		this.prefix = prefix;
		this.command = command;
		this.aliases = aliases;
		this.usage = usage;
	}

	public abstract String[] helpMessage(LanguageManager langm);

	public abstract CommandCategory categoria();

	public abstract void action(String[] args, String label, TextChannel channel);

	public User getUser() {
		return event.getAuthor();
	}
	
	public String getUserId() {
		return userid;
	}
	
	public boolean isValidCommand(GuildMessageReceivedEvent e) {
		args = e.getMessage().getContentRaw().split(" ");
		if (e.getAuthor().isBot()) {
			return false;
		}

		if (e.getJDA().getSelfUser().getAsMention().equals(e.getAuthor().getAsMention())) {
			return false;
		}

		if (e.getChannel().isNSFW()) {
			return false;
		}
		
		if (!args[0].toLowerCase().contains(command)) {
			return false;
		}

		GuildsDB sql = new GuildsDB(e.getGuild());

		prefix = sql.get("prefix");
		if (!hasAliases()) {
			if (!args[0].equalsIgnoreCase(prefix + command)) {
				return false;
			}
		}
		if (new CooldownManager().isInCooldown(e.getAuthor().getId())) {
			String[] str = getLang().translatedArrayMessages("COOLDOWN_MESSAGE");
			reply(TypeEmbed.DefaultEmbed(str[0], str[1]).build());
			return false;
		}

		this.label = prefix + command;
		this.getLang = new LanguageManager(Language.valueOf(sql.get("language")));

		this.channel = e.getChannel();
		this.userid = e.getAuthor().getId();
		this.event = e;
		return true;
	}

	public boolean hasPermission(String userId, Permission permission) {
		if (event.getGuild().getMemberById(userId).hasPermission(permission)) {
			return true;
		}
		return false;
	}

	public boolean isOwner() {
		if (!userid.equals("247096601242238991")) {
			return false;
		} else {
			return true;
		}
	}

	public boolean hasAliases() {
		if (getAliases() == null) {
			return false;
		}

		int a = getAliases().size();
		for (int i = 0; i < a; i++) {
			if (args[0].equalsIgnoreCase(prefix + getAliases().get(i))) {
				return true;
			}
		}
		return false;
	}

	public String getCommand() {
		return command;
	}

	public String getCommandFull() {
		return prefix + command;
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if (isValidCommand(event) == false) {
			return;
		}
		
		StringBuilder complete = new StringBuilder();
		String data = new SimpleDateFormat("HH:mm:ss").format(new Date());

		complete.append("[" + channel.getGuild().getName());
		complete.append(":" + channel.getName());
		complete.append(" | " + data + "]:");

		String userFull = event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator();
		
		OusuBot.getOusu().logger(complete.toString() + userFull + " executou o comando " + getCommandFull());

		Runnable commandrunnable = new Runnable() {

			@Override
			public void run() {
				//setEnd(false);
				channel.sendTyping().queue();
				final long startElapsed = System.currentTimeMillis();
				String[] sarray = event.getMessage().getContentRaw().split(" ");
				sarray = StringUtils.removeString(sarray, 0);
				
				try {Thread.sleep(200);} 
				catch (InterruptedException e) {}
				
				System.out.println("[args] = " + StringUtils.arrayToString2(0, sarray));
				new CooldownManager().addToCooldown(userid, 2);
				
				action(sarray, label, channel);

				final long result = startElapsed - System.currentTimeMillis();
				String elapsedtime = new DecimalFormat("#.0").format(result / 1000) + "s";

				if (elapsedtime.startsWith(",")) {
					OusuBot.getOusu().logger("[" + getCommandFull() + " | Elapsed Time: 0s]");
					Thread.currentThread().interrupt();
					return;
				}
				OusuBot.getOusu()
						.logger("[" + getCommandFull() + " | Elapsed Time: " + elapsedtime.replace("-", "") + "]");
				Thread.currentThread().interrupt();
			}
		};
		
		Thread t = new Thread(commandrunnable, "BotCommand_" + this.getCommand());
		t.start();
	}

	public boolean isInsuficient() {
		try {
			String a = args[1];
			a.length();
			return false;
		} catch (ArrayIndexOutOfBoundsException e) {
			return true;
		}
	}

	public void sendUsage() {
		String[] msg = getLang().translatedArrayMessages("INCORRECT_USE");
		reply(TypeEmbed.WarningEmbed(msg[0], OusuEmojis.getEmoteAsMention("small_red_diamond") + msg[1] + getUsage())
				.setFooter("ou!help to help!")
				.build());
	}

	public void noPermissionMessage(Permission permission) {
		String[] str = getLang.translatedArrayHelp("INSUFICIENT_PERMISSIONS");
		StringBuffer buffer = new StringBuffer();
		buffer.append(OusuEmojis.getEmoteAsMention("small_red_diamond") + " ");
		for (String append : str) {
			if (append != str[0]) {
				buffer.append(append);
			}
		}
		buffer.append("\n");
		reply(TypeEmbed.WarningEmbed(str[0], buffer.toString() + permission.getName()).build());
	}

	public List<String> getAliases() {
		return aliases;
	}

	public String getUsage() {
		if (usage == null) {
			setUsage(getCommandFull());
		}
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public GuildMessageReceivedEvent getEvent() {
		return event;
	}

	public CommandCategory getCategoria() {
		return this.categoria();
	}

	public LanguageManager getLang() {
		return getLang;
	}

	public Language getLanguage() {
		GuildsDB sql = new GuildsDB(getEvent().getGuild());
		return Language.valueOf(sql.get("language"));
	}

}