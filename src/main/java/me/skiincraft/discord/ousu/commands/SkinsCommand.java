package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.discord.core.reactions.ReactionObject;
import me.skiincraft.discord.core.utils.Emoji;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.emojis.OusuEmote;
import me.skiincraft.discord.ousu.htmlpage.JSoupGetters;
import me.skiincraft.discord.ousu.messages.TypeEmbed;
import me.skiincraft.discord.ousu.object.OsuSkin;
import me.skiincraft.discord.ousu.reactions.HistoryLists;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class SkinsCommand extends Comando{

	public SkinsCommand() {
		super("skins", Collections.singletonList("skin"), "skins");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Osu;
	}

	public void execute(User arg0, String[] arg1, TextChannel arg2) {
		reply(TypeEmbed.LoadingEmbed().build(), message ->{
			try {
				List<OsuSkin> skins = JSoupGetters.pageskins();
				List<EmbedBuilder> embeds = new ArrayList<>();
				int first = 0; 
				for (OsuSkin skin : skins) {
					EmbedBuilder sEmbed = embed(skin);
					if (first == 0) {
						first = 1;
						message.editMessage(sEmbed.build()).queue();
					}
					embeds.add(sEmbed);
				}
				
				EmbedBuilder[] embed = new EmbedBuilder[embeds.size()];
				embeds.toArray(embed);
				message.editMessage(embeds.get(0).build()).queue();
				message.addReaction("U+25C0").queue();
				message.addReaction("U+25B6").queue();
				
				HistoryLists.addToReaction(arg0, message, new ReactionObject(embed, 0));
			} catch (Exception e) {
				message.delete().queue();
			}
		});
		
	}
	
	public EmbedBuilder embed(OsuSkin osu) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(osu.getSkinname());
		embed.setImage(osu.getSkinimage());
		embed.addField("Skin", osu.getSkinname(), true);
		embed.addField(getLanguageManager().getString("Titles", "CREATOR"), osu.getCreator(), true);
		embed.addField("Download", OusuEmote.getEmoteAsMention("download") + "[__Here__](" + osu.getDownloadurl() + ")",
				true);
		StringBuilder gamemodes = new StringBuilder();
		for (Gamemode mode : osu.getGamemodes()) {
			gamemodes.append(OusuEmote.getEmote(mode.name().toLowerCase()).getAsMention()).append(" ").append(mode.name()).append("\n");
		}
		embed.addField("Gamemodes", gamemodes.toString(), true);
		embed.setDescription(Emoji.EYE.getAsMention() + " " + osu.getStatistics().getViewes() + " "
				+ OusuEmote.getEmoteAsMention("download") + " " + osu.getStatistics().getDownloads() + " "
				+ Emoji.CLOUD.getAsMention() + " " + osu.getStatistics().getComments());

		embed.setColor(Color.ORANGE);
		embed.setThumbnail("https://i.imgur.com/bz1MKtv.jpg");
		embed.setFooter("Skins by osuskins.net", "https://osuskins.net/favicon-32x32.png");

		return embed;
	}

}
