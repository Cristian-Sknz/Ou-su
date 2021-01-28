package me.skiincraft.ousubot.view.emotes;

public class GenericEmote {

    String ICON_URL = "https://cdn.discordapp.com/emojis/";

    private final String name;
    private final long id;
    private final boolean animated;
    private final long guildId;

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
        return ICON_URL + id + ((animated) ? ".gif" : ".png");
    }

    public boolean isAnimated(){
        return animated;
    }

    public long getId() {
        return id;
    }

    public String getReaction(){
        return getName() + ":" + getId();
    }

    public String getAsMention() {
        return "<:" + getName() + ":" +getId() + ">";
    }
}
