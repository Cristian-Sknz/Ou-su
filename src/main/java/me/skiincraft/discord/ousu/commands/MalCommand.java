package me.skiincraft.discord.ousu.commands;

import me.skiincraft.discord.core.configuration.LanguageManager;
import me.skiincraft.discord.ousu.common.Comando;
import me.skiincraft.discord.ousu.common.CommandCategory;
import me.skiincraft.discord.ousu.utils.OusuUtils;
import me.skiincraft.mal.MyAnimeList;
import me.skiincraft.mal.entity.anime.Anime;
import me.skiincraft.mal.entity.anime.Animeography;
import me.skiincraft.mal.entity.characters.Character;
import me.skiincraft.mal.entity.characters.CharacterShort;
import me.skiincraft.mal.entity.manga.Mangaography;
import me.skiincraft.mal.entity.objects.Genre;
import me.skiincraft.mal.util.By;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;

public class MalCommand extends Comando {

    private static MyAnimeList myAnimeList;

    public MalCommand() {
        super("mal", Collections.singletonList("myanimelist"), "mal <anime/character> <name>");
        myAnimeList = new MyAnimeList();
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.Owner;
    }

    @Override
    public void execute(User user, String[] args, TextChannel channel) {
        if (args.length <= 1){
            replyUsage();
            return;
        }

        if (args[0].equalsIgnoreCase("anime")) {
            Anime anime = myAnimeList.getAnime(By.search(appendArgs(1, args))).get();
            try {
                if (anime.getCharacters() != null) {
                    List<CharacterShort> characters = anime.getCharacters().get();
                    reply(animeEmbed(anime, characters.get(0).getCharacter().get()).build());
                    return;
                }
            } catch (NullPointerException e) {
                reply(animeEmbed(anime, null).build());
                return;
            }

            reply(animeEmbed(anime, null).build());
            return;
        }

        if (args[0].equalsIgnoreCase("character")) {
            Character character = myAnimeList.getCharacter(By.search(appendArgs(1, args))).get();
            reply(characterEmbed(character).build());
            return;
        }

        replyUsage();
    }

    public String appendArgs(int start, String[] args){
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < args.length; i++) {
            builder.append(args[i])
                    .append(" ");
        }
        return builder.substring(0, builder.length()-1);
    }


    public EmbedBuilder animeEmbed(Anime anime, Character characterShort) {
        EmbedBuilder embed = new EmbedBuilder();
        LanguageManager lang = getLanguageManager();

        String avatar = (characterShort == null) ? anime.getDefaultAvatar() : characterShort.getDefaultAvatar();

        embed.setAuthor(anime.getName(), anime.getMALPage(), avatar);
        embed.setTitle("MyAnimeList");
        embed.setDescription(OusuUtils.dcText(lang.getString("Embeds","STATISTICS"), OusuUtils.DCText.BOLD) + "\n");

        StringBuilder builder = new StringBuilder();
        NumberFormat nf = NumberFormat.getInstance(lang.getLanguage().getLocale());
        builder.append("<:owner:585789630800986114>").append("Score")
                .append(": ").append(anime.getScore())
                .append("\n");

        builder.append(":shinto_shrine: ").append(lang.getString("Embeds","POPULARITY"))
                .append(" #")
                .append(nf.format(anime.getPopularity()))
                .append("\n");

        builder.append("<:members:658538493470965787> ").append(lang.getString("Embeds","MEMBERS"))
                .append(" ")
                .append(nf.format(anime.getMembers()))
                .append("\n");

        embed.appendDescription(builder.toString());
        StringBuilder genres = new StringBuilder();
        int i = 1;
        List<Genre> genresEnum = anime.getInformation().getGenres();
        if (genresEnum.size() == 0){
            genres.append(Genre.Adventure.getName());
        }

        for (Genre genre : genresEnum){
            if (i == 2){
                genres.append(genre.getName()).append("\n");
                i = 1;
                continue;
            }
            if (genre != genresEnum.get(genresEnum.size()-1)){
                genres.append(genre.getName()).append(", ");
                i++;
            }
        }

        StringBuilder information = new StringBuilder();
        information.append(":tv: ")
                .append(anime.getInformation().getType().name())
                .append("\n");

        information.append(":frame_photo: ")
                .append(anime.getInformation().getEpisodes())
                .append(lang.getString("Embeds", "EPISODES"))
                .append("\n");

        information.append(":green_circle: ")
                .append(anime.getInformation().getAired());

        embed.addField(lang.getString("Titles", "GENRE"), genres.toString(), true);
        embed.addField(lang.getString("Embeds", "PREMIERED"), anime.getInformation().getPremiered(), true);
        embed.addField(lang.getString("Embeds", "INFORMATION"), information.toString(), true);
        embed.setColor(new Color(255, 137, 81));

        embed.setThumbnail(anime.getDefaultAvatar());

        String englishname = (anime.getEnglishName() == null) ? (characterShort != null) ? characterShort.getName() : "-" : anime.getEnglishName();
        embed.setFooter(englishname, avatar);

        return embed;
    }

    public EmbedBuilder characterEmbed(Character character) {
        EmbedBuilder embed = new EmbedBuilder();
        LanguageManager lang = getLanguageManager();
        embed.setAuthor(character.getName(), character.getMALPage(), character.getDefaultAvatar());
        embed.setTitle("MyAnimeList");
        embed.setThumbnail(character.getDefaultAvatar());
        embed.setDescription(OusuUtils.dcText(lang.getString("Embeds","STATISTICS"), OusuUtils.DCText.BOLD) + "\n");

        StringBuilder builder = new StringBuilder();
        NumberFormat nf = NumberFormat.getInstance(lang.getLanguage().getLocale());
        builder.append("<:owner:585789630800986114>").append("Favorites: ")
                .append(nf.format(character.getFavorites()))
                .append("\n");

        embed.appendDescription(builder.toString());
        embed.setColor(new Color(255, 137, 81));

        if (character.getAnimeography().getSize() != 0) {
            StringBuilder graphy = new StringBuilder();
            Animeography animeography = character.getAnimeography();

            int i = 1;
            for (Animeography.Animeshort anime : animeography.getAnimes()){
                graphy.append(anime.getName())
                        .append("\n");
                i++;
                if (i == 3 && animeography.getSize() > 3){
                    graphy.append("[...]");
                    break;
                }
            }
            embed.addField(lang.getString("Embeds", "ANIMEOGRAPHY"), graphy.toString(), true);
        }
        if (character.getMangaography().getSize() != 0) {
            StringBuilder graphy = new StringBuilder();
            Mangaography animeography = character.getMangaography();

            int i = 1;
            for (Mangaography.Mangashort anime : animeography.getMangas()){
                graphy.append(anime.getName())
                        .append("\n");
                i++;
                if (i == 3 && animeography.getSize() > 3){
                    graphy.append("[...]");
                    break;
                }
            }
            embed.addField(lang.getString("Embeds", "MANGAOGRAPHY"), graphy.toString(), true);
        }


        return embed;
    }

}
