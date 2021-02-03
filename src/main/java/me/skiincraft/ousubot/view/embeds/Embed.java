package me.skiincraft.ousubot.view.embeds;

import me.skiincraft.ousucore.language.Language;
import me.skiincraft.ousubot.view.emotes.GenericsEmotes;
import me.skiincraft.ousubot.view.token.ClassToken;
import net.dv8tion.jda.api.EmbedBuilder;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Embed {

    private final String title;
    private final String description;
    private final String url;
    private final String color;

    private final List<Field> fields;
    private final Author author;
    private final Footer footer;

    private final Image image;
    private final Thumbnail thumbnail;

    public Embed(String title, String description, String url, String color, List<Field> fields, Author author, Footer footer, Image image, Thumbnail thumbnail) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.color = color;
        this.fields = fields;
        this.author = author;
        this.footer = footer;
        this.image = image;
        this.thumbnail = thumbnail;
    }

    private Color convertColor() {
        String[] rgb = color.split(",");
        if (rgb.length < 3) {
            return Color.CYAN;
        }
        Integer[] rgbInt = Arrays.stream(rgb).map(Integer::parseInt).toArray(Integer[]::new);
        return new Color(rgbInt[0], rgbInt[1], rgbInt[2]);
    }

    private String replaceToken(String string, Language language, Map<String, ClassToken<?>> tokens) {
        List<String> elements = language.findElements(string, "#{", "}");
        for (String element : Objects.requireNonNull(elements)) {
            String[] parameters = element.split("\\.");
            if (tokens.containsKey(parameters[0].toLowerCase())) {
                try {
                    string = string.replace("#{" + element + "}", tokens.get(parameters[0].toLowerCase())
                            .get(Arrays.copyOfRange(parameters, 1, parameters.length)));
                } catch (Exception ignored) {
                }
            }
        }
        return string;
    }

    private String replaceEmote(String string, Language language, Map<String, ClassToken<?>> tokens) {
        List<String> emotesElements = language.findElements(string, "@{", "}");
        GenericsEmotes emotes = (GenericsEmotes) tokens.get("emotes").getItem();
        for (String element : Objects.requireNonNull(emotesElements)) {
            if (Objects.isNull(emotes.getEmoteEquals(element))) {
                continue;
            }
            string = string.replace("@{" + element + "}", emotes.getEmoteAsMentionEquals(element));
        }
        return string;
    }

    private String replace(String string, Language language, Map<String, ClassToken<?>> tokens) {
        if (tokens == null)
            return language.replace(string);

        String str = language.replace(string);
        str = replaceToken(str, language, tokens);
        str = replaceEmote(str, language, tokens);

        return str;
    }

    public EmbedBuilder toMessageEmbed(@Nonnull Language language, Map<String, ClassToken<?>> tokens) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(replace(title, language, tokens));
        builder.setDescription(replace(description, language, tokens));
        if (title != null && url != null)
            builder.setTitle(replace(title, language, tokens), replace(url, language, tokens));

        if (color != null) {
            builder.setColor(convertColor());
        }
        if (tokens.containsKey("color")) {
            ClassToken<?> color = tokens.get("color");
            if (color.getItem() instanceof Color) {
                builder.setColor((Color) color.getItem());
            }
        }

        if (author != null)
            builder.setAuthor(replace(author.getName(), language, tokens), replace(author.getURL(), language, tokens), replace(author.getIconURL(), language, tokens));

        if (fields != null)
            for (Field field : fields)
                builder.addField(replace(field.getName(), language, tokens), replace(field.getValue(), language, tokens), field.isInline());

        if (footer != null)
            builder.setFooter(replace(footer.getText(), language, tokens), replace(footer.getIconUrl(), language, tokens));

        if (image != null)
            builder.setImage(replace(image.getUrl(), language, tokens));

        if (thumbnail != null) {
            builder.setThumbnail(replace(thumbnail.getUrl(), language, tokens));
        }

        return builder;
    }

    public static class Author {

        private final String name;
        private final String url;
        private final String icon_url;

        public Author(String name, String url, String icon_url) {
            this.name = name;
            this.url = url;
            this.icon_url = icon_url;
        }

        public String getName() {
            return name;
        }

        public String getURL() {
            return url;
        }

        public String getIconURL() {
            return icon_url;
        }

    }

    public static class Field {
        private final String name;
        private final String value;
        private final boolean inline;

        public Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public boolean isInline() {
            return inline;
        }

    }

    public static class Footer {
        private final String text;
        private final String icon_url;

        public Footer(String text, String icon_url) {
            this.text = text;
            this.icon_url = icon_url;
        }

        public String getText() {
            return text;
        }

        public String getIconUrl() {
            return icon_url;
        }
    }

    public static class Image {
        private String url;

        public Image(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public Image setUrl(String url) {
            this.url = url;
            return this;
        }
    }

    public static class Thumbnail {
        private String url;

        public Thumbnail(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }

        public Thumbnail setUrl(String url) {
            this.url = url;
            return this;
        }
    }

}
