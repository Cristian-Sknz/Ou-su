package me.skiincraft.discord.ousu.commands;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.oopsjpeg.osu4j.GameMode;
import com.oopsjpeg.osu4j.exception.OsuAPIException;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.osu.BeatmapOsu;
import me.skiincraft.discord.ousu.utils.DefaultEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class BeatMapCommand extends Commands {

	public BeatMapCommand() {
		super("ou!", "beatmap", "ou!beatmap <id>", null);
	}

	@Override
	public String[] helpMessage() {
		return new String[] {"Este comando possibilita pegar informações de um beatmap",
				"basta colocar o ID do beatmap.",
				"BeatmapSets ainda não estão disponiveis"};
	}
	@Override
	public CommandCategory categoria() {
		return CommandCategory.Osu;
	}
	
	public static InputStream idb;
	
	@Override
	public void action(String[] args, User user, TextChannel channel) {
		if (args.length == 1) {
			sendUsage().queue();
			return;
		}
		
		if (args.length == 2) {
			BeatmapOsu osuBeat = null;
			try {
				osuBeat = new BeatmapOsu(Integer.valueOf(args[1]));
			} catch (MalformedURLException | OsuAPIException e) {
				e.printStackTrace();
				return;
			} catch (IndexOutOfBoundsException e) {
				sendEmbedMessage(new DefaultEmbed("Usuario inexistente", "Este usuario que você solicitou não existe."))
				.queue();
				return;
			} catch (NumberFormatException e) {
				sendEmbedMessage(new DefaultEmbed("Utilize numeros!", "IDs são formados de numeros, utilize numeros."))
				.queue();
				return;
			} catch (UnsupportedOperationException e ) {
				sendEmbedMessage(new DefaultEmbed("Recurso Indisponivel", "IDs do osu!taiko não estão funcionando."))
				.queue();
				return;
			}
			
			sendEmbedMessage(beatmapEmbed(osuBeat)).queue(new Consumer<Message>() {

				@Override
				public void accept(Message message) {
					message.getChannel().sendFile(idb, message.getEmbeds().get(0).getTitle() + ".mp3").queue();
				}
			});
			return;
		}
	}

	public static EmbedBuilder beatmapEmbed(BeatmapOsu beatmap) {
		EmbedBuilder embed = new EmbedBuilder();
		
		embed.setTitle(beatmap.getTitulo());
		int id = 0;
		try {
			id = beatmap.getCriador().get().getID();
		} catch (OsuAPIException e) {
			e.printStackTrace();
		}
		embed.setAuthor(beatmap.getCriadorName(), "https://osu.ppy.sh/users/" + id, "https://a.ppy.sh/" + id);
		embed.addField("Artista:", beatmap.getArtista() , true);
		embed.addField("BPM:", "" + beatmap.getBpm() , true);
		embed.addField("Genero:", "" + beatmap.getGenero().getName() , true);
		
		embed.addField("Modo de Jogo", beatmap.getGamemode().getName(), true);
		embed.addField("Estrelas:", beatmap.getMapStars(), true);
		embed.addField("Dificuldade:", beatmap.getDificuldade(), true);
		
		embed.addField("Aprovado em", beatmap.getData() + "\n" + beatmap.getAprovação(), true);
		embed.addField("Taxa de Sucesso:", beatmap.getSucessfull(), true);
		embed.addField("Combo Maximo", beatmap.getCombo()+"", true);
		
		
		embed.setImage(beatmap.getBeatmapCover());
		User user = OusuBot.getOusu().getJda().getUserById("247096601242238991");
		embed.setFooter(user.getName() + "#" + user.getDiscriminator() + " | Ou!su bot ™", user.getAvatarUrl());
		idb = beatmap.getBeatmapPreview();
		return embed;
	}
	
	public GameMode getGamemode(String gamemode) {
		String gm = gamemode.toLowerCase();
		Map<String, GameMode> map = new HashMap<>();
		
		map.put("standard", GameMode.STANDARD);
		map.put("catch", GameMode.CATCH_THE_BEAT);
		map.put("mania", GameMode.MANIA);
		map.put("taiko", GameMode.TAIKO);

		if (map.containsKey(gm)) {
			return map.get(gamemode);
		}
		
		return null;
	}




}
