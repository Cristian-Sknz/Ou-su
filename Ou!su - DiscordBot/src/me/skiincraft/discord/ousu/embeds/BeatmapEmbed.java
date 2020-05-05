package me.skiincraft.discord.ousu.embeds;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import me.skiincraft.api.ousu.beatmaps.Beatmap;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class BeatmapEmbed {

	public static InputStream idb;

	public synchronized static EmbedBuilder beatmapEmbed(Beatmap beatmap, Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();
		SQLAccess sql = new SQLAccess(guild);

		LanguageManager lang = new LanguageManager(Language.valueOf(sql.get("language")));

		embed.setTitle(beatmap.getTitle());
		int id = beatmap.getCreatorId();

		embed.setAuthor(beatmap.getCreator(), "https://osu.ppy.sh/users/" + id, "https://a.ppy.sh/" + id);

		embed.addField(lang.translatedEmbeds("ARTIST"), beatmap.getArtistUnicode(), true);
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
		User user = OusuBot.getOusu().getJda().getUserById("247096601242238991");
		embed.setFooter(lang.translatedBot("FOOTER_DEFAULT"), user.getAvatarUrl());

		try {
			idb = beatmap.getBeatmapPreview();
		} catch (IOException e) {
			e.printStackTrace();
			return embed;
		}
		return embed;
	}

}
