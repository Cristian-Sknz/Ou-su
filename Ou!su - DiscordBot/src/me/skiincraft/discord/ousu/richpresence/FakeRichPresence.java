package me.skiincraft.discord.ousu.richpresence;

import java.util.EnumSet;

import net.dv8tion.jda.api.entities.ActivityFlag;
import net.dv8tion.jda.api.entities.RichPresence;

public class FakeRichPresence {

	private String nickname;

	public FakeRichPresence(String nickname) {
		this.nickname = nickname;
	}

	public RichPresence build() {
		return new RichPresence() {

			@Override
			public boolean isRich() {
				return true;
			}

			@Override
			public String getUrl() {
				return null;
			}

			@Override
			public ActivityType getType() {
				return ActivityType.DEFAULT;
			}

			@Override
			public Timestamps getTimestamps() {
				return null;
			}

			@Override
			public String getName() {
				return "Osu!";
			}

			@Override
			public Emoji getEmoji() {
				return null;
			}

			@Override
			public RichPresence asRichPresence() {
				return build();
			}

			@Override
			public String getSyncId() {
				return null;
			}

			@Override
			public String getState() {
				return "OFFLINE";
			}

			@Override
			public Image getSmallImage() {
				return new Image(367827983903490050L, "373370493127884800", nickname);
			}

			@Override
			public String getSessionId() {
				return null;
			}

			@Override
			public Party getParty() {
				return null;
			}

			@Override
			public Image getLargeImage() {
				return new Image(367827983903490050L, "373344233077211136", nickname);
			}

			@Override
			public int getFlags() {
				return 0;
			}

			@Override
			public EnumSet<ActivityFlag> getFlagSet() {
				return null;
			}

			@Override
			public String getDetails() {
				return "Offline Rich";
			}

			@Override
			public long getApplicationIdLong() {
				return 367827983903490050L;
			}

			@Override
			public String getApplicationId() {
				return "367827983903490050";
			}
		};
	}

}
