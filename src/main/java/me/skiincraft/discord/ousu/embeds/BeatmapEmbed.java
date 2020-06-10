package me.skiincraft.discord.ousu.embeds;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;

import me.skiincraft.api.ousu.beatmaps.Beatmap;
import me.skiincraft.discord.ousu.customemoji.OusuEmojis;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.language.LanguageManager.Language;
import me.skiincraft.discord.ousu.sqlite.GuildsDB;
import me.skiincraft.discord.ousu.utils.Emoji;
import me.skiincraft.discord.ousu.utils.ImageUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

public class BeatmapEmbed {

	public synchronized static EmbedBuilder beatmapEmbed(Beatmap beatmap, Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();
		GuildsDB sql = new GuildsDB(guild);
		LanguageManager lang = new LanguageManager(Language.valueOf(sql.get("language")));
		
		embed.setTitle(Emoji.HEADPHONES.getAsMention() + " " + beatmap.getTitle(), beatmap.getURL());
		int id = beatmap.getCreatorId();

		embed.setAuthor(beatmap.getCreator(), "https://osu.ppy.sh/users/" + id, "https://a.ppy.sh/" + id);

		String artist = beatmap.getArtist();

		// Isso daqui esta Temporario, pois esta ocorrendo um erro desconhecido....
		if (beatmap.getArtistUnicode() == null) {
			artist = beatmap.getArtist();
		} else {
			if (!beatmap.getArtistUnicode().equalsIgnoreCase(beatmap.getArtist())) {
				artist = beatmap.getArtistUnicode() + " \n(" + beatmap.getArtist() + ")";
			}
		}

		embed.addField(lang.translatedEmbeds("ARTIST"), artist, true);
		embed.addField("BPM:", OusuEmojis.getEmoteAsMention("reversearrow") + beatmap.getBPM(), true);
		embed.addField(lang.translatedEmbeds("GENRE"), "" + beatmap.getGenre().getDisplayName(), true);

		embed.addField(lang.translatedEmbeds("GAMEMODE"),
				OusuEmojis.getEmoteAsMention(beatmap.getGameMode().name().toLowerCase()) + " "
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
				+ OusuEmojis.getEmoteAsMention("arrow_pause") + beatmap.getApprovated().name(), true);
		embed.addField(lang.translatedEmbeds("SUCCESS_RATE"),
				Emoji.BAR_CHART.getAsMention() + " " + beatmap.getSuccessRate(), true);
		embed.addField(lang.translatedEmbeds("MAX_COMBO"), beatmap.getMaxCombo() + "", true);
		//https://i.imgur.com/LfF0VBR.gif
		
		embed.setImage((ImageUtils.existsImage(beatmap.getBeatmapCoverUrl()))
				? beatmap.getBeatmapCoverUrl()
				: "https://i.imgur.com/LfF0VBR.gif");

		embed.setFooter("[BeatmapSetID] " + beatmap.getBeatmapSetID() + " | " + "[BeatmapID]" + beatmap.getBeatmapID());
		try {
			embed.setColor(ImageUtils.getPredominatColor(ImageIO.read(new URL(beatmap.getBeatmapThumbnailUrl()))));
		} catch (NullPointerException | IOException e) {
			embed.setColor(Color.BLUE);
		}

		return embed;
	}

}
