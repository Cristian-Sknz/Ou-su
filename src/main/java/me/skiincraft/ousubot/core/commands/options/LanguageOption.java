package me.skiincraft.ousubot.core.commands.options;

import me.skiincraft.api.osu.object.beatmap.Language;

import java.util.Arrays;

public class LanguageOption extends CommandOption {

    private Language language;

    public LanguageOption(Language language) {
        super(language.name(), new String[0], "language");
        this.language = language;
    }

    public Language getLanguage() {
        return language;
    }

    public static LanguageOption[] getAllOptions(){
        return Arrays.stream(Language.values()).map(LanguageOption::new).toArray(LanguageOption[]::new);
    }
}
