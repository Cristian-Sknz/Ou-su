package me.skiincraft.discord.ousu.commands.reactions;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import me.skiincraft.api.ousu.beatmaps.Beatmap;
import me.skiincraft.api.ousu.exceptions.InvalidBeatmapException;
import me.skiincraft.api.ousu.exceptions.InvalidUserException;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.commands.PlayersCommand;
import me.skiincraft.discord.ousu.commands.UserCommand;
import me.skiincraft.discord.ousu.embeds.SearchEmbed;
import me.skiincraft.discord.ousu.events.TopUserReaction;
import me.skiincraft.discord.ousu.exception.SearchNotFoundException;
import me.skiincraft.discord.ousu.manager.ReactionUtils;
import me.skiincraft.discord.ousu.manager.ReactionsManager;
import me.skiincraft.discord.ousu.richpresence.Rich;
import me.skiincraft.discord.ousu.search.SearchBearmap;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class PlayerReactionEvent extends ReactionsManager {

	@Override
	public List<ReactionUtils> listHistory() {
		return ReactionMessage.playersHistory;
	}

	@Override
	public void action(User user, TextChannel channel, String emoji) {

		if (emoji.equalsIgnoreCase("◀")) {
			listHistory().remove(getUtils());

			Object obj = getUtils().getObject();
			Rich[] score = (Rich[]) obj;
			int v = getUtils().getValue();
			if (v <= 0) {
				v = 0;
				listHistory().add(new TopUserReaction(user, getEvent().getMessageId(), obj, v));
				return;
			} else {
				v = getUtils().getValue() - 1;
			}

			EmbedBuilder embed = PlayersCommand.richformat(Arrays.asList(score), v, channel.getGuild());

			channel.editMessageById(getEvent().getMessageId(), embed.build()).queue();
			listHistory().add(new TopUserReaction(user, getEvent().getMessageId(), obj, v));

		}
		
		if (emoji.equalsIgnoreCase("🔍")) {
			
			SearchBearmap searchBearmap;
			List<Beatmap> beat;
			
			Object obj = getUtils().getObject();
			Rich[] score = (Rich[]) obj;
			int v = getUtils().getValue();
			
			EmbedBuilder embed = PlayersCommand.richformat(Arrays.asList(score), v, channel.getGuild());
			
			boolean contains = false;
			String value = "";
			for (Field field : embed.getFields()) {
				if (field.getName().contains("Beatmap")) {
					contains = true;
					value = String.join("-", field.getValue());
					System.out.println(value);
				}
			}
			
			if (contains == false) {
				return;
			}
			
			try {
				searchBearmap = new SearchBearmap(value);
				beat = OusuBot.getOsu().getBeatmapSet(searchBearmap.getBeatmapSetIDs2().get(0));
				getEvent().getChannel().removeReactionById(getUtils().getMessageID(), emoji, OusuBot.getSelfUser()).queue();
			} catch (SearchNotFoundException | InvalidBeatmapException e) {
				return;
			}
			
			sendEmbedMessage(SearchEmbed.beatmapEmbed(beat, channel.getGuild())).queue(new Consumer<Message>() {

				@Override
				public void accept(Message message) {
					channel.sendFile(SearchEmbed.getAudioPreview(), beat.get(0).getTitle() + ".mp3").queue();
					Beatmap[] bm = new Beatmap[beat.size()];
					beat.toArray(bm);

					ReactionMessage.searchReactions.add(new TopUserReaction(user, message.getId(), bm, 0));
					message.addReaction("U+1F3AF").queue();
					
				}
			});

			return;
		}
		
		if (emoji.equalsIgnoreCase("💫")) {
			Object obj = getUtils().getObject();
			Rich[] score = (Rich[]) obj;
			Rich r = score[getUtils().getValue()];

			String[] st = r.getRich().getLargeImage().getText().split(" ");
			String nickname = st[0];
			if (nickname.equalsIgnoreCase("guest")) {
				return;
			}
			
			

			listHistory().remove(getUtils());
			try {
				EmbedBuilder embed = UserCommand.embed(OusuBot.getOsu().getUser(nickname), channel.getGuild());

				try {
					channel.clearReactionsById(getUtils().getMessageID()).queue();
				} catch (InsufficientPermissionException e) {
					//
				}

				channel.editMessageById(getUtils().getMessageID(), embed.build()).queue();
			} catch (InvalidUserException e) {
				listHistory().add(new TopUserReaction(user, getEvent().getMessageId(), obj, getUtils().getValue()));
				return;
			}
		}

		if (emoji.equalsIgnoreCase("▶")) {
			listHistory().remove(getUtils());
			int v = getUtils().getValue();
			v += 1;

			Object obj = getUtils().getObject();
			Rich[] score = (Rich[]) obj;

			if (v >= score.length) {
				listHistory().add(new TopUserReaction(user, getEvent().getMessageId(), obj, score.length - 1));
				return;
			}
			EmbedBuilder embed = PlayersCommand.richformat(Arrays.asList(score), v, channel.getGuild());
			
			channel.editMessageById(getEvent().getMessageId(), embed.build()).queue();
			listHistory().add(new TopUserReaction(user, getEvent().getMessageId(), obj, v));
		}
	}

}
