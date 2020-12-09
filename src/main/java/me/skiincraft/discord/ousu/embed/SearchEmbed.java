package me.skiincraft.discord.ousu.embed;

import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.emojis.GenericsEmotes;
import me.skiincraft.discord.ousu.osu.BeatmapSearch;
import me.skiincraft.discord.ousu.utils.ImageUtils;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Objects;

public class SearchEmbed {

	public static EmbedBuilder searchEmbed(BeatmapSearch beatmap, LanguageManager lang){
		EmbedBuilder embed = new EmbedBuilder();
		StringBuilder gamemodes = new StringBuilder();
		embed.setAuthor(beatmap.getCreator() + "| Search Beatmaps", "https://osu.ppy.sh/users/" + beatmap.getCreatorId(), "https://a.ppy.sh/" + beatmap.getCreatorId());
		embed.setTitle(beatmap.getTitle());

		embed.setDescription(":microphone: " + lang.getString("Titles", "ARTIST") + " " + beatmap.getAuthor().concat("\n"))
				.appendDescription(":tickets: " + lang.getString("Titles", "GENRE") + " " + beatmap.getGenre().getDisplayName().concat("\n"))
				.appendDescription(GenericsEmotes.getEmoteAsMention(beatmap.getGamemodes()[0].name().toLowerCase()).concat(" ") + lang.getString("Titles", "GAMEMODE") + " " + beatmap.getGamemodes()[0].getDisplayName().concat("\n"))
				.appendDescription(":compass: " + ((Objects.isNull(beatmap.getApprovated())) ? "?" : beatmap.getApprovated().name()));

		StringBuilder versions = new StringBuilder();

		int i = 0;
		for (String dif : beatmap.getDifficult()) {
			if (dif.length() == 0) continue;
			versions.append(GenericsEmotes.getEmoteAsMention("rainbowcircle")).append(" ").append(dif).append("\n");
			if (i >= 4) {
				versions.append("*[...]*");
				break;
			}
			i++;
		}

		embed.addField(lang.getString("Titles", "DIFFICULT"), versions.toString(), true);
		embed.addField("Link:", GenericsEmotes.getEmoteAsMention("download") + "[Download](" + beatmap.getURL().concat(")"), true);

		embed.setColor(new Color(255, 128, 87));
		embed.setThumbnail("https://cdn.discordapp.com/emojis/770368513726087179.png?v=1");
		embed.setImage((ImageUtils.existsImage(beatmap.getBeatmapCoverUrl()))
				? beatmap.getBeatmapCoverUrl()
				: "https://i.imgur.com/LfF0VBR.gif");
		embed.setFooter("[BeatmapSetID] " + beatmap.getBeatmapSetId(), Objects.requireNonNull(OusuBot.getInstance().getShardManager().getShardById(0)).getSelfUser().getAvatarUrl());
		return embed;
	}
}
