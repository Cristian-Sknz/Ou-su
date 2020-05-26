package me.skiincraft.discord.ousu.embeds;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.imageio.ImageIO;

import me.skiincraft.api.ousu.beatmaps.Beatmap;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.mysql.SQLAccess;
import me.skiincraft.discord.ousu.utils.Emoji;
import me.skiincraft.discord.ousu.utils.ImageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

public class BeatmapEmbed {

	public static InputStream idb;

	public synchronized static EmbedBuilder beatmapEmbed(List<Beatmap> beat, int value, Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();
		SQLAccess sql = new SQLAccess(guild);

		LanguageManager lang = new LanguageManager(Language.valueOf(sql.get("language")));

		Beatmap beatmap = beat.get(value);

		embed.setTitle(Emoji.HEADPHONES.getAsMention() + " " + beatmap.getTitle(), beatmap.getURL());
		int id = beatmap.getCreatorId();

		embed.setAuthor(beatmap.getCreator(), "https://osu.ppy.sh/users/" + id, "https://a.ppy.sh/" + id);

		String artist = beatmap.getArtist();
		
		//Isso daqui esta Temporario, pois esta ocorrendo um erro desconhecido....
		if (beatmap.getArtistUnicode() == null) {
			artist = beatmap.getArtist();
		} else {
			if (!beatmap.getArtistUnicode().equalsIgnoreCase(beatmap.getArtist())) {
				artist = beatmap.getArtistUnicode() + " \n(" + beatmap.getArtist() + ")";
			}
		}
		
		embed.addField(lang.translatedEmbeds("ARTIST"), artist, true);
		embed.addField("BPM:", OusuBot.getEmoteAsMention("reversearrow") + beatmap.getBPM(), true);
		embed.addField(lang.translatedEmbeds("GENRE"), "" + beatmap.getGenre().getDisplayName(), true);

		embed.addField(lang.translatedEmbeds("GAMEMODE"),
				OusuBot.getEmoteAsMention(beatmap.getGameMode().name().toLowerCase()) + " "
						+ beatmap.getGameMode().getDisplayName(),
				true);
		embed.addField(lang.translatedEmbeds("STARS"), beatmap.getStarsEmoji(), true);
		embed.addField(lang.translatedEmbeds("DIFFICULT"), beatmap.getVersion(), true);

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

		String app = "?";
		if (beatmap.getApprovedDate() != null) {
			app = format.format(beatmap.getApprovedDate());
		}

		embed.addField(lang.translatedEmbeds("APPROVATED_IN"), Emoji.DATE.getAsMention() + " " + app + "\n"
				+ OusuBot.getEmoteAsMention("arrow_pause") + beatmap.getApprovated().name(), true);
		embed.addField(lang.translatedEmbeds("SUCCESS_RATE"),
				Emoji.BAR_CHART.getAsMention() + " " + beatmap.getSuccessRate(), true);
		embed.addField(lang.translatedEmbeds("MAX_COMBO"), beatmap.getMaxCombo() + "", true);

		embed.setImage(beatmap.getBeatmapCoverUrl());

		// User user = OusuBot.getOusu().getJda().getUserById("247096601242238991");
		// embed.setFooter(lang.translatedBot("FOOTER_DEFAULT"), user.getAvatarUrl());

		embed.setFooter("[BeatmapSetID] " + beatmap.getBeatmapSetID() + " | " + "[BeatmapID]" + beatmap.getBeatmapID());
		try {
			embed.setColor(ImageUtils.getPredominatColor(ImageIO.read(new URL(beatmap.getBeatmapThumbnailUrl()))));
		} catch (NullPointerException | IOException e) {
			embed.setColor(Color.BLUE);
		}

		try {
			idb = beatmap.getBeatmapPreview();
		} catch (IOException e) {
			e.printStackTrace();
			return embed;
		}
		return embed;
	}

}
