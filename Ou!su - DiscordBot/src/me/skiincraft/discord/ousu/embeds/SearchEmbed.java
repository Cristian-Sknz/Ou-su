package me.skiincraft.discord.ousu.embeds;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
				versions.append("*[...]*");
				break;
			}
			if (!gamemodes.toString().contains(b.getGameMode().getDisplayName())) {
				gamemodes.append(OusuBot.getEmoteAsMention(b.getGameMode().name().toLowerCase()) + " "
						+ b.getGameMode().getDisplayName() + "\n");
			}
			if (versions.length() == 0) {
				versions.append(OusuBot.getEmoteAsMention("rainbowcircle")+" "+ b.getVersion());	
			} else {
			versions.append("\n"+ OusuBot.getEmoteAsMention("rainbowcircle")+" "+ b.getVersion());
			}
			i++;
		}
		
		String artist = beatmap.getArtist();
		if (!beatmap.getArtistUnicode().equalsIgnoreCase(beatmap.getArtist())) {
			artist = beatmap.getArtistUnicode() + " \n(" + beatmap.getArtist() + ")";
		}
		
		String video = Emoji.WHITE_CHECK_MARK.getAsMention() + " | Video";
		String storyboard = Emoji.WHITE_CHECK_MARK.getAsMention() + " | Storyboard";
		if (beatmap.hasVideo() == false) {
			video = Emoji.X.getAsMention() + " | Video";
		}
		if (beatmap.hasStoryboard() == false) {
			storyboard = Emoji.X.getAsMention() + " | Storyboard";
		}
		
		String approvated = OusuBot.getEmoteAsMention("arrow_pause") + " " + beatmap.getApprovated().name();
		embed.setTitle(beatmap.getTitle(), beatmap.getURL());
		int id = beatmap.getCreatorId();
		
		embed.setThumbnail("https://i.imgur.com/v8iU2Js.jpg");
		embed.setAuthor(lang.translatedEmbeds("SEARCH") + " | " + beatmap.getCreator(),
				"https://osu.ppy.sh/users/" + id, "https://a.ppy.sh/" + id);

		embed.addField(lang.translatedEmbeds("ARTIST"), artist,
				true);
		embed.addField("BPM:", OusuBot.getEmoteAsMention("reversearrow") + beatmap.getBPM(), true);
		embed.addField(lang.translatedEmbeds("GENRE"), "" + beatmap.getGenre().getDisplayName(), true);
		embed.addField("Link", OusuBot.getEmoteAsMention("download") + "__[Download]" + "(" + beatmap.getURL() + ")__\n\n" +
				storyboard + "\n" + video + "\n" + approvated
				, true);

		embed.addField(lang.translatedEmbeds("GAMEMODE"), gamemodes.toString(), true);
		embed.addField(lang.translatedEmbeds("DIFFICULT"), versions.toString(), true);

		embed.setImage(beatmap.getBeatmapCoverUrl());

		// User user = OusuBot.getOusu().getJda().getUserById("247096601242238991");
		// embed.setFooter(lang.translatedBot("FOOTER_DEFAULT"), user.getAvatarUrl());

		embed.setFooter("[BeatmapSetID] " + beatmap.getBeatmapSetID(), OusuBot.getSelfUser().getAvatarUrl());
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
