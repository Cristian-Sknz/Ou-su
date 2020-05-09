package me.skiincraft.discord.ousu.embeds;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import me.skiincraft.api.ousu.beatmaps.Beatmap;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

public class BeatmapEmbed {

	public static InputStream idb;

	public synchronized static EmbedBuilder beatmapEmbed(List<Beatmap> beat, int value, Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();
		SQLAccess sql = new SQLAccess(guild);

		LanguageManager lang = new LanguageManager(Language.valueOf(sql.get("language")));

		Beatmap beatmap = beat.get(value);
		
		embed.setTitle(beatmap.getTitle(), beatmap.getURL());
		int id = beatmap.getCreatorId();

		embed.setAuthor(beatmap.getCreator(), "https://osu.ppy.sh/users/" + id, "https://a.ppy.sh/" + id);

		embed.addField(lang.translatedEmbeds("ARTIST"), beatmap.getArtistUnicode() + " \n(" + beatmap.getArtist() + ")", true);
		embed.addField("BPM:", "" + beatmap.getBPM(), true);
		embed.addField(lang.translatedEmbeds("GENRE"), "" + beatmap.getGenre().getDisplayName(), true);

		embed.addField(lang.translatedEmbeds("GAMEMODE"), beatmap.getGameMode().getDisplayName(), true);
		embed.addField(lang.translatedEmbeds("STARS"), beatmap.getStarsEmoji(), true);
		embed.addField(lang.translatedEmbeds("DIFFICULT"), beatmap.getVersion(), true);

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy - HH:mm");

		embed.addField(lang.translatedEmbeds("APPROVATED_IN"),
				format.format(beatmap.getApprovedDate()) + "\n" + beatmap.getApprovated().name(), true);
		embed.addField(lang.translatedEmbeds("SUCCESS_RATE"), beatmap.getSuccessRate(), true);
		embed.addField(lang.translatedEmbeds("MAX_COMBO"), beatmap.getMaxCombo() + "", true);

		embed.setImage(beatmap.getBeatmapCoverUrl());
		
		//User user = OusuBot.getOusu().getJda().getUserById("247096601242238991");
		//embed.setFooter(lang.translatedBot("FOOTER_DEFAULT"), user.getAvatarUrl());
		
		embed.setFooter("[BeatmapSetID] " + beatmap.getBeatmapSetID() + " | "
				+ "[BeatmapID]" + beatmap.getBeatmapID());

		try {
			idb = beatmap.getBeatmapPreview();
		} catch (IOException e) {
			e.printStackTrace();
			return embed;
		}
		return embed;
	}

}