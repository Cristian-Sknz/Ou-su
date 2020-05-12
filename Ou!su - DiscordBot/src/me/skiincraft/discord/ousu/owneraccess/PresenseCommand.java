package me.skiincraft.discord.ousu.owneraccess;

import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.managers.Presence;

public class PresenseCommand extends Commands {

	public PresenseCommand() {
		super("ou!", "presense", "presense <0/2>", null);
	}

	@Override
	public String[] helpMessage(LanguageManager langm) {
		return null;
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Owner;
	}

	@Override
	public void action(String[] args, String label, User user, TextChannel channel) {
		if (!isOwner()) {
			return;
		}
		
		if (args.length == 0) {
			sendUsage().queue();
			return;
		}
		
		Presence presense = channel.getJDA().getPresence();
		String mention = "<@247096601242238991>";
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("0")) {
				presense.setActivity(Activity
						.watching(channel.getJDA()
						.getGuilds().size() + " Servidores."));
				sendMessage(mention + " Presence alterada com sucesso.").queue();
				return;
			}
			if (args[0].equalsIgnoreCase("1")) {
				presense.setActivity(Activity
						.watching(channel.getJDA()
						.getUsers().size() + " Usuarios Online."));
				sendMessage(mention + " Presence alterada com sucesso.").queue();
				return;
			}
			if (args[0].equalsIgnoreCase("2")) {
				presense.setActivity(Activity
						.listening("Type ou!help for help."));
				sendMessage(mention + " Presence alterada com sucesso.").queue();
				return;
			}
			presense.setActivity(Activity
					.playing(args[0]));
			sendMessage(mention + " Presence alterada com sucesso.").queue();
			return;
		}
		
		if (args.length >= 2) {
			presense.setActivity(Activity.playing(StringUtils.arrayToString(0, args)));
			return;
		}
	}

}
