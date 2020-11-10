package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.util.Arrays;

import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

public class InviteCommand extends Comando{

	public InviteCommand() {
		super("invite", Arrays.asList("convite", "convide", "convidar"), "invite");
	}

	public CommandCategory getCategory() {
		return CommandCategory.About;
	}

	public void execute(Member user, String[] args, InteractChannel channel) {
		channel.reply(embed(channel.getTextChannel().getGuild()));
	}
	
	public MessageEmbed embed(Guild guild) {
		EmbedBuilder embed = new EmbedBuilder();

		SelfUser self = (SelfUser) guild.getSelfMember().getUser();
		String[] links = new String[] {"https://discordapp.com/oauth2/authorize?client_id=701825726449582192&scope=bot&permissions=1678108752",
				"https://discord.gg/VtkYdBR"};
		
		StringBuilder builder = new StringBuilder();
		LanguageManager lang = getLanguageManager(guild);
		embed.setTitle("Convide-me!");
		embed.setAuthor(self.getName() + "#" + self.getDiscriminator(), links[0], self.getAvatarUrl());
		embed.setColor(new Color(252, 171, 151));
		builder.append(lang.getString("Messages", "INVITE_COMMAND_MESSAGE").replace("{l}", "\n"));
		
		embed.setDescription(builder.toString().replace("{InviteUrl}", links[0]).replace("{BotDiscordUrl}", links[1]));

		embed.setThumbnail("https://i.imgur.com/f7Qc9nA.png");
		embed.setFooter(lang.getString("Default", "FOOTER_DEFAULT"));
		return embed.build();
	}

}
