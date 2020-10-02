package me.skiincraft.discord.ousu.commands.owner;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import me.skiincraft.discord.core.reactions.ReactionObject;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.messages.TypeEmbed;
import me.skiincraft.discord.ousu.object.LoadPersonagens;
import me.skiincraft.discord.ousu.object.Personagem;
import me.skiincraft.discord.ousu.permission.IPermission.InternalPermission;
import me.skiincraft.discord.ousu.reactions.HistoryLists;
import me.skiincraft.discord.ousu.utils.OusuUtils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class CharactersCommand extends Comando {

	public CharactersCommand() {
		super("characters", Arrays.asList("cs", "chars"), "characters <name>");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Owner;
	}
	
	private List<EmbedBuilder> lastrequest;
	
	private List<Personagem> getPersonagemByName(String name) {
		Stream<Personagem> s = LoadPersonagens.getPersonagems().stream();
		return s.filter(per -> per.getName().equalsIgnoreCase(name)).collect(Collectors.toList());
	}
	
	private boolean eq(String str, String str2) {
		return str.equalsIgnoreCase(str2);
	}

	public void execute(User user, String[] args, TextChannel channel) {
		if (args.length == 0) {
			if (!hasIPermission(user, InternalPermission.VIEW)) {
				reply("Você não tem permissão para ver todos os personagens.");
				return;
			}
			
			List<Personagem> personagens = LoadPersonagens.getPersonagems();
			reply(TypeEmbed.LoadingEmbed().build(), message ->{
				if (lastrequest == null || lastrequest.size() != personagens.size()) {
					List<EmbedBuilder> embeds = new ArrayList<>();
					for (Personagem p : personagens) {
						embeds.add(embed(p));
					}
					
					if (lastrequest == null) {
						lastrequest = embeds;
					}
				}
				message.editMessage(lastrequest.get(0).build()).queue();
				message.addReaction("U+25C0").queue();
				message.addReaction("U+25B6").queue();

				HistoryLists.addToReaction(user, message, new ReactionObject(lastrequest, 0));
			});
			return;
		}
		if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("refresh")) {
				LoadPersonagens.refreshPersonagens();
				reply("Todos os personagens foram reescritos.",m -> m.addReaction("U+2705").queue());
				return;
			}

			if (!existAlias(args)) {
				System.out.println(String.join(" ", args));
				List<Personagem> perso = getPersonagemByName(String.join(" ", args));
				if (perso.size() == 0) {
					reply("**"+ user.getName() +"**, Não foi encontrado esse personagem no banco de dados.");
					return;
				}

				List<EmbedBuilder> embeds = new ArrayList<>();
				if (perso.size() > 1) {
					for (Personagem per : perso) {
						embeds.add(embed(per));
					}
					reply(embeds.get(0).build(), message -> {
						message.addReaction("U+25C0").queue();
						message.addReaction("U+25B6").queue();

						HistoryLists.addToReaction(user, message, new ReactionObject(embeds, 0));
					});
					return;
				}

				reply(embed(perso.get(0)).build());
				return;
			}
			if (!hasIPermission(user, InternalPermission.WRITE_AND_VIEW)) {
				reply("Você não tem permissão para rescrever personagens.");
				return;
			}

			if (args.length == 1) {
				sendUsages(args[0]);
				return;
			}

			if (args.length >= 2) {
				if (!existAlias(args)) {
					sendUsages(args);
					return;
				}
				List<String> strs = new ArrayList<>(Arrays.asList(args));
				strs.remove(0);
				String[] separado = OusuUtils.splitString(String.join(" ", strs), "$");
				if (separado.length != 2) {
					if (eq(args[0], "remove")) {
						List<Personagem> perso = getPersonagemByName(separado[0]);
						if (perso.size() == 0) {
							sendUsages(args[0]);
							return;
						}
						reply(changed(perso.get(0), "0", "0").build(), m-> m.addReaction("U+2705").queue());
						LoadPersonagens.removePersonagem(perso.get(0));
						return;
					}
					System.out.println(separado[0]);
					sendUsages(args);
					return;
				}
				System.out.println(separado[0]);

				List<Personagem> perso = getPersonagemByName(separado[0]);
				if (perso.size() == 0) {
					reply("Não foi possivel encontrar o personagem solicitado.");
					return;
				}
				System.out.println(perso);
				Personagem personagem = perso.get(0);
				if (eq(args[0], "editname")) {
					if (separado[1].length() >= 40) {
						reply("> O numero de caracteres permitido é até 40");
						return;
					}
					if (OusuUtils.containsSpecialCharacters(separado[1])) {
						System.out.println(separado[1]);
						reply("> Você não pode alterar o nome com caracteres especiais.");
						return;
					}

					reply(changed(personagem, separado[1], "0").build(), m-> {
						m.addReaction("U+2705").queue();
					});
					LoadPersonagens.editPersonagem(personagem, separado[1]);
					return;
				}

				if (eq(args[0], "img")) {
					if (OusuUtils.isImage(separado[1].split(" ")[0])) {
						reply("**"+ user.getName()+ "**, esse link que você enviou não é uma imagem valida.");
						return;
					}

					reply(changed(personagem, "0", separado[1]).build(), m-> m.addReaction("U+2705").queue());
					LoadPersonagens.editImagePersonagem(personagem, separado[1]);
					return;
				}
			}
		}
			return;
	}
	
	public boolean existAlias(String...args) {
		if (args[0].equalsIgnoreCase("img")) {
			return true;
		}
		if (args[0].equalsIgnoreCase("editname")) {
			return true;
		}
		if (args[0].equalsIgnoreCase("remove")) {
			return true;
		} else {
			return false;
		}
	}
	
	public void sendUsages(String... args) {
		if (args[0].equalsIgnoreCase("img")) {
			reply("> Tente digitar: img <name>,<image>\n`<name>,<image> juntos.`");
			return;
		}
		if (args[0].equalsIgnoreCase("editname")) {
			reply("> Tente digitar: editname <name>,<newname>\n`<name>,<newname> juntos.`");
			return;
		}
		if (args[0].equalsIgnoreCase("remove")) {
			reply("> Tente digitar: remove <name>`");
		} else {
			reply("Os comandos possiveis são: editname, img e remove.");
		}
	}
	
	public EmbedBuilder changed(Personagem personagem, String name, String image) {
		EmbedBuilder embed = new EmbedBuilder();
		if (!name.equalsIgnoreCase("0")) {
			embed.setAuthor("Personagem atualizado.");
			embed.setDescription("`" + personagem.getName() + "` teve seu nome atualizado.\n `" + name + "` agora é seu novo nome.");
		}
		
		if (!image.equalsIgnoreCase("0")) {
			embed.setAuthor("Personagem atualizado.");
			embed.setDescription("`" + personagem.getName() + "` teve sua imagem atualizada.");
		}
		
		if (name.equalsIgnoreCase("0") && image.equalsIgnoreCase("0")) {
			embed.setAuthor("Personagem removido.");
			embed.setDescription("`" + personagem.getName() + "` foi removido do banco de dados.");
		}
		
		embed.setColor(Color.ORANGE);
		embed.setImage(personagem.getImage());
		embed.setFooter("OusuBot - Database");
		return embed;
	}
	
	public EmbedBuilder embed(Personagem personagem) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setAuthor(personagem.getName());
		embed.setColor(Color.ORANGE);
		embed.setDescription("Hair: " + personagem.getHaircolor()+ "\n");
		embed.appendDescription("Gender: " + personagem.getGender().getName());
		embed.setImage(personagem.getImage());
		embed.setFooter("OusuBot - Database");
		return embed;
	}
	

}
