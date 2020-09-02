package me.skiincraft.discord.ousu.embed;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import me.skiincraft.api.ousu.entity.beatmap.Beatmap;
import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.core.utils.Emoji;
import me.skiincraft.discord.core.utils.ImageUtils;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.emojis.OusuEmote;
import me.skiincraft.discord.ousu.htmlpage.BeatmapSearch;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

public class SearchEmbed {

	public synchronized static EmbedBuilder searchEmbed(BeatmapSearch beatmap, Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();
		LanguageManager lang = new LanguageManager(guild);

		StringBuffer gamemodes = new StringBuffer();
		StringBuffer versions = new StringBuffer();

		int i = 0;
		for (String dif : beatmap.getDifficult()) {
			if (dif.length() == 0) {
				continue;
			}
			versions.append(OusuEmote.getEmoteAsMention("rainbowcircle") + " " + dif + "\n");
			if (i >= 4) {
				versions.append("*[...]*");
				break;
			}
			i++;
		}

		for (Gamemode gm : beatmap.getGamemodes()) {
			gamemodes.append(OusuEmote.getEmoteAsMention(gm.name().toLowerCase()) + " " + gm.getDisplayName());
		}

		String artist = beatmap.getAuthor();
		// beatmap_download_link
		String video = Emoji.WHITE_CHECK_MARK.getAsMention() + " | Video";
		if (beatmap.hasVideo() == false) {
			video = Emoji.X.getAsMention() + " | Video";
		}

		String approvated = OusuEmote.getEmoteAsMention("arrow_pause") + " " + beatmap.getApprovated().name();
		embed.setTitle(beatmap.getTitle(), beatmap.getURL());
		int id = beatmap.getCreatorid();

		embed.setThumbnail("https://i.imgur.com/v8iU2Js.jpg");
		embed.setAuthor(lang.getString("Titles", "SEARCH") + " | " + beatmap.getCreator(),
				"https://osu.ppy.sh/users/" + id, "https://a.ppy.sh/" + id);

		embed.addField(lang.getString("Titles", "ARTIST") , artist, true);
		embed.addField("BPM:", OusuEmote.getEmoteAsMention("reversearrow") + beatmap.getBpm(), true);
		embed.addField(lang.getString("Titles", "GENRE") , "" + beatmap.getGenre().getDisplayName(), true);
		embed.addField("Link", OusuEmote.getEmoteAsMention("download") + "__[Download]" + "(" + beatmap.getURL() + ")__\n"
				+ video + "\n" + approvated, true);

		embed.addField(lang.getString("Titles", "GAMEMODE"), gamemodes.toString(), true);
		embed.addField(lang.getString("Titles", "DIFFICULT") , versions.toString(), true);

		String url = beatmap.getBeatmapCoverUrl();
		embed.setImage((ImageUtils.existsImage(url))
				? url
				: "https://i.imgur.com/LfF0VBR.gif");

		// User user = OusuBot.getOusu().getJda().getUserById("247096601242238991");
		// embed.setFooter(lang.translatedBot("FOOTER_DEFAULT"), user.getAvatarUrl());

		embed.setFooter("[BeatmapSetID] " + beatmap.getBeatmapsetid(), OusuBot.getMain().getShardManager().getShardById(0).getSelfUser().getAvatarUrl());
		try {
			embed.setColor(ImageUtils.getPredominatColor(ImageIO.read(new URL(beatmap.getBeatmapThumbnailUrl()))));
		} catch (NullPointerException | IOException e) {
			embed.setColor(Color.BLUE);
		}
		return embed;
	}

	@Deprecated
	public synchronized static EmbedBuilder beatmapEmbed(List<Beatmap> beat, Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();
		LanguageManager lang = new LanguageManager(guild);
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
				gamemodes.append(OusuEmote.getEmoteAsMention(b.getGameMode().name().toLowerCase()) + " "
						+ b.getGameMode().getDisplayName() + "\n");
			}
			if (versions.length() == 0) {
				versions.append(OusuEmote.getEmoteAsMention("rainbowcircle") + " " + b.getVersion());
			} else {
				versions.append("\n" + OusuEmote.getEmoteAsMention("rainbowcircle") + " " + b.getVersion());
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

		String approvated = OusuEmote.getEmoteAsMention("arrow_pause") + " " + beatmap.getApprovated().name();
		embed.setTitle(beatmap.getTitle(), beatmap.getURL());
		long id = beatmap.getCreatorId();

		embed.setThumbnail("https://i.imgur.com/v8iU2Js.jpg");
		embed.setAuthor(lang.getString("Titles", "SEARCH") + " | " + beatmap.getCreator(),
				"https://osu.ppy.sh/users/" + id, "https://a.ppy.sh/" + id);

		embed.addField(lang.getString("Titles", "ARTIST") , artist, true);
		embed.addField("BPM:", OusuEmote.getEmoteAsMention("reversearrow") + beatmap.getBPM(), true);
		embed.addField(lang.getString("Titles", "GENRE") , "" + beatmap.getGenre().getDisplayName(), true);
		embed.addField("Link", OusuEmote.getEmoteAsMention("download") + "__[Download]" + "(" + beatmap.getURL()
				+ ")__\n\n" + storyboard + "\n" + video + "\n" + approvated, true);

		embed.addField(lang.getString("Titles", "GAMEMODE") , gamemodes.toString(), true);
		embed.addField(lang.getString("Titles", "DIFFICULT"), versions.toString(), true);

		embed.setImage(beatmap.getBeatmapCoverUrl());

		// User user = OusuBot.getOusu().getJda().getUserById("247096601242238991");
		// embed.setFooter(lang.translatedBot("FOOTER_DEFAULT"), user.getAvatarUrl());

		embed.setFooter("[BeatmapSetID] " + beatmap.getBeatmapSetId(), OusuBot.getMain().getShardManager().getShardById(0).getSelfUser().getAvatarUrl());
		try {
			embed.setColor(ImageUtils.getPredominatColor(ImageIO.read(new URL(beatmap.getBeatmapThumbnailUrl()))));
		} catch (NullPointerException | IOException e) {
			embed.setColor(Color.BLUE);
		}
		return embed;
	}

}
