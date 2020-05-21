package me.skiincraft.discord.ousu.customemoji;

public enum OsuEmoji {

	Hit300("<:300:700307179348951070>"), Hit100("<:100:700307179630100491>"), Hit50("<:50:700307179705598002>"),
	Miss("<:miss:700307179713986611>"), PP("<:pp:700302019612639272>"),

	SSPlus("<:ss_plus:700444558227669094>"), SS("<:ss:700444558189920306>"), SPlus("<:s_plus:700444558210629662>"),
	S("<:s:700444558433189957>"), A("<:a:700444557711638620>"), B("<:b:700444557950582895>"),
	C("<:c:700444558403567637>"), F("<:f:700444558080737451>"),

	OsuLogo("<:osulogo:700458186204905593>"), Star("<:star:702696688439001088>"),
	OusuEmoji("<:osuemoji:712793537661632532>"),
	HalfStar("<:halfstar:702696688355377224>"), ThinkAnime("<:thinkanime:710287212557762680>"), Pippi("<:pippi:712091295987925004>");

	private String custom;

	OsuEmoji(String custom) {
		this.setEmojiString(custom);
	}

	public String getEmojiString() {
		return custom;
	}

	public void setEmojiString(String custom) {
		this.custom = custom;
	}
}
