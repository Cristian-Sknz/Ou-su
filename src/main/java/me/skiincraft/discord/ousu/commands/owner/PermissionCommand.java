package me.skiincraft.discord.ousu.commands.owner;

import java.util.Collections;

import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.permission.IPermission;
import me.skiincraft.discord.ousu.permission.IPermission.InternalPermission;

import net.dv8tion.jda.api.entities.Member;

public class PermissionCommand extends Comando{

	public PermissionCommand() {
		super("permission", Collections.singletonList("perm"), "permission <add/remove> [code]");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Owner;
	}

	public void execute(Member user, String[] args, InteractChannel channel) {
		if (!hasIPermission(user.getUser(), InternalPermission.ALL)) {
			channel.reply("Você não tem permissão para utilizar este comando.");
			return;
		}
		if (args.length <= 2) {
			replyUsage(channel.getTextChannel());
			return;
		}

		if (args[0].equalsIgnoreCase("add")) {
			String id = args[1].replaceAll("\\D+", "");
			if (id.length() != 18) {
				channel.reply("> Este UserID que você digitou está incorreto!");
				return;
			}
			Member member = channel.getTextChannel().getGuild().getMemberById(id);
			if (member == null) {
				channel.reply("Este usuario solicitado não esta compartilhando o mesmo servidor.");
				return;
			}
			if (!args[2].matches("-?\\d+(\\.\\d+)?")) {
				replyUsage(channel.getTextChannel());
				return;
			}
			new IPermission(member.getUser()).set("permission", args[2]);
			channel.reply("Foi adionado as permissões ao usuario solicitado.");
		}

		if (args[0].equalsIgnoreCase("remove")) {
			String id = args[1].replaceAll("\\D+", "");
			if (id.length() != 18) {
				channel.reply("> Este UserID que você digitou está incorreto!");
				return;
			}
			Member member = channel.getTextChannel().getGuild().getMemberById(id);
			if (member == null) {
				channel.reply("Este usuario solicitado não esta compartilhando o mesmo servidor.");
				return;
			}
			IPermission perm = new IPermission(member.getUser());
			if (perm.exists()) {
				perm.delete();
				channel.reply("Foi adicionado as permissões ao usuario solicitado.");
			} else {
				channel.reply("Este usuario solicitado não tem nenhuma permissão.");
			}
		}

	}
}
