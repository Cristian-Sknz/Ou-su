package me.skiincraft.discord.ousu.reactions;

import java.awt.Color;
import java.util.List;

import me.skiincraft.discord.core.reactions.Reaction;
import me.skiincraft.discord.core.reactions.ReactionUtil;
import me.skiincraft.discord.ousu.object.LoadPersonagens;
import me.skiincraft.discord.ousu.object.Personagem;

import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class CharReaction extends Reaction {

	public CharReaction() {
		super("CharReaction");
	}

	public List<ReactionUtil> listHistory() {
		return HistoryLists.reationsList;
	}

	@SuppressWarnings("unchecked")
	public void execute(User user, TextChannel channel, ReactionEmote reactionEmote) {
		if (user.getIdLong() != getUtils().getUserId()) {
			return;
		}
		if (!reactionEmote.isEmoji()) {
			return;
		}
		
		if (reactionEmote.getEmoji().equalsIgnoreCase("⬅")) {
			getContext().changeEmbedBack(getUtils().getReactionObjects()[0]);
		}
		if (reactionEmote.getEmoji().equalsIgnoreCase("✅")) {
			Personagem personagem = ((List<Personagem>) getUtils().getReactionObjects()[1].getObject()).get(getUtils().getReactionObjects()[0].getOrdem());
			channel.editMessageById(getUtils().getMessageId(), embed(personagem).build()).queue();
			listHistory().remove(getUtils());
			
			LoadPersonagens.savePersonagem(personagem);
		}
		if (reactionEmote.getEmoji().equalsIgnoreCase("➡")) {
			getContext().changeEmbedNext(getUtils().getReactionObjects()[0]);
		}
	}
	
	
	
	public EmbedBuilder embed(Personagem previewer) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setAuthor("Personagem adicionado");
		embed.setImage(previewer.getImage());
		embed.setDescription("`" + previewer.getName() + "` foi adicionada no banco de dados.");
		embed.setColor(Color.ORANGE);
		embed.setFooter("OusuBot - Database");
		return embed;
	}
	

}
