package me.skiincraft.discord.ousu.emojis;

public class GenericEmote {

    String ICON_URL = "https://cdn.discordapp.com/emojis/%s.%s";

    private String name;
    private long id;
    private boolean animated;
    private long guildId;

    public GenericEmote(String name, long id, long guildId, boolean animated) {
        this.name = name;
        this.id = id;
        this.animated = animated;
        this.guildId = guildId;
    }

    public String getName() {
        return name;
    }

    public long getGuildId(){
        return guildId;
    }

    public String getEmoteUrl(){
        return String.format(ICON_URL, id, (animated) ? ".gif" : ".png");
    }

    public boolean isAnimated(){
        return animated;
    }

    public long getId() {
        return id;
    }
}
