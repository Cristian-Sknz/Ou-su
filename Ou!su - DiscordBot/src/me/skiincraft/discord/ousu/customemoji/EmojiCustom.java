package me.skiincraft.discord.ousu.customemoji;

public enum EmojiCustom {

	S_RDiamond("<:small_red_diamond:712782771701153804>"), S_GDiamond("<:small_green_diamond:712782771658948729>");

	private String custom;

	EmojiCustom(String custom) {
		this.setEmoji(custom);
	}

	public String getEmoji() {
		return custom;
	}

	public void setEmoji(String custom) {
		this.custom = custom;
	}
}
