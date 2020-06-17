package me.skiincraft.discord.ousu.commands;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import me.skiincraft.api.ousu.exceptions.InvalidUserException;
import me.skiincraft.api.ousu.modifiers.Gamemode;
import me.skiincraft.api.ousu.modifiers.ProfileEvents;
import me.skiincraft.api.ousu.modifiers.ProfileEvents.EventDisplay;
import me.skiincraft.api.ousu.users.User;
import me.skiincraft.discord.ousu.OusuBot;
import me.skiincraft.discord.ousu.abstractcore.CommandCategory;
import me.skiincraft.discord.ousu.abstractcore.Commands;
import me.skiincraft.discord.ousu.customemoji.OusuEmojis;
import me.skiincraft.discord.ousu.embeds.TypeEmbed;
import me.skiincraft.discord.ousu.events.DoubleReaction;
import me.skiincraft.discord.ousu.imagebuilders.OsuProfileNote;
import me.skiincraft.discord.ousu.language.LanguageManager;
import me.skiincraft.discord.ousu.search.JSoupGetters;
import me.skiincraft.discord.ousu.search.UserStatistics;
import me.skiincraft.discord.ousu.utils.Emoji;
import me.skiincraft.discord.ousu.utils.ImageUtils;
import me.skiincraft.discord.ousu.utils.InputStreamFile;
import me.skiincraft.discord.ousu.utils.ReactionMessage;
import me.skiincraft.discord.ousu.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
			sendUsage();
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
				String aname = osuUser.getUserID() + "osuuser_scores";
				EmbedBuilder embedlocal = embed(osuUser).setImage("attachment://" + aname + ".png");
				InputStreamFile isfile = new InputStreamFile(drawer, aname, ".png");
				
				replyQueue(embedlocal.build(), message -> {
					if (osuUser.getGamemode() != Gamemode.Standard) {
						return;
					}
					
					try {
						UserStatistics getter = JSoupGetters.inputType(osuUser, getLang());
						EmbedBuilder embed1 = new EmbedBuilder(message.getEmbeds().get(0))
								.setImage("attachment://" + isfile.getFullname());
						EmbedBuilder embed2 = embed2(getter, embedlocal)
								.setImage("attachment://" + isfile.getFullname());
						List<EmbedBuilder> e = embed3(osuUser, 
								"attachment://" + isfile.getFullname());
						EmbedBuilder[] events = new EmbedBuilder[e.size()];
						EmbedBuilder[] embeds = new EmbedBuilder[] { embed1, embed2 };
						e.toArray(events);

						ReactionMessage.userReactions
								.add(new DoubleReaction(getUserId(), message.getId(), embeds, events, 0, 0));
						message.addReaction("U+1F4F0").queue();
						if (events.length != 0) {
							message.addReaction("U+1F4CB").queue();
						}

					} catch (IOException e) {
						e.printStackTrace();
					}
				},isfile, 12);
	
			} catch (InvalidUserException e) {
				String[] str = getLang().translatedArrayOsuMessages("INEXISTENT_USER");
				StringBuffer buffer = new StringBuffer();
				for (String append : str) {
					if (append != str[0]) {
						buffer.append(OusuEmojis.getEmoteAsMention("small_red_diamond") + " " + append);
					}
				}

				MessageEmbed embed = TypeEmbed.WarningEmbed(str[0], buffer.toString()).build();
				reply(embed);
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

		String useravatar = osuUser.getUserAvatar();

		embed.setThumbnail(useravatar);
		embed.setAuthor(osuUser.getUserName(), osuUser.getURL(), useravatar);

		embed.setTitle(getLang().translatedEmbeds("TITLE_USER_COMMAND_PLAYERSTATS"));
		embed.setDescription(Emoji.SMALL_BLUE_DIAMOND.getAsMention() + getLang().translatedEmbeds("MESSAGE_USER")
				.replace("{USERNAME}", "[" + osuUser.getUserName() + "](" + osuUser.getURL() + ")"));
		embed.addField(getLang().translatedEmbeds("RANKING"),
				Emoji.MAP.getAsMention() + " #" + f.format(osuUser.getRanking()), true);
		embed.addField(getLang().translatedEmbeds("NATIONAL_RANKING"),
				code + " #" + f.format(osuUser.getNacionalRanking()), true);
		embed.addField(getLang().translatedEmbeds("PLAYED_TIME"), "🕒 " + osuUser.getPlayedHours().toString(), true);
		embed.addField(getLang().translatedEmbeds("PERFORMANCE"),
				Emoji.PEN_BALLPOINT.getAsMention() + " " + getLang().translatedEmbeds("ACCURACY") + "`"
						+ (accuracy += "%") + "`" + "\n" + PP + " " + f.format(osuUser.getPP()),
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
			embed.setColor(Color.YELLOW);
		}
		return embed;
	}

	public EmbedBuilder embed2(UserStatistics osuUser, EmbedBuilder embed) {
		embed.clearFields();
		embed.setTitle(getLang().translatedEmbeds("TITLE_USER_COMMAND_PLAYERSTATS"));
		embed.setDescription(Emoji.SMALL_BLUE_DIAMOND.getAsMention() + " "
				+ getLang().translatedEmbeds("MESSAGE_USER_STATISTICS").replace("{USERNAME}",
						"[" + osuUser.getUser().getUserName() + "](" + osuUser.getUser().getURL() + ")"));

		embed.addField(getLang().translatedEmbeds("INPUTS"), osuUser.getInputEmotes(), true);
		embed.addField("Level", osuUser.getUser().getLevel() + "", true);
		embed.addField("UserID", osuUser.getUser().getUserID() + "", true);
		embed.addField(getLang().translatedEmbeds("LASTPP"), osuUser.getLastPpCapture() + "", true);
		embed.addField(getLang().translatedEmbeds("LASTACTIVE"), osuUser.getLastActive() + "", true);
		embed.addField(getLang().translatedEmbeds("JOINED"), osuUser.getFirstlogin(), true);

		embed.setFooter(getLang().translatedBot("FOOTER_DEFAULT"),
				"https://osu.ppy.sh/images/flags/" + osuUser.getUser().getCountryCode() + ".png");
		return embed;
	}

	public List<EmbedBuilder> embed3(User osuUser, String image) {
		List<EmbedBuilder> b = new ArrayList<EmbedBuilder>();
		if (osuUser.getProfileEvents() == null) return b;
		if (osuUser.getProfileEvents().size() == 0) return b;
		
		for (ProfileEvents profile : osuUser.getProfileEvents()) {
			EmbedBuilder embed = new EmbedBuilder();
			EventDisplay events = profile.getEventDisplay();
			String beat1 = events.getBeatmapDisplay().substring(0,
					StringUtils.getFirstLetters("[", events.getBeatmapDisplay()));
			String beatmapurl = "https://osu.ppy.sh/beatmapsets/" + profile.getBeatmapSetID();
			beat1 = "[" + beat1 + "](" + beatmapurl + ")";
			embed.setTitle(getLang().translatedEmbeds("TITLE_USER_COMMAND_RECENTEVENTS"));
			embed.setDescription(Emoji.SMALL_BLUE_DIAMOND.getAsMention() + " "
					+ getLang().translatedEmbeds("MESSAGE_EVENTS")
							.replace("{USERNAME}", "[" + osuUser.getUserName() + "](" + osuUser.getURL() + ")")
							.replace("{RANKING}", events.getRankingPosition()).replace("{BEATMAPNAME}", beat1));

			embed.setThumbnail(osuUser.getUserAvatar());
			embed.setAuthor(osuUser.getUserName(), osuUser.getURL(), osuUser.getUserAvatar());

			embed.addField("Beatmap:", beat1, true);
			embed.addField("BeatmapSet:", profile.getBeatmapSetID() + "", true);
			embed.addField("Position:", events.getRankingPosition(), true);
			embed.addField("Beatmap:", profile.getBeatmapid() + "", true);
			embed.addBlankField(true);
			embed.addField("Date:", profile.getEventDate() + "", true);
			embed.setImage(image);
			StringBuffer display = new StringBuffer();
			embed.appendDescription("\n\n");
			// "MESSAGE_EVENTS": "{USERNAME} achieved rank #{RANKING} on {BEATMAPNAME}",
			embed.appendDescription(display.toString());
			embed.setFooter(getLang().translatedBot("FOOTER_DEFAULT"),
					"https://osu.ppy.sh/images/flags/" + osuUser.getCountryCode() + ".png");
			b.add(embed);
		};
		return b;
	}
}
