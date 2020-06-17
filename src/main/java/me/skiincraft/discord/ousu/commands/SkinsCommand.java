package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import me.skiincraft.api.ousu.modifiers.Gamemode;
import me.skiincraft.discord.ousu.abstractcore.CommandCategory;
import me.skiincraft.discord.ousu.abstractcore.Commands;
import me.skiincraft.discord.ousu.customemoji.OusuEmojis;
import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.events.DefaultReaction;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.osuskins.OsuSkin;
import me.skiincraft.discord.ousu.search.JSoupGetters;
import me.skiincraft.discord.ousu.utils.Emoji;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class SkinsCommand extends Commands {

	public SkinsCommand() {
		super("ou!", "skins", "ou!skins", null);
	}

	@Override
	public String[] helpMessage(LanguageManager langm) {
		return langm.translatedArrayHelp("OSU_HELPMESSAGE_SKINS");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Osu;
	}

	@Override
	public void action(String[] args, String label, TextChannel channel) {
		replyQueue(TypeEmbed.LoadingEmbed().build(), message -> {
			try {
				List<OsuSkin> skins = JSoupGetters.pageskins();
				List<EmbedBuilder> embeds = new ArrayList<EmbedBuilder>();
				
				int i = 0;
				for (OsuSkin osu : skins) {
					EmbedBuilder embed = embed(osu);
					embeds.add(embed);
					if (i == 0) {
						message.editMessage(embed.build()).queue();
					}
					i++;
				}
				EmbedBuilder[] embed = new EmbedBuilder[embeds.size()];
				embeds.toArray(embed);
				message.editMessage(embeds.get(0).build()).queue();
				message.addReaction("U+25C0").queue();
				message.addReaction("U+25B6").queue();
				ReactionMessage.skinsReaction.add(new DefaultReaction(getUserId(), message.getId(), embed, 0));

			} catch (IOException e) {
				message.delete().queue();
			}
		});

	}

	public EmbedBuilder embed(OsuSkin osu) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(osu.getSkinname());
		embed.setImage(osu.getSkinimage());
		embed.addField("Skin", osu.getSkinname(), true);
		embed.addField(getLang().translatedEmbeds("CREATOR"), osu.getCreator(), true);
		embed.addField("Download", OusuEmojis.getEmoteAsMention("download") + "[__Here__](" + osu.getDownloadurl() + ")",
				true);
		StringBuffer gamemodes = new StringBuffer();
		for (Gamemode mode : osu.getGamemodes()) {
			gamemodes.append(
					OusuEmojis.getEmote(mode.name().toLowerCase()).getAsMention() + " " + mode.getDisplayName() + "\n");
		}
		embed.addField("Gamemodes", gamemodes.toString(), true);
		embed.setDescription(Emoji.EYE.getAsMention() + " " + osu.getStatistics().getViewes() + " "
				+ OusuEmojis.getEmoteAsMention("download") + " " + osu.getStatistics().getDownloads() + " "
				+ Emoji.CLOUD.getAsMention() + " " + osu.getStatistics().getComments());

		embed.setColor(Color.ORANGE);
		embed.setThumbnail("https://i.imgur.com/bz1MKtv.jpg");
		embed.setFooter("Skins by osuskins.net", "https://osuskins.net/favicon-32x32.png");

		return embed;
	}

}
