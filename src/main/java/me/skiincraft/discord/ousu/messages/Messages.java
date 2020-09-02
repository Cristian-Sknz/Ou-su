package me.skiincraft.discord.ousu.messages;

import me.skiincraft.discord.core.configuration.Language;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.core.objects.OubedBuilder;
import me.skiincraft.discord.core.objects.RandomThumbnail;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.emojis.OusuEmote;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;

public class Messages {
	
	public static Message UsageMessage(Comando command, Language language) {
		MessageBuilder builder = new MessageBuilder();
		LanguageManager lang = new LanguageManager(language);
		OubedBuilder embed = new OubedBuilder(lang.getString("Warnings", "T_USAGE"), OusuEmote.getEmoteAsMention("small_red_diamond") + " ");
		embed.applyWarning(new RandomThumbnail("https://i.imgur.com/4ZkdIyq.png"));
		embed.appendDescription(lang.getString("Warnings", "Usage").replace("{usage}", command.getUsage()));
		
		embed.setFooter("Type ou!help");
		
		builder.setEmbed(embed.build());
		return builder.build();
	}

}
