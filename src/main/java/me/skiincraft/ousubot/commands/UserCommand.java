package me.skiincraft.ousubot.commands;

import me.skiincraft.api.osu.entity.user.User;
import me.skiincraft.api.osu.exceptions.ResourceNotFoundException;
import me.skiincraft.api.osu.object.game.GameMode;
import me.skiincraft.api.osu.object.user.Grade;
import me.skiincraft.api.osu.requests.Endpoint;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.discord.core.OusuCore;
import me.skiincraft.discord.core.command.ContentMessage;
import me.skiincraft.discord.core.command.InteractChannel;
import me.skiincraft.discord.core.common.CustomFont;
import me.skiincraft.discord.core.language.Language;
import me.skiincraft.ousubot.api.AbstractCommand;
import me.skiincraft.ousubot.api.OusuAPI;
import me.skiincraft.ousubot.view.Messages;
import me.skiincraft.ousubot.view.embeds.MessageModel;
import me.skiincraft.ousubot.view.models.UserAdapter;
import me.skiincraft.ousubot.view.utils.ColorThief;
import me.skiincraft.ousucanvas.ImageBuilder;
import me.skiincraft.ousucanvas.elements.ElementAlignment;
import me.skiincraft.ousucanvas.elements.ElementContainer;
import me.skiincraft.ousucanvas.image.ImageElement;
import me.skiincraft.ousucanvas.text.TextElement;
import me.skiincraft.ousucanvas.text.TextOrientation;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@CommandMap
public class UserCommand extends AbstractCommand {

    @Inject
    private OusuAPI api;

    public UserCommand() {
        super("user", Arrays.asList("profile", "player", "u"), "user <username> [gamemode]");
    }

    @Override
    public CommandType getCategory() {
        return CommandType.Statistics;
    }

    @Override
    public void execute(Member member, String[] args, InteractChannel channel) {
        if (args.length == 0){
            replyUsage(channel.getTextChannel());
            return;
        }
        Language language = Language.getGuildLanguage(channel.getTextChannel().getGuild());
        try {
            Endpoint endpoint = api.getAvailableTokens().getEndpoint();
            Object[] parameters = getParameters(args);
            User user = endpoint.getUser(getUserId(endpoint, String.valueOf(parameters[0])), (GameMode) parameters[1]).get().getUser();
            MessageModel model = new MessageModel("embeds/user", language);
            ContentMessage content = new ContentMessage(getModelEmbed(model, user).build(), userImage(user, language), "png")
                    .setInputName("user_ousu");
            channel.reply(content);
        } catch (ResourceNotFoundException e){
            channel.reply(Messages.getWarning("command.messages.user.inexistent_user", channel.getTextChannel().getGuild()));
        } catch (Exception e) {
            channel.reply(Messages.getError(e, channel.getTextChannel().getGuild()).build());
        }
    }

    public Object[] getParameters(String[] args){
        if (args.length == 1){
            return new Object[] {args[0], GameMode.Osu};
        }
        GameMode gamemode = GameMode.byName(args[args.length-1]);
        if (Objects.isNull(gamemode)){
            return new Object[]{ String.join(" ", args), GameMode.Osu};
        }
        return new Object[]{String.join(" ", Arrays.copyOfRange(args, 0, args.length - 1)), gamemode};
    }

    public boolean isUserId(String string){
        return string.matches("-?\\d+(\\.\\d+)?");
    }

    public long getUserId(Endpoint endpoint, String string){
        if (isUserId(string)) {
            return Long.parseLong(string);
        }
        return endpoint.getUserId(string).get();
    }

    public EmbedBuilder getModelEmbed(MessageModel model , User user){
        AtomicReference<Color> color = new AtomicReference<>(Color.ORANGE);
        try {
            color.set(ColorThief.getPredominatColor(ImageIO.read(new URL(user.getAvatarURL())), false));
        } catch (IOException e){
            e.printStackTrace();
        }
        model.addProperty("userAdapter", new UserAdapter(user));
        return model.getEmbedBuilder();
    }


    public InputStream userImage(User user, Language language) throws IOException {
        ImageBuilder imageBuilder = new ImageBuilder(900, 250, BufferedImage.TYPE_INT_RGB);
        imageBuilder.setBackground(new ImageElement(ImageIO.read(new URL(user.getCoverURL()))));
        imageBuilder.addElement(new ElementContainer(new ImageElement(ImageIO.read(new File(OusuCore.getAssetsPath() + "/osu_images/notes/layer_" + language.getString("locale") + ".png"))), ElementAlignment.BOTTOM_LEFT, 0,0));
        Grade grade = user.getStatistics().getGrade();
        int[] notes = new int[]{ grade.getSsh(), grade.getSS(), grade.getSh(), grade.getS(), grade.getA()};
        Iterator<Integer> x = Arrays.asList(150, 294, 444, 594, 739).iterator();
        for (int n : notes){
            TextElement element = new TextElement(String.valueOf(n), CustomFont.getFont("ARLRDBD", Font.PLAIN, 34F), TextOrientation.MIDDLE);
            element.setColor(new Color(94, 18, 62));
            imageBuilder.addElement(new ElementContainer(element, ElementAlignment.CENTER, x.next(),211));
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(imageBuilder.toImage(), "png", outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
