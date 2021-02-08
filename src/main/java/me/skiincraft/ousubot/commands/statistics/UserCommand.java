package me.skiincraft.ousubot.commands.statistics;

import me.skiincraft.api.osu.entity.user.User;
import me.skiincraft.api.osu.exceptions.ResourceNotFoundException;
import me.skiincraft.api.osu.exceptions.TokenException;
import me.skiincraft.api.osu.object.game.GameMode;
import me.skiincraft.api.osu.object.user.Grade;
import me.skiincraft.api.osu.requests.Endpoint;
import me.skiincraft.beans.annotation.Inject;
import me.skiincraft.beans.stereotypes.CommandMap;
import me.skiincraft.ousubot.OusuBot;
import me.skiincraft.ousubot.core.OusuAPI;
import me.skiincraft.ousubot.core.commands.OptionCommand;
import me.skiincraft.ousubot.core.commands.options.CommandOption;
import me.skiincraft.ousubot.core.commands.options.GamemodeOption;
import me.skiincraft.ousubot.core.commands.options.Options;
import me.skiincraft.ousubot.models.OusuUser;
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
import me.skiincraft.ousucore.OusuCore;
import me.skiincraft.ousucore.command.objecs.Command;
import me.skiincraft.ousucore.command.objecs.ContentMessage;
import me.skiincraft.ousucore.command.utils.CommandTools;
import me.skiincraft.ousucore.common.CustomFont;
import me.skiincraft.ousucore.common.reactions.ReactionObject;
import me.skiincraft.ousucore.common.reactions.Reactions;
import me.skiincraft.ousucore.common.reactions.custom.ReactionPage;
import me.skiincraft.ousucore.language.Language;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@CommandMap
public class UserCommand extends OptionCommand {

    @Inject
    private OusuAPI api;
    private final CommandOption[] options = GamemodeOption.getAllGameOptions();
    public UserCommand() {
        super("user", Arrays.asList("profile", "player", "u"), "user <username> [-gamemode]");
    }

    @Override
    public CommandType getCategory() {
        return CommandType.Statistics;
    }

    @Override
    public void executeWithOptions(String label, String[] args, Options options, CommandTools channel) throws Exception {
        if (args.length == 0) {
            long userId = getOsuId(channel.getMember());
            if (userId == 0) {
                replyUsage(channel.getChannel());
                return;
            }
            args = new String[]{String.valueOf(userId)};
        }
        Language language = Language.getGuildLanguage(channel.getChannel().getGuild());
        Endpoint endpoint = api.getAvailableTokens().getEndpoint();
        User user = endpoint.getUser(getUserId(endpoint, String.join(" ", args)), getGamemode(options)).get().getUser();

        MessageModel model = new MessageModel("embeds/user", language);
        UserAdapter adapter = new UserAdapter(user, model.getEmotes());
        List<EmbedBuilder> embeds = new ArrayList<>(Collections.singletonList(getModelEmbed(model, user, adapter)));

        ContentMessage content = new ContentMessage(embeds.get(0).build(),
                userImage(user, language),
                "png")
                .setInputName("user_ousu");

        channel.reply(content, message -> {
            MessageModel otherModel = new MessageModel("embeds/user2", language);
            embeds.add(getModelEmbed(otherModel, user, adapter));
            Reactions.getInstance().registerReaction(new ReactionObject(message.getMessage(), channel.getMember().getIdLong(), new String[]{"U+1F4CE"}),
                    new ReactionPage(embeds, true));
        });
    }

    private GameMode getGamemode(Options options) {
        return Arrays.stream(options.getOptionArguments()).filter(op -> op.getOption() instanceof GamemodeOption)
                .findFirst().map(op -> ((GamemodeOption) op.getOption()).getGameMode()).orElse(GameMode.Osu);
    }

    @Override
    public CommandOption[] getCommandOptions() {
        return options;
    }

    @Override
    public void onFailure(Exception exception, Command command) {
        CommandTools tools = new CommandTools(command.getMessage());
        if (exception instanceof ResourceNotFoundException) {
            tools.reply(Messages.getWarning("command.messages.user.inexistent_user", tools.getGuild()));
            return;
        }
        if (exception instanceof TokenException){
            tools.reply(Messages.getWarning("messages.error.token", tools.getGuild()));
            return;
        }
        tools.reply(Messages.getError(exception, tools.getGuild()).build());
    }

    public Object[] getParameters(String[] args) {
        if (args.length == 1) {
            return new Object[]{args[0], GameMode.Osu};
        }
        GameMode gamemode = GameMode.byName(args[args.length - 1]);
        if (Objects.isNull(gamemode)) {
            return new Object[]{String.join(" ", args), GameMode.Osu};
        }
        return new Object[]{String.join(" ", Arrays.copyOfRange(args, 0, args.length - 1)), gamemode};
    }

    public boolean isUserId(String string) {
        return string.matches("-?\\d+(\\.\\d+)?");
    }

    public long getUserId(Endpoint endpoint, String string) {
        if (isUserId(string)) {
            return Long.parseLong(string);
        }
        return endpoint.getUserId(string).get();
    }

    private long getOsuId(Member member) {
        OusuUser user = OusuBot.getUserRepository().getById(member.getIdLong()).orElse(null);
        if (user == null) {
            return 0;
        }
        return user.getOsuId();
    }

    public EmbedBuilder getModelEmbed(MessageModel model, User user, UserAdapter adapter) {
        AtomicReference<Color> color = new AtomicReference<>(Color.ORANGE);
        try {
            color.set(ColorThief.getPredominatColor(ImageIO.read(new URL(user.getAvatarURL())), false));
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.addProperty("userAdapter", adapter);
        return model.getEmbedBuilder();
    }


    public InputStream userImage(User user, Language language) throws IOException {
        ImageBuilder imageBuilder = new ImageBuilder(900, 250, BufferedImage.TYPE_INT_RGB);
        imageBuilder.setBackground(new ImageElement(ImageIO.read(new URL(user.getCoverURL()))));
        imageBuilder.addElement(new ElementContainer(new ImageElement(ImageIO.read(new File(OusuCore.getAssetsPath() + "/osu_images/notes/layer_" + language.getString("locale") + ".png"))), ElementAlignment.BOTTOM_LEFT, 0, 0));
        Grade grade = user.getStatistics().getGrade();
        int[] notes = new int[]{grade.getSsh(), grade.getSS(), grade.getSh(), grade.getS(), grade.getA()};
        Iterator<Integer> x = Arrays.asList(150, 294, 444, 594, 739).iterator();
        for (int n : notes) {
            TextElement element = new TextElement(String.valueOf(n), CustomFont.getFont("ARLRDBD", Font.PLAIN, 34F), TextOrientation.MIDDLE);
            element.setColor(new Color(94, 18, 62));
            imageBuilder.addElement(new ElementContainer(element, ElementAlignment.CENTER, x.next(), 211));
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(imageBuilder.toImage(), "png", outputStream);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
