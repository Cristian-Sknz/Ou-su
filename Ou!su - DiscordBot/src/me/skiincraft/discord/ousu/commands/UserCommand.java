package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.oopsjpeg.osu4j.GameMode;
import com.oopsjpeg.osu4j.exception.OsuAPIException;

import me.skiincraft.discord.ousu.customemoji.OsuEmoji;
import me.skiincraft.discord.ousu.imagebuilders.OsuProfileNote;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.osu.UserOsu;
import me.skiincraft.discord.ousu.utils.DefaultEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class UserCommand extends Commands {

	public UserCommand() {
		super("ou!", "user", "ou!user <nickname> <gamemode>", Arrays.asList("osuplayer"));
	}
	
	@Override
	public String[] helpMessage() {
		return new String[] {};
	}
	
	@Override
	public CommandCategory categoria() {
		return CommandCategory.Osu;
	}
	
	@Override
	public void action(String[] args, User user, TextChannel channel) {
		if (args.length == 1) {
			sendUsage().queue();
			return;
		}
		
		if (args.length == 2) {
			
			UserOsu osuUser;
			try {
				osuUser = new UserOsu(args[1], GameMode.STANDARD);
			} catch (MalformedURLException | OsuAPIException e) {
				e.printStackTrace();
				return;
			} catch (IndexOutOfBoundsException e) {
				sendEmbedMessage(new DefaultEmbed("Usuario inexistente", "Este usuario que você solicitou não existe."))
				.queue();
				return;
			} catch (UnsupportedOperationException e) {
				sendEmbedMessage(new DefaultEmbed("OsuAPI", "Não foi possivel pegar as informações deste usuario\nPois a API esta delimitando isso.")).queue();;
				return;
			}
			InputStream drawer = OsuProfileNote.drawImage(osuUser);
			String aname =osuUser.getUserid() + "userOsu.png";
			channel.sendFile(drawer, aname)
			       .embed(embed(osuUser).setImage("attachment://" + aname).build()).queue();
			return;
		}
		
		if (args.length >= 3) {
			GameMode gm = getGamemode(args[2]);
			if (gm == null) {
				gm = GameMode.STANDARD;
			}
			
			UserOsu osuUser;
			try {
				osuUser = new UserOsu(args[1], gm);
			} catch (MalformedURLException | OsuAPIException e) {
				e.printStackTrace();
				return;
			} catch (IndexOutOfBoundsException e) {
				sendEmbedMessage(new DefaultEmbed("Usuario inexistente", "Este usuario que você solicitou não existe."))
				.queue();;
				return;
			} catch (UnsupportedOperationException e) {
				sendEmbedMessage(new DefaultEmbed("OsuAPI", "Não foi possivel pegar as informações deste usuario\nPois a API esta delimitando isso.")).queue();;
				return;
			}
			
			InputStream drawer = OsuProfileNote.drawImage(osuUser);
			String aname =osuUser.getUserid() + "userOsu.png";
			channel.sendFile(drawer, aname)
		       .embed(embed(osuUser).setImage("attachment://" + aname).build()).queue();
			return;
		}
	}

	public EmbedBuilder embed(UserOsu osuUser) {
		EmbedBuilder embed = new EmbedBuilder();
		
		embed.setAuthor(osuUser.getUsername(), osuUser.getUserUrl(), osuUser.getAvatarURL());
		
		embed.setColor(Color.gray);
		embed.setTitle("Informações do Jogador");
		embed.setDescription("Você esta visualizando as informações do usuario [" + osuUser.getUsername() +"]("+ osuUser.getUserUrl() +")");
		
		NumberFormat f = NumberFormat.getNumberInstance();
		embed.addField("Ranking: ", "#"+f.format(osuUser.getRank()), true);
		embed.addField("Ranking Nacional:", osuUser.getPais().getName() + " #" + f.format(osuUser.getNacionalRank()), true);
		//embed.addBlankField(true);
		
		
		String accuracy = new DecimalFormat("#.0").format(osuUser.getUser().getAccuracy());

		accuracy+= "%";
		String PP = OsuEmoji.PP.getEmojiString();
		embed.addField(
				"Desempenho:",
				
				"Precisão: `" + accuracy + "`" + 
				"\n" + PP + " " + f.format(osuUser.getPp()), true);
			
		embed.addField("Pontuação Total:", f.format(osuUser.getTotalscore())+"", true);
		//embed.addField("Link:", osuUser.getUserUrl(), true);
		
		//embed.setThumbnail(osuUser.getAvatarURL());
		embed.setFooter("Sknz#4260 | Yagateiro Master", "https://osu.ppy.sh/images/flags/" + osuUser.getPais().getAlpha2() + ".png");
		return embed;
	}
	
	public GameMode getGamemode(String gamemode) {
		String gm = gamemode.toLowerCase();
		Map<String, GameMode> map = new HashMap<>();
		
		map.put("standard", GameMode.STANDARD);
		map.put("catch", GameMode.CATCH_THE_BEAT);
		map.put("mania", GameMode.MANIA);
		map.put("taiko", GameMode.TAIKO);

		if (map.containsKey(gm)) {
			return map.get(gamemode);
		}
		
		return null;
	}


}
