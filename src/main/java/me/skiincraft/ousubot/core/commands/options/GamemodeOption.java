package me.skiincraft.ousubot.core.commands.options;

import me.skiincraft.api.osu.object.game.GameMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GamemodeOption extends CommandOption{

    private GameMode mode;

    public GamemodeOption(GameMode mode) {
        super(mode.name(), addFirstWord(mode.getAlternate()), "gamemode");
        this.mode = mode;
    }

    public GameMode getGameMode(){
        return mode;
    }

    private static String[] addFirstWord(String[] str){
        List<String> l = new ArrayList<>(Arrays.asList(str));
        l.add(str[0].substring(0, 1));
        return l.toArray(new String[0]);
    }

    public static GamemodeOption[] getAllGameOptions() {
        return Arrays.stream(GameMode.values()).map(GamemodeOption::new).toArray(GamemodeOption[]::new);
    }

}
