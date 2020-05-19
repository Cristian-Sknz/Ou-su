package me.skiincraft.discord.ousu.manager;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.embedtypes.DefaultEmbed;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public abstract class Commands extends ListenerAdapter {

	private String prefix;
	private String command;
	private List<String> aliases;
	private String usage;

	private String[] args;
	private String label;
	private GuildMessageReceivedEvent event;

	private TextChannel channel;
	private User user;

	private LanguageManager lang;

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

	public abstract void action(String[] args, String label, User user, TextChannel channel);

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

		SQLAccess sql = new SQLAccess(e.getGuild());

		prefix = sql.get("prefix");
		if (!hasAliases()) {
			if (!args[0].equalsIgnoreCase(prefix + command)) {
				return false;
			}
		}

		this.label = prefix + command;
		this.lang = new LanguageManager(Language.valueOf(sql.get("language")));

		this.channel = e.getChannel();
		this.user = e.getAuthor();
		this.event = e;
		return true;
	}

	public boolean hasPermission(User user, Permission permission) {
		if (event.getGuild().getMember(user).hasPermission(permission)) {
			return true;
		}
		return false;
	}

	public boolean hasRole(User user, String rolename) {
		List<Role> role = getEvent().getGuild().getRolesByName(rolename, true);
		List<Role> memberRoles = event.getGuild().getMember(user).getRoles();

		if (memberRoles.contains(role.get(0))) {
			return true;
		}
		return false;
	}

	public boolean isOwner() {
		if (!user.equals(OusuBot.getJda().getUserById("247096601242238991"))) {
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

		String userFull = user.getName() + "#" + user.getDiscriminator();
		OusuBot.getOusu().logger(complete.toString() + userFull + " executou o comando " + getCommandFull());

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				final long startElapsed = System.currentTimeMillis();
				channel.sendTyping().queue();
				String[] sarray = event.getMessage().getContentRaw().split(" ");

				sarray = StringUtils.removeString(sarray, 0);

				action(sarray, label, user, channel);

				final long result = startElapsed - System.currentTimeMillis();
				String elapsedtime = new DecimalFormat("#.0").format(result / 1000) + "s";
				if (elapsedtime.startsWith(",")) {
					OusuBot.getOusu().logger("[" + getCommandFull() + " | Elapsed Time: 0s]");
					return;
				}
				OusuBot.getOusu()
						.logger("[" + getCommandFull() + " | Elapsed Time: " + elapsedtime.replace("-", "") + "]");
			}
		});

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

	public void DeletarMSGReceived() {
		event.getMessage().delete().queue();
	}

	public MessageAction sendUsage() {
		String[] msg = getLang().translatedArrayMessages("INCORRECT_USE");
		MessageAction a = event.getChannel().sendMessage(new DefaultEmbed(msg[0], msg[1] + getUsage()).construir());
		return a;
	}

	public MessageAction noPermissionMessage(Permission permission) {
		String[] str = lang.translatedArrayHelp("INSUFICIENT_PERMISSIONS");
		StringBuffer buffer = new StringBuffer();
		for (String append : str) {
			if (append != str[0]) {
				buffer.append(append);
			}
		}
		buffer.append("\n");
		MessageAction a = event.getChannel()
				.sendMessage(new DefaultEmbed(str[0], buffer.toString() + permission.getName()).construir());
		return a;
	}

	public MessageAction sendMessage(String message) {
		MessageAction a = event.getChannel().sendMessage(message);
		return a;
	}

	public MessageAction sendFile(File file) {
		MessageAction a = event.getChannel().sendFile(file, file.getName());
		return a;
	}

	public MessageAction sendPrivateMessage(String message) {
		MessageAction a = event.getAuthor().openPrivateChannel().complete().sendMessage(message);
		return a;
	}

	public MessageAction sendEmbedMessage(EmbedBuilder e) {
		MessageAction a = event.getChannel().sendMessage(e.build());
		return a;
	}

	public MessageAction sendEmbedMessage(MessageEmbed e) {
		MessageAction a = event.getChannel().sendMessage(e);
		return a;
	}

	public MessageAction sendFileEmbeded(EmbedBuilder e, File file) {
		MessageAction b = event.getChannel().sendFile(file, file.getName())
				.embed(e.setImage("attachment://" + file.getName()).build());
		return b;
	}

	public MessageAction sendFileEmbeded(EmbedBuilder e, InputStream input) {
		MessageAction b = event.getChannel().sendFile(input, "profile_osu.png")
				.embed(e.setImage("attachment://profile_osu.png").build());
		return b;
	}

	public MessageAction sendFileEmbeded(DefaultEmbed e, File file) {
		MessageAction b = event.getChannel().sendFile(file, file.getName())
				.embed(e.construirEmbed().setImage("attachment://" + file.getName()).build());
		return b;
	}

	public MessageAction sendEmbedMessage(DefaultEmbed e) {
		MessageAction a = event.getChannel().sendMessage(e.construir());
		return a;
	}

	public MessageAction sendPrivateEmbedMessage(DefaultEmbed e) {
		MessageAction a = event.getAuthor().openPrivateChannel().complete().sendMessage(e.construir());
		return a;
	}

	public List<String> getAliases() {
		return aliases;
	}

	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
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

	public void setEvent(GuildMessageReceivedEvent event) {
		this.event = event;
	}

	public LanguageManager getLang() {
		return lang;
	}

	public Language getLanguage() {
		SQLAccess sql = new SQLAccess(getEvent().getGuild());
		return Language.valueOf(sql.get("language"));
	}

}
