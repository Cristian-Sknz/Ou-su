package me.skiincraft.discord.ousu.commands;

import me.skiincraft.discord.core.command.Command;
import me.skiincraft.discord.core.configuration.GuildDB;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.emojis.OusuEmote;
import me.skiincraft.discord.ousu.messages.TypeEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.skiincraft.discord.core.utils.Emoji.SMALL_ORANGE_DIAMOND;
import static me.skiincraft.discord.core.utils.Emoji.SPACE_INVADER;

public class HelpCommand extends Comando {

	public HelpCommand() {
		super("help", Collections.singletonList("ajuda"), "help <command>");
	}

	public static final List<Command> commands = OusuBot.getInstance().getPlugin().getCommandManager().getCommands();
	
	
	public CommandCategory getCategory() {
		return CommandCategory.Sobre;
	}

	@Override
	public void execute(User user, String[] args, TextChannel channel) {
		if (args.length == 0) {
			MessageEmbed embed = embed(channel.getGuild()).build();
			reply(embed);
			return;
		}

		if (args.length == 1) {
			MessageEmbed embed = emb(args[0], channel.getGuild()).build();
			reply(embed);
		}
	}

	public EmbedBuilder emb(String comando, Guild guild) {
		EmbedBuilder embed = TypeEmbed.HelpEmbed("help title", "help description");
		String prefix = new GuildDB(guild).get("prefix");
		LanguageManager lang = getLanguageManager();
		for (Command com : commands) {
			if (comando.equalsIgnoreCase(com.getCommandName())) {
				embed.setTitle("Help <" + com.getCommandName() + ">");
				if (com.getCommandDescription(getLanguageManager()) != null) {
					String emoji = SMALL_ORANGE_DIAMOND.getAsMention();
					String builder = emoji +
							" " +
							com.getCommandDescription(getLanguageManager()) +
							"\n";

					embed.setDescription(builder);
				} else {
					embed.setDescription(lang.getString("Warnings", "NO_COMMAND_DESCRIPTION"));
				}

				if (com.getAliases() != null && com.getAliases().size() != 0) {
					String[] alias = new String[com.getAliases().size()];
					com.getAliases().toArray(alias);
					StringBuilder builder = new StringBuilder();
					for (String str : alias) {
						builder.append(prefix)
								.append(str)
								.append("\n");
					}
					embed.addField(":mega: Aliases", builder.toString(), true);
				}

				embed.addField(":pencil: Usage", com.getUsage().replace("ou!", prefix), true);
				embed.setFooter(prefix + "help to help!");
				return embed;
			}
		}

		String[] msg = lang.getStrings("Messages", "INEXISTENT_COMMAND_HELP");
		return TypeEmbed.SoftWarningEmbed(OusuEmote.getEmoteAsMention("thinkanime") + msg[0],
				SPACE_INVADER.getAsMention() + " " + StringUtils.commandMessage(msg)).setFooter(prefix + "help to help!");
	}

	public EmbedBuilder embed(Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();
		LanguageManager lang = getLanguageManager();

		List<StringBuilder> builders = new ArrayList<>();
		for (int i = 0; i <= 3; i++) {
			builders.add(new StringBuilder());
		}

		for (Command command : commands) {
			if (!(command instanceof Comando)) {
				continue;
			}
			Comando comando = (Comando) command;
			String prefix = new GuildDB(guild).get("prefix");
			StringBuilder builder;
			switch (comando.getCategory()) {
				case Administracao:
					builder = builders.get(0);
					builder.append("- ")
							.append(prefix)
							.append(comando.getCommandName())
							.append(",");
					continue;
				case Osu:
					builder = builders.get(1);
					builder.append("- ")
							.append(prefix)
							.append(comando.getCommandName())
							.append(",");
					continue;
				case Sobre:
					builder = builders.get(2);
					builder.append("- ")
							.append(prefix)
							.append(comando.getCommandName())
							.append(",");
			}
		}

		List<String> stringssorted = new ArrayList<>();
		for (int i = 0; i <= 3; i++){
			String[] strings = builders.get(i).toString().split(",");
			Arrays.sort(strings);
			stringssorted.add(joinString(strings));
		}
		
		String[] str = lang.getStrings("Messages", "HELP_COMMAND_MESSAGE");

		embed.setTitle(str[0]);
		embed.setThumbnail(OusuBot.getInstance().getShardManager().getShardById(0).getSelfUser().getAvatarUrl());
		StringBuilder buffer = new StringBuilder();
		for (String append : str) {
			if (!append.equals(str[0])) buffer.append(append).append("\n");
		}

		embed.setDescription(buffer.toString());
		embed.addField(":computer: **" + CommandCategory.Administracao.getCategoryName(lang) + "**",
				"`" + stringssorted.get(0)  + "`", true);
		embed.addField(OusuEmote.getEmoteAsMention("osulogo") + " **" + CommandCategory.Osu.getCategoryName(lang) + "**",
				"`" + stringssorted.get(1)  + "`", true);
		embed.addField(":bulb: **" + CommandCategory.Sobre.getCategoryName(lang) + "**",
				"`" + stringssorted.get(2)  + "`", true);

		User user = OusuBot.getInstance().getShardManager().getUserById("247096601242238991");
		embed.setColor(new Color(226, 41, 230));
		embed.setFooter(user.getName() + "#" + user.getDiscriminator() + " | Ou!su bot ™", user.getAvatarUrl());
		return embed;
	}

	public String joinString(String[] strings){
		return String.join("\n", strings);
	}
}
