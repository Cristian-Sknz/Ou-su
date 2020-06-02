package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.util.Arrays;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;

public class InviteCommand extends Commands {

	public InviteCommand() {
		super("ou!", "invite", "ou!invite", Arrays.asList("convidar"));
	}

	@Override
	public String[] helpMessage(LanguageManager lang) {
		return lang.translatedArrayHelp("HELPMESSAGE_INVITE");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Sobre;
	}

	@Override
	public void action(String[] args, String label, TextChannel channel) {
		channel.sendMessage(embed(channel.getGuild())).queue();

	}

	public MessageEmbed embed(Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();

		SelfUser self = OusuBot.getJda().getSelfUser();
		String[] links = new String[] {"https://discordapp.com/oauth2/authorize?client_id=701825726449582192&scope=bot&permissions=1678108752",
				"https://discord.gg/VtkYdBR"};
		
		StringBuffer buffer = new StringBuffer();
		embed.setTitle("Convide-me!");
		embed.setAuthor(self.getName() + "#" + self.getDiscriminator(), links[0], self.getAvatarUrl());
		embed.setColor(new Color(252, 171, 151));
		buffer.append(getLang().translatedMessages("INVITE_COMMAND_MESSAGE"));

		embed.setDescription(buffer.toString().replace("{InviteUrl}", links[0]).replace("{BotDiscordUrl}", links[1]));

		embed.setThumbnail("https://i.imgur.com/f7Qc9nA.png");
		embed.setFooter(getLang().translatedBot("FOOTER_DEFAULT"));
		return embed.build();
	}
}
