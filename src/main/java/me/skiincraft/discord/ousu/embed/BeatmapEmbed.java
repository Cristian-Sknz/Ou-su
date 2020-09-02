package me.skiincraft.discord.ousu.embed;

import java.awt.Color;
import java.text.SimpleDateFormat;

import me.skiincraft.api.ousu.entity.beatmap.Beatmap;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.core.utils.Emoji;
import me.skiincraft.discord.core.utils.ImageUtils;
import me.skiincraft.discord.ousu.emojis.OusuEmote;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;

public class BeatmapEmbed {
	
	public synchronized static EmbedBuilder beatmapEmbed(Beatmap beatmap, Guild guild, Color cor) {
		EmbedBuilder embed = new EmbedBuilder();
		LanguageManager lang = new LanguageManager(guild);
		
		embed.setTitle(beatmap.getTitle() + " - Beatmap", beatmap.getURL());
		long id = beatmap.getCreatorId();

		embed.setAuthor(beatmap.getCreatorName(), "https://osu.ppy.sh/users/" + id, "https://a.ppy.sh/" + id);

		String artist = beatmap.getArtist();

		if (beatmap.getArtistUnicode() == null) {
			artist = beatmap.getArtist();
		} else {
			if (!beatmap.getArtistUnicode().equalsIgnoreCase(beatmap.getArtist())) {
				artist = "`"+ beatmap.getArtistUnicode() + "`  (" + beatmap.getArtist() + ")";
			}
		}

		embed.setDescription(":microphone: "+ lang.getString("Titles", "ARTIST") + " " + artist + "\n");
		embed.appendDescription(":tickets: "+ lang.getString("Titles", "GENRE") + " " + beatmap.getGenre().getDisplayName() + "\n");
		embed.appendDescription(OusuEmote.getEmoteAsMention(beatmap.getGameMode().name().toLowerCase()) + " "
				+ lang.getString("Titles", "GAMEMODE") + " " + beatmap.getGameMode().name() + "\n");
		
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");

		String app = "?";
		if (beatmap.getApprovedDate() != null) {
			app = format.format(beatmap.getApprovedDate());
		}
		
		embed.appendDescription(OusuEmote.getEmoteAsMention("arrow_pause") + beatmap.getApprovated().name());
		
		embed.addField(lang.getString("Titles", "DIFFICULT"), beatmap.getVersion(), true);
		
		embed.addField(lang.getString("Titles", "SUCCESS_RATE"),
				Emoji.BAR_CHART.getAsMention() + " " + beatmap.getSuccessRate(), true);
		
		embed.addField(lang.getString("Titles", "MAX_COMBO"), beatmap.getMaxCombo() + "", true);
		
		embed.addField(lang.getString("Titles", "APPROVATED_IN"), Emoji.DATE.getAsMention() + " " + app, true);
		
		embed.addField("BPM:", OusuEmote.getEmoteAsMention("reversearrow") + beatmap.getBPM(), true);
		embed.addField(lang.getString("Titles", "STARS"), beatmap.getStarsEmoji(), true);
		
		embed.setThumbnail(beatmap.getBeatmapThumbnailUrl());
		
		embed.setImage((ImageUtils.existsImage(beatmap.getBeatmapCoverUrl()))
				? beatmap.getBeatmapCoverUrl()
				: "https://i.imgur.com/LfF0VBR.gif");

		embed.setFooter("[BeatmapSetID] " + beatmap.getBeatmapSetId() + " | " + "[BeatmapID]" + beatmap.getBeatmapId());
		embed.setColor(cor);

		return embed;
	}

}
