package me.skiincraft.discord.ousu.messages;

import me.skiincraft.discord.core.configuration.GuildDB;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.emojis.GenericEmote;
import me.skiincraft.discord.ousu.emojis.GenericsEmotes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import org.apache.commons.lang3.text.WordUtils;

import java.awt.*;

public class Messages {

	public static MessageEmbed usageMessage(Comando command, Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();
		LanguageManager lang = new LanguageManager(guild);
		String prefix = new GuildDB(guild).get("prefix");
		SelfUser self = OusuBot.getInstance().getShardManager().getShards().get(0).getSelfUser();

		embed.setAuthor(lang.getString("UsageMessage", "WANT_HELP"), null, self.getAvatarUrl());
		embed.setTitle(lang.getString("UsageMessage", "SEE_THE_USE"));
		embed.setThumbnail(getCategoryEmote(command.getCategory()).getEmoteUrl());
		embed.setDescription("**`"+ prefix + command.getCommandName() + "`**\n")
				.appendDescription(WordUtils.wrap(command.getCommandDescription(lang),38));

		embed.addField(lang.getStrings("UsageMessage", "HOW_TO_USE")[0],
				lang.getStrings("UsageMessage", "HOW_TO_USE")[1]
						.concat(" **`")
						.concat(prefix)
						.concat(command.getUsage()) + "`**", false);

		embed.setFooter(lang.getString("UsageMessage", "HELP_COMMAND"));
		embed.setColor(new Color(255, 128, 87));

		return embed.build();
	}

	private static GenericEmote getCategoryEmote(CommandCategory category){
		return GenericsEmotes.getEmoteEquals(category.name());
	}

}
