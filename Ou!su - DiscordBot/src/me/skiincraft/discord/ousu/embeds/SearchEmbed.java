package me.skiincraft.discord.ousu.embeds;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import me.skiincraft.api.ousu.beatmaps.Beatmap;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

public class SearchEmbed {

	public static InputStream idb;

	public synchronized static InputStream getAudioPreview() {
		return idb;
	}

	public synchronized static EmbedBuilder beatmapEmbed(List<Beatmap> beat, Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();
		SQLAccess sql = new SQLAccess(guild);

		LanguageManager lang = new LanguageManager(Language.valueOf(sql.get("language")));

		Beatmap beatmap = beat.get(0);

		StringBuffer gamemodes = new StringBuffer();
		StringBuffer versions = new StringBuffer();
		int i = 0;
		for (Beatmap b : beat) {
			if (i >= 5) {
				versions.append("...");
				break;
			}
			if (!gamemodes.toString().contains(b.getGameMode().getDisplayName())) {
				gamemodes.append(b.getGameMode().getDisplayName() + "\n");
			}

			versions.append(b.getVersion() + "\n");
			i++;
		}

		embed.setTitle(beatmap.getTitle(), beatmap.getURL());
		int id = beatmap.getCreatorId();

		embed.setAuthor(lang.translatedEmbeds("SEARCH") + " | " + beatmap.getCreator(),
				"https://osu.ppy.sh/users/" + id, "https://a.ppy.sh/" + id);

		embed.addField(lang.translatedEmbeds("ARTIST"), beatmap.getArtistUnicode() + " \n(" + beatmap.getArtist() + ")",
				true);
		embed.addField("BPM:", "" + beatmap.getBPM(), true);
		embed.addField(lang.translatedEmbeds("GENRE"), "" + beatmap.getGenre().getDisplayName(), true);
		embed.addField("Link", "[Download]" + "(" + beatmap.getURL() + ")", true);

		embed.addField(lang.translatedEmbeds("GAMEMODE"), gamemodes.toString(), true);
		embed.addField(lang.translatedEmbeds("DIFFICULT"), versions.toString(), true);

		embed.setImage(beatmap.getBeatmapCoverUrl());

		// User user = OusuBot.getOusu().getJda().getUserById("247096601242238991");
		// embed.setFooter(lang.translatedBot("FOOTER_DEFAULT"), user.getAvatarUrl());

		embed.setFooter("[BeatmapSetID] " + beatmap.getBeatmapSetID(), OusuBot.getSelfUser().getAvatarUrl());

		try {
			idb = beatmap.getBeatmapPreview();
		} catch (IOException e) {
			e.printStackTrace();
			return embed;
		}
		return embed;
	}

}
