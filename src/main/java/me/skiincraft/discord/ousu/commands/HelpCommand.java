package me.skiincraft.discord.ousu.commands;

import me.skiincraft.discord.core.OusuCore;
import me.skiincraft.discord.core.command.Command;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.common.reactions.ReactionObject;
import me.skiincraft.discord.core.common.reactions.Reactions;
import me.skiincraft.discord.core.common.reactions.custom.ReactionSelector;
import me.skiincraft.discord.core.configuration.GuildDB;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.core.utils.StringUtils;
import me.skiincraft.discord.ousu.common.OusuCommand;
import me.skiincraft.discord.ousu.emojis.GenericEmote;
import me.skiincraft.discord.ousu.emojis.GenericsEmotes;
import me.skiincraft.discord.ousu.messages.TypeEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand extends OusuCommand {

	public HelpCommand() {
		super("help", Collections.singletonList("ajuda"), "help <command>");
	}

	public static final List<Command> commands = OusuCore.getCommandManager().getCommands();
	
	
	public CommandCategory getCategory() {
		return CommandCategory.About;
	}

	@Override
	public void execute(Member user, String[] args, InteractChannel channel) {
		if (args.length == 0) {
			EmbedBuilder embed = helpMessage(channel.getTextChannel().getGuild(), channel.getTextChannel().getJDA().getSelfUser(), null);
			channel.reply(embed.build(), message -> {
				List<String> emote = new ArrayList<>();
				emote.add(GenericsEmotes.getEmoteEquals("home").getReaction());
                emote.addAll(Arrays.stream(CommandCategory.values())
                        .map(this::getCategoryEmote)
                        .map(GenericEmote::getReaction).collect(Collectors.toList()));
				emote.remove(emote.size()-1);

				List<EmbedBuilder> embeds = new ArrayList<>();
				embeds.add(embed);
				for (int i = 0; i < emote.size() - 1; i++) {
					embeds.add(helpMessage(channel.getTextChannel().getGuild(), channel.getTextChannel().getGuild().getJDA().getSelfUser(), CommandCategory.values()[i]));
				}
				Objects.requireNonNull(Reactions.getInstance()).registerReaction(new ReactionObject(message, user.getIdLong(), emote.toArray(new String[0])), new ReactionSelector(embeds, true));
			});
			return;
		}

		if (args.length == 1) {
			MessageEmbed embed = emb(args[0], channel.getTextChannel().getGuild()).build();
			channel.reply(embed);
		}
	}

	public EmbedBuilder emb(String comando, Guild guild) {
		EmbedBuilder embed = TypeEmbed.HelpEmbed("help title", "help description");
		String prefix = new GuildDB(guild).get("prefix");
		LanguageManager lang = getLanguageManager(guild);
		for (Command com : commands) {
			if (comando.equalsIgnoreCase(com.getCommandName())) {
				embed.setTitle("Help <" + com.getCommandName() + ">");
				if (com.getCommandDescription(lang) != null) {
					String builder = ":small_orange_diamond: " +
							com.getCommandDescription(lang) +
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
		return TypeEmbed.SoftWarningEmbed(GenericsEmotes.getEmoteAsMention("thinkanime") + msg[0],
				":space_invader: " + StringUtils.commandMessage(msg)).setFooter(prefix + "help to help!");
	}

	public EmbedBuilder helpMessage(Guild guild, SelfUser selfUser, CommandCategory category) {
		EmbedBuilder embed = new EmbedBuilder();
		LanguageManager lang = getLanguageManager(guild);
		embed.setAuthor(selfUser.getName(), null, selfUser.getAvatarUrl());

		if (category == null){
			String[] message = lang.getStrings("HelpCommand", "HOME_MESSAGE");
			embed.setTitle(message[0]);
			embed.setDescription(StringUtils.commandMessage(message));
			embed.setThumbnail(selfUser.getAvatarUrl());
		} else {
			String[] message = lang.getStrings("HelpCommand", category.name().toUpperCase() + "_MESSAGE");
			embed.setTitle(message[0]);
			embed.setDescription(StringUtils.commandMessage(message));
			embed.setThumbnail(selfUser.getAvatarUrl());
			embed.setThumbnail(getCategoryEmote(category).getEmoteUrl());
			String prefix = new GuildDB(guild).get("prefix");

			embed.addField(lang.getString("Embeds", "COMMANDS"), String.join("\n", OusuCore.getCommandManager().getCommands()
					.stream()
					.filter(command -> command instanceof OusuCommand)
					.filter(comando -> ((OusuCommand) comando).getCategory() == category)
					.map(command -> prefix + command.getCommandName())
					.sorted().toArray(String[]::new)), true);
		}
		embed.setImage("https://i.imgur.com/CvE3ONU.png");
		embed.setColor(new Color(255, 152, 119));
		embed.setFooter("Sknz#4260 | Ou!su bot ™");
		return embed;
	}

	public GenericEmote getCategoryEmote(CommandCategory category){
		return GenericsEmotes.getEmoteEquals(category.name());
	}

}
