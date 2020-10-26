package me.skiincraft.discord.ousu.emojis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class GenericsEmotes {

    private static List<GenericEmote> emotes = new ArrayList<>();

    public static void loadEmotes(String path) throws IOException {
        loadEmotes(Paths.get(path));
    }

    public static void loadEmotes(Path path) throws IOException {
        emotes.clear();
        AtomicInteger loaded = new AtomicInteger();
        Files.newDirectoryStream(path).forEach(path1 -> {
            if (path1.toFile().getName().toLowerCase().contains(".emotejson")){
                try {
                    Gson gson = new Gson();
                    GenericEmote[] genericEmotes = gson.fromJson(new FileReader(path1.toFile()), GenericEmote[].class);
                    emotes.addAll(Arrays.asList(genericEmotes));
                    loaded.getAndIncrement();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println(loaded + " emotes loaded");
    }

    public static List<GenericEmote> parseEmotes(List<Emote> emotes) {
        return emotes.stream().map(emote -> new GenericEmote(emote.getName(), emote.getIdLong(), Objects.requireNonNull(emote.getGuild(), "Guild is null").getIdLong(), emote.isAnimated()))
                .collect(Collectors.toList());
    }

    public static List<GenericEmote> parseEmotes(Guild guild) {
        return parseEmotes(guild.getEmotes());
    }

    public static void saveEmotes(String path, List<GenericEmote> emotes){
        try {
            File file = new File((path.endsWith("/") ? path.substring(0, path.length() - 2) : path) + "/" + emotes.get(0).getGuildId() + ".emotejson");
            file.mkdir();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            FileWriter writer = new FileWriter(file);
            writer.write(gson.toJson(emotes));
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
