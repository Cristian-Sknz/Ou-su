package me.skiincraft.discord.ousu.embeds;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import me.skiincraft.api.ousu.beatmaps.Beatmap;
import me.skiincraft.discord.ousu.OusuBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class BeatmapEmbed {

	public static InputStream idb;

	public synchronized static EmbedBuilder beatmapEmbed(Beatmap beatmap) {
		EmbedBuilder embed = new EmbedBuilder();

		embed.setTitle(beatmap.getTitle());
		int id = beatmap.getCreatorId();

		embed.setAuthor(beatmap.getCreator(), "https://osu.ppy.sh/users/" + id, "https://a.ppy.sh/" + id);

		embed.addField("Artista:", beatmap.getArtistUnicode(), true);
		embed.addField("BPM:", "" + beatmap.getBPM(), true);
		embed.addField("Genero:", "" + beatmap.getGenre().getDisplayName(), true);

		embed.addField("Modo de Jogo", beatmap.getGameMode().getDisplayName(), true);
		embed.addField("Estrelas:", beatmap.getStarsEmoji(), true);
		embed.addField("Dificuldade:", beatmap.getVersion(), true);

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy - HH:mm");

		embed.addField("Aprovado em", format.format(beatmap.getApprovedDate()) + "\n" + beatmap.getApprovated().name(),
				true);
		embed.addField("Taxa de Sucesso:", beatmap.getSuccessRate(), true);
		embed.addField("Combo Maximo", beatmap.getMaxCombo() + "", true);

		embed.setImage(beatmap.getBeatmapCoverUrl());
		User user = OusuBot.getOusu().getJda().getUserById("247096601242238991");
		embed.setFooter(user.getName() + "#" + user.getDiscriminator() + " | Ou!su bot ™", user.getAvatarUrl());

		try {
			idb = beatmap.getBeatmapPreview();
		} catch (IOException e) {
			e.printStackTrace();
			return embed;
		}
		return embed;
	}

}
