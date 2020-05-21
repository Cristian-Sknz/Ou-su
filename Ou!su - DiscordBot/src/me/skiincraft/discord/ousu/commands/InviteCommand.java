package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.util.Arrays;

import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class InviteCommand extends Commands {

	public InviteCommand() {
		super("ou!", "invite", "ou!invite", Arrays.asList("convidar"));
	}

	private String invitelink = "https://discordapp.com/oauth2/authorize?client_id=701825726449582192&scope=bot&permissions=1678108752";
	private String serverlink = "https://discord.gg/VtkYdBR";

	@Override
	public String[] helpMessage(LanguageManager lang) {
		return lang.translatedArrayHelp("HELPMESSAGE_INVITE");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Sobre;
	}

	@Override
	public void action(String[] args, String label, User user, TextChannel channel) {
		channel.sendMessage(embed(channel.getGuild()).build()).queue();

	}

	public EmbedBuilder embed(Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();

		SelfUser self = OusuBot.getJda().getSelfUser();

		embed.setAuthor(self.getName() + "#" + self.getDiscriminator(), invitelink, self.getAvatarUrl());
		embed.setColor(Color.BLUE);

		embed.setTitle("Convide-me!");
		StringBuffer buffer = new StringBuffer();

		buffer.append(getLang().translatedMessages("INVITE_COMMAND_MESSAGE"));

		embed.setDescription(
				buffer.toString().replace("{InviteUrl}", invitelink).replace("{BotDiscordUrl}", serverlink));

		embed.setThumbnail("https://i.imgur.com/f7Qc9nA.png");
		embed.setFooter(getLang().translatedBot("FOOTER_DEFAULT"));
		return embed;
	}
}
