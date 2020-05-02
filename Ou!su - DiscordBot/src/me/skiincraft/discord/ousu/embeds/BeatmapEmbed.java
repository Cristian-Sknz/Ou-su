package me.skiincraft.discord.ousu.embeds;

import java.io.InputStream;

import com.oopsjpeg.osu4j.exception.OsuAPIException;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.osu.BeatmapOsu;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class BeatmapEmbed {
	
	public static InputStream idb;
	
	public synchronized static EmbedBuilder beatmapEmbed(BeatmapOsu beatmap) {
		EmbedBuilder embed = new EmbedBuilder();

		embed.setTitle(beatmap.getTitulo());
		int id = 0;
		try {
			id = beatmap.getCriador().get().getID();
		} catch (OsuAPIException e) {
			e.printStackTrace();
		}
		embed.setAuthor(beatmap.getCriadorName(), "https://osu.ppy.sh/users/" + id, "https://a.ppy.sh/" + id);
		embed.addField("Artista:", beatmap.getArtista(), true);
		embed.addField("BPM:", "" + beatmap.getBpm(), true);
		embed.addField("Genero:", "" + beatmap.getGenero().getName(), true);

		embed.addField("Modo de Jogo",
				"[" + beatmap.getGamemode().getName() + "](osu://dl/" + beatmap.getBeatmap().getBeatmapSetID() + ")",
				true);
		embed.addField("Estrelas:", beatmap.getMapStars(), true);
		embed.addField("Dificuldade:", beatmap.getDificuldade(), true);

		embed.addField("Aprovado em", beatmap.getData() + "\n" + beatmap.getAprovação(), true);
		embed.addField("Taxa de Sucesso:", beatmap.getSucessfull(), true);
		embed.addField("Combo Maximo", beatmap.getCombo() + "", true);

		embed.setImage(beatmap.getBeatmapCover());
		User user = OusuBot.getOusu().getJda().getUserById("247096601242238991");
		embed.setFooter(user.getName() + "#" + user.getDiscriminator() + " | Ou!su bot ™", user.getAvatarUrl());
		idb = beatmap.getBeatmapPreview();
		return embed;
	}
	
}
