package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import javax.imageio.ImageIO;

import me.skiincraft.api.ousu.exceptions.InvalidUserException;
import me.skiincraft.api.ousu.modifiers.Gamemode;
import me.skiincraft.api.ousu.users.User;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.customemoji.OusuEmojis;
import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.events.DefaultReaction;
import me.skiincraft.discord.ousu.imagebuilders.OsuProfileNote;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.manager.CommandCategory;
import me.skiincraft.discord.ousu.manager.Commands;
import me.skiincraft.discord.ousu.search.JSoupGetters;
import me.skiincraft.discord.ousu.search.UserStatistics;
import me.skiincraft.discord.ousu.utils.Emoji;
import me.skiincraft.discord.ousu.utils.ImageUtils;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

public class UserCommand extends Commands {

	public UserCommand() {
		super("ou!", "user", "ou!user <nickname> <gamemode>", Arrays.asList("player", "profile", "usuario"));
	}

	@Override
	public String[] helpMessage(LanguageManager lang) {
		return lang.translatedArrayHelp("OSU_HELPMESSAGE_USER");
	}

	@Override
	public CommandCategory categoria() {
		return CommandCategory.Osu;
	}

	@Override
	public void action(String[] args, String label, TextChannel channel) {
		if (args.length == 0) {
			sendUsage().queue();
			return;
		}

		if (args.length >= 1) {
			User osuUser;
			try {
				StringBuffer stringArgs = new StringBuffer();
				for (int i = 0; i < args.length; i++) {
					stringArgs.append(args[i] + " ");
				}

				int length = stringArgs.toString().length() - 1;

				String usermsg = stringArgs.toString().substring(0, length);
				String lastmsg = args[args.length - 1];
				String name = usermsg.replace(" " + lastmsg, "");

				if (Gamemode.getGamemode(lastmsg) != null) {
					osuUser = OusuBot.getOsu().getUser(name, Gamemode.getGamemode(lastmsg));
				} else {
					osuUser = OusuBot.getOsu().getUser(usermsg);
				}
				InputStream drawer = OsuProfileNote.drawImage(osuUser, getLanguage());
				String aname = osuUser.getUserID() + "userOsu.png";
				EmbedBuilder embedlocal = embed(osuUser).setImage("attachment://" + aname);
				
				channel.sendFile(drawer, aname).embed(embedlocal.build()).queue(message -> {
					if (osuUser.getGamemode() != Gamemode.Standard) {
						return;
					}
					
					try {
						EmbedBuilder[] embeds = new EmbedBuilder[] {embedlocal, 
								embed2(JSoupGetters.inputType(osuUser, getLang()), message.getEmbeds().get(0).getColor()).setImage("attachment://" + aname)};
						ReactionMessage.userReactions.add(new DefaultReaction(getUserId(), message.getId(), embeds, 0));
						message.addReaction("U+1F4F0").queue();
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			} catch (InvalidUserException e) {
				String[] str = getLang().translatedArrayOsuMessages("INEXISTENT_USER");
				StringBuffer buffer = new StringBuffer();
				for (String append : str) {
					if (append != str[0]) {
						buffer.append(OusuEmojis.getEmoteAsMention("small_red_diamond") + " " + append);
					}
				}

				sendEmbedMessage(TypeEmbed.WarningEmbed(str[0], buffer.toString())).queue();
				return;
			}
		}
	}

	public EmbedBuilder embed(User osuUser) {
		EmbedBuilder embed = new EmbedBuilder();
		NumberFormat f = NumberFormat.getNumberInstance();
		String accuracy = new DecimalFormat("#.0").format(osuUser.getAccuracy());
		String PP = OusuEmojis.getEmoteAsMentionEquals("pp");

		String code = ":flag_" + osuUser.getCountryCode().toLowerCase() + ": " + osuUser.getCountryCode();

		embed.setThumbnail(osuUser.getUserAvatar());

		embed.setAuthor(osuUser.getUserName(), osuUser.getURL(), osuUser.getUserAvatar());
		embed.setTitle(getLang().translatedEmbeds("TITLE_USER_COMMAND_PLAYERSTATS"));
		embed.setDescription(Emoji.SMALL_BLUE_DIAMOND.getAsMention() + getLang().translatedEmbeds("MESSAGE_USER")
				.replace("{USERNAME}", "[" + osuUser.getUserName() + "](" + osuUser.getURL() + ")"));
		embed.addField(getLang().translatedEmbeds("RANKING"),
				Emoji.MAP.getAsMention() + " #" + f.format(osuUser.getRanking()), true);
		embed.addField(getLang().translatedEmbeds("NATIONAL_RANKING"), code + " #" + f.format(osuUser.getNacionalRanking()),
				true);
		embed.addField(getLang().translatedEmbeds("PLAYED_TIME"), "🕒 " + osuUser.getPlayedHours().toString(), true);
		embed.addField(getLang().translatedEmbeds("PERFORMANCE"),
				Emoji.PEN_BALLPOINT.getAsMention() + " " + getLang().translatedEmbeds("ACCURACY") + "`" + (accuracy += "%")
						+ "`" + "\n" + PP + " " + f.format(osuUser.getPP()),
				true);

		embed.addField(getLang().translatedEmbeds("TOTAL_SCORE"), f.format(osuUser.getTotalScore()) + "", true);

		embed.setFooter(getLang().translatedBot("FOOTER_DEFAULT"),
				"https://osu.ppy.sh/images/flags/" + osuUser.getCountryCode() + ".png");

		try {
			BufferedImage im = new BufferedImage(200, 200, 2);
			Image image = ImageIO.read(new URL(osuUser.getUserAvatar()));
			im.createGraphics().drawImage(image, 0, 0, 200, 200, null);
			embed.setColor(ImageUtils.getPredominatColor(im));
		} catch (NullPointerException | IOException e) {
			embed.setColor(Color.BLUE);
		}
		return embed;
	}
	
	public EmbedBuilder embed2(UserStatistics osuUser, Color color) {
		EmbedBuilder embed = new EmbedBuilder();
		embed.setTitle(getLang().translatedEmbeds("TITLE_USER_COMMAND_PLAYERSTATS"));
		embed.setDescription(Emoji.SMALL_BLUE_DIAMOND.getAsMention() + " " + getLang().translatedEmbeds("MESSAGE_USER_STATISTICS")
				.replace("{USERNAME}",
						"[" + osuUser.getUser().getUserName() + "](" + osuUser.getUser().getURL() +")"));
		embed.setThumbnail(osuUser.getUser().getUserAvatar());
		embed.setAuthor(osuUser.getUser().getUserName(), osuUser.getUser().getURL(), osuUser.getUser().getUserAvatar());
		
		embed.addField(getLang().translatedEmbeds("INPUTS"), osuUser.getInputEmotes(), true);
		embed.addField("Level", osuUser.getUser().getLevel() + "", true);
		embed.addField("UserID", osuUser.getUser().getUserID() + "", true);
		embed.addField(getLang().translatedEmbeds("LASTPP"), osuUser.getLastPpCapture() + "", true);
		embed.addField(getLang().translatedEmbeds("LASTACTIVE"), osuUser.getLastActive() + "", true);
		embed.addField(getLang().translatedEmbeds("JOINED"), osuUser.getFirstlogin(), true);
		
		embed.setColor(color);
		embed.setFooter(getLang().translatedBot("FOOTER_DEFAULT"),
				"https://osu.ppy.sh/images/flags/" + osuUser.getUser().getCountryCode() + ".png");
		return embed;
	}
}
