package me.skiincraft.discord.ousu.commands.fun;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import me.skiincraft.discord.core.utils.ImageUtils;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.object.LoadPersonagens;
import me.skiincraft.discord.ousu.object.Participante;
import me.skiincraft.discord.ousu.object.Personagem;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class RollCommand extends Comando {

	public static List<Participante> participando = new ArrayList<>();
	public static HashMap<String, List<Personagem>> lasts = new HashMap<>();
	
	public static synchronized boolean contains(long id) {
		return participando.stream().filter(p -> p.getId() == id).findAny().orElse(null) != null;
	}
	
	public static synchronized Participante get(long id) {
		return participando.stream().filter(p -> p.getId() == id).findAny().orElse(null);
	}
	
	private static Personagem lastPersonagem;
	
	public RollCommand() {
		super("roll", Arrays.asList("rolar"), "roll");
	}

	public void execute(User user, String[] args, TextChannel channel) {
		if (contains(user.getIdLong())) {
			reply("Você está participando de uma rodada!");
			return;
		}
		
		List<Personagem> personagens = LoadPersonagens.getPersonagems();
		Personagem random = personagens.get(new Random().nextInt(personagens.size()));
		if (lasts.containsKey(user.getId())) {
			if (lasts.get(user.getId()) != null) {
				if (lasts.get(user.getId()).size() == personagens.size()) {
					lasts.get(user.getId()).clear();
				}
			}
		}
		
		if (lastPersonagem != null) {
			while (containsLast(user.getId(), random) && lastPersonagem == random) {
				random = personagens.get(new Random().nextInt(personagens.size()));
			}	
		}
		Participante p = new Participante(user.getName(), user.getIdLong(), random, channel.getGuild().getIdLong(), channel.getIdLong());
		participando.add(p);
		addLasts(p);
		reply(makeEmbed(p).build());
	}
	
	public EmbedBuilder makeEmbed(Participante participante) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setAuthor("Que personagem é esse?");
		embed.setDescription("Você saberia me dizer que personagem é esse?\n" + "Tem 3 tentativas! Caso não saiba digite `quit`");
		embed.setImage(participante.getPersonagem().getImage());
		embed.setFooter("Solicitado por "+ participante.getName());
		
		try {
			embed.setColor(ImageUtils.getPredominatColor(ImageIO.read(new URL(participante.getPersonagem().getImage()))));
		} catch (IOException e) {
			embed.setColor(Color.YELLOW);
		}
		return embed;
	}
	
	public void addLasts(Participante participante) {
		if (lasts.containsKey(String.valueOf(participante.getId()))) {
			lasts.get(String.valueOf(participante.getId())).add(participante.getPersonagem());
			return;
		}
		List<Personagem> personagem = new ArrayList<>();
		personagem.add(participante.getPersonagem());
		lasts.put(participante.getId()+"", personagem);
	}
	public boolean containsLast(String id, Personagem personagem) {
		if (!lasts.containsKey(id)) {
			return false;
		}
		return lasts.get(id).contains(personagem);
	}

	public CommandCategory getCategory() {
		return CommandCategory.Owner;
	}

}
