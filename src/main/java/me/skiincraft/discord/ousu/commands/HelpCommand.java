package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import me.skiincraft.discord.core.command.Command;
import me.skiincraft.discord.core.configuration.GuildDB;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.core.utils.Emoji;
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

public class HelpCommand extends Comando {

	public HelpCommand() {
		super("help", Arrays.asList("ajuda"), "help <command>");
	}

	public static List<Command> commands = OusuBot.getMain().getPlugin().getCommandManager().getCommands();
	
	
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
			return;
		}

		return;
	}

	public EmbedBuilder emb(String comando, Guild guild) {
		EmbedBuilder embed = TypeEmbed.HelpEmbed("help title", "help description");
		String prefix = new GuildDB(guild).get("prefix");
		LanguageManager lang = getLanguageManager();
		for (Command com : commands) {
			if (comando.equalsIgnoreCase(com.getCommandName())) {
				embed.setTitle("Help <" + com.getCommandName() + ">");
				if (com.getCommandDescription(getLanguageManager()) != null) {
					String emoji = Emoji.SMALL_ORANGE_DIAMOND.getAsMention();;
					StringBuffer buffer = new StringBuffer();
					
					List<String> helpmessages = Arrays.asList(com.getCommandDescription(getLanguageManager()));
					helpmessages.forEach(str ->{
						buffer.append(emoji + " " + str + "\n");
					});
					
					embed.setDescription(buffer.toString());
				} else {
					embed.setDescription(lang.getString("Warnings", "NO_COMMAND_DESCRIPTION"));
				}

				if (com.getAliases() != null && com.getAliases().size() != 0) {
					String[] alias = new String[com.getAliases().size()];
					com.getAliases().toArray(alias);
					StringBuffer buffer = new StringBuffer();
					for (String str : alias) {
						buffer.append(prefix + str + "\n");
					}
					embed.addField(":mega: Aliases", buffer.toString(), true);
				}

				embed.addField(":pencil: Usage", com.getUsage().replace("ou!", prefix), true);
				embed.setFooter(prefix + "help to help!");
				return embed;
			}
		}

		String[] msg = lang.getStrings("Messages", "INEXISTENT_COMMAND_HELP");
		return TypeEmbed.SoftWarningEmbed(OusuEmote.getEmoteAsMention("thinkanime") + msg[0],
				Emoji.SPACE_INVADER.getAsMention() + " " + StringUtils.commandMessage(msg)).setFooter(prefix + "help to help!");
	}

	public EmbedBuilder embed(Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();

		StringBuffer a = new StringBuffer();
		StringBuffer c = new StringBuffer();
		StringBuffer d = new StringBuffer();
		StringBuffer e = new StringBuffer();

		for (int i = 0; i < commands.size(); i++) {
			Comando comando = (Comando) commands.get(i);
			String prefix = new GuildDB(guild).get("prefix");
			if (comando.getCategory() == CommandCategory.Osu) {
				c.append("- " + prefix + comando.getCommandName());
				c.append(",");
			}
			if (comando.getCategory() == CommandCategory.Sobre) {
				d.append("- " + prefix + comando.getCommandName());
				d.append(",");
			}
			if (comando.getCategory() == CommandCategory.Administracao) {
				a.append("- " + prefix + comando.getCommandName());
				a.append(",");
			}
			if (comando.getCategory() == CommandCategory.Utilidade) {
				e.append("- " + prefix + comando.getCommandName());
				e.append(",");
			}
		}

		String[] Adm = a.toString().split(",");
		Arrays.sort(Adm);
		String[] Osu = c.toString().split(",");
		Arrays.sort(Osu);
		String[] Sobre = d.toString().split(",");
		Arrays.sort(Sobre);
		String[] Util = e.toString().split(",");
		Arrays.sort(Util);
		
		LanguageManager lang = getLanguageManager();
		
		String[] str = lang.getStrings("Messages", "HELP_COMMAND_MESSAGE");

		embed.setTitle(str[0]);
		embed.setThumbnail(OusuBot.getMain().getShardManager().getShardById(0).getSelfUser().getAvatarUrl());
		StringBuffer buffer = new StringBuffer();
		for (String append : str) {
			if (append != str[0]) {
				buffer.append(append + "\n");
			}
		}

		embed.setDescription(buffer.toString());
		
		embed.addField(OusuEmote.getEmoteAsMention("osulogo") + " **" + CommandCategory.Osu.getCategoryName(lang) + "**",
				"`" + String.join("\n", Osu) + "`", true);
		embed.addField(":computer: **" + CommandCategory.Administracao.getCategoryName(lang) + "**",
				"`" + String.join("\n", Adm) + "`", true);
		embed.addField(":bulb: **" + CommandCategory.Sobre.getCategoryName(lang) + "**",
				"`" + String.join("\n", Sobre) + "`", true);

		User user = OusuBot.getMain().getShardManager().getUserById("247096601242238991");
		embed.setColor(new Color(226, 41, 230));
		embed.setFooter(user.getName() + "#" + user.getDiscriminator() + " | Ou!su bot ™", user.getAvatarUrl());
		return embed;
	}
}
