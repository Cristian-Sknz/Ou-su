package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import me.skiincraft.api.ousu.entity.objects.Gamemode;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.common.reactions.ReactionObject;
import me.skiincraft.discord.core.common.reactions.Reactions;
import me.skiincraft.discord.core.common.reactions.custom.ReactionPage;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.ousu.common.OusuCommand;
import me.skiincraft.discord.ousu.crawler.WebCrawler;
import me.skiincraft.discord.ousu.emojis.GenericsEmotes;
import me.skiincraft.discord.ousu.messages.TypeEmbed;
import me.skiincraft.discord.ousu.osu.OsuSkin;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

public class SkinsCommand extends OusuCommand {

	public SkinsCommand() {
		super("skins", Collections.singletonList("skin"), "skins");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Gameplay;
	}

	public void execute(Member user, String[] args, InteractChannel channel) {
		channel.reply(TypeEmbed.LoadingEmbed().build(), message ->{
			try {
				List<OsuSkin> skins = WebCrawler.getHomePageSkins();
				List<EmbedBuilder> embeds = new ArrayList<>();
				int first = 0; 
				for (OsuSkin skin : skins) {
					EmbedBuilder sEmbed = embed(getLanguageManager(channel.getTextChannel().getGuild()), skin);
					if (first == 0) {
						first = 1;
						message.editMessage(sEmbed.build()).queue();
					}
					embeds.add(sEmbed);
				}

				Objects.requireNonNull(Reactions.getInstance()).registerReaction(new ReactionObject(message, user.getIdLong(),
						new String[]{"U+25C0", "U+25B6"}), new ReactionPage(embeds, true));
			} catch (Exception e){
				message.delete().queue();
				channel.reply(TypeEmbed.errorMessage(e, channel.getTextChannel()).build());
			}
		});
		
	}
	
	public EmbedBuilder embed(LanguageManager lang, OsuSkin osu) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(osu.getSkinName());
		embed.setImage(osu.getSkinImage());
		embed.addField("Skin", osu.getSkinName(), true);
		embed.addField(lang.getString("Titles", "CREATOR"), osu.getCreator(), true);
		embed.addField("Download", GenericsEmotes.getEmoteAsMention("download") + "[__Here__](" + osu.getDownloadURL() + ")",
				true);
		StringBuilder gamemodes = new StringBuilder();
		for (Gamemode mode : osu.getGamemodes()) {
			gamemodes.append(Objects.requireNonNull(GenericsEmotes.getEmote(mode.name().toLowerCase())).getAsMention()).append(" ").append(mode.name()).append("\n");
		}
		embed.addField("Gamemodes", gamemodes.toString(), true);
		embed.setDescription(":eye: " + osu.getStatistics().getViewes() + " "
				+ GenericsEmotes.getEmoteAsMention("download") + " " + osu.getStatistics().getDownloads() + " "
				+ ":cloud: " + osu.getStatistics().getComments());

		embed.setColor(Color.ORANGE);
		embed.setThumbnail("https://i.imgur.com/bz1MKtv.jpg");
		embed.setFooter("Skins by osuskins.net", "https://osuskins.net/favicon-32x32.png");

		return embed;
	}

}
