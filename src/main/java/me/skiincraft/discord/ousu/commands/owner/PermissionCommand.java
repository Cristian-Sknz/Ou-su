package me.skiincraft.discord.ousu.commands.owner;

import java.util.Collections;

import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.permission.IPermission;
import me.skiincraft.discord.ousu.permission.IPermission.InternalPermission;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class PermissionCommand extends Comando{

	public PermissionCommand() {
		super("permission", Collections.singletonList("perm"), "permission <add/remove> [code]");
	}

	public CommandCategory getCategory() {
		return CommandCategory.Owner;
	}

	public void execute(User user, String[] args, TextChannel channel) {
		if (!hasIPermission(user, InternalPermission.ALL)) {
			reply("Você não tem permissão para utilizar este comando.");
			return;
		}
		if (args.length <= 2) {
			replyUsage();
			return;
		}

		if (args[0].equalsIgnoreCase("add")) {
			String id = args[1].replaceAll("\\D+", "");
			if (id.length() != 18) {
				reply("> Este UserID que você digitou está incorreto!");
				return;
			}
			Member member = channel.getGuild().getMemberById(id);
			if (member == null) {
				reply("Este usuario solicitado não esta compartilhando o mesmo servidor.");
				return;
			}
			if (!args[2].matches("-?\\d+(\\.\\d+)?")) {
				replyUsage();
				return;
			}
			new IPermission(member.getUser()).set("permission", args[2]);
			reply("Foi adionado as permissões ao usuario solicitado.");
		}

		if (args[0].equalsIgnoreCase("remove")) {
			String id = args[1].replaceAll("\\D+", "");
			if (id.length() != 18) {
				reply("> Este UserID que você digitou está incorreto!");
				return;
			}
			Member member = channel.getGuild().getMemberById(id);
			if (member == null) {
				reply("Este usuario solicitado não esta compartilhando o mesmo servidor.");
				return;
			}
			IPermission perm = new IPermission(member.getUser());
			if (perm.exists()) {
				perm.delete();
				reply("Foi adicionado as permissões ao usuario solicitado.");
			} else {
				reply("Este usuario solicitado não tem nenhuma permissão.");
			}
		}

	}
}
