package me.skiincraft.discord.ousu.commands.owner;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.skiincraft.discord.core.reactions.ReactionObject;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.htmlpage.CharacterGetter;
import me.skiincraft.discord.ousu.messages.TypeEmbed;
import me.skiincraft.discord.ousu.object.LoadPersonagens;
import me.skiincraft.discord.ousu.object.Personagem;
import me.skiincraft.discord.ousu.reactions.HistoryLists;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class CharacterCommand extends Comando {

	public CharacterCommand() {
		super("character", Arrays.asList("personagem", "c", "char"), "character <name>");
	}
	
	public boolean containsPermission(long id){
		if (Long.parseLong("503381807358672896") == id){
			return true;
		}

		return Long.parseLong("247096601242238991") == id;
	}

	public void execute(User user, String[] args, TextChannel channel) {
		if (!containsPermission(user.getIdLong())) {
			reply("Você não tem permissão para isso.");
			return;
		}
		
		if (args.length == 0) {
			reply("Escreva o nome da personagem!");
			return;
		}
		
		reply(TypeEmbed.LoadingEmbed().build(), message -> {
			List<Personagem> personagens = CharacterGetter.searchCharacter(String.join(" ", args));
			if (personagens.size() == 0) {
				reply("Não foi encontrado nenhum personagem com esse nome!");
				return;
			}
			
			List<EmbedBuilder> embeds = new ArrayList<>();
			for (Personagem previewer : personagens) {
				embeds.add(embed(previewer, personagens.size() != 1));
			}
			
			message.editMessage(embeds.get(0).build()).queue();
			if (personagens.size() == 1) {	
				LoadPersonagens.savePersonagem(personagens.get(0));
				return;
			}
			
			message.addReaction("U+2B05").queue();
			message.addReaction("U+2705").queue();
			message.addReaction("U+27A1").queue();
			
			HistoryLists.addToReaction(user, message, new ReactionObject(embeds, 0), new ReactionObject(personagens, 0));
		});
	}
	
	
	public EmbedBuilder embed(Personagem previewer, boolean containsmore) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setAuthor((containsmore)? previewer.getName() : "Personagem Adicionado");
		embed.setImage(previewer.getImage());
		embed.setDescription((containsmore)? "Encontrei varios personagem com este nome\n Confira: ": "`"+ previewer.getName() + "` foi adicionado ao banco de dados.");
		embed.setColor(Color.ORANGE);
		embed.setFooter("OusuBot - Database");
		return embed;
	}

	public CommandCategory getCategory() {
		return CommandCategory.Owner;
	}
	
	

}
